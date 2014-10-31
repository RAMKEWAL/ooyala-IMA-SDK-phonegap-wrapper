// Constants
const PLAYER_PLUGIN = "OoyalaPlayerPlugin";
const ACTION_SET_MSGBUSEVENTHANDLER = "setMessageBusEventHandler";
const ACTION_CREATE_PLAYER = "createPlayer";

const ACTION_SET_EMBEDCODE = "setEmbedCode";
const ACTION_SET_EMBEDCODES = "setEmbedCodes";
const ACTION_SET_EMBEDCODE_WITHADSETCODE = "setEmbedCodeWithAdSetCode";
const ACTION_SET_EMBEDCODES_WITHADSETCODE = "setEmbedCodesWithAdSetCode";
const ACTION_SET_EXTERNALID = "setExternalId";
const ACTION_SET_EXTERNALIDS = "setExternalIds";
const ACTION_CHANGECURRENTITEM = "changeCurrentItem";
const ACTION_GET_METADATA = "getMetadata";
const ACTION_GET_EMBEDCODE = "getEmbedCode";
const ACTION_GET_AUTHTOKEN = "getAuthToken";
const ACTION_GET_CUSTOMDRMDATA = "getCustomDRMData";
const ACTION_SET_CUSTOMDRMDATA = "setCustomDRMData";
const ACTION_GET_STATE = "getState";
const ACTION_PAUSE = "pause";
const ACTION_PLAY = "play";
const ACTION_PLAYWITHINITIALTIME = "playWithInitialTime";
const ACTION_SUSPEND = "suspend";
const ACTION_RESUME = "resume";
const ACTION_ISFULLSCREEN = "isFullscreen";
const ACTION_SET_FULLSCREEN = "setFullscreen";
const ACTION_GET_TOPBAROFFSET = "getTopBarOffset";
const ACTION_GET_PLAYHEADTIME = "getPlayheadTime";
const ACTION_SET_PLAYHEADTIME = "setPlayheadTime";
const ACTION_SEEKABLE = "seekable";
const ACTION_SEEK = "seek";
const ACTION_PREVIOUSVIDEO = "previousVideo";
const ACTION_NEXTVIDEO = "nextVideo";
const ACTION_GET_ACTIONATEND = "getActionAtEnd";
const ACTION_SET_ACTIONATEND = "setActionAtEnd";
const ACTION_SET_CLOSEDCAPTIONSLANGUAGE = "setClosedCaptionsLanguage";
const ACTION_SET_CLOSEDCAPTIONSPRESENTATIONSTYLE = "setClosedCaptionsPresentationStyle";
const ACTION_GET_CLOSEDCAPTIONSLANGUAGE = "getClosedCaptionsLanguage";
const ACTION_GET_AVAILABLECLOSEDCAPTIONSLANGUAGES = "getAvailableClosedCaptionsLanguages";
const ACTION_GET_BITRATE = "getBitrate";
const ACTION_ISPLAYING = "isPlaying";
const ACTION_ISADPLAYING = "isAdPlaying";
const ACTION_SEEKTOPERCENT = "seekToPercent";
const ACTION_GET_DURATION = "getDuration";
const ACTION_GET_BUFFER_PERCENTAGE = "getBufferPercentage";
const ACTION_GET_PLAYHEAD_PERCENTAGE = "getPlayheadPercentage";
const ACTION_SET_ADS_SEEKABLE = "setAdsSeekable";
const ACTION_SET_SEEKABLE = "setSeekable";
const ACTION_RESETADS = "resetAds";
const ACTION_SKIPAD = "skipAd";
const ACTION_ISSHOWINGAD = "isShowingAd";
const ACTION_SET_CLOSEDCAPTIONSBOTTOMMARGIN = "setClosedCaptionsBottomMargin";
const ACTION_DISPLAYCLOSEDCAPTIONTEXT = "displayClosedCaptionText";
const ACTION_SET_CUSTOMANALYTICSTAGS = "setCustomAnalyticsTags";
const ACTION_GET_SEEKSTYLE = "getSeekStyle";
const ACTION_GET_CUEPOINTSINMILLISECONDS = "getCuePointsInMilliSeconds";
const ACTION_GET_CUEPOINTSINPERCENTAGE = "getCuePointsInPercentage";

const EVENTS = {
    authTokenChanged : "authTokenChanged",
    ADS_CLICK : "ADS_CLICK",
    ADS_PLAYED : "ADS_PLAYED",
    AD_AUTHORIZATION_FETCHED : "AD_AUTHORIZATION_FETCHED",
    AD_CONFIG_READY : "AD_CONFIG_READY",
    AUTHORIZATION_FETCHED : "AUTHORIZATION_FETCHED",
    BITRATE_CHANGED : "BITRATE_CHANGED",
    BUFFERED : "BUFFERED",
    BUFFERING : "BUFFERING",
    CHANGE_VOLUME : "CHANGE_VOLUME",
    CONTENT_TREE_FETCHED : "CONTENT_TREE_FETCHED",
    CONTROLS_HIDDEN : "CONTROLS_HIDDEN",
    CONTROLS_SHOWN : "CONTROLS_SHOWN",
    DESTROY : "DESTROY",
    DOWNLOADING : "DOWNLOADING",
    EMBED_CODE_CHANGED : "EMBED_CODE_CHANGED",
    ERROR : "ERROR",
    FIRST_AD_FETCHED : "FIRST_AD_FETCHED",
    FULLSCREEN_CHANGED : "FULLSCREEN_CHANGED",
    METADATA_FETCHED : "METADATA_FETCHED",
    MIDROLL_PLAY_FAILED : "MIDROLL_PLAY_FAILED",
    MIDROLL_STREAM_PLAYED : "MIDROLL_STREAM_PLAYED",
    PAUSE : "PAUSE",
    PAUSED : "PAUSED",
    PAUSE_STREAM : "PAUSE_STREAM",
    PLAY : "PLAY",
    PLAYBACK_READY : "PLAYBACK_READY",
    PLAYED : "PLAYED",
    PLAYER_CREATED : "PLAYER_CREATED",
    PLAYHEAD_TIME_CHANGED : "PLAYHEAD_TIME_CHANGED",
    PLAYING : "PLAYING",
    PLAY_FAILED : "PLAY_FAILED",
    PLAY_MIDROLL_STREAM : "PLAY_MIDROLL_STREAM",
    PLAY_STREAM : "PLAY_STREAM",
    PRELOAD_STREAM : "PRELOAD_STREAM",
    SEEK : "SEEK",
    SEEKED : "SEEKED",
    SEEK_STREAM : "SEEK_STREAM",
    SET_EMBED_CODE : "SET_EMBED_CODE",
    SINGLE_AD_PLAYED : "SINGLE_AD_PLAYED",
    SIZE_CHANGED : "SIZE_CHANGED",
    STREAM_PAUSED : "STREAM_PAUSED",
    STREAM_PLAYED : "STREAM_PLAYED",
    STREAM_PLAYING : "STREAM_PLAYING",
    STREAM_PLAY_FAILED : "STREAM_PLAY_FAILED",
    VOLUME_CHANGED : "VOLUME_CHANGED",
    WILL_CHANGE_FULLSCREEN : "WILL_CHANGE_FULLSCREEN",
    WILL_FETCH_ADS : "WILL_FETCH_ADS",
    WILL_FETCH_AD_AUTHORIZATION : "WILL_FETCH_AD_AUTHORIZATION",
    WILL_FETCH_AUTHORIZATION : "WILL_FETCH_AUTHORIZATION",
    WILL_FETCH_CONTENT_TREE : "WILL_FETCH_CONTENT_TREE",
    WILL_FETCH_METADATA : "WILL_FETCH_METADATA",
    WILL_PLAY : "WILL_PLAY",
    WILL_PLAY_ADS : "WILL_PLAY_ADS",
    WILL_PLAY_FROM_BEGINNING : "WILL_PLAY_FROM_BEGINNING",
    WILL_PLAY_SINGLE_AD : "WILL_PLAY_SINGLE_AD",
    WILL_PLAY_STREAM : "WILL_PLAY_STREAM",
    WILL_RESUME_MAIN_VIDEO : "WILL_RESUME_MAIN_VIDEO",
    WILL_SHOW_COMPANION_ADS : "WILL_SHOW_COMPANION_ADS"
};

const SeekStyle = {
    NONE : 0,
    BASIC : 1,
    ENHANCED : 2
};

const State = {
    INIT : 0,
    LOADING : 1,
    READY : 2,
    PLAYING : 3,
    PAUSED : 4,
    COMPLETED : 5,
    SUSPENDED : 6,
    ERROR : 7
};

const ActionAtEnd = {
    CONTINUE : 0,
    PAUSE : 1,
    STOP : 2,
    RESET : 3
};

const OOClosedCaptionPresentation = {
    OOClosedCaptionPopOn : 0,
    OOClosedCaptionRollUp : 1,
    OOClosedCaptionPaintOn : 2
};

function create_player(pcode, domain) {
    ooyala_player.mb = MessageBus;

    cordova.exec(MessageBus.handler, null, PLAYER_PLUGIN,
            ACTION_SET_MSGBUSEVENTHANDLER, [null]);
    cordova.exec(null, null, PLAYER_PLUGIN, ACTION_CREATE_PLAYER, [{"pcode":pcode, "domain":domain}]);

    return ooyala_player;
}

var _callbacks = {};
var MessageBus = {
    handler : function(jsonObj) {
        _callbacks[jsonObj.event].forEach(function(item, index, array) {
            item.callback(jsonObj.params);
        });
    },

    subscribe : function(eventName, subscriber, callback) {
        if (!_callbacks[eventName]) {
            _callbacks[eventName] = new Array();
        }

        _callbacks[eventName].push({"subscriber": subscriber, "callback": callback});
    }
}

var ooyala_player = {
    mb : null,

    setEmbedCode: function(embedCode, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_EMBEDCODE, [embedCode]);
    },

    setEmbedCodes: function(embedCodes, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_EMBEDCODES, embedCodes);
    },

    setEmbedCodeWithAdSetCode: function(embedCode, adsetcode, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_EMBEDCODE_WITHADSETCODE,
         [{"EmbedCode":embedCode, "AdSetCode":adsetcode}]);
    },

    setEmbedCodesWithAdSetCode: function(embedCodes, adsetcode, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_EMBEDCODES_WITHADSETCODE,
         [{"EmbedCodes":embedCodes, "AdSetCode":adsetcode}]);
    },

    setExternalIds: function(externalIDs, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_EXTERNALIDS, externalIDs);
    },

    changeCurrentItem: function(item, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_CHANGECURRENTITEM, [item]);
    },

    getMetadata : function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_METADATA, [null]);
    },

    getEmbedCode: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_EMBEDCODE, [null]);
    },

    getAuthToken: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_AUTHTOKEN, [null]);
    },

    getCustomDRMData: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_CUSTOMDRMDATA, [null]);
    },

    setCustomDRMData: function(customDRMData, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_CUSTOMDRMDATA, [customDRMData]);
    },

    getState: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_STATE, [null]);
    },

    pause: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_XXX, [null]);
    },

    play : function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_PLAY, [null]);
    },

    playWithInitialTime : function(time, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_PLAYWITHINITIALTIME, [time]);
    },

    suspend: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SUSPEND, [null]);
    },

    resume: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_RESUME, [null]);
    },

    isFullscreen: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_ISFULLSCREEN, [null]);
    },

    setFullscreen: function(bFullScreen, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_FULLSCREEN, [bFullScreen]);
    },

    getTopBarOffset: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_TOPBAROFFSET, [null]);
    },

    getPlayheadTime: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_PLAYHEADTIME, [null]);
    },

    setPlayheadTime: function(time, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_PLAYHEADTIME, [time]);
    },

    seekable: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SEEKABLE, [null]);
    },

    seek: function(time, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SEEK, [time]);
    },

    previousVideo: function(what, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_PREVIOUSVIDEO, [what]);
    },

    nextVideo: function(what, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_NEXTVIDEO, [what]);
    },

    getActionAtEnd: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_ACTIONATEND, [null]);
    },

    setActionAtEnd: function(actionAtEnd, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_ACTIONATEND, [actionAtEnd]);
    },

    setClosedCaptionsLanguage: function(lang, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_CLOSEDCAPTIONSLANGUAGE, [lang]);
    },

    setClosedCaptionsPresentationStyle: function(style, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_CLOSEDCAPTIONSPRESENTATIONSTYLE, [style]);
    },

    getClosedCaptionsLanguage: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_CLOSEDCAPTIONSLANGUAGE, [null]);
    },

    getAvailableClosedCaptionsLanguages: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_AVAILABLECLOSEDCAPTIONSLANGUAGES, [null]);
    },

    getBitrate: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_BITRATE, [null]);
    },

    isPlaying: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_ISPLAYING, [null]);
    },

    isAdPlaying: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_ISADPLAYING, [null]);
    },

    seekToPercent: function(percent, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SEEKTOPERCENT, [percent]);
    },

    getDuration: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_DURATION, [null]);
    },

    getBufferPercentage: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_BUFFER_PERCENTAGE, [null]);
    },

    getPlayheadPercentage: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_PLAYHEAD_PERCENTAGE, [null]);
    },

    setAdsSeekable: function(flag, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_ADS_SEEKABLE, [flag]);
    },

    setSeekable: function(flag, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_SEEKABLE, [flag]);
    },

    resetAds: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_RESETADS, [null]);
    },

    skipAd: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SKIPAD, [null]);
    },

    isShowingAd: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_ISSHOWINGAD, [null]);
    },

    setClosedCaptionsBottomMargin: function(bottomMargin, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_CLOSEDCAPTIONSBOTTOMMARGIN, [bottomMargin]);
    },

    displayClosedCaptionText: function(txt, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_DISPLAYCLOSEDCAPTIONTEXT, [txt]);
    },

    setCustomAnalyticsTags: function(tags, success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_SET_CUSTOMANALYTICSTAGS, tags);
    },

    getSeekStyle: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_SEEKSTYLE, [null]);
    },

    getCuePointsInMilliSeconds: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_CUEPOINTSINMILLISECONDS, [null]);
    },

    getCuePointsInPercentage: function(success, failure) {
        cordova.exec(success, failure, PLAYER_PLUGIN, ACTION_GET_CUEPOINTSINPERCENTAGE, [null]);
    }
}