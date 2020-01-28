package tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.defano.jsegue.AnimatedSegue;
import com.defano.jsegue.Segue;
import com.defano.jsegue.SegueAnimationObserver;
import com.defano.jsegue.SegueBuilder;
import com.defano.jsegue.SegueCompletionObserver;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

public class JSegueDemo implements SegueAnimationObserver, SegueCompletionObserver {

	private AnimatedSegue effect;
	private String name;

	private JFrame frame;
	private JLabel image;
	private JCheckBox blend;
	private JComboBox effectSelection;
	private JPanel demoPanel;
	private JSlider progressSlider;
	private JSpinner duration;

	public static void main(String[] argc) {
		new JSegueDemo();
	}

	@SuppressWarnings("unchecked")
	public JSegueDemo() {
		SwingUtilities.invokeLater(() -> {
			frame = new JFrame("JSegue Demo");
			frame.setLayout(new BorderLayout());
			frame.setPreferredSize(new Dimension(300, 300));
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.add(demoPanel);
			frame.setVisible(true);

			image.setSize(150, 150);

			ComboBoxModel effectNames = new DefaultComboBoxModel<>(Segue.names().toArray(new String[0]));
			effectSelection.setModel(effectNames);
			effectSelection.addActionListener(e -> {
				run((String) effectSelection.getSelectedItem());
			});

			blend.addActionListener(e -> run(name));
			duration.addChangeListener(e -> run(name));

			progressSlider.addChangeListener(e -> {
				effect.stop();
				try {
					onFrameRendered(effect,
							effect.render(getBlueCircle(image.getWidth(), image.getHeight()),
									getOrangeRect(image.getWidth(), image.getHeight()),
									(float) progressSlider.getValue() / 100f));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});

			duration.setModel(new SpinnerNumberModel(1000, 10, 10000, 20));

			run(Segue.names().get(0));
		});
	}

	private void run(String name) {
		this.name = name;

		if (effect != null) {
			effect.stop();
		}

		try {
			effect = SegueBuilder.of(Segue.classNamed(name))
					.withSource(getBlueCircle(image.getWidth(), image.getHeight()))
					.withDestination(getOrangeRect(image.getWidth(), image.getHeight()))
					.withDuration((int) duration.getValue(), TimeUnit.MILLISECONDS).withMaxFramesPerSecond(30)
					.withAnimationObserver(this).withCompletionObserver(this).overlay(blend.isSelected()).build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		effect.start();
	}

	public static BufferedImage getBlueCircle(int width, int height) {
		BufferedImage src = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = src.createGraphics();
		g.setPaint(new Color(10, 53, 150));
		g.fillOval(0, 0, (int) (width * .75), (int) (height * .75));
		g.dispose();

		return src;
	}

	public static BufferedImage getOrangeRect(int width, int height) throws IOException {
		Image img = ImageIO.read(new File("test.png"));
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}

//	public static BufferedImage getOrangeRect(int width, int height) {
//		BufferedImage src = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		Graphics2D g = src.createGraphics();
//		g.setPaint(new Color(235, 111, 46));
//		g.fillRect((int) (width * .25), (int) (height * .25), (int) (width * .75), (int) (height * .75));
//		g.dispose();
//
//		return src;
//	}

	@Override
	public void onSegueAnimationCompleted(AnimatedSegue segue) {
		BufferedImage to = segue.getSource();
		BufferedImage from = segue.getDestination();

		segue.setDestination(to);
		segue.setSource(from);
		segue.start();
	}

	@Override
	public void onFrameRendered(AnimatedSegue segue, BufferedImage image) {
		this.image.setIcon(new ImageIcon(image));
		this.image.repaint();
	}

	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT
	 * edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		final JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
		demoPanel = new JPanel();
		demoPanel.setLayout(new GridLayoutManager(5, 4, new Insets(10, 10, 10, 10), -1, -1));
		panel1.add(demoPanel,
				new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
						GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
						GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null,
						0, false));
		image = new JLabel();
		image.setText("");
		demoPanel.add(image, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		blend = new JCheckBox();
		blend.setText("Blend");
		demoPanel.add(blend,
				new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
						GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
						GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		effectSelection = new JComboBox();
		demoPanel.add(effectSelection,
				new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
						GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
						false));
		final Spacer spacer1 = new Spacer();
		demoPanel.add(spacer1, new GridConstraints(4, 0, 1, 3, GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		progressSlider = new JSlider();
		progressSlider.setMajorTickSpacing(20);
		progressSlider.setMinorTickSpacing(5);
		progressSlider.setPaintTicks(true);
		demoPanel.add(progressSlider,
				new GridConstraints(3, 0, 1, 4, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
						GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
						false));
		duration = new JSpinner();
		demoPanel.add(duration,
				new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL,
						GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0,
						false));
		final Spacer spacer2 = new Spacer();
		demoPanel.add(spacer2, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER,
				GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("ms");
		demoPanel.add(label1, new GridConstraints(2, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}
}
