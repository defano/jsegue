package com.defano.jsegue.renderers;

/**
 * The destination image expands over the source in a circular aperture.
 */
public class IrisOpenEffect extends AbstractZoomEffect {

    public IrisOpenEffect() {
        super(ZoomShape.CIRCLE, ZoomDirection.ZOOM_OUT);
    }

}
