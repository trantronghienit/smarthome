/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.allseen.lsf.sampleapp;

import org.allseen.lsf.sdk.LightingDirector;
import org.allseen.lsf.sdk.LightingItem;
import org.allseen.lsf.sdk.LightingItemErrorEvent;
import org.allseen.lsf.sdk.TrackingID;

public class LightingListenerUtil {

    public static void listenFor(final TrackingID trackingId, final TrackingIDListener listener) {
        LightingDirector.get().addListener(new AnyLightingItemListenerBase() {

            @Override
            public void onAnyInitialized(TrackingID tid, LightingItem item) {
                if (tid != null && tid.value == trackingId.value) {
                    LightingDirector.get().removeListener(this);
                    listener.onTrackingIDReceived(tid, item);
                }
            }

            @Override
            public void onAnyError(LightingItemErrorEvent error) {
                if (error.trackingID != null && error.trackingID.value == trackingId.value) {
                    LightingDirector.get().removeListener(this);
                    listener.onTrackingIDError(error);
                }
            }
        });
    }
}
