
import React, {useEffect, useState} from 'react';
import {
  NativeModules,
  Text,
  View,
} from 'react-native';

const { RootDetection } = NativeModules;

function App() {

  const [isRootDetected, setIsRootDetected] = useState(false);

  useEffect(()=>{
    RootDetection.detect((err: any, msg: any) => {
      if (err) {
        console.log("ERR: ", err);
        return;
      }
      setIsRootDetected(msg);
    })
  },[])

  return (
    <View style={{justifyContent: 'center', alignItems: 'center', height: 800, backgroundColor: 'lightblue'}}>
      <Text style={{fontWeight: 'bold'}}> ROOT DETECTION ANDROID </Text>
      <Text style={{marginTop: 50}}>Is Device Rooted : {isRootDetected + ""}</Text>
    </View>
  );
}
export default App;
