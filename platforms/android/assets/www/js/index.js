/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var app = {
    // Application Constructor
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        app.receivedEvent('deviceready');

        // For testing
        var pcode = 'cf6121d0b92d4760917dae9b93ae92f1';
        var embedCode = 'h1aG5kcTrQz1rq8L2Pw6qF0Zn9zhmnAk';
        var embedCodes = ['h1aG5kcTrQz1rq8L2Pw6qF0Zn9zhmnAk'];
        var adsetCode = 'd01204eea15f466c92e890d14c7df8b6';
        var domain = 'http://www.ooyala.com';
        var externalId = 'externalID';
        var externalIds = ['externalID1', 'externalID2'];
        var customAnalyticsTags = ['tag1', 'tag2'];

        // Create ooyala player
        player = create_player(pcode, domain);

        // Set embedcodes
        player.setEmbedCode(embedCode,
            function(msg) {
                console.log("success : " + msg);
            },
            function(msg) {
                console.log("failure : " + msg);
            }
        );
        //player.setEmbedCodes(embedCodes, null, null);
        //player.setEmbedCodeWithAdSetCode(embedCode, adsetCode, null, null);
        //player.setEmbedCodesWithAdSetCode(embedCodes, adsetCode, null, null);

        // Set fullscreen
        player.setFullscreen(true, null, null);

        // Set external IDs .. Please change externalId(s) values to actual ones
        //player.setExternalId(externalId, null, null);
        //player.setExternalIds(externalIds, null, null);

        // Set custom analytics tags
        player.setCustomAnalyticsTags(customAnalyticsTags, null, null);

        // Set custom DRM Data
        player.setCustomDRMData('xxx', null, null);

        // Set closed captions lang
        player.setClosedCaptionsLanguage('eng', null, null);

        // Set ads seekable
        player.setAdsSeekable(true, null, null);

        // Set video seekable
        player.setSeekable(true, null, null);

        // Set playhead time
        //player.setPlayheadTime(20 * 1000);

        // Set action at end
        player.setActionAtEnd(ActionAtEnd.PAUSE, null, null);

        // Set closed captions presentation style
        //player.setClosedCaptionsPresentationStyle(OOClosedCaptionPresentation.OOClosedCaptionPopOn, null, null);

        // Set closed captions bottom margin
        //player.setClosedCaptionsBottomMargin(0, null, null);

        // Play video
        player.play(null, null);
        //player.playWithInitialTime(3 * 1000, null, null);

        // LISTEN TO MESSAGE BUS EVENT
        player.mb.subscribe(EVENTS.PAUSED, 'cordova-app',
            function(params) {
                console.log("player is paused.");
                player.resume(null, null);
                player.setFullscreen(true, null, null);
            }
        );

        player.mb.subscribe(EVENTS.PLAYING, 'cordova-app',
            function(params) {
                console.log("player is playing");
            }
        );

        player.mb.subscribe(EVENTS.SEEKED, 'cordova-app',
            function(params) {
                console.log("player is seeked");
            }
        );
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

app.initialize();