package com.dong.ooyala;

import android.content.Intent;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created with IntelliJ IDEA.
 * User: DongKai.Li
 * Date: 10/22/14
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class OoyalaPlayerPlugin extends CordovaPlugin {
    // Constants for actions
    public static final String ACTION_SET_PCODE = "setPcode";
    public static final String ACTION_SET_DOMAIN = "setDomain";
    public static final String ACTION_BEGIN_FETCHING_ADVERTISING_ID = "beginFetchingAdvertisingID";
    public static final String ACTION_SET_EMBED_CODES = "setEmbedCodes";
    public static final String ACTION_SET_EMBED_CODES_WITH_ADSETCODE = "setEmbedCodesWithAdSetCode";
    public static final String ACTION_GET_EMBED_CODE = "getEmbedCode";
    public static final String ACTION_GET_STATE = "getState";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_SUSPEND = "suspend";
    public static final String ACTION_RESUME = "resume";
    public static final String ACTION_ISFULLSCREEN = "isFullscreen";
    public static final String ACTION_SET_FULLSCREEN = "setFullscreen";
    public static final String ACTION_GET_PLAYHEADTIME = "getPlayheadTime";
    public static final String ACTION_SET_PLAYHEADTIME = "setPlayheadTime";
    public static final String ACTION_SEEKABLE = "seekable";
    public static final String ACTION_SEEK = "seek";
    public static final String ACTION_GET_BITRATE = "getBitrate";
    public static final String ACTION_ISPLAYING = "isPlaying";
    public static final String ACTION_ISADPLAYING = "isAdPlaying";
    public static final String ACTION_SEEKTOPERCENT = "seekToPercent";
    public static final String ACTION_GET_DURATION = "getDuration";
    public static final String ACTION_GET_BUFFER_PERCENTAGE = "getBufferPercentage";
    public static final String ACTION_GET_PLAYHEAD_PERCENTAGE = "getPlayheadPercentage";
    public static final String ACTION_SET_ADS_SEEKABLE = "setAdsSeekable";
    public static final String ACTION_SET_SEEKABLE = "setSeekable";
    public static final String ACTION_RESETADS = "resetAds";
    public static final String ACTION_SKIPAD = "skipAd";
    public static final String ACTION_ISSHOWINGAD = "isShowingAd";
    public static final String ACTION_SET_CURRENTITEM_CHANGEDCALLBACK = "setCurrentItemChangedCallback";

    public static final String ACTION_ADD_COMPANIONSLOT = "addCompanionSlot";
    public static final String ACTION_SET_ADURLOVERRIDE = "setAdUrlOverride";
    public static final String ACTION_SET_ADTAGPARAMS = "setAdTagParameters";

    // Member variables
    private String [] aEmbedCodes = null;
    private String sPcode = null;
    private String sDomain = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if (ACTION_SET_EMBED_CODES.equals(action)) {
                if (args.length() == 0) {
                    aEmbedCodes = null;
                } else {
                    aEmbedCodes = new String [args.length()];
                    for (int i = 0; i < args.length(); i ++) {
                        aEmbedCodes[i] = args.getString(i);
                    }
                }

                return true;
            } else if (ACTION_SET_PCODE.equals(action)) {
                sPcode = args.getString(0);

                return true;
            } else if (ACTION_SET_DOMAIN.equals(action)) {
                sDomain = args.getString(0);

                return true;
            } else if (ACTION_PLAY.equals(action)) {
                // Start activity


                /*
                if (aEmbedCodes != null) intent.putExtra(ACTION_SET_EMBED_CODES, aEmbedCodes);
                if (sPcode != null) intent.putExtra(ACTION_SET_PCODE, sPcode);
                if (sDomain != null) intent.putExtra(ACTION_SET_DOMAIN, sDomain);
                */

                /*
                this.cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    */
                        Intent intent = new Intent(OoyalaPlayerPlugin.this.cordova.getActivity(), PlayerActivity.class);
                        OoyalaPlayerPlugin.this.cordova.getActivity().startActivity(intent);
                /*
                    }
                });
                */

                return true;
            }

            callbackContext.error("Invalid action");

            return false;
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
            if (callbackContext != null) {
                callbackContext.error(e.getMessage());
            }

            return false;
        }
    }
}
