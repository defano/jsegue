package com.defano.jsegue;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import java.awt.*;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class GifGenerator {

    private final static int SIZE = 50;

    public static void main(String[] argv) {

        System.err.println(new File("doc/images/").getAbsolutePath());

        for (String thisName : Segue.names()) {

            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.start("doc/images/" + thisName + ".gif");
            e.setDelay(1000 / 20);   // 20 frames per sec
            e.setTransparent(Color.BLACK);
            e.setRepeat(0);

            forwardThenBackward(Segue.classNamed(thisName), e);
        }
    }

    public static void forwardThenBackward(Class<? extends AnimatedSegue> segue, AnimatedGifEncoder e) {
        SegueBuilder.of(segue)
                .withSource(JSegueDemo.getBlueCircle(SIZE, SIZE))
                .withDestination(JSegueDemo.getOrangeRect(SIZE, SIZE))
                .withDuration(1000, TimeUnit.MILLISECONDS)
                .withMaxFramesPerSecond(20)
                .overlay(true)
                .withAnimationObserver((s, image) -> e.addFrame(image))
                .withCompletionObserver(effect -> backward(segue, e))
                .build()
                .start();
    }

    public static void backward(Class<? extends AnimatedSegue> segue, AnimatedGifEncoder e) {
        SegueBuilder.of(segue)
                .withSource(JSegueDemo.getOrangeRect(SIZE, SIZE))
                .withDestination(JSegueDemo.getBlueCircle(SIZE, SIZE))
                .withDuration(1000, TimeUnit.MILLISECONDS)
                .withMaxFramesPerSecond(20)
                .overlay(true)
                .withAnimationObserver((s, image) -> e.addFrame(image))
                .withCompletionObserver(effect -> e.finish())
                .build()
                .start();
    }

}
