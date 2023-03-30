package com.rootdetector.rooting;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.scottyab.rootbeer.RootBeer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class RootDetection extends ReactContextBaseJavaModule {

    private ReactContext reactContext;
    private static final String[] blackListedMountPaths = { "magisk", "core/mirror", "core/img"};
    private static final String TAG = "RootDetection-";

    public RootDetection(@Nullable ReactApplicationContext reactContext){
        super(reactContext);
        this.reactContext = reactContext;
    }
    @Override
    public String getName() {
        return "RootDetection";
    }

    @ReactMethod
    public void detect(Callback cb) {

        boolean isMagiskPresent;
        boolean isRooted;

        File file = new File("/proc/self/mounts");

        try {
            FileInputStream fis = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String str;
            int count =0;
            while((str = reader.readLine()) != null && (count==0)){
                //Log.d(TAG, "MountPath:"+ str);
                for(String path:blackListedMountPaths){
                    if(str.contains(path)){
                        Log.d(TAG, "Blacklisted Path found "+ path);
                        count++;
                        break;
                    }
                }
            }
            reader.close();
            fis.close();
            Log.d(TAG, "Count of detected paths "+ count);
            if(count > 0){
                Log.d(TAG, "Found magisk in atleast 1 mount path ");
                isMagiskPresent = true;
            }else {
                /*Incase the java calls are hooked, there is 1 more level
                of check in the native to detect if the same blacklisted paths are
                found in the proc maps along with checks for su files when accessed
                from native.Native functions can also be hooked.But requires some effort
                if it is properly obfuscated and syscalls are used in place of libc calls
                 */
                isMagiskPresent = Native.isMagiskPresentNative();
                Log.d(TAG, "Found Magisk in Native " + isMagiskPresent);
            }

            if (!isMagiskPresent) {
                PackageManager pm = reactContext.getPackageManager();
                @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

                for (int i = 0; i < installedPackages.size(); i++) {
                    PackageInfo info = installedPackages.get(i);
                    ApplicationInfo appInfo = info.applicationInfo;

                    String nativeLibraryDir = appInfo.nativeLibraryDir;

                    Log.i("Magisk Detection", "Checking App: " + nativeLibraryDir);

                    File f = new File(nativeLibraryDir + "/libstub.so");
                    if (f.exists()) {
                        Log.e("Magisk Detection", "Magisk was Detected!");
                        isMagiskPresent = true;
                    }
                }
            }

            RootBeer rootBeer = new RootBeer(reactContext);
            boolean defaultRooted = rootBeer.isRooted();
            boolean busyBoxRooted = rootBeer.isRootedWithBusyBoxCheck();

            Log.i(TAG, "Detect root by root beer: " + defaultRooted);
            Log.i(TAG, "Detect root by busy box: " + busyBoxRooted);

            if (defaultRooted || busyBoxRooted) {
                isRooted = true;
            } else {
                isRooted = false;
            }

            cb.invoke(null, isMagiskPresent || isRooted);

        } catch (IOException e) {
            e.getMessage();
            cb.invoke(e, null);
        }
    }
}
