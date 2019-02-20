package com.verisjudge.utils;

public class ParsingUtils {

	public static boolean isValidTimeString(String timeString) {
		return parseTime(timeString) != null;
	}

	public static Long parseTime(String timeString) {
		if (timeString == null) {
			return null;
		}
		timeString = timeString.toLowerCase().replaceAll("\\s|,", "");
        try {
            return Long.parseLong(timeString);
        } catch (NumberFormatException e) {}
        if (timeString.endsWith("ms")) { // milliseconds
            try {
                return Long.parseLong(timeString.substring(
                    0, timeString.length() - 2));
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (timeString.endsWith("ns")) { // nanoseconds
            try {
                return (Long.parseLong(timeString.substring(
                    0, timeString.length() - 2)) + 999) / 1000;
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (timeString.endsWith("s")) { // seconds
            try {
                return Long.parseLong(timeString.substring(
                    0, timeString.length() - 1)) * 1000;
            } catch (NumberFormatException e) {
                return null;
            }
        } else { // assume milliseconds
        	try {
                return Long.parseLong(timeString);
            } catch (NumberFormatException e) {
                return null;
            }
        }
	}
}
