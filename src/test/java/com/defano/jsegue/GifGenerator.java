package com.defano.jsegue;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class GifGenerator {

    private final static int SIZE = 50;

    public static void main(String[] argv) {

        for (Segue thisName : Segue.values()) {

            AnimatedGifEncoder e = new AnimatedGifEncoder();
            e.start("doc/images/" + thisName + ".gif");
            e.setDelay(1000 / 20);   // 20 frames per sec
            e.setTransparent(Color.BLACK);
            e.setRepeat(0);

            forwardThenBackward(thisName, e);
        }
    }

    public static void forwardThenBackward(Segue name, AnimatedGifEncoder e) {
        SegueBuilder.of(name.getSegueClass())
                .withSource(JSegueDemo.getBlueCircle(SIZE, SIZE))
                .withDestination(JSegueDemo.getOrangeRect(SIZE, SIZE))
                .withDuration(1000, TimeUnit.MILLISECONDS)
                .withMaxFramesPerSecond(20)
                .overlay(true)
                .withAnimationObserver((segue, image) -> e.addFrame(image))
                .withCompletionObserver(effect -> backward(name, e))
                .build()
                .start();
    }

    public static void backward(Segue name, AnimatedGifEncoder e) {
        SegueBuilder.of(name.getSegueClass())
                .withSource(JSegueDemo.getOrangeRect(SIZE, SIZE))
                .withDestination(JSegueDemo.getBlueCircle(SIZE, SIZE))
                .withDuration(1000, TimeUnit.MILLISECONDS)
                .withMaxFramesPerSecond(20)
                .overlay(true)
                .withAnimationObserver((segue, image) -> e.addFrame(image))
                .withCompletionObserver(effect -> e.finish())
                .build()
                .start();
    }

}
