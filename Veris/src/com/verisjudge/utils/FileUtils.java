package com.verisjudge.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

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
}
