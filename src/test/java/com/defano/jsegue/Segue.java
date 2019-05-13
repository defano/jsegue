package com.defano.jsegue;

import com.defano.jsegue.renderers.*;

enum Segue {
    PIXEL_DISSOLVE(PixelDissolveEffect.class),
    ALPHA_DISSOLVE(AlphaDissolveEffect.class),
    BARN_DOOR_OPEN(BarnDoorOpenEffect.class),
    BARN_DOOR_CLOSE(BarnDoorCloseEffect.class),
    CHECKERBOARD(CheckerboardEffect.class),
    IRIS_OPEN(IrisOpenEffect.class),
    IRIS_CLOSE(IrisCloseEffect.class),
    PLAIN(PlainEffect.class),
    SCROLL_DOWN(ScrollDownEffect.class),
    SCROLL_UP(ScrollUpEffect.class),
    SCROLL_LEFT(ScrollLeftEffect.class),
    SCROLL_RIGHT(ScrollRightEffect.class),
    SHRINK_TO_TOP(ShrinkToTopEffect.class),
    SHRINK_TO_CENTER(ShrinkToCenterEffect.class),
    SHRINK_TO_BOTTOM(ShrinkToBottomEffect.class),
    STRETCH_FROM_TOP(StretchFromTopEffect.class),
    STRETCH_FROM_CENTER(StretchFromCenterEffect.class),
    STRETCH_FROM_BOTTOM(StretchFromBottomEffect.class),
    VENETIAN_BLINDS(BlindsEffect.class),
    WIPE_UP(WipeUpEffect.class),
    WIPE_DOWN(WipeDownEffect.class),
    WIPE_LEFT(WipeLeftEffect.class),
    WIPE_RIGHT(WipeRightEffect.class),
    ZOOM_IN(ZoomInEffect.class),
    ZOOM_OUT(ZoomOutEffect.class);

    private final Class<? extends AnimatedSegue> klass;

    Segue(Class<? extends AnimatedSegue> klass) {
        this.klass = klass;
    }

    public Class<? extends AnimatedSegue> getSegueClass() {
        return klass;
    }
}
