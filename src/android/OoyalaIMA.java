package com.fubotv.cordova.ooyalaIMA;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.ooyala.android.*;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;
import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: DongKai.Li
 * Date: 10/22/14
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class OoyalaIMA extends CordovaPlugin {
    // Member variables
    private ArrayList<String> m_aEmbedCodes = new ArrayList<String>();
    private ArrayList<String> m_aExternalIDs = new ArrayList<String>();
    private ArrayList<String> m_aCustomAnalyticsTags = new ArrayList<String>();

    private boolean m_bIsFullscreen = false;

    // Callback contexts
    private CallbackContext cbc_setEmbedCode;
    private CallbackContext cbc_setEmbedCodes;
    private CallbackContext cbc_setEmbedCodeWithAdSetCode;
    private CallbackContext cbc_setEmbedCodesWithAdSetCode;
    private CallbackContext cbc_setExternalId;
    private CallbackContext cbc_setExternalIds;
    private CallbackContext cbc_changeCurrentItem;
    private CallbackContext cbc_getPlayheadTime;
    private CallbackContext cbc_getDuration;
    private CallbackContext cbc_setPlayheadTime;
    private CallbackContext cbc_getState;
    private CallbackContext cbc_pause;
    private CallbackContext cbc_play;
    private CallbackContext cbc_playWithInitialTime;
    private CallbackContext cbc_seek;
    private CallbackContext cbc_isPlaying;
    private CallbackContext cbc_isShowingAd;
    private CallbackContext cbc_nextVideo;
    private CallbackContext cbc_previousVideo;
    private CallbackContext cbc_getAvailableClosedCaptionsLanguages;
    private CallbackContext cbc_setClosedCaptionsLanguage;
    private CallbackContext cbc_setClosedCaptionsPresentationStyle;
    private CallbackContext cbc_getBitrate;
    private CallbackContext cbc_resetAds;
    private CallbackContext cbc_skipAd;
    private CallbackContext cbc_setCustomAnalyticsTags;
    private CallbackContext cbc_getMetadata;
    private CallbackContext cbc_seekable;
    private CallbackContext cbc_setSeekable;
    private CallbackContext cbc_setAdsSeekable;
    private CallbackContext cbc_getSeekStyle;
    private CallbackContext cbc_getClosedCaptionsLanguage;
    private CallbackContext cbc_getActionAtEnd;
    private CallbackContext cbc_setActionAtEnd;
    private CallbackContext cbc_getAuthToken;
    private CallbackContext cbc_getEmbedCode;
    private CallbackContext cbc_getCustomDRMData;
    private CallbackContext cbc_setCustomDRMData;
    private CallbackContext cbc_suspend;
    private CallbackContext cbc_resume;
    private CallbackContext cbc_isFullscreen;
    private CallbackContext cbc_setFullscreen;
    private CallbackContext cbc_isAdPlaying;
    private CallbackContext cbc_seekToPercent;
    private CallbackContext cbc_getBufferPercentage;
    private CallbackContext cbc_getPlayheadPercentage;
    private CallbackContext cbc_displayClosedCaptionText;
    private CallbackContext cbc_getCuePointsInMilliSeconds;
    private CallbackContext cbc_getCuePointsInPercentage;

    private CallbackContext msgBusEventCallback = null;

    // UI
    static private FrameLayout playerParentLayout = null;
    static private OoyalaPlayer player = null;
    private Activity cordovaActivity = null;
    private OoyalaPlayerLayout playerLayout = null;
    private CustomOoyalaIMAManager imaManager = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    static public boolean isPlayerVisible() {
        if (playerParentLayout == null) return false;

        return (playerParentLayout.getVisibility() == View.VISIBLE);
    }

    static public void hidePlayer() {
        if (player != null) {
            player.deleteObservers();
            player.pause();
        }

        if (playerParentLayout != null) {
            playerParentLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            // Private mechanism methods
            if (Constants.ACTION_SET_MSGBUSEVENTHANDLER.equals(action)) {
                msgBusEventCallback = callbackContext;

                return true;
            } else if (Constants.ACTION_CREATE_PLAYER.equals(action)) {
                // Get cordova activity
                cordovaActivity = cordova.getActivity();

                // Create player with pcode and domain
                JSONObject jsonObject = args.getJSONObject(0);
                final String sPcode = jsonObject.getString("pcode");
                final String sDomain = jsonObject.getString("domain");
                cordovaActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater layoutInflater = cordovaActivity.getLayoutInflater();
                        FrameLayout activityLayout = (FrameLayout) cordovaActivity.findViewById(android.R.id.content);

                        if (player != null) {
                            player.deleteObservers();
                            player.pause();
                        }
                        if (playerParentLayout != null) activityLayout.removeView(playerParentLayout);

                        playerParentLayout = new FrameLayout(cordovaActivity);
                        ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        playerParentLayout.setLayoutParams(layoutParams);

                        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 450);
                        playerLayout = new OoyalaPlayerLayout(cordovaActivity);
                        playerLayout.setLayoutParams(layoutParams);
                        playerParentLayout.addView(playerLayout);

                        playerParentLayout.setVisibility(View.INVISIBLE);
                        activityLayout.addView(playerParentLayout);
                        boolean bCreatedPlayer = true;
                        try {
                            OptimizedOoyalaPlayerLayoutController playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout,
                                    sPcode, new PlayerDomain(sDomain));
                            player = playerLayoutController.getPlayer();
                            player.addObserver(playerObserver);
                        } catch (Exception e) {
                            e.printStackTrace();
                            bCreatedPlayer = false;
                        }

                        // Publish 'PLAYER_CREATED' event
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put(Constants.IP_EVENT, Constants.PLAYER_CREATED);
                            jsonObject.put(Constants.IP_PARAMS, bCreatedPlayer);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sendCallbackEvent(jsonObject);
                    }
                });

                return true;

            // Later set able methods
            } else if (Constants.ACTION_SET_EMBEDCODE.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setEmbedCode] failed : embed code is missing");
                } else {
                    final String sEmbedCode = args.getString(0);
                    if (player != null) {
                        cbc_setEmbedCode = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.setEmbedCode(sEmbedCode);
                                cbc_setEmbedCode.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[setEmbedCode] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EMBEDCODES.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setEmbedCodes] failed : embed codes are missing");
                } else {
                    m_aEmbedCodes.clear();
                    for (int i = 0; i < args.length(); i ++) m_aEmbedCodes.add(args.getString(i));

                    if (player != null) {
                        cbc_setEmbedCodes = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.setEmbedCodes(m_aEmbedCodes);
                                cbc_setEmbedCodes.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[setEmbedCodes] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EMBEDCODE_WITHADSETCODE.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setEmbedCodeWithAdSetCode] failed : embed code and adset code are missing");
                } else {
                    JSONObject jsonObject = args.getJSONObject(0);
                    final String sEmbedCode = jsonObject.getString("EmbedCode");
                    final String sAdSetCode = jsonObject.getString("AdSetCode");

                    if (player != null) {
                        cbc_setEmbedCodeWithAdSetCode = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.setEmbedCodeWithAdSetCode(sEmbedCode, sAdSetCode);
                                cbc_setEmbedCodeWithAdSetCode.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[setEmbedCodeWithAdSetCode] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EMBEDCODES_WITHADSETCODE.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setEmbedCodesWithAdSetCode] failed : embed codes and adset code are missing");
                } else {
                    JSONObject jsonObject = args.getJSONObject(0);
                    JSONArray embedCodes = jsonObject.getJSONArray("EmbedCodes");
                    final String sAdSetCode = jsonObject.getString("AdSetCode");

                    m_aEmbedCodes.clear();
                    for (int i = 0; i < embedCodes.length(); i ++) m_aEmbedCodes.add(embedCodes.getString(i));

                    if (player != null) {
                        cbc_setEmbedCodesWithAdSetCode = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.setEmbedCodesWithAdSetCode(m_aEmbedCodes, sAdSetCode);
                                cbc_setEmbedCodesWithAdSetCode.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[setEmbedCodesWithAdSetCode] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EXTERNALID.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setExternalId] failed : external id is missing");
                } else {
                    final String sExternalID = args.getString(0);
                    if (player != null) {
                        cbc_setExternalId = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.setExternalId(sExternalID);
                                cbc_setExternalId.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[setExternalId] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_EXTERNALIDS.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setExternalIds] failed : external ids are missing");
                } else {
                    m_aExternalIDs.clear();
                    for (int i = 0; i < args.length(); i ++) m_aExternalIDs.add(args.getString(i));

                    if (player != null) {
                        cbc_setExternalIds = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.setExternalIds(m_aExternalIDs);
                                cbc_setExternalIds.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[setExternalIds] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_CHANGECURRENTITEM.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[changeCurrentItem] failed : embed code is missing");
                } else {
                    final String sEmbedCode = args.getString(0);
                    if (player != null) {
                        cbc_changeCurrentItem = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.changeCurrentItem(sEmbedCode);
                                cbc_changeCurrentItem.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[changeCurrentItem] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_PLAYHEADTIME.equals(action)) {
                if (player != null) {
                    cbc_getPlayheadTime = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int playheadTime = player.getPlayheadTime();
                            cbc_getPlayheadTime.success(playheadTime);
                        }
                    });
                } else {
                    callbackContext.error("[getPlayheadTime] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_GET_DURATION.equals(action)) {
                if (player != null) {
                    cbc_getDuration = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int duration = player.getDuration();
                            cbc_getDuration.success(duration);
                        }
                    });
                } else {
                    callbackContext.error("[getDuration] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SET_PLAYHEADTIME.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setPlayheadTime] failed : time is missing");
                } else {
                    final int time = args.getInt(0);
                    if (player != null) {
                        cbc_setPlayheadTime = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setPlayheadTime(time);
                                cbc_setPlayheadTime.success("[setPlayheadTime] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setPlayheadTime] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_STATE.equals(action)) {
                if (player != null) {
                    cbc_getState = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OoyalaPlayer.State state = player.getState();
                            String sState = state.toString();
                            cbc_getState.success(sState);
                        }
                    });
                } else {
                    callbackContext.error("[getState] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_PAUSE.equals(action)) {
                if (player != null) {
                    cbc_pause = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player.pause();
                            cbc_pause.success("[pause] success");
                        }
                    });
                } else {
                    callbackContext.error("[pause] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_PLAY.equals(action)) {
                if (player != null) {
                    cbc_play = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playerParentLayout.setVisibility(View.VISIBLE);
                            player.play();
                            cbc_play.success("[play] success");
                        }
                    });
                } else {
                    callbackContext.error("[play] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_PLAYWITHINITIALTIME.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[playWithInitialTime] failed : time is missing");
                } else {
                    final int time = args.getInt(0);
                    if (player != null) {
                        cbc_playWithInitialTime = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.play(time);
                                cbc_playWithInitialTime.success("[playWithInitialTime] success");
                            }
                        });
                    } else {
                        callbackContext.error("[playWithInitialTime] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SEEK.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[seek] failed : time is missing");
                } else {
                    final int time = args.getInt(0);
                    if (player != null) {
                        cbc_seek = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.seek(time);
                                cbc_seek.success("[seek] success");
                            }
                        });
                    } else {
                        callbackContext.error("[seek] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_ISPLAYING.equals(action)) {
                if (player != null) {
                    cbc_isPlaying = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean bIsPlaying = player.isPlaying();
                            cbc_isPlaying.success(bIsPlaying ? "true" : "false");
                        }
                    });
                } else {
                    callbackContext.error("[isPlaying] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_ISSHOWINGAD.equals(action)) {
                if (player != null) {
                    cbc_isShowingAd = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean bIsShowingAd = player.isShowingAd();
                            cbc_isShowingAd.success(bIsShowingAd ? "true" : "false");
                        }
                    });
                } else {
                    callbackContext.error("[isShowingAd] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_NEXTVIDEO.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[nextVideo] failed : param is missing");
                } else {
                    final int what = args.getInt(0);
                    if (player != null) {
                        cbc_nextVideo = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.nextVideo(what);
                                cbc_nextVideo.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[nextVideo] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_PREVIOUSVIDEO.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[previousVideo] failed : param is missing");
                } else {
                    final int what = args.getInt(0);
                    if (player != null) {
                        cbc_previousVideo = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean bRet = player.previousVideo(what);
                                cbc_previousVideo.success(bRet ? "true" : "false");
                            }
                        });
                    } else {
                        callbackContext.error("[previousVideo] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_AVAILABLECLOSEDCAPTIONSLANGUAGES.equals(action)) {
                if (player != null) {
                    cbc_getAvailableClosedCaptionsLanguages = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Set<String> availableClosedCaptionsLanguages = player.getAvailableClosedCaptionsLanguages();
                            JSONArray jsonArray = new JSONArray();
                            for (String lang : availableClosedCaptionsLanguages) {
                                jsonArray.put(lang);
                            }
                            cbc_getAvailableClosedCaptionsLanguages.success(jsonArray);
                        }
                    });
                } else {
                    callbackContext.error("[getAvailableClosedCaptionsLanguages] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSLANGUAGE.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setClosedCaptionsLanguage] failed : language is missing");
                } else {
                    final String sLang = args.getString(0);
                    if (player != null) {
                        cbc_setClosedCaptionsLanguage = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setClosedCaptionsLanguage(sLang);
                                cbc_setClosedCaptionsLanguage.success("[setClosedCaptionsLanguage] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setClosedCaptionsLanguage] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSPRESENTATIONSTYLE.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setClosedCaptionsPresentationStyle] failed : presentation style is missing");
                } else {
                    final String sPresentationStyle = args.getString(0);
                    if (player != null) {
                        cbc_setClosedCaptionsPresentationStyle = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setClosedCaptionsPresentationStyle(ClosedCaptionsStyle.OOClosedCaptionPresentation.valueOf(sPresentationStyle));
                                cbc_setClosedCaptionsPresentationStyle.success("[setClosedCaptionsPresentationStyle] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setClosedCaptionsPresentationStyle] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_BITRATE.equals(action)) {
                if (player != null) {
                    cbc_getBitrate = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double bitrate = player.getBitrate();
                            cbc_getBitrate.success(String.valueOf(bitrate));
                        }
                    });
                } else {
                    callbackContext.error("[getBitrate] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_RESETADS.equals(action)) {
                if (player != null) {
                    cbc_resetAds = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player.resetAds();
                            cbc_resetAds.success("[resetAds] success");
                        }
                    });
                } else {
                    callbackContext.error("[resetAds] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SKIPAD.equals(action)) {
                if (player != null) {
                    cbc_skipAd = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player.skipAd();
                            cbc_skipAd.success("[skipAd] success");
                        }
                    });
                } else {
                    callbackContext.error("[skipAd] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SET_CUSTOMANALYTICSTAGS.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setCustomAnalyticsTags] failed : tags are missing");
                } else {
                    m_aCustomAnalyticsTags.clear();
                    for (int i = 0; i < args.length(); i ++) m_aCustomAnalyticsTags.add(args.getString(i));

                    if (player != null) {
                        cbc_setCustomAnalyticsTags = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setCustomAnalyticsTags(m_aCustomAnalyticsTags);
                                cbc_setCustomAnalyticsTags.success("[setCustomAnalyticsTags] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setCustomAnalyticsTags] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_METADATA.equals(action)) {
                if (player != null) {
                    cbc_getMetadata = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject metadata = player.getMetadata();
                            if (metadata != null) {
                                cbc_getMetadata.success(metadata);
                            } else {
                                cbc_getMetadata.error("[getMetadata] failed : return null");
                            }
                        }
                    });
                } else {
                    callbackContext.error("[getMetadata] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SEEKABLE.equals(action)) {
                if (player != null) {
                    cbc_seekable = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean bRet = player.seekable();
                            cbc_seekable.success(bRet ? "true" : "false");
                        }
                    });
                } else {
                    callbackContext.error("[seekable] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SET_SEEKABLE.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setSeekable] failed : param is missing");
                } else {
                    final boolean bFlag = args.getBoolean(0);
                    if (player != null) {
                        cbc_setSeekable = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setSeekable(bFlag);
                                cbc_setSeekable.success("[setSeekable] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setSeekable] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_ADS_SEEKABLE.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setAdsSeekable] failed : param is missing");
                } else {
                    final boolean bFlag = args.getBoolean(0);
                    if (player != null) {
                        cbc_setAdsSeekable = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setAdsSeekable(bFlag);
                                cbc_setAdsSeekable.success("[setAdsSeekable] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setAdsSeekable] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_SEEKSTYLE.equals(action)) {
                if (player != null) {
                    cbc_getSeekStyle = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OoyalaPlayer.SeekStyle seekStyle = player.getSeekStyle();
                            cbc_getSeekStyle.success(seekStyle.ordinal());
                        }
                    });
                } else {
                    callbackContext.error("[getSeekStyle] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_GET_CLOSEDCAPTIONSLANGUAGE.equals(action)) {
                if (player != null) {
                    cbc_getClosedCaptionsLanguage = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String sLang = player.getClosedCaptionsLanguage();
                            cbc_getClosedCaptionsLanguage.success(sLang);
                        }
                    });
                } else {
                    callbackContext.error("[getClosedCaptionsLanguage] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_GET_ACTIONATEND.equals(action)) {
                if (player != null) {
                    cbc_getActionAtEnd = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OoyalaPlayer.ActionAtEnd actionAtEnd = player.getActionAtEnd();
                            cbc_getActionAtEnd.success(actionAtEnd.ordinal());
                        }
                    });
                } else {
                    callbackContext.error("[getActionAtEnd] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SET_ACTIONATEND.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setActionAtEnd] failed : param is missing");
                } else {
                    final String sActionAtEnd = args.getString(0);
                    if (player != null) {
                        cbc_setActionAtEnd = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setActionAtEnd(OoyalaPlayer.ActionAtEnd.valueOf(sActionAtEnd));
                                cbc_setActionAtEnd.success("[setActionAtEnd] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setActionAtEnd] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_AUTHTOKEN.equals(action)) {
                if (player != null) {
                    cbc_getAuthToken = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String sToken = player.getAuthToken();
                            cbc_getAuthToken.success(sToken);
                        }
                    });
                } else {
                    callbackContext.error("[getAuthToken] failed : player is not created");
                }

                return true;
            }

            // Android only functions
            else if (Constants.ACTION_GET_EMBEDCODE.equals(action)) {
                if (player != null) {
                    cbc_getEmbedCode = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String sEmbedCode = player.getEmbedCode();
                            cbc_getEmbedCode.success(sEmbedCode);
                        }
                    });
                } else {
                    callbackContext.error("[getEmbedCode] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_GET_CUSTOMDRMDATA.equals(action)) {
                if (player != null) {
                    cbc_getCustomDRMData = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String DRMData = player.getCustomDRMData();
                            cbc_getCustomDRMData.success(DRMData);
                        }
                    });
                } else {
                    callbackContext.error("[getCustomDRMData] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SET_CUSTOMDRMDATA.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setCustomDRMData] failed : DRM data is missing");
                } else {
                    final String sDRMData = args.getString(0);
                    if (player != null) {
                        cbc_setCustomDRMData = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setCustomDRMData(sDRMData);
                                cbc_setCustomDRMData.success("[setCustomDRMData] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setCustomDRMData] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SUSPEND.equals(action)) {
                if (player != null) {
                    cbc_suspend = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player.suspend();
                            cbc_suspend.success("[suspend] success");
                        }
                    });
                } else {
                    callbackContext.error("[suspend] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_RESUME.equals(action)) {
                if (player != null) {
                    cbc_resume = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            player.resume();
                            cbc_resume.success("[resume] success");
                        }
                    });
                } else {
                    callbackContext.error("[resume] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_ISFULLSCREEN.equals(action)) {
                if (player != null) {
                    cbc_isFullscreen = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean bFullScreen = player.isFullscreen();
                            cbc_isFullscreen.success(bFullScreen ? "true" : "false");
                        }
                    });
                } else {
                    callbackContext.error("[isFullscreen] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SET_FULLSCREEN.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setFullscreen] failed : param is missing");
                } else {
                    final Boolean bFlag = args.getBoolean(0);
                    if (player != null) {
                        cbc_setFullscreen = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.setFullscreen(bFlag);
                                cbc_setFullscreen.success("[setFullscreen] success");
                            }
                        });
                    } else {
                        callbackContext.error("[setFullscreen] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_ISADPLAYING.equals(action)) {
                if (player != null) {
                    cbc_isAdPlaying = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean bRet = player.isAdPlaying();
                            cbc_isAdPlaying.success(bRet ? "true" : "false");
                        }
                    });
                } else {
                    callbackContext.error("[isAdPlaying] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_SEEKTOPERCENT.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[seekToPercent] failed : embed code is missing");
                } else {
                    final int percent = args.getInt(0);
                    if (player != null) {
                        cbc_seekToPercent = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.seekToPercent(percent);
                                cbc_seekToPercent.success("[seekToPercent] success");
                            }
                        });
                    } else {
                        callbackContext.error("[seekToPercent] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_BUFFER_PERCENTAGE.equals(action)) {
                if (player != null) {
                    cbc_getBufferPercentage = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int percentage = player.getBufferPercentage();
                            cbc_getBufferPercentage.success(percentage);
                        }
                    });
                } else {
                    callbackContext.error("[getBufferPercentage] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_GET_PLAYHEAD_PERCENTAGE.equals(action)) {
                if (player != null) {
                    cbc_getPlayheadPercentage = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int percentage = player.getPlayheadPercentage();
                            cbc_getPlayheadPercentage.success(percentage);
                        }
                    });
                } else {
                    callbackContext.error("[getPlayheadPercentage] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_DISPLAYCLOSEDCAPTIONTEXT.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[displayClosedCaptionText] failed : embed code is missing");
                } else {
                    final String sCaptionText = args.getString(0);
                    if (player != null) {
                        cbc_displayClosedCaptionText = callbackContext;
                        cordovaActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                player.displayClosedCaptionText(sCaptionText);
                                cbc_displayClosedCaptionText.success("[displayClosedCaptionText] success");
                            }
                        });
                    } else {
                        callbackContext.error("[displayClosedCaptionText] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_GET_CUEPOINTSINMILLISECONDS.equals(action)) {
                if (player != null) {
                    cbc_getCuePointsInMilliSeconds = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Set<Integer> points = player.getCuePointsInMilliSeconds();
                            JSONArray jsonArray = new JSONArray();
                            for (Integer point : points) jsonArray.put(point);
                            cbc_getCuePointsInMilliSeconds.success(jsonArray);
                        }
                    });
                } else {
                    callbackContext.error("[getCuePointsInMilliSeconds] failed : player is not created");
                }

                return true;
            } else if (Constants.ACTION_GET_CUEPOINTSINPERCENTAGE.equals(action)) {
                if (player != null) {
                    cbc_getCuePointsInPercentage = callbackContext;
                    cordovaActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Set<Integer> points = player.getCuePointsInPercentage();
                            JSONArray jsonArray = new JSONArray();
                            for (Integer point : points) jsonArray.put(point);
                            cbc_getCuePointsInPercentage.success(jsonArray);
                        }
                    });
                } else {
                    callbackContext.error("[getCuePointsInPercentage] failed : player is not created");
                }

                return true;
            }

            // IMA manager functions
            else if (Constants.ACTION_SET_ADURLOVERRIDE.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setAdUrlOverride] failed : embed code is missing");
                } else {
                    final String sUrl = args.getString(0);
                    if (imaManager != null) {
                        imaManager.setAdUrlOverride(sUrl);
                        callbackContext.success("[setAdUrlOverride] success");
                    } else {
                        callbackContext.error("[setAdUrlOverride] failed : player is not created");
                    }
                }

                return true;
            } else if (Constants.ACTION_SET_ADTAGPARAMS.equals(action)) {
                if (args.length() == 1 && args.getString(0).equals("null")) {
                    callbackContext.error("[setAdTagParameters] failed : embed code is missing");
                } else {
                    final JSONObject jsonObject = args.getJSONObject(0);
                    if (imaManager != null) {
                        Iterator keysToCopyIterator = jsonObject.keys();
                        HashMap<String, String> tagParams = new HashMap<String, String>();
                        while(keysToCopyIterator.hasNext()) {
                            String key = (String) keysToCopyIterator.next();
                            String value = jsonObject.getString(key);
                            tagParams.put(key, value);
                        }

                        imaManager.setAdTagParameters(tagParams);
                        callbackContext.success("[setAdTagParameters] success");
                    } else {
                        callbackContext.error("[setAdTagParameters] failed : player is not created");
                    }
                }

                return true;
            }

            return false;
        } catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());

            return false;
        }
    }

    private void sendCallbackEvent(JSONObject jsonObject) {
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
        pluginResult.setKeepCallback(true);
        msgBusEventCallback.sendPluginResult(pluginResult);
    }

    private Observer playerObserver = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            OoyalaPlayer player = (OoyalaPlayer) observable;
            JSONObject jsonObject = null;

            if (o.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.STATE_CHANGED_NOTIFICATION)) {
                OoyalaPlayer.State state = player.getState();
                if (state == OoyalaPlayer.State.INIT) {
                } else if (state == OoyalaPlayer.State.LOADING) {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Constants.IP_EVENT, Constants.DOWNLOADING);
                        jsonObject.put(Constants.IP_PARAMS, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (state == OoyalaPlayer.State.READY) {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Constants.IP_EVENT, Constants.PLAYBACK_READY);
                        jsonObject.put(Constants.IP_PARAMS, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (state == OoyalaPlayer.State.PLAYING) {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Constants.IP_EVENT, Constants.PLAYING);
                        jsonObject.put(Constants.IP_PARAMS, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (state == OoyalaPlayer.State.PAUSED) {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Constants.IP_EVENT, Constants.PAUSED);
                        jsonObject.put(Constants.IP_PARAMS, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (state == OoyalaPlayer.State.COMPLETED) {
                    jsonObject = new JSONObject();
                    try {
                        jsonObject.put(Constants.IP_EVENT, Constants.PLAYED);
                        jsonObject.put(Constants.IP_PARAMS, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (state == OoyalaPlayer.State.SUSPENDED) {
                    jsonObject = null;
                } else if (state == OoyalaPlayer.State.ERROR) {
                    jsonObject = new JSONObject();
                    String sErrMsg = player.getError().getMessage();
                    try {
                        jsonObject.put(Constants.IP_EVENT, Constants.PLAY_FAILED);
                        jsonObject.put(Constants.IP_PARAMS, sErrMsg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else if (o.equals(OoyalaPlayer.BUFFER_CHANGED_NOTIFICATION)) {
                jsonObject = null;
            } else if (o.equals(OoyalaPlayer.CONTENT_TREE_READY_NOTIFICATION)) {
                jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.IP_EVENT, Constants.CONTENT_TREE_FETCHED);
                    jsonObject.put(Constants.IP_PARAMS, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (o.equals(OoyalaPlayer.AUTHORIZATION_READY_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.ERROR_NOTIFICATION)) {
                jsonObject = new JSONObject();
                String sErrMsg = player.getError().getMessage();
                try {
                    jsonObject.put(Constants.IP_EVENT, Constants.ERROR);
                    jsonObject.put(Constants.IP_PARAMS, sErrMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (o.equals(OoyalaPlayer.PLAY_STARTED_NOTIFICATION)) {
                jsonObject = null;
            } else if (o.equals(OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION)) {
                jsonObject = null;
            } else if (o.equals(OoyalaPlayer.SEEK_COMPLETED_NOTIFICATION)) {
                jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.IP_EVENT, Constants.SEEKED);
                    jsonObject.put(Constants.IP_PARAMS, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (o.equals(OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION)) {
                String embedCode = player.getEmbedCode();
                jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.IP_EVENT, Constants.EMBED_CODE_CHANGED);
                    jsonObject.put(Constants.IP_PARAMS, embedCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (o.equals(OoyalaPlayer.AD_STARTED_NOTIFICATION)) {
                jsonObject = null;
            } else if (o.equals(OoyalaPlayer.AD_COMPLETED_NOTIFICATION)) {
                jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.IP_EVENT, Constants.ADS_PLAYED);
                    jsonObject.put(Constants.IP_PARAMS, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (o.equals(OoyalaPlayer.AD_SKIPPED_NOTIFICATION)) {
                jsonObject = null;
            } else if (o.equals(OoyalaPlayer.AD_ERROR_NOTIFICATION)) {
                jsonObject = null;
            } else if (o.equals(OoyalaPlayer.METADATA_READY_NOTIFICATION)) {
                JSONObject metadata = player.getMetadata();
                jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.IP_EVENT, Constants.METADATA_FETCHED);
                    jsonObject.put(Constants.IP_PARAMS, metadata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Check if screen size is changed
            boolean bFullScreen = player.isFullscreen();
            if (m_bIsFullscreen != bFullScreen) {
                m_bIsFullscreen = bFullScreen;

                // Publish FULLSCREEN_CHANGED event
                jsonObject = new JSONObject();
                try {
                    jsonObject.put(Constants.IP_EVENT, Constants.FULLSCREEN_CHANGED);
                    jsonObject.put(Constants.IP_PARAMS, bFullScreen);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (jsonObject != null) sendCallbackEvent(jsonObject);
        }
    };
}
