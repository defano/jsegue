package tests;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.defano.jsegue.AnimatedSegue;
import com.defano.jsegue.Main;
import com.defano.jsegue.Segue;
import com.defano.jsegue.SegueBuilder;
import com.madgag.gif.fmsware.AnimatedGifEncoder;

public class GifGenerator {

	private final static int SIZE = 50;

	public static void main(String[] argv) throws IOException {

		System.err.println(new File("doc/images/").getAbsolutePath());

		for (String thisName : Segue.names()) {

			AnimatedGifEncoder e = new AnimatedGifEncoder();
			e.start("doc/images/" + thisName + ".gif");
			e.setDelay(1000 / 20); // 20 frames per sec
			e.setTransparent(Color.BLACK);
			e.setRepeat(0);

			forwardThenBackward(Segue.classNamed(thisName), e);
		}
	}

	public static void forwardThenBackward(Class<? extends AnimatedSegue> segue, AnimatedGifEncoder e)
			throws IOException {
		SegueBuilder.of(segue).withSource(Main.getBlueCircle(SIZE, SIZE))
				.withDestination(Main.getOrangeRect(SIZE, SIZE)).withDuration(1000, TimeUnit.MILLISECONDS)
				.withMaxFramesPerSecond(20).overlay(true).withAnimationObserver((s, image) -> e.addFrame(image))
				.withCompletionObserver(effect -> {
					try {
						backward(segue, e);
					} catch (IOException e1) {
						//
					}
				}).build().start();
	}

	public static void backward(Class<? extends AnimatedSegue> segue, AnimatedGifEncoder e) throws IOException {
		SegueBuilder.of(segue).withSource(Main.getOrangeRect(SIZE, SIZE))
				.withDestination(Main.getBlueCircle(SIZE, SIZE)).withDuration(1000, TimeUnit.MILLISECONDS)
				.withMaxFramesPerSecond(20).overlay(true).withAnimationObserver((s, image) -> e.addFrame(image))
				.withCompletionObserver(effect -> e.finish()).build().start();
	}

}
