ooyala-IMA-SDK-phonegap-wrapper
===============================
android cordova plugin for Ooyala IMA SDK

- Create player object with pcode and domain

  // Create player 
  player = window.ooyalaIMA.createPlayer('cf6121d0b92d4760917dae9b93ae92f1', 'http://www.ooyala.com');
  

- Listen to 'PLAYER_CREATED' event. If player is created, set embed code, full screen and then play video.

  player.mb.subscribe('PLAYER_CREATED', 'cordova-app',
  
    function(result) {
    
      if (result) {
        
        console.log("---PLAYER IS CREATED SUCCESSFULLY---");
        
        // Set embed code
        player.setEmbedCode('h1aG5kcTrQz1rq8L2Pw6qF0Zn9zhmnAk', null, null);
        
        // Set full screen
        player.setFullscreen(true, null, null);
        
        // Play video
        player.play(null, null);
      } else {
        console.log("---PLAYER IS NOT CREATED---");
      }
    }
  );
