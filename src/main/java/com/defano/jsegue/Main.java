package com.defano.jsegue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.defano.jsegue.renderers.ZoomInEffect;

@SuppressWarnings("all")

public class Main extends javax.swing.JFrame implements ActionListener, ChangeListener, SegueAnimationObserver {

	private JLabel imagen = new JLabel();

	public static void main(String[] args) throws IOException {
		new Main();
	}

	public Main() throws IOException {

		setTitle("Periquito v3 About");
		setType(Type.UTILITY);
		initComponents();

		animar();

		this.setVisible(true);

	}

	void animar() throws IOException {

		BufferedImage mySource = getOrangeRect(200, 200);
		BufferedImage myDestination = getBlueCircle(200, 200);

		// Create a cross-dissolve segue
		AnimatedSegue mySegue = SegueBuilder.of(ZoomInEffect.class).withSource(mySource).withDestination(myDestination)
				.withDuration(1500, TimeUnit.MILLISECONDS) // Animation lasts 1.5 seconds
				.withMaxFramesPerSecond(30) // No more than 30fps
				.withAnimationObserver(this) // Make this class an observer
				// Overlay images; see FAQs
				.build();

		// Kick it off...
		mySegue.start();

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
		Image img = ImageIO.read(new File("C:\\Users\\Yeah\\Desktop\\test.png"));
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

	private void initComponents() {

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		setResizable(false);
		imagen.setHorizontalAlignment(SwingConstants.CENTER);

		imagen.addMouseListener(new MouseAdapter() {

			@Override

			public void mousePressed(MouseEvent e) {
				try {
					animar();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		imagen.setIcon(new ImageIcon("C:\\Users\\Yeah\\Desktop\\test.png"));
		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(34)
						.addComponent(imagen, GroupLayout.PREFERRED_SIZE, 221, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(48, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(34)
						.addComponent(imagen, GroupLayout.PREFERRED_SIZE, 219, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(23, Short.MAX_VALUE)));
		getContentPane().setLayout(layout);
		setSize(new Dimension(309, 305));
		setLocationRelativeTo(null);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFrameRendered(AnimatedSegue segue, BufferedImage image) {
		imagen.setIcon(new ImageIcon(image));
	}
}
