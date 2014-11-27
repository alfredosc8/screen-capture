package com.m4c.capture;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class Main {
	private Server server;
	private TrayIcon trayIcon;

	public static void main(String[] args) throws AWTException {
		new Main().start();
	}

	private void start() throws AWTException {
		server = new Server();
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initTray();
	}

	private void initTray() throws AWTException {
		trayIcon = new TrayIcon(createImage("plane.png", "tray icon"));
		SystemTray tray = SystemTray.getSystemTray();
		tray.add(trayIcon);
		
		PopupMenu menu = new PopupMenu();
		MenuItem exitItem = new MenuItem("Exit");
		menu.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		});
		trayIcon.setPopupMenu(menu);
		
		trayIcon.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						EditorWindow window = new EditorWindow(takeScreenshot(), trayIcon);
						window.setVisible(true);
						window.toFront();
					}
				});
			}
		});
	}
	
	private void exit() {
		SystemTray.getSystemTray().remove(trayIcon);
		
		server.stop();
		System.exit(0);
	}

	private BufferedImage takeScreenshot() {
		try {
			return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		throw new AssertionError();
	}

    protected static Image createImage(String path, String description) {
        URL imageURL = Main.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}