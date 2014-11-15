package com.m4c.capture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	private static Config instance = null;
	private Properties p;
	
	protected Config() {
		FileInputStream is = null;
		try {
			is = new FileInputStream("config.properties");
			p = new Properties();
			p.load(is);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getIp() {
		return p.getProperty("ip");
	}
	
	public String getPort() {
		return p.getProperty("port");
	}
	
	public static Config get() {
		if (instance == null) {
			instance = new Config();
		}
		
		return instance;
	}
}
