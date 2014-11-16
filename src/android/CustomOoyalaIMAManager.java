package com.fubotv.cordova.ooyalaIMA;

import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.imasdk.OoyalaIMAManager;

/**
 * Created with IntelliJ IDEA.
 * User: DongKai.Li
 * Date: 11/15/14
 * Time: 4:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomOoyalaIMAManager extends OoyalaIMAManager {
    public CustomOoyalaIMAManager(OoyalaPlayer ooyalaPlayer) {
        super(ooyalaPlayer);
    }

    @Override
    public boolean onInitialPlay() {
        return super.onInitialPlay();
    }

    @Override
    public boolean onContentChanged() {
        return super.onContentChanged();
    }

    @Override
    public boolean onPlayheadUpdate(int playhead) {
        return super.onPlayheadUpdate(playhead);
    }

    @Override
    public boolean onContentFinished() {
        return super.onContentFinished();
    }

    @Override
    public boolean onCuePoint(int cuePointIndex) {
        return super.onCuePoint(cuePointIndex);
    }

    @Override
    public boolean onContentError(int errorCode) {
        return super.onContentError(errorCode);
    }

    @Override
    public void onAdModeEntered() {
        super.onAdModeEntered();
    }
}
