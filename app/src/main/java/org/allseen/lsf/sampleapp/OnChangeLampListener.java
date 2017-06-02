package org.allseen.lsf.sampleapp;

import org.allseen.lsf.sdk.Lamp;

/**
 * Created by admin on 3/21/2017.
 */

public interface OnChangeLampListener {
    void onLampChange(Lamp lamp);
    Lamp onLampDetected(Lamp lamp);
}
