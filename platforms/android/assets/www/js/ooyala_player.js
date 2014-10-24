var ooyala_player = {
    setEmbedCodes: function(embedCodes, successCallback, errorCallback)
     {
        cordova.exec(
            successCallback,
            errorCallback,
            'OoyalaPlayerPlugin',
            'setEmbedCodes',
            embedCodes
        );
     },

     setPcode : function(pcode, successCallback, errorCallback) {
        cordova.exec(
                    successCallback,
                    errorCallback,
                    'OoyalaPlayerPlugin',
                    'setPcode',
                    [pcode]
                );
     },

     setDomain : function(domain, successCallback, errorCallback) {
        cordova.exec(
                    successCallback,
                    errorCallback,
                    'OoyalaPlayerPlugin',
                    'setDomain',
                    [domain]
        );
     },

     play : function(successCallback, errorCallback) {
        cordova.exec(
                        successCallback,
                        errorCallback,
                        'OoyalaPlayerPlugin',
                        'play',
                        [null]
            );
     }

/*
    setEmbedCodes: function(title, location, notes, startDate, endDate, successCallback, errorCallback) {
        cordova.exec(
            successCallback,
            errorCallback,
            'OoyalaPlayerPlugin',
            'setEmbedCodes',
            [{
                "title": title,
                "description": notes,
                "eventLocation": location,
                "startTimeMillis": startDate.getTime(),
                "endTimeMillis": endDate.getTime()
            }]
        );
     }
     */
}