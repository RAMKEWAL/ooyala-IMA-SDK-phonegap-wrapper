package com.dong.ooyala;

/**
 * Created with IntelliJ IDEA.
 * User: DongKai.Li
 * Date: 10/30/14
 * Time: 9:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Constants {
    // Intent params
    public static final String IP_EVENT = "event";  // Message bus event
    public static final String IP_PARAMS = "params";

    // ACTION CODES
    // Common
    public static final String ACTION_SET_MSGBUSEVENTHANDLER = "setMessageBusEventHandler";     // Private
    public static final String ACTION_CREATE_PLAYER = "createPlayer";                                                   // Private
    public static final String ACTION_SET_EMBEDCODE = "setEmbedCode";
    public static final String ACTION_SET_EMBEDCODES = "setEmbedCodes";
    public static final String ACTION_SET_EMBEDCODE_WITHADSETCODE = "setEmbedCodeWithAdSetCode";
    public static final String ACTION_SET_EMBEDCODES_WITHADSETCODE = "setEmbedCodesWithAdSetCode";
    public static final String ACTION_SET_EXTERNALID = "setExternalId";
    public static final String ACTION_SET_EXTERNALIDS = "setExternalIds";
    public static final String ACTION_CHANGECURRENTITEM = "changeCurrentItem";
    public static final String ACTION_GET_PLAYHEADTIME = "getPlayheadTime";
    public static final String ACTION_GET_DURATION = "getDuration";
    public static final String ACTION_SET_PLAYHEADTIME = "setPlayheadTime";
    public static final String ACTION_GET_STATE = "getState";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_PLAYWITHINITIALTIME = "playWithInitialTime";
    public static final String ACTION_SEEK = "seek";
    public static final String ACTION_ISPLAYING = "isPlaying";
    public static final String ACTION_ISSHOWINGAD = "isShowingAd";
    public static final String ACTION_NEXTVIDEO = "nextVideo";
    public static final String ACTION_PREVIOUSVIDEO = "previousVideo";
    public static final String ACTION_GET_AVAILABLECLOSEDCAPTIONSLANGUAGES = "getAvailableClosedCaptionsLanguages";
    public static final String ACTION_SET_CLOSEDCAPTIONSLANGUAGE = "setClosedCaptionsLanguage";
    public static final String ACTION_SET_CLOSEDCAPTIONSPRESENTATIONSTYLE = "setClosedCaptionsPresentationStyle";
    public static final String ACTION_GET_BITRATE = "getBitrate";
    public static final String ACTION_RESETADS = "resetAds";
    public static final String ACTION_SKIPAD = "skipAd";
    public static final String ACTION_SET_CUSTOMANALYTICSTAGS = "setCustomAnalyticsTags";
    public static final String ACTION_GET_METADATA = "getMetadata";
    public static final String ACTION_SEEKABLE = "seekable";
    public static final String ACTION_SET_SEEKABLE = "setSeekable";
    public static final String ACTION_SET_ADS_SEEKABLE = "setAdsSeekable";
    public static final String ACTION_GET_SEEKSTYLE = "getSeekStyle";
    public static final String ACTION_GET_CLOSEDCAPTIONSLANGUAGE = "getClosedCaptionsLanguage";
    public static final String ACTION_GET_ACTIONATEND = "getActionAtEnd";
    public static final String ACTION_SET_ACTIONATEND = "setActionAtEnd";
    public static final String ACTION_GET_AUTHTOKEN = "getAuthToken";

    // Android only
    public static final String ACTION_GET_EMBEDCODE = "getEmbedCode";
    public static final String ACTION_GET_CUSTOMDRMDATA = "getCustomDRMData";
    public static final String ACTION_SET_CUSTOMDRMDATA = "setCustomDRMData";
    public static final String ACTION_SUSPEND = "suspend";
    public static final String ACTION_RESUME = "resume";
    public static final String ACTION_ISFULLSCREEN = "isFullscreen";
    public static final String ACTION_SET_FULLSCREEN = "setFullscreen";
    public static final String ACTION_ISADPLAYING = "isAdPlaying";
    public static final String ACTION_SEEKTOPERCENT = "seekToPercent";
    public static final String ACTION_GET_BUFFER_PERCENTAGE = "getBufferPercentage";
    public static final String ACTION_GET_PLAYHEAD_PERCENTAGE = "getPlayheadPercentage";
    public static final String ACTION_DISPLAYCLOSEDCAPTIONTEXT = "displayClosedCaptionText";
    public static final String ACTION_GET_CUEPOINTSINMILLISECONDS = "getCuePointsInMilliSeconds";
    public static final String ACTION_GET_CUEPOINTSINPERCENTAGE = "getCuePointsInPercentage";

    // IMA Manager actions
    public static final String ACTION_SET_ADURLOVERRIDE = "setAdUrlOverride";
    public static final String ACTION_SET_ADTAGPARAMS = "setAdTagParameters";

    // Events
    public static final String authTokenChanged = "authTokenChanged";
    public static final String ADS_CLICK = "ADS_CLICK";
    public static final String ADS_PLAYED = "ADS_PLAYED";
    public static final String AD_AUTHORIZATION_FETCHED = "AD_AUTHORIZATION_FETCHED";
    public static final String AD_CONFIG_READY = "AD_CONFIG_READY";
    public static final String AUTHORIZATION_FETCHED = "AUTHORIZATION_FETCHED";
    public static final String BITRATE_CHANGED = "BITRATE_CHANGED";
    public static final String BUFFERED = "BUFFERED";
    public static final String BUFFERING = "BUFFERING";
    public static final String CONTENT_TREE_FETCHED = "CONTENT_TREE_FETCHED";
    public static final String CONTROLS_HIDDEN = "CONTROLS_HIDDEN";
    public static final String CONTROLS_SHOWN = "CONTROLS_SHOWN";
    public static final String DESTROY = "DESTROY";
    public static final String DOWNLOADING = "DOWNLOADING";
    public static final String EMBED_CODE_CHANGED = "EMBED_CODE_CHANGED";
    public static final String ERROR = "ERROR";
    public static final String FIRST_AD_FETCHED = "FIRST_AD_FETCHED";
    public static final String FULLSCREEN_CHANGED = "FULLSCREEN_CHANGED";
    public static final String METADATA_FETCHED = "METADATA_FETCHED";
    public static final String MIDROLL_PLAY_FAILED = "MIDROLL_PLAY_FAILED";
    public static final String MIDROLL_STREAM_PLAYED = "MIDROLL_STREAM_PLAYED";
    public static final String PAUSED = "PAUSED";
    public static final String PLAYBACK_READY = "PLAYBACK_READY";
    public static final String PLAYED = "PLAYED";
    public static final String PLAYER_CREATED = "PLAYER_CREATED";
    public static final String PLAYHEAD_TIME_CHANGED = "PLAYHEAD_TIME_CHANGED";
    public static final String PLAYING = "PLAYING";
    public static final String PLAY_FAILED = "PLAY_FAILED";
    public static final String PRELOAD_STREAM = "PRELOAD_STREAM";
    public static final String SEEKED = "SEEKED";
    public static final String SINGLE_AD_PLAYED = "SINGLE_AD_PLAYED";
    public static final String SIZE_CHANGED = "SIZE_CHANGED";
    public static final String STREAM_PAUSED = "STREAM_PAUSED";
    public static final String STREAM_PLAYED = "STREAM_PLAYED";
    public static final String STREAM_PLAYING = "STREAM_PLAYING";
    public static final String STREAM_PLAY_FAILED = "STREAM_PLAY_FAILED";
    public static final String VOLUME_CHANGED = "VOLUME_CHANGED";
}
