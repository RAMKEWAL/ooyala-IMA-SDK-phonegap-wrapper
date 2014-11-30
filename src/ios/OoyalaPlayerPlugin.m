//
//  OoyalaPlayerPlugin.m
//  Ooyala
//
//  Created by DongKai. Li on 11/6/14.
//
//

#import "OoyalaPlayerPlugin.h"

@implementation OoyalaPlayerPlugin

- (void) setMessageBusEventHandler:(CDVInvokedUrlCommand *) command {
    msgBusEventCallbackID = command.callbackId;
}

- (void) createPlayer:(CDVInvokedUrlCommand *) command {
    NSDictionary *dicParams = [command argumentAtIndex:0];
    NSString *pcode = (NSString *)[dicParams objectForKey:@"pcode"];
    NSString *domain = (NSString *)[dicParams objectForKey:@"domain"];
    int left, top, width, height;
    BOOL bHasFrameRect = NO;
    for (NSString *key in dicParams) {
        if ([key isEqualToString:@"left"]) {
            bHasFrameRect = YES;
            break;
        }
    }
    
    if (bHasFrameRect) {
        left = [[dicParams objectForKey:@"left"] intValue];
        top = [[dicParams objectForKey:@"top"] intValue];
        width = [[dicParams objectForKey:@"width"] intValue];
        height = [[dicParams objectForKey:@"height"] intValue];
    }
    
    NSLog(@"PCODE : %@", pcode);
    NSLog(@"DOMAIN : %@", domain);
    
    if (ooyalaPlayerVC != nil) {
        [ooyalaPlayerVC.player pause];
    }
    
    // Add view for video play
    UIView *mainView = [[self viewController] view];
    CGSize scrSz = [UIScreen mainScreen].bounds.size;
    if (bHasFrameRect) {
        vPlayer = [[UIView alloc] initWithFrame:CGRectMake(left, top, width, height)];
    } else {
        vPlayer = [[UIView alloc] initWithFrame:CGRectMake(0, 0, scrSz.width, scrSz.height / 3.0)];
    }
    vPlayer.backgroundColor = [UIColor clearColor];
    vPlayer.hidden = YES;
    [mainView addSubview:vPlayer];
    
    // Create ooyala player view controller and add player view
    ooyalaPlayerVC = [[OOOoyalaPlayerViewController alloc] initWithPcode:pcode domain:[[OOPlayerDomain alloc] initWithString:domain]];
    [ooyalaPlayerVC.view setFrame:vPlayer.bounds];
    [[self viewController] addChildViewController:ooyalaPlayerVC];
    
    [vPlayer addSubview:ooyalaPlayerVC.view];
    
    adsManager = [[OOIMAManager alloc] initWithOoyalaPlayerViewController:ooyalaPlayerVC];
    
    // Publish 'PLAYER_CREATED' event
    NSDictionary *msgParam = [NSDictionary dictionaryWithObjectsAndKeys:EVENT_PLAYER_CREATED, IP_EVENT,
                              @(YES), IP_PARAMS, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:msgParam];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:msgBusEventCallbackID];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerTimeChanged) name:OOOoyalaPlayerTimeChangedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerStateChanged) name:OOOoyalaPlayerStateChangedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerContentTreeReady) name:OOOoyalaPlayerContentTreeReadyNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerAuthorizationReady) name:OOOoyalaPlayerAuthorizationReadyNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerStarted) name:OOOoyalaPlayerPlayStartedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerLicenseAcquisition) name:OOOoyalaplayerLicenseAcquisitionNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerPlayCompleted) name:OOOoyalaPlayerPlayCompletedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerCurrentItemChanged) name:OOOoyalaPlayerCurrentItemChangedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerAdStarted) name:OOOoyalaPlayerAdStartedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerAdCompleted) name:OOOoyalaPlayerAdCompletedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerAdsLoaded) name:OOOoyalaPlayerAdsLoadedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerAdSkipped) name:OOOoyalaPlayerAdSkippedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerError) name:OOOoyalaPlayerErrorNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerAdError) name:OOOoyalaPlayerAdErrorNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerMetadataReady) name:OOOoyalaPlayerMetadataReadyNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerLanguageChanged) name:OOOoyalaPlayerLanguageChangedNotification object:ooyalaPlayerVC.player];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayerSeekCompleted) name:OOOoyalaPlayerSeekCompletedNotification object:ooyalaPlayerVC.player];
}

// Notification handler functions
- (void)onPlayerTimeChanged {
    
}

- (void)onPlayerStateChanged {
    OOOoyalaPlayerState state = ooyalaPlayerVC.player.state;
    NSString *sEventName = nil;
    id param = nil;
    
    if (state == OOOoyalaPlayerStateInit) {
        
    } else if (state == OOOoyalaPlayerStateLoading) {
        sEventName = EVENT_DOWNLOADING;
    } else if (state == OOOoyalaPlayerStateReady) {
        sEventName = EVENT_PLAYBACK_READY;
    } else if (state == OOOoyalaPlayerStatePlaying) {
        sEventName = EVENT_PLAYING;
    } else if (state == OOOoyalaPlayerStatePaused) {
        sEventName = EVENT_PAUSED;
    } else if (state == OOOoyalaPlayerStateCompleted) {
        sEventName = EVENT_PLAYED;
    } else if (state == OOOoyalaPlayerStateError) {
        sEventName = EVENT_PLAY_FAILED;
    }
    
    BOOL bFullscreen = ooyalaPlayerVC.isFullscreen;
    if (m_bIsFullscreen != bFullscreen) {
        m_bIsFullscreen = bFullscreen;
        
        sEventName = EVENT_FULLSCREEN_CHANGED;
        NSDictionary *msgParam = [NSDictionary dictionaryWithObjectsAndKeys:sEventName, IP_EVENT,
                                  @(bFullscreen), IP_PARAMS, nil];
        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:msgParam];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:msgBusEventCallbackID];
    }
    
    NSDictionary *msgParam = [NSDictionary dictionaryWithObjectsAndKeys:sEventName, IP_EVENT,
                              param, IP_PARAMS, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:msgParam];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:msgBusEventCallbackID];
}

- (void)onPlayerContentTreeReady {
    NSDictionary *msgParam = [NSDictionary dictionaryWithObjectsAndKeys:EVENT_CONTENT_TREE_FETCHED, IP_EVENT,
                              nil, IP_PARAMS, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:msgParam];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:msgBusEventCallbackID];
}

- (void)onPlayerAuthorizationReady {
    
}

- (void)onPlayerStarted {
    
}

- (void)onPlayerLicenseAcquisition {
    
}

- (void)onPlayerPlayCompleted {
    
}

- (void)onPlayerCurrentItemChanged {
    NSString *embedCode = [ooyalaPlayerVC.player rootItem].embedCode;
    NSDictionary *msgParam = [NSDictionary dictionaryWithObjectsAndKeys:EVENT_EMBED_CODE_CHANGED, IP_EVENT,
                              embedCode, IP_PARAMS, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:msgParam];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:msgBusEventCallbackID];
}

- (void)onPlayerAdStarted {
    
}

- (void)onPlayerAdCompleted {
    NSDictionary *msgParam = [NSDictionary dictionaryWithObjectsAndKeys:EVENT_ADS_PLAYED, IP_EVENT,
                              nil, IP_PARAMS, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:msgParam];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:msgBusEventCallbackID];
}

- (void)onPlayerAdsLoaded {
    
}

- (void)onPlayerAdSkipped {

}

- (void)onPlayerError {

}

- (void)onPlayerAdError {

}

- (void)onPlayerMetadataReady {
    NSDictionary *msgParam = [NSDictionary dictionaryWithObjectsAndKeys:EVENT_METADATA_FETCHED, IP_EVENT,
                              ooyalaPlayerVC.player.metadata, IP_PARAMS, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:msgParam];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:msgBusEventCallbackID];
}

- (void)onPlayerLanguageChanged {

}

- (void)onPlayerSeekCompleted {
    NSDictionary *msgParam = [NSDictionary dictionaryWithObjectsAndKeys:EVENT_SEEKED, IP_EVENT,
                              nil, IP_PARAMS, nil];
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:msgParam];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:msgBusEventCallbackID];
}

// Plugin functions
- (void) setEmbedCode:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setEmbedCode] failed : embed code is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setEmbedCode] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL bRet = [ooyalaPlayerVC.player setEmbedCode:param];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(bRet ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setEmbedCodes:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setEmbedCodes] failed : embed codes are missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setEmbedCodes] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL bRet = [ooyalaPlayerVC.player setEmbedCodes:command.arguments];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(bRet ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setEmbedCodeWithAdSetCode:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setEmbedCodeWithAdSetCode] failed : embed code and adset code are missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setEmbedCodeWithAdSetCode] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        NSDictionary *dicParam = [command argumentAtIndex:0];
        NSString *embedCode = [dicParam objectForKey:@"EmbedCode"];
        NSString *adsetCode = [dicParam objectForKey:@"AdSetCode"];
        BOOL bRet = [ooyalaPlayerVC.player setEmbedCode:embedCode adSetCode:adsetCode];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(bRet ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setEmbedCodesWithAdSetCode:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setEmbedCodesWithAdSetCode] failed : embed codes and adset code are missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setEmbedCodesWithAdSetCode] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        NSDictionary *dicParam = [command argumentAtIndex:0];
        NSArray *embedCodes = [dicParam objectForKey:@"EmbedCodes"];
        NSString *adsetCode = [dicParam objectForKey:@"AdSetCode"];
        BOOL bRet = [ooyalaPlayerVC.player setEmbedCodes:embedCodes adSetCode:adsetCode];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(bRet ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setExternalID:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setExternalID] failed : external id is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setExternalID] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL bRet = [ooyalaPlayerVC.player setExternalId:param];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(bRet ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setExternalIds:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setExternalIds] failed : external ids are missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setExternalIds] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL bRet = [ooyalaPlayerVC.player setExternalIds:command.arguments];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(bRet ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) changeCurrentItemToEmbedCode:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[changeCurrentItemToEmbedCode] failed : embed code is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[changeCurrentItemToEmbedCode] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL bRet = [ooyalaPlayerVC.player changeCurrentItemToEmbedCode:param];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(bRet ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getPlayheadTime:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getPlayheadTime] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        int retVal = (int) ooyalaPlayerVC.player.playheadTime;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:retVal];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getDuration:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getDuration] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        int retVal = (int) (ooyalaPlayerVC.player.duration * 1000);
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:retVal];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setPlayheadTime:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setPlayheadTime] failed : time is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setPlayheadTime] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        double time = (double)([[command argumentAtIndex:0] intValue] / 1000.0);
        [ooyalaPlayerVC.player setPlayheadTime:time];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setPlayheadTime] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getState:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getState] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        OOOoyalaPlayerState state = ooyalaPlayerVC.player.state;
        NSString *sState = nil;
        if (state == OOOoyalaPlayerStateInit) {
            sState = @"INIT";
        } else if (state == OOOoyalaPlayerStateLoading) {
            sState = @"LOADING";
        } else if (state == OOOoyalaPlayerStateReady) {
            sState = @"READY";
        } else if (state == OOOoyalaPlayerStatePlaying) {
            sState = @"PLAYING";
        } else if (state == OOOoyalaPlayerStatePaused) {
            sState = @"PAUSED";
        } else if (state == OOOoyalaPlayerStateCompleted) {
            sState = @"COMPLETED";
        } else if (state == OOOoyalaPlayerStateError) {
            sState = @"ERROR";
        }
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:sState];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) pause:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[pause] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        [ooyalaPlayerVC.player pause];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[pause] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) play:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[play] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        vPlayer.hidden = NO;
        [ooyalaPlayerVC.player play];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[play] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) playWithInitialTime:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[playWithInitialTime] failed : time is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[playWithInitialTime] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        double time = (double) ([[command argumentAtIndex:0] intValue] / 1000.0);
        [ooyalaPlayerVC.player playWithInitialTime:time];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[playWithInitialTime] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) seek:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[seek] failed : time is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[seek] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        double time = (double) ([[command argumentAtIndex:0] intValue] / 1000.0);
        [ooyalaPlayerVC.player seek:time];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[seek] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) isPlaying:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[isPlaying] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL retVal = ooyalaPlayerVC.player.isPlaying;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(retVal ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) isShowingAd:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[isShowingAd] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL retVal = ooyalaPlayerVC.player.isShowingAd;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(retVal ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) nextVideo:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[nextVideo] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL retVal = [ooyalaPlayerVC.player nextVideo];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(retVal ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) previousVideo:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[previousVideo] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL retVal = [ooyalaPlayerVC.player previousVideo];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(retVal ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getAvailableClosedCaptionsLanguages:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getAvailableClosedCaptionsLanguages] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        NSArray *langs = [ooyalaPlayerVC.player availableClosedCaptionsLanguages];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:langs];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setClosedCaptionsLanguage:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setClosedCaptionsLanguage] failed : language is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setClosedCaptionsLanguage] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL bRet = [ooyalaPlayerVC.player setEmbedCode:param];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(bRet ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setClosedCaptionsPresentationStyle:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setClosedCaptionsPresentationStyle] failed : presentation style is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setClosedCaptionsPresentationStyle] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        OOClosedCaptionPresentation presentation = OOClosedCaptionPopOn;
        if ([param isEqualToString:@"OOClosedCaptionPopOn"]) {
            presentation = OOClosedCaptionPopOn;
        } else if ([param isEqualToString:@"OOClosedCaptionRollUp"]) {
            presentation = OOClosedCaptionRollUp;
        } else if ([param isEqualToString:@"OOClosedCaptionPaintOn"]) {
            presentation = OOClosedCaptionPaintOn;
        }
        
        [ooyalaPlayerVC.player setClosedCaptionsPresentationStyle:presentation];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setClosedCaptionsPresentationStyle] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getBitrate:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getBitrate] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        double retVal = ooyalaPlayerVC.player.bitrate;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[NSString stringWithFormat:@"%f", retVal]];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) resetAds:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[resetAds] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        vPlayer.hidden = NO;
        [ooyalaPlayerVC.player resetAds];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[resetAds] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) skipAd:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[skipAd] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        vPlayer.hidden = NO;
        [ooyalaPlayerVC.player skipAd];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[skipAd] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setCustomAnalyticsTags:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setCustomAnalyticsTags] failed : tags is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setCustomAnalyticsTags] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        [ooyalaPlayerVC.player setCustomAnalyticsTags:command.arguments];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setCustomAnalyticsTags] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getMetadata:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getMetadata] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        NSDictionary *retVal = ooyalaPlayerVC.player.metadata;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:retVal];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) seekable:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[seekable] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL retVal = ooyalaPlayerVC.player.seekable;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(retVal ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setSeekable:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil) {
        errStr = @"[setSeekable] failed : param is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setSeekable] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL flag = [[command argumentAtIndex:0] boolValue];
        [ooyalaPlayerVC.player setSeekable:flag];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setSeekable] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setAdsSeekable:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil) {
        errStr = @"[setAdsSeekable] failed : param is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setAdsSeekable] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL flag = [[command argumentAtIndex:0] boolValue];
        [ooyalaPlayerVC.player setAdsSeekable:flag];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setAdsSeekable] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getSeekStyle:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getPlayheadTime] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        OOSeekStyle style = ooyalaPlayerVC.player.seekStyle;
        NSString *retVal = nil;
        if (style == OOSeekStyleNone) {
            retVal = @"NONE";
        } else if (style == OOSeekStyleBasic) {
            retVal = @"BASIC";
        } else if (style == OOSeekStyleEnhanced) {
            retVal = @"ENHANCED";
        }
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:retVal];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getClosedCaptionsLanguage:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getClosedCaptionsLanguage] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        NSString *retVal = ooyalaPlayerVC.player.closedCaptionsLanguage;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:retVal];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getActionAtEnd:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getActionAtEnd] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        OOOoyalaPlayerActionAtEnd actionAtEnd = (int) ooyalaPlayerVC.player.actionAtEnd;
        NSString *retVal = nil;
        if (actionAtEnd == OOOoyalaPlayerActionAtEndContinue) {
            retVal = @"CONTINUE";
        } else if (actionAtEnd == OOOoyalaPlayerActionAtEndPause) {
            retVal = @"PAUSE";
        } else if (actionAtEnd == OOOoyalaPlayerActionAtEndStop) {
            retVal = @"STOP";
        } else if (actionAtEnd == OOOoyalaPlayerActionAtEndReset) {
            retVal = @"RESET";
        }
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:retVal];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setActionAtEnd:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setActionAtEnd] failed : param is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setActionAtEnd] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        OOOoyalaPlayerActionAtEnd actionAtEnd = 0;
        if ([param isEqualToString:@"CONTINUE"]) {
            actionAtEnd = OOOoyalaPlayerActionAtEndContinue;
        } else if ([param isEqualToString:@"PAUSE"]) {
            actionAtEnd = OOOoyalaPlayerActionAtEndPause;
        } else if ([param isEqualToString:@"STOP"]) {
            actionAtEnd = OOOoyalaPlayerActionAtEndStop;
        } else if ([param isEqualToString:@"RESET"]) {
            actionAtEnd = OOOoyalaPlayerActionAtEndReset;
        }
        
        [ooyalaPlayerVC.player setActionAtEnd:actionAtEnd];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setActionAtEnd] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) getAuthToken:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[getAuthToken] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        NSString *retVal = ooyalaPlayerVC.player.authToken;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:retVal];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setAdTagParameters:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setAdTagParameters] failed : params are missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setAdTagParameters] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        NSDictionary *dicParam = [command argumentAtIndex:0];
        [adsManager setAdTagParameters:dicParam];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setAdTagParameters] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setAdUrlOverride:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil || [param isEqualToString:@"null"]) {
        errStr = @"[setAdUrlOverride] failed : params is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setAdUrlOverride] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        [adsManager setAdUrlOverride:param];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setAdUrlOverride] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) adsSeekable:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[adsSeekable] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL retVal = ooyalaPlayerVC.player.adsSeekable;
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(retVal ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) isFullscreen:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    
    if (ooyalaPlayerVC == nil) {
        errStr = @"[isFullscreen] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL retVal = [ooyalaPlayerVC isFullscreen];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:(retVal ? @"true" : @"false")];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void) setFullscreen:(CDVInvokedUrlCommand *) command {
    NSString *errStr = nil;
    NSString *param = (NSString *)[command argumentAtIndex:0];
    
    if (param == nil) {
        errStr = @"[setFullscreen] failed : param is missing";
    } else if (ooyalaPlayerVC == nil) {
        errStr = @"[setFullscreen] failed : player is not created";
    }
    
    CDVPluginResult *pluginResult;
    if (errStr) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errStr];
    } else {
        BOOL bFlag = [[command argumentAtIndex:0] boolValue];
        [ooyalaPlayerVC setFullscreen:bFlag];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"[setFullscreen] success"];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
