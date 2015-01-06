//
//  OoyalaPlayerPlugin.h
//  Ooyala
//
//  Created by DongKai. Li on 11/6/14.
//
//

#import <Cordova/CDVPlugin.h>
#import "OOOoyalaPlayerViewController.h"
#import "OOOoyalaPlayer.h"
#import "OOIMAManager.h"
#import "OOPlayerDomain.h"
#import "OoyalaPlayer+Orientation.h"

#define IP_EVENT    @"event"
#define IP_PARAMS   @"params"

// Events
#define EVENT_ADS_PLAYED              @"ADS_PLAYED"
#define EVENT_CONTENT_TREE_FETCHED    @"CONTENT_TREE_FETCHED"
#define EVENT_DOWNLOADING             @"DOWNLOADING"
#define EVENT_EMBED_CODE_CHANGED      @"EMBED_CODE_CHANGED"
#define EVENT_ERROR                   @"ERROR"
#define EVENT_FULLSCREEN_CHANGED      @"FULLSCREEN_CHANGED"
#define EVENT_METADATA_FETCHED        @"METADATA_FETCHED"
#define EVENT_PAUSED                  @"PAUSED"
#define EVENT_PLAYBACK_READY          @"PLAYBACK_READY"
#define EVENT_PLAYED                  @"PLAYED"
#define EVENT_PLAYER_CREATED          @"PLAYER_CREATED"
#define EVENT_PLAYING                 @"PLAYING"
#define EVENT_PLAY_FAILED             @"PLAY_FAILED"
#define EVENT_SEEKED                  @"SEEKED"

@interface OoyalaPlayerPlugin : CDVPlugin
{
    NSString *_pcode;
    NSString *_domain;
    NSString *msgBusEventCallbackID;
    OOOoyalaPlayerViewController *ooyalaPlayerVC;
    OOIMAManager *adsManager;
    UIView *vPlayer;
    
    BOOL m_bIsFullscreen;
}

@property (strong, nonatomic) CDVInvokedUrlCommand *lastCommand;

- (void) setMessageBusEventHandler:(CDVInvokedUrlCommand *) command;
- (void) createPlayer:(CDVInvokedUrlCommand *) command;
- (void) setEmbedCode:(CDVInvokedUrlCommand *) command;
- (void) setEmbedCodes:(CDVInvokedUrlCommand *) command;
- (void) setEmbedCodeWithAdSetCode:(CDVInvokedUrlCommand *) command;
- (void) setEmbedCodesWithAdSetCode:(CDVInvokedUrlCommand *) command;
- (void) setExternalID:(CDVInvokedUrlCommand *) command;
- (void) setExternalIds:(CDVInvokedUrlCommand *) command;
- (void) changeCurrentItemToEmbedCode:(CDVInvokedUrlCommand *) command;
- (void) getPlayheadTime:(CDVInvokedUrlCommand *) command;
- (void) getDuration:(CDVInvokedUrlCommand *) command;
- (void) setPlayheadTime:(CDVInvokedUrlCommand *) command;
- (void) getState:(CDVInvokedUrlCommand *) command;
- (void) pause:(CDVInvokedUrlCommand *) command;
- (void) play:(CDVInvokedUrlCommand *) command;
- (void) playWithInitialTime:(CDVInvokedUrlCommand *) command;
- (void) seek:(CDVInvokedUrlCommand *) command;
- (void) isPlaying:(CDVInvokedUrlCommand *) command;
- (void) isShowingAd:(CDVInvokedUrlCommand *) command;
- (void) nextVideo:(CDVInvokedUrlCommand *) command;
- (void) previousVideo:(CDVInvokedUrlCommand *) command;
- (void) getAvailableClosedCaptionsLanguages:(CDVInvokedUrlCommand *) command;
- (void) setClosedCaptionsLanguage:(CDVInvokedUrlCommand *) command;
- (void) setClosedCaptionsPresentationStyle:(CDVInvokedUrlCommand *) command;
- (void) getBitrate:(CDVInvokedUrlCommand *) command;
- (void) resetAds:(CDVInvokedUrlCommand *) command;
- (void) skipAd:(CDVInvokedUrlCommand *) command;
- (void) setCustomAnalyticsTags:(CDVInvokedUrlCommand *) command;
- (void) getMetadata:(CDVInvokedUrlCommand *) command;
- (void) seekable:(CDVInvokedUrlCommand *) command;
- (void) setSeekable:(CDVInvokedUrlCommand *) command;
- (void) setAdsSeekable:(CDVInvokedUrlCommand *) command;
- (void) getSeekStyle:(CDVInvokedUrlCommand *) command;
- (void) getClosedCaptionsLanguage:(CDVInvokedUrlCommand *) command;
- (void) getActionAtEnd:(CDVInvokedUrlCommand *) command;
- (void) setActionAtEnd:(CDVInvokedUrlCommand *) command;
- (void) getAuthToken:(CDVInvokedUrlCommand *) command;
- (void) isFullscreen:(CDVInvokedUrlCommand *) command;

// iOS only
- (void) adsSeekable:(CDVInvokedUrlCommand *) command;

- (void) setAdTagParameters:(CDVInvokedUrlCommand *) command;
- (void) setAdUrlOverride:(CDVInvokedUrlCommand *) command;

@end
