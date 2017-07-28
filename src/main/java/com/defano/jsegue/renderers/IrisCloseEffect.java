package com.defano.jsegue.renderers;

/**
 * The source image collapses over the destination in a circular aperture.
 */
public class IrisCloseEffect extends AbstractZoomEffect {

    public IrisCloseEffect() {
        super(ZoomShape.CIRCLE, ZoomDirection.ZOOM_IN);
    }
}
