package com.verisjudge.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

public class FileUtils {

	public static File getConfigDirectory() {
		if (SystemUtils.IS_OS_LINUX) {
			String homeDir = System.getProperty("user.home");
			File dir = new File(homeDir + "/.config/Verisimilitude/");
			dir.mkdirs();
			return dir;
		}
		if (SystemUtils.IS_OS_WINDOWS) {
			String appDataDir = System.getenv("APPDATA");
			File dir = new File(appDataDir + "/Local/Verisimilitude/");
			dir.mkdirs();
			return dir;
		}
		if (SystemUtils.IS_OS_MAC_OSX) {
			String homeDir = System.getProperty("user.home");
			File dir = new File(homeDir + "/Library/Application Support/Verisimilitude/");
			dir.mkdirs();
			return dir;
		}
		File dir = new File("./Verisimilitude/");
		dir.mkdirs();
		return dir;
	}
	
	public static File getConfigFile(String filename) {
		return new File(getConfigDirectory(), filename);
	}

	public static String readEntireFile(File file) {
		if (file == null)
			return "";
		try {
			return readEntireInputStream(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String readEntireInputStream(InputStream inputStream) {
		StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
			while (br.ready() && (line = br.readLine()) != null) {
			    sb.append(line);
			    sb.append('\n');
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
        return sb.toString();
	}
	
	public static String readLimitedFile(File file, long maxLength) {
		if (file == null)
			return "";
		try {
			return readLimitedInputStream(new FileInputStream(file), maxLength);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static List<String> fileToLines(File file) {
		List<String> lines = new ArrayList<>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public static String readLimitedInputStream(InputStream inputStream, long maxLength) {
		StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        long currentLength = 0;
        try {
			while (br.ready() && (line = br.readLine()) != null) {
				currentLength += line.length();
				if (currentLength <= maxLength) {
					sb.append(line);
					sb.append('\n');
				}
			}
			if (currentLength > maxLength) {
				sb.append("...\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
        return sb.toString();
	}
	
	public static boolean writeStringToFile(File file, String str) {
		if (file == null || str == null)
			return false;
		try {
			PrintWriter out = new PrintWriter(file);
			out.println(str);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
