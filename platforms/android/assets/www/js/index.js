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

var player;

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

        //var pcode = 'cf6121d0b92d4760917dae9b93ae92f1';
        var pcode = 'R2d3I6s06RyB712DN0_2GsQS-R-Y';
        //var embedCode = 'h1aG5kcTrQz1rq8L2Pw6qF0Zn9zhmnAk';
        var embedCode = 'h5OWFoYTrG4YIPdrDKrIz5-VhobsuT-M';
        var embedCodes = ['h1aG5kcTrQz1rq8L2Pw6qF0Zn9zhmnAk'];
        var adsetCode = 'd01204eea15f466c92e890d14c7df8b6';
        var domain = 'http://www.ooyala.com';
        var externalId = 'externalID';
        var externalIds = ['externalID1', 'externalID2'];
        var customAnalyticsTags = ['tag1', 'tag2'];

        // Create ooyala player
        player = create_player(pcode, domain);

        // LISTEN TO MESSAGE BUS EVENT
        player.mb.subscribe(EVENTS.PLAYER_CREATED, 'cordova-app',
                    function(result) {
                        if (result) {
                            console.log("---PLAYER IS CREATED SUCCESSFULLY---");

                            // Set embed code
                            player.setEmbedCode(embedCode, app.successHandler, app.failureHandler);

                            // Play video
                            player.play(app.successHandler, app.failureHandler);

                            // Set full screen
                            player.setFullscreen(true, app.successHandler, app.failureHandler);

                            // Get metadata
                            player.getMetadata(app.successHandler, app.failureHandler);

                        } else {
                            console.log("---PLAYER IS NOT CREATED---")
                        }
                    }
                );

        // PAUSED event callback
        player.mb.subscribe(EVENTS.PAUSED, 'cordova-app',
            function(params) {
                console.log("---PLAYER IS PAUSED---");
            }
        );

        // PLAYING event callback
        player.mb.subscribe(EVENTS.PLAYING, 'cordova-app',
            function(params) {
                console.log("---PLAYER IS PLAYING---");
            }
        );

        // SEEKED event callback
        player.mb.subscribe(EVENTS.SEEKED, 'cordova-app',
            function(params) {
                console.log("---PLAYER IS SEEKED---");
            }
        );

        // SEEKED event callback
        player.mb.subscribe(EVENTS.METADATA_FETCHED, 'cordova-app',
            function(params) {
                console.log("---META DATA IS FETCHED---");
            }
        );

        // FULLSCREEN_CHANGED event callback
                player.mb.subscribe(EVENTS.FULLSCREEN_CHANGED, 'cordova-app',
                    function(params) {
                        console.log("---FULLSCREEN CHANGED---");
                        alert(params);
                        console.log(params);
                    }
                );

        // ERROR event callback
        player.mb.subscribe(EVENTS.ERROR, 'cordova-app',
            function(params) {
                console.log("---PLAYER ERROR---");
                console.log(params);
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
    },

    successHandler: function(result) {
        console.log(result);
    },

    failureHandler: function(error) {
        console.log(error)
    }
};

app.initialize();