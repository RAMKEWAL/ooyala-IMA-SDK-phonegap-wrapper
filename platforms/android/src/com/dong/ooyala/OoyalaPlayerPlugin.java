package com.dong.ooyala;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: DongKai.Li
 * Date: 10/22/14
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class OoyalaPlayerPlugin extends CordovaPlugin {
    // Constants
    private static final String MSGERR_NOPLAYERACTIVITY = "Player activity is not opened yet";

    // Member variables
    private String sPcode;
    private String sDomain;
    private String adSetCode;
    private int initialPlayTime;
    private ArrayList<String> aEmbedCodes;
    private ArrayList<String> aExternalIDs;
    private ArrayList<String> aCustomAnalyticsTags;
    private boolean bIsFullscreen;
    private String sCustomDRMData;
    private String sClosedCaptionsLang;
    private boolean bAdsSeekable;
    private boolean bSeekable;
    private int playHeadTime;
    private int actionAtEnd;
    private int closedCaptionsPresentationStyle;
    private int closedCaptionsBottomMargin;

    private CallbackContext msgBusEventCallback = null;
    private HashMap<String, CallbackContext>  actionCallbacks;
    private PlayerActivityEventReceiver receiver = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView)
    {
        super.initialize(cordova, webView);

        // Create action callbacks
        actionCallbacks = new HashMap<String, CallbackContext>();

        // Register receiver for events from player activity
        IntentFilter filter = new IntentFilter(PlayerActivityEventReceiver.ACTION);
        receiver = new PlayerActivityEventReceiver();
        cordova.getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        cordova.getActivity().unregisterReceiver(receiver);

        super.onDestroy();
    }

    private void initVariables() {
        // Initialize variables to send as intent params to player activity
        sPcode = null;
        sDomain = null;
        adSetCode = null;
        initialPlayTime = 0;
        aEmbedCodes = new ArrayList<String>();
        aExternalIDs = new ArrayList<String>();
        aCustomAnalyticsTags = new ArrayList<String>();
        bIsFullscreen = false;
        sCustomDRMData = null;
        sClosedCaptionsLang = null;
        bAdsSeekable = false;
        bSeekable = true;
        playHeadTime = -1;
        actionAtEnd = -1;
        closedCaptionsPresentationStyle = -1;
        closedCaptionsBottomMargin = -1;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            actionCallbacks.put(action, callbackContext);

            // Private mechanism methods
            if (Constants.ACTION_SET_MSGBUSEVENTHANDLER.equals(action)) {
                msgBusEventCallback = callbackContext;

                return true;
            } else if (Constants.ACTION_CREATE_PLAYER.equals(action)) {
                initVariables();

                JSONObject jsonObject = args.getJSONObject(0);
                sPcode = jsonObject.getString("pcode");
                sDomain = jsonObject.getString("domain");

                return true;

            // Later set able methods
            } else if (Constants.ACTION_SET_EMBEDCODE.equals(action)) {
                if (args.length() > 0) {
                    aEmbedCodes.clear();
                    aEmbedCodes.add(args.getString(0));
                    adSetCode = null;

                    if (CordovaApp.bPlayerActivityRunning) {
                        Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                        intent.putExtra(Constants.IP_ACTION, action);
                        intent.putExtra(Constants.IP_EMBEDCODE, aEmbedCodes.get(0));
                        this.cordova.getActivity().sendBroadcast(intent);
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EMBEDCODES.equals(action)) {
                if (args.length() > 0) {
                    aEmbedCodes.clear();
                    for (int i = 0; i < args.length(); i ++) aEmbedCodes.add(args.getString(i));
                    adSetCode = null;

                    if (CordovaApp.bPlayerActivityRunning) {
                        Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                        intent.putExtra(Constants.IP_ACTION, action);
                        intent.putExtra(Constants.IP_EMBEDCODES, aEmbedCodes);
                        this.cordova.getActivity().sendBroadcast(intent);
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EMBEDCODE_WITHADSETCODE.equals(action)) {
                if (args.length() > 0) {
                    JSONObject jsonObject = args.getJSONObject(0);
                    aEmbedCodes.clear();
                    aEmbedCodes.add(jsonObject.getString("EmbedCode"));
                    adSetCode = jsonObject.getString("AdSetCode");

                    if (CordovaApp.bPlayerActivityRunning) {
                        Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                        intent.putExtra(Constants.IP_ACTION, action);
                        intent.putExtra(Constants.IP_EMBEDCODE, aEmbedCodes.get(0));
                        intent.putExtra(Constants.IP_ADSETCODE, adSetCode);
                        this.cordova.getActivity().sendBroadcast(intent);
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EMBEDCODES_WITHADSETCODE.equals(action)) {
                if (args.length() > 0) {
                    JSONObject jsonObject = args.getJSONObject(0);
                    JSONArray embedCodes = jsonObject.getJSONArray("EmbedCodes");

                    aEmbedCodes.clear();
                    for (int i = 0; i < embedCodes.length(); i ++) aEmbedCodes.add(embedCodes.getString(i));
                    adSetCode = jsonObject.getString("AdSetCode");

                    if (CordovaApp.bPlayerActivityRunning) {
                        Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                        intent.putExtra(Constants.IP_ACTION, action);
                        intent.putExtra(Constants.IP_EMBEDCODES, aEmbedCodes);
                        intent.putExtra(Constants.IP_ADSETCODE, adSetCode);
                        this.cordova.getActivity().sendBroadcast(intent);
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EXTERNALID.equals(action)) {
                if (args.length() > 0) {
                    aExternalIDs.clear();
                    aExternalIDs.add(args.getString(0));

                    if (CordovaApp.bPlayerActivityRunning) {
                        Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                        intent.putExtra(Constants.IP_ACTION, action);
                        intent.putExtra(Constants.IP_EXTERNALID, aExternalIDs.get(0));
                        this.cordova.getActivity().sendBroadcast(intent);
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EXTERNALIDS.equals(action)) {
                if (args.length() > 0) {
                    aExternalIDs.clear();
                    for (int i = 0; i < args.length(); i ++) aExternalIDs.add(args.getString(i));

                    if (CordovaApp.bPlayerActivityRunning) {
                        Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                        intent.putExtra(Constants.IP_ACTION, action);
                        intent.putExtra(Constants.IP_EXTERNALIDS, aExternalIDs);
                        this.cordova.getActivity().sendBroadcast(intent);
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_CUSTOMANALYTICSTAGS.equals(action)) {
                if (args.length() > 0) {
                    aCustomAnalyticsTags.clear();
                    for (int i = 0; i < args.length(); i ++) aCustomAnalyticsTags.add(args.getString(i));

                    if (CordovaApp.bPlayerActivityRunning) {
                        Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                        intent.putExtra(Constants.IP_ACTION, action);
                        intent.putExtra(Constants.IP_CUSTOMANALYTICSTAGS, aCustomAnalyticsTags);
                        this.cordova.getActivity().sendBroadcast(intent);
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_FULLSCREEN.equals(action)) {
                bIsFullscreen = args.getBoolean(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_FULLSCREEN, bIsFullscreen);
                    this.cordova.getActivity().sendBroadcast(intent);
                }

                return true;
            } else if (Constants.ACTION_SET_CUSTOMDRMDATA.equals(action)) {
                sCustomDRMData = args.getString(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_CUSTOMDRMDATA, sCustomDRMData);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSLANGUAGE.equals(action)) {
                sClosedCaptionsLang = args.getString(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_CLOSEDCAPTIONSLANG, sClosedCaptionsLang);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;

            } else if (Constants.ACTION_SET_ADS_SEEKABLE.equals(action)) {
                bAdsSeekable = args.getBoolean(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_ADSSEEKABLE, bAdsSeekable);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            } else if (Constants.ACTION_SET_SEEKABLE.equals(action)) {
                bSeekable = args.getBoolean(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_SEEKABLE, bSeekable);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            } else if (Constants.ACTION_SET_PLAYHEADTIME.equals(action)) {
                playHeadTime = args.getInt(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_PLAYHEADTIME, playHeadTime);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            } else if (Constants.ACTION_SET_ACTIONATEND.equals(action)) {
                actionAtEnd = args.getInt(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_ACTIONATEND, actionAtEnd);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSPRESENTATIONSTYLE.equals(action)) {
                closedCaptionsPresentationStyle = args.getInt(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_CLOSEDCAPTIONSPRESENTATIONSTYLE, closedCaptionsPresentationStyle);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSBOTTOMMARGIN.equals(action)) {
                closedCaptionsBottomMargin = args.getInt(0);

                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_CLOSEDCAPTIONSBOTTOMMARGIN, closedCaptionsBottomMargin);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            // Player running needs methods
            } else if (Constants.ACTION_CHANGECURRENTITEM.equals(action) ||
                        Constants.ACTION_DISPLAYCLOSEDCAPTIONTEXT.equals(action)) {
                if (CordovaApp.bPlayerActivityRunning) {
                    String strVal = args.getString(0);

                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_STRINGVAL, strVal);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            } else if (Constants.ACTION_SEEK.equals(action) ||
                        Constants.ACTION_PREVIOUSVIDEO.equals(action) ||
                        Constants.ACTION_NEXTVIDEO.equals(action) ||
                        Constants.ACTION_SEEKTOPERCENT.equals(action)) {
                if (CordovaApp.bPlayerActivityRunning) {
                    int intVal = args.getInt(0);

                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    intent.putExtra(Constants.IP_INTVAL, intVal);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            } else if (Constants.ACTION_PLAY.equals(action) ||
                        Constants.ACTION_PLAYWITHINITIALTIME.equals(action)) {
                Intent intent = new Intent(OoyalaPlayerPlugin.this.cordova.getActivity(), PlayerActivity.class);

                if (args.length() == 0) {
                    initialPlayTime = 0;
                } else {
                    initialPlayTime = args.getInt(0);
                }

                if (sPcode != null) {
                    intent.putExtra(Constants.IP_PCODE, sPcode);
                }
                if (sDomain != null) {
                    intent.putExtra(Constants.IP_DOMAIN, sDomain);
                }
                if (adSetCode != null) {
                    intent.putExtra(Constants.IP_ADSETCODE, adSetCode);
                }
                if (aEmbedCodes.size() > 0) {
                    intent.putExtra(Constants.IP_EMBEDCODES, aEmbedCodes);
                }
                if (aExternalIDs.size() > 0) {
                    intent.putExtra(Constants.IP_EXTERNALIDS, aExternalIDs);
                }
                if (aCustomAnalyticsTags.size() > 0) {
                    intent.putExtra(Constants.IP_CUSTOMANALYTICSTAGS, aCustomAnalyticsTags);
                }
                if (initialPlayTime > 0) {
                    intent.putExtra(Constants.IP_INITPLAYTIME, initialPlayTime);
                }
                intent.putExtra(Constants.IP_FULLSCREEN, bIsFullscreen);
                if (sCustomDRMData != null) {
                    intent.putExtra(Constants.IP_CUSTOMDRMDATA, sCustomDRMData);
                }
                if (sClosedCaptionsLang != null) {
                    intent.putExtra(Constants.IP_CLOSEDCAPTIONSLANG, sClosedCaptionsLang);
                }
                intent.putExtra(Constants.IP_ADSSEEKABLE, bAdsSeekable);
                intent.putExtra(Constants.IP_SEEKABLE, bSeekable);
                if (playHeadTime != -1) {
                    intent.putExtra(Constants.IP_PLAYHEADTIME, playHeadTime);
                }
                if (actionAtEnd != -1) {
                    intent.putExtra(Constants.IP_ACTIONATEND, actionAtEnd);
                }
                if (closedCaptionsPresentationStyle != -1) {
                    intent.putExtra(Constants.IP_CLOSEDCAPTIONSPRESENTATIONSTYLE, closedCaptionsPresentationStyle);
                }
                if (closedCaptionsBottomMargin != -1) {
                    intent.putExtra(Constants.IP_CLOSEDCAPTIONSBOTTOMMARGIN, closedCaptionsBottomMargin);
                }
                OoyalaPlayerPlugin.this.cordova.getActivity().startActivity(intent);

                return true;
            } else if (Constants.ACTION_GET_METADATA.equals(action) ||
                    Constants.ACTION_GET_EMBEDCODE.equals(action) ||
                    Constants.ACTION_GET_AUTHTOKEN.equals(action) ||
                    Constants.ACTION_GET_CUSTOMDRMDATA.equals(action) ||
                    Constants.ACTION_GET_STATE.equals(action) ||
                    Constants.ACTION_PAUSE.equals(action) ||
                    Constants.ACTION_SUSPEND.equals(action) ||
                    Constants.ACTION_RESUME.equals(action) ||
                    Constants.ACTION_ISFULLSCREEN.equals(action) ||
                    Constants.ACTION_GET_TOPBAROFFSET.equals(action) ||
                    Constants.ACTION_GET_PLAYHEADTIME.equals(action) ||
                    Constants.ACTION_SEEKABLE.equals(action) ||
                    Constants.ACTION_GET_ACTIONATEND.equals(action) ||
                    Constants.ACTION_GET_CLOSEDCAPTIONSLANGUAGE.equals(action) ||
                    Constants.ACTION_GET_AVAILABLECLOSEDCAPTIONSLANGUAGES.equals(action) ||
                    Constants.ACTION_GET_BITRATE.equals(action) ||
                    Constants.ACTION_ISPLAYING.equals(action) ||
                    Constants.ACTION_ISADPLAYING.equals(action) ||
                    Constants.ACTION_GET_DURATION.equals(action) ||
                    Constants.ACTION_GET_BUFFER_PERCENTAGE.equals(action) ||
                    Constants.ACTION_GET_PLAYHEAD_PERCENTAGE.equals(action) ||
                    Constants.ACTION_RESETADS.equals(action) ||
                    Constants.ACTION_SKIPAD.equals(action) ||
                    Constants.ACTION_ISSHOWINGAD.equals(action) ||
                    Constants.ACTION_GET_SEEKSTYLE.equals(action) ||
                    Constants.ACTION_GET_CUEPOINTSINMILLISECONDS.equals(action) ||
                    Constants.ACTION_GET_CUEPOINTSINPERCENTAGE.equals(action)) {
                if (CordovaApp.bPlayerActivityRunning) {
                    Intent intent = new Intent(PlayerActivity.PluginCommandReceiver.ACTION);
                    intent.putExtra(Constants.IP_ACTION, action);
                    this.cordova.getActivity().sendBroadcast(intent);
                } else {
                    callbackContext.error(MSGERR_NOPLAYERACTIVITY);
                }

                return true;
            }

            return false;
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());

            return false;
        }
    }

    public class PlayerActivityEventReceiver extends BroadcastReceiver {

        public static final String ACTION = "PLAYER_EVENT_RECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
            // Message bus event handling
            if (intent.hasExtra(Constants.IP_EVENT)) {
                String sEvent = intent.getStringExtra(Constants.IP_EVENT);
                String sParams = null;

                if (intent.hasExtra(Constants.IP_PARAMS)) {
                    sParams = intent.getStringExtra(Constants.IP_PARAMS);
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.IP_EVENT, sEvent);
                    jsonObject.put(Constants.IP_PARAMS, sParams);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
                pluginResult.setKeepCallback(true);
                msgBusEventCallback.sendPluginResult(pluginResult);
            }

            // Action return value handling
            if (intent.hasExtra(Constants.IP_ACTION)) {
                String action = intent.getStringExtra(Constants.IP_ACTION);
                boolean bSuccess = intent.getBooleanExtra(Constants.IP_ACTIONSUCCESS, false);
                CallbackContext callbackContext = actionCallbacks.get(action);

                // If action call failed, call failure handler callback
                if (!bSuccess) {
                    String errMsg = "error";
                    if (intent.hasExtra(Constants.IP_ERRMSG)) {
                        errMsg = intent.getStringExtra(Constants.IP_ERRMSG);
                    }

                    callbackContext.error(errMsg);
                } else {
                    if (action.equals(Constants.ACTION_SET_EMBEDCODE)) {
                        boolean bRet = intent.getBooleanExtra(Constants.IP_RET_BOOL, false);
                        if (bRet) callbackContext.success("true");
                        else callbackContext.success("false");
                    }
                }
            }
        }
    }
}
