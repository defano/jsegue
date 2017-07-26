package com.defano.jsegue;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

public class GifGenerator {

    private final static int SIZE = 50;

    public static void main(String[] argv) {

        for (SegueName thisName : SegueName.values()) {

            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.start("doc/images/" + thisName + ".gif");
            e.setDelay(1000 / 15);   // 15 frames per sec
            e.setTransparent(Color.BLACK);

            forward(thisName, e);
        }
    }

    public static void forward(SegueName name, AnimatedGifEncoder e) {
        SegueBuilder.of(name)
                .withSource(JSegueDemo.getBlueCircle(SIZE, SIZE))
                .withDestination(JSegueDemo.getYellowRect(SIZE, SIZE))
                .withDuration(1000, TimeUnit.MILLISECONDS)
                .withMaxFramesPerSecond(15)
                .alphaBlend(true)
                .withAnimationObserver(image -> e.addFrame(image))
                .withCompletionObserver(effect -> backward(name, e))
                .build()
                .start();
    }

    public static void backward(SegueName name, AnimatedGifEncoder e) {
        SegueBuilder.of(name)
                .withSource(JSegueDemo.getYellowRect(SIZE, SIZE))
                .withDestination(JSegueDemo.getBlueCircle(SIZE, SIZE))
                .withDuration(1000, TimeUnit.MILLISECONDS)
                .withMaxFramesPerSecond(15)
                .alphaBlend(true)
                .withAnimationObserver(image -> e.addFrame(image))
                .withCompletionObserver(effect -> e.finish())
                .build()
                .start();
    }

}
