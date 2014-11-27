package com.m4c.capture;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class EditorWindow extends JFrame {
	private JPanel contentPane;
	private JLabel label;
	
	protected Vector2D start;
	protected Vector2D end;
	private TrayIcon trayIcon;

	public EditorWindow(BufferedImage image, TrayIcon trayIcon) {
		this.trayIcon = trayIcon;
		setExtendedState(MAXIMIZED_BOTH);
		setUndecorated(true);
        
        pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		BorderLayout layout = new BorderLayout(0, 0);
		contentPane.setLayout(layout);
		
		setContentPane(contentPane);
		
		JPanel scrollPane = new JPanel() {
			@Override
			public void paint(Graphics arg0) {
				super.paint(arg0);
				
				if (start != null) {
					arg0.drawRect(start.X, start.Y, end.X - start.X, end.Y - start.Y);
				}
			}
		};
		scrollPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(scrollPane, BorderLayout.PAGE_START);

		label = new JLabel(new ImageIcon(image));

		scrollPane.add(label);
		init();
	}

	private void init() {
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				start = new Vector2D(e.getX(), e.getY()); 
			}
		});
		
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				cutImage(start, end);
				start = null;
			}
		});
		
		label.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				end = new Vector2D(e.getX(), e.getY());
				repaint();
			}
		});
	}
	
	protected void cutImage(Vector2D start, Vector2D end) {
		Icon icon = label.getIcon();
		BufferedImage buffImg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		
		buffImg = buffImg.getSubimage(start.X, start.Y, end.X - start.X, end.Y - start.Y);
		
		try {
			long time = System.currentTimeMillis();
			ImageIO.write(buffImg, "png", new File(time + ".png"));
			
			setClipboard(String.format("http://%s:%s/?file=%s", Config.get().getIp(), Config.get().getPort(), time));
			
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					trayIcon.displayMessage("Clip", "captured!", MessageType.INFO);
				}
			});
			setVisible(false);
			dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setClipboard(String url) {
		StringSelection stringSelection = new StringSelection (url);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard ();
		clpbrd.setContents(stringSelection, null);
	}

	private class Vector2D {
		public final int X;
		public final int Y;
		
		public Vector2D(int x, int y) {
			this.X = x;
			this.Y = y;
		}
	}
}
