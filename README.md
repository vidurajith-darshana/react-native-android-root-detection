# react-native-android-root-detection
Rootbeer and MagiskHide implementation for React Native (Android)

Please do this change if you are going to add these files to your own project.

* Aware about the package names
* If your RootDetection.class, package name is com.abc.security, you have to do the below change when you copy this code.


```sh
    In this example project -> android -> app -> src -> main -> c -> native-lib.c
    line no 40:
    
    replace this line,
    
    Java_com_rootdetector_rooting_
    
    with your package name of the RootDetection.class. in my case it is com.rootdetector.rooting
    
    ex: Java_com_abc_security_Native_isMagiskPresentNative(
    
```

* add this line to android -> app -> build.gradle

```sh    
    implementation 'com.scottyab:rootbeer-lib:0.1.0'
```

* android -> app -> build.gradle


    under the build types, add this line.

```sh    
    buildTypes {
        debug {
            ...
        }
        release {
            ...
        }
        externalNativeBuild {
            cmake {
                path "src/main/c/CMakeLists.txt"
            }
        }
    }
```

    under the default config, add this line.

```sh 
    defaultConfig {
        ...
        externalNativeBuild {
            cmake {
                cppFlags ""
            }
        }
    }
```


