package com.dong.ooyala;


import java.util.*;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import com.ooyala.android.*;
import com.ooyala.android.imasdk.OoyalaIMAManager;
import com.ooyala.android.ui.OptimizedOoyalaPlayerLayoutController;

/**
 * A Sample integration of OoyalaPlayer and Google IMA Manager
 *
 * This application will not run unless you link Google Play Service's project
 *
 * http://developer.android.com/google/play-services/setup.html
 *
 * @author michael.len
 *
 */
public class PlayerActivity extends Activity {
    // Constants
    public final static String TAG = "PlayerActivity";

    private OptimizedOoyalaPlayerLayoutController playerLayoutController;
    private OoyalaIMAManager imaManager;
    private OoyalaPlayer player = null;

    private PluginCommandReceiver receiver;

    private String _sPcode = null;
    private String _sDomain = null;
    private String _sAdsetCode = null;
    private boolean _bIsFullscreen = false;
    private ArrayList<String> _aEmbedCodes = null;
    private ArrayList<String> _aExternalIDs = null;
    private ArrayList<String> _aCustomAnalyticsTags = null;
    private int _initialPlayTime = 0;
    private String _sCustomDRMData = null;
    private String _sClosedCaptionsLang = null;
    private boolean _bAdsSeekable = false;
    private boolean _bSeekable = true;
    private int _playHeadTime = -1;
    private int _actionAtEnd = -1;
    private int _closedCaptionsPresentationStyle = -1;
    private int _closedCaptionsBottomMargin = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        CordovaApp.bPlayerActivityRunning = true;
        readIntentVariables();
        registererBroadcastReceiver();

        OoyalaPlayerLayout playerLayout = (OoyalaPlayerLayout) findViewById(R.id.ooyalaPlayer);
        try {
            playerLayoutController = new OptimizedOoyalaPlayerLayoutController(playerLayout,
                    _sPcode, new PlayerDomain(_sDomain));
            player = playerLayoutController.getPlayer();
            player.addObserver(playerObserver);
        } catch (Exception e) {
            e.printStackTrace();
            player = null;
        }

        if (player == null) return;

        //Initialize IMA classes
        /*
        try {
            imaManager = new OoyalaIMAManager(player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ViewGroup companionView = (ViewGroup) findViewById(R.id.companionFrame);
        imaManager.addCompanionSlot(companionView, 300, 50);
        String adtag = "d01204eea15f466c92e890d14c7df8b6";
        imaManager.setAdUrlOverride(adtag);
        imaManager.setAdTagParameters(null);
        */

        // Setting up player config values
        if (_aEmbedCodes != null) {
            if (_aEmbedCodes.size() == 1) {
                if (_sAdsetCode == null) {
                    player.setEmbedCode(_aEmbedCodes.get(0));
                } else {
                    player.setEmbedCodeWithAdSetCode(_aEmbedCodes.get(0), _sAdsetCode);
                }
            } else {
                if (_sAdsetCode == null) {
                    ArrayList<String> embedCodes = new ArrayList<String>();
                    embedCodes.addAll(_aEmbedCodes);
                    player.setEmbedCodes(_aEmbedCodes);
                } else {
                    player.setEmbedCodesWithAdSetCode(_aEmbedCodes, _sAdsetCode);
                }
            }
        }

        player.setFullscreen(_bIsFullscreen);

        if (_aExternalIDs != null) {
            if (_aExternalIDs.size() == 1) {
                player.setExternalId(_aExternalIDs.get(0));
            } else {
                player.setExternalIds(_aExternalIDs);
            }
        }

        if (_aCustomAnalyticsTags != null) {
            player.setCustomAnalyticsTags(_aCustomAnalyticsTags);
        }

        if (_sCustomDRMData != null) {
            player.setCustomDRMData(_sCustomDRMData);
        }

        if (_sClosedCaptionsLang != null) {
            player.setClosedCaptionsLanguage(_sClosedCaptionsLang);
        }

        player.setAdsSeekable(_bAdsSeekable);
        player.setSeekable(_bSeekable);

        if (_playHeadTime != -1) {
            player.setPlayheadTime(_playHeadTime);
        }

        if (_actionAtEnd != -1) {
            player.setActionAtEnd(OoyalaPlayer.ActionAtEnd.values()[_actionAtEnd]);
        }

        if (_closedCaptionsPresentationStyle != -1) {
            player.setClosedCaptionsPresentationStyle(ClosedCaptionsStyle.OOClosedCaptionPresentation.values()[_closedCaptionsPresentationStyle]);
        }

        if (_closedCaptionsBottomMargin != -1) {
            player.setClosedCaptionsBottomMargin(_closedCaptionsBottomMargin);
        }

        // Finally play video
        if (_initialPlayTime == 0) {
            player.play();
        } else {
            player.play(_initialPlayTime);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (playerLayoutController.getPlayer() != null) {
            playerLayoutController.getPlayer().suspend();
        }
        unregisterReceiver(receiver);

        // TODO check if backpressed is needed later
        CordovaApp.bPlayerActivityRunning = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (playerLayoutController.getPlayer() != null) {
            playerLayoutController.getPlayer().resume();
        }
    }

    private void readIntentVariables() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.IP_EMBEDCODES)) {
            _aEmbedCodes = (ArrayList<String>) intent.getSerializableExtra(Constants.IP_EMBEDCODES);
        }
        if (intent.hasExtra(Constants.IP_PCODE)) {
            _sPcode = intent.getStringExtra(Constants.IP_PCODE);
        }
        if (intent.hasExtra(Constants.IP_DOMAIN)) {
            _sDomain = intent.getStringExtra(Constants.IP_DOMAIN);
        }
        if (intent.hasExtra(Constants.IP_ADSETCODE)) {
            _sAdsetCode = intent.getStringExtra(Constants.IP_ADSETCODE);
        }
        if (intent.hasExtra(Constants.IP_EXTERNALIDS)) {
            _aExternalIDs = (ArrayList<String>) intent.getSerializableExtra(Constants.IP_EXTERNALIDS);
        }
        if (intent.hasExtra(Constants.IP_CUSTOMANALYTICSTAGS)) {
            _aCustomAnalyticsTags = (ArrayList<String>) intent.getSerializableExtra(Constants.IP_CUSTOMANALYTICSTAGS);
        }
        if (intent.hasExtra(Constants.IP_INITPLAYTIME)) {
            _initialPlayTime = intent.getIntExtra(Constants.IP_INITPLAYTIME, 0);
        }
        if (intent.hasExtra(Constants.IP_FULLSCREEN)) {
            _bIsFullscreen = intent.getBooleanExtra(Constants.IP_FULLSCREEN, false);
        }
        if (intent.hasExtra(Constants.IP_CUSTOMDRMDATA)) {
            _sCustomDRMData = intent.getStringExtra(Constants.IP_CUSTOMDRMDATA);
        }
        if (intent.hasExtra(Constants.IP_CLOSEDCAPTIONSLANG)) {
            _sClosedCaptionsLang = intent.getStringExtra(Constants.IP_CLOSEDCAPTIONSLANG);
        }
        if (intent.hasExtra(Constants.IP_ADSSEEKABLE)) {
            _bAdsSeekable = intent.getBooleanExtra(Constants.IP_ADSSEEKABLE, false);
        }
        if (intent.hasExtra(Constants.IP_SEEKABLE)) {
            _bSeekable = intent.getBooleanExtra(Constants.IP_SEEKABLE, false);
        }
        if (intent.hasExtra(Constants.IP_PLAYHEADTIME)) {
            _playHeadTime = intent.getIntExtra(Constants.IP_PLAYHEADTIME, -1);
        }
        if (intent.hasExtra(Constants.IP_ACTIONATEND)) {
            _actionAtEnd = intent.getIntExtra(Constants.IP_ACTIONATEND, -1);
        }
        if (intent.hasExtra(Constants.IP_CLOSEDCAPTIONSPRESENTATIONSTYLE)) {
            _closedCaptionsPresentationStyle = intent.getIntExtra(Constants.IP_CLOSEDCAPTIONSPRESENTATIONSTYLE, -1);
        }
        if (intent.hasExtra(Constants.IP_CLOSEDCAPTIONSBOTTOMMARGIN)) {
            _closedCaptionsBottomMargin = intent.getIntExtra(Constants.IP_CLOSEDCAPTIONSBOTTOMMARGIN, -1);
        }
    }

    private void registererBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(PluginCommandReceiver.ACTION);
        receiver = new PluginCommandReceiver();
        registerReceiver(receiver, filter);
    }

    private Observer playerObserver = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            Intent intent = new Intent(OoyalaPlayerPlugin.PlayerActivityEventReceiver.ACTION);

            if (o.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.STATE_CHANGED_NOTIFICATION)) {
                OoyalaPlayer.State state = player.getState();
                if (state == OoyalaPlayer.State.INIT) {

                } else if (state == OoyalaPlayer.State.LOADING) {

                } else if (state == OoyalaPlayer.State.READY) {
                    intent.putExtra(Constants.IP_EVENT, Constants.PLAYBACK_READY);
                    sendBroadcast(intent);

                } else if (state == OoyalaPlayer.State.PLAYING) {
                    intent.putExtra(Constants.IP_EVENT, Constants.PLAYING);
                    sendBroadcast(intent);

                } else if (state == OoyalaPlayer.State.PAUSED) {
                    intent.putExtra(Constants.IP_EVENT, Constants.PAUSED);
                    sendBroadcast(intent);

                } else if (state == OoyalaPlayer.State.COMPLETED) {
                    intent.putExtra(Constants.IP_EVENT, Constants.PLAYED);
                    sendBroadcast(intent);

                } else if (state == OoyalaPlayer.State.SUSPENDED) {

                } else if (state == OoyalaPlayer.State.ERROR) {
                    intent.putExtra(Constants.IP_EVENT, Constants.PLAY_FAILED);
                    sendBroadcast(intent);

                }
            } else if (o.equals(OoyalaPlayer.BUFFER_CHANGED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.CONTENT_TREE_READY_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.AUTHORIZATION_READY_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.ERROR_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.PLAY_STARTED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.PLAY_COMPLETED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.SEEK_COMPLETED_NOTIFICATION)) {
                intent.putExtra(Constants.IP_EVENT, Constants.SEEKED);
                sendBroadcast(intent);

            } else if (o.equals(OoyalaPlayer.CURRENT_ITEM_CHANGED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.AD_STARTED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.AD_COMPLETED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.AD_SKIPPED_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.AD_ERROR_NOTIFICATION)) {

            } else if (o.equals(OoyalaPlayer.METADATA_READY_NOTIFICATION)) {
                // TODO pass param together
                intent.putExtra(Constants.IP_EVENT, Constants.METADATA_FETCHED);
                sendBroadcast(intent);
            }
        }
    };

    public class PluginCommandReceiver extends BroadcastReceiver {

        public static final String ACTION = "PLUGIN_COMMAND_RECEIVER";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra(Constants.IP_ACTION);

            Intent broadcastIntent = new Intent(OoyalaPlayerPlugin.PlayerActivityEventReceiver.ACTION);
            broadcastIntent.putExtra(Constants.IP_ACTION, action);

            // If player is not initialized, call fail callback
            if (player == null) {
                broadcastIntent.putExtra(Constants.IP_ACTIONSUCCESS, false);
                broadcastIntent.putExtra(Constants.IP_ERRMSG, "Player is not initialized yet.");
                sendBroadcast(broadcastIntent);

                return;
            }

            broadcastIntent.putExtra(Constants.IP_ACTIONSUCCESS, true);

            if (Constants.ACTION_BEGIN_FETCHING_ADVERTISING_ID.equals(action)) {
                player.beginFetchingAdvertisingId(PlayerActivity.this, new AdvertisingIdUtils.IAdvertisingIdListener() {
                    @Override
                    public void onAdvertisingIdSuccess(String s) {
                        Log.d(TAG, s);
                    }

                    @Override
                    public void onAdvertisingIdError(OoyalaException e) {
                        Log.d(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                });

            } else if (Constants.ACTION_SET_EMBEDCODE.equals(action)) {
                String embedCode = intent.getStringExtra(Constants.IP_EMBEDCODE);
                boolean bRet = player.setEmbedCode(embedCode);
                broadcastIntent.putExtra(Constants.IP_RET_BOOL, bRet);

            } else if (Constants.ACTION_SET_EMBEDCODES.equals(action)) {
                ArrayList<String> embedCodes = (ArrayList<String>) intent.getSerializableExtra(Constants.IP_EMBEDCODES);
                boolean bRet = player.setEmbedCodes(embedCodes);
                broadcastIntent.putExtra(Constants.IP_RET_BOOL, bRet);

            } else if (Constants.ACTION_SET_EMBEDCODE_WITHADSETCODE.equals(action)) {
                String embedCode = intent.getStringExtra(Constants.IP_EMBEDCODE);
                String adSetCode = intent.getStringExtra(Constants.IP_ADSETCODE);
                boolean bRet = player.setEmbedCodeWithAdSetCode(embedCode, adSetCode);
                broadcastIntent.putExtra(Constants.IP_RET_BOOL, bRet);

            } else if (Constants.ACTION_SET_EMBEDCODES_WITHADSETCODE.equals(action)) {
                ArrayList<String> embedCodes = (ArrayList<String>) intent.getSerializableExtra(Constants.IP_EMBEDCODES);
                String adSetCode = intent.getStringExtra(Constants.IP_ADSETCODE);
                boolean bRet = player.setEmbedCodesWithAdSetCode(embedCodes, adSetCode);
                broadcastIntent.putExtra(Constants.IP_RET_BOOL, bRet);

            } /*else if (Constants.ACTION_SET_EXTERNALID.equals(action)) {
                String externalID = intent.getStringExtra(Constants.IP_EXTERNALID);
                player.setExternalId(externalID);

            } else if (Constants.ACTION_SET_EXTERNALIDS.equals(action)) {
                player.setExternalIds();

            } else if (Constants.ACTION_CHANGECURRENTITEM.equals(action)) {
                player.changeCurrentItem();

            } else if (Constants.ACTION_GET_METADATA.equals(action)) {
                JSONObject jsonObject = player.getMetadata();

            } else if (Constants.ACTION_GET_EMBEDCODE.equals(action)) {
                String embedCode = player.getEmbedCode();

            } else if (Constants.ACTION_GET_AUTHTOKEN.equals(action)) {
                String authToken = player.getAuthToken();

            } else if (Constants.ACTION_GET_CUSTOMDRMDATA.equals(action)) {
                String customDRMData = player.getCustomDRMData();

            } else if (Constants.ACTION_SET_CUSTOMDRMDATA.equals(action)) {
                player.setCustomDRMData();

            } else if (Constants.ACTION_GET_STATE.equals(action)) {
                OoyalaPlayer.State state = player.getState();

            } else if (Constants.ACTION_PAUSE.equals(action)) {
                player.pause();

            } else if (Constants.ACTION_PLAY.equals(action)) {
                player.play();

            } else if (Constants.ACTION_PLAYWITHINITIALTIME.equals(action)) {
                player.play(00);

            } else if (Constants.ACTION_SUSPEND.equals(action)) {
                player.suspend();

            } else if (Constants.ACTION_RESUME.equals(action)) {
                player.resume();

            } else if (Constants.ACTION_ISFULLSCREEN.equals(action)) {
                boolean bFullScreen = player.isFullscreen();

            } else if (Constants.ACTION_SET_FULLSCREEN.equals(action)) {
                player.setFullscreen();

            } else if (Constants.ACTION_GET_TOPBAROFFSET.equals(action)) {
                int topBarOffset = player.getTopBarOffset();

            } else if (Constants.ACTION_GET_PLAYHEADTIME.equals(action)) {
                player.getPlayheadTime();

            } else if (Constants.ACTION_SET_PLAYHEADTIME.equals(action)) {
                player.setPlayheadTime(00);

            } else if (Constants.ACTION_SEEKABLE.equals(action)) {
                player.seekable();

            } else if (Constants.ACTION_SEEK.equals(action)) {
                player.seek(00);

            } else if (Constants.ACTION_PREVIOUSVIDEO.equals(action)) {
                player.previousVideo(00);

            } else if (Constants.ACTION_NEXTVIDEO.equals(action)) {
                player.nextVideo(00);

            } else if (Constants.ACTION_GET_ACTIONATEND.equals(action)) {
                player.getActionAtEnd();

            } else if (Constants.ACTION_SET_ACTIONATEND.equals(action)) {
                player.setActionAtEnd();

            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSLANGUAGE.equals(action)) {
                player.setClosedCaptionsLanguage();

            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSPRESENTATIONSTYLE.equals(action)) {
                player.setClosedCaptionsPresentationStyle();

            } else if (Constants.ACTION_GET_CLOSEDCAPTIONSLANGUAGE.equals(action)) {
                player.getClosedCaptionsLanguage()

            } else if (Constants.ACTION_GET_AVAILABLECLOSEDCAPTIONSLANGUAGES.equals(action)) {
                player.getAvailableClosedCaptionsLanguages();

            } else if (Constants.ACTION_GET_BITRATE.equals(action)) {
                player.getBitrate();

            } else if (Constants.ACTION_ISPLAYING.equals(action)) {
                player.isPlaying();

            } else if (Constants.ACTION_ISADPLAYING.equals(action)) {
                player.isAdPlaying();

            } else if (Constants.ACTION_SEEKTOPERCENT.equals(action)) {
                player.seekToPercent();

            } else if (Constants.ACTION_GET_DURATION.equals(action)) {
                player.getDuration();

            } else if (Constants.ACTION_GET_BUFFER_PERCENTAGE.equals(action)) {
                player.getBufferPercentage();

            } else if (Constants.ACTION_GET_PLAYHEAD_PERCENTAGE.equals(action)) {
                player.getPlayheadPercentage();

            } else if (Constants.ACTION_SET_ADS_SEEKABLE.equals(action)) {
                player.setAdsSeekable();

            } else if (Constants.ACTION_SET_SEEKABLE.equals(action)) {
                player.setSeekable();

            } else if (Constants.ACTION_RESETADS.equals(action)) {
                player.resetAds();

            } else if (Constants.ACTION_SKIPAD.equals(action)) {
                player.skipAd();

            } else if (Constants.ACTION_ISSHOWINGAD.equals(action)) {
                player.isShowingAd();

            } else if (Constants.ACTION_GET_CLOSEDCAPTONSSTYLE.equals(action)) {
                player.getClosedCaptionsStyle()

            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSSTYLE.equals(action)) {
                player.setClosedCaptionsStyle();

            } else if (Constants.ACTION_SET_CLOSEDCAPTIONSBOTTOMMARGIN.equals(action)) {
                player.setClosedCaptionsBottomMargin();

            } else if (Constants.ACTION_DISPLAYCLOSEDCAPTIONTEXT.equals(action)) {
                player.displayClosedCaptionText();

            } else if (Constants.ACTION_SET_CUSTOMANALYTICSTAG.equals(action)) {
                player.setCustomAnalyticsTags();

            } else if (Constants.ACTION_GET_SEEKSTYLE.equals(action)) {
                player.getSeekStyle()

            } else if (Constants.ACTION_GET_CUEPOINTSINMILLISECONDS.equals(action)) {
                player.getCuePointsInMilliSeconds();

            } else if (Constants.ACTION_GET_CUEPOINTSINPERCENTAGE.equals(action)) {
                player.getCuePointsInPercentage();

            }  */

            sendBroadcast(broadcastIntent);
        }
    }
}