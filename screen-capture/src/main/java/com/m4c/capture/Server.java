package com.m4c.capture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class Server extends NanoHTTPD {
	public Server() {
		super(8081);
		Properties p = new Properties();
	}

	@Override
	public Response serve(IHTTPSession session) {
		Map<String, String> params = session.getParms();
		String file = params.get("file");
		
		if (file == null) {
			return new NanoHTTPD.Response("");
		}

		file = file.replace("\\", "")
			.replace("/", "")
			.replace("..", "");
		
		FileInputStream is;
		try {
			is = new FileInputStream("./" + file + ".png");
			return new NanoHTTPD.Response(Status.OK, "image/png", is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return new NanoHTTPD.Response("error");
	}
}
