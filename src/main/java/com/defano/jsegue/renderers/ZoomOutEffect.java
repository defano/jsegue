package com.defano.jsegue.renderers;

/**
 * The destination image expands over the source in a rectangle aperture.
 */
public class ZoomOutEffect extends AbstractZoomEffect {

    public ZoomOutEffect() {
        super(ZoomShape.RECTANGLE, ZoomDirection.ZOOM_OUT);
    }
}
