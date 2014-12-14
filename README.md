ooyala-IMA-SDK-phonegap-wrapper
===============================
Cordova plugin for OoyalaIMA SDK

- Installation
```
  cordova plugin add https://github.com/fubotv/ooyala-IMA-SDK-phonegap-wrapper.git
```
 
- Create player object
```
  // Create player
  player = window.ooyalaIMA.createPlayer('cf6121d0b92d4760917dae9b93ae92f1', 'http://www.ooyala.com');
  
  // Create player with frame rectangle info
  player = window.ooyalaIMA.createPlayer('cf6121d0b92d4760917dae9b93ae92f1', 'http://www.ooyala.com', 
                                            84, 256, 600, 512);
```
  
- Listen to 'PLAYER_CREATED' event. If player is created, set embed code, full screen and then play video.
```
  player.mb.subscribe('PLAYER_CREATED', 'cordova-app',
    function(result) {
      if (result) {
        console.log("---PLAYER IS CREATED SUCCESSFULLY---");
        
        // Set embed code
        player.setEmbedCode('h1aG5kcTrQz1rq8L2Pw6qF0Zn9zhmnAk', null, null);
        
        // Play video
        player.play(null, null);
      } else {
        console.log("---PLAYER IS NOT CREATED---");
      }
    }
  );
```

- For iOS only
  add additional libraries to project. 
  on Xcode, select project icon, click 'Build Phases'.
  open 'Link Binary With Libraries' and add following libraries.

  ```
  libz.dylib
  libc++.dylib
  libxml2.dylib
  ```

- For more information, please refer to 'Reference.pdf' in doc folder. You can find more functions and events which you can listen to there.
