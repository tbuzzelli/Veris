package com.verisjudge.utils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class BoxWriter extends Writer {

    public static final int BORDER_BASIC    = 0;
    public static final int BORDER_SINGLE   = 1;
    public static final int BORDER_THICK    = 2;
    public static final int BORDER_DOUBLE   = 3;
    public static final int BORDER_ROUNDED  = 4;

    public static final int BORDER_HORIZONTAL_LINE     = 0;
    public static final int BORDER_VERTICAL_LINE       = 1;
    public static final int BORDER_CROSS               = 2;
    public static final int BORDER_CORNER_TOP_RIGHT    = 3;
    public static final int BORDER_CORNER_TOP_LEFT     = 4;
    public static final int BORDER_CORNER_BOTTOM_LEFT  = 5;
    public static final int BORDER_CORNER_BOTTOM_RIGHT = 6;
    public static final int BORDER_EDGE_RIGHT          = 7;
    public static final int BORDER_EDGE_TOP            = 8;
    public static final int BORDER_EDGE_LEFT           = 9;
    public static final int BORDER_EDGE_BOTTOM         = 10;

    public static final char[][] BORDER_CHARS = {
            {'-', '|', '+', '+', '+', '+', '+', '+', '+', '+', '+'},
            {'\u2500', '\u2502', '\u253C', '\u2510', '\u250C', '\u2514',
             '\u2518', '\u2524', '\u252C', '\u251C', '\u2534'},
            {'\u2501', '\u2503', '\u254B', '\u2513', '\u250F', '\u2517',
             '\u251B', '\u252B', '\u2533', '\u2523', '\u253B'},
            {'\u2550', '\u2551', '\u256C', '\u2557', '\u2554', '\u255A',
             '\u255D', '\u2563', '\u2566', '\u2560', '\u2569'},
            {'\u2500', '\u2502', '\u253C', '\u256E', '\u256D', '\u2570',
             '\u256F', '\u2524', '\u252C', '\u251C', '\u2534'}
        };

    public static final int DEFAULT_BORDER = BORDER_DOUBLE;

    private PrintWriter out;
    private int borderType = 0;
    private int boxWidth = 0;
    private int lineLength = 0;
    private boolean center;

    public BoxWriter(OutputStream outputStream) {
        out = new PrintWriter(outputStream);
    }

    public void openBox(int width) {
        openBox(width, DEFAULT_BORDER);
    }

    public void openBox(int width, int borderType) {
        if (borderType < 0 && borderType >= BORDER_CHARS.length) {
            borderType = DEFAULT_BORDER;
        }
        this.borderType = borderType;
        closeBox();
        if (lineLength > 0) {
            out.println();
            lineLength = 0;
        }
        boxWidth = width;
        out.print(BORDER_CHARS[borderType][BORDER_CORNER_TOP_LEFT]);
        repeatCharacter(BORDER_CHARS[borderType][BORDER_HORIZONTAL_LINE], boxWidth - 2);
        out.println(BORDER_CHARS[borderType][BORDER_CORNER_TOP_RIGHT]);
        flush();
        lineLength = 0;
    }

    public void printDivider() {
        if (lineLength > 0) {
            finishLine();
            out.println();
        }
        out.print(BORDER_CHARS[borderType][BORDER_EDGE_LEFT]);
        repeatCharacter(BORDER_CHARS[borderType][BORDER_HORIZONTAL_LINE], boxWidth - 2);
        out.println(BORDER_CHARS[borderType][BORDER_EDGE_RIGHT]);
        flush();
        lineLength = 0;
    }

    public void closeBox() {
        if (boxWidth == 0) {
            return;
        }
        if (lineLength > 0) {
            finishLine();
            out.println();
        }
        out.print(BORDER_CHARS[borderType][BORDER_CORNER_BOTTOM_LEFT]);
        repeatCharacter(BORDER_CHARS[borderType][BORDER_HORIZONTAL_LINE], boxWidth - 2);
        out.println(BORDER_CHARS[borderType][BORDER_CORNER_BOTTOM_RIGHT]);
        flush();
        boxWidth = 0;
        lineLength = 0;
    }

    private void startLine() {
        if (boxWidth == 0 || lineLength > 0) {
            return;
        }
        finishLine();
        lineLength = 1;
        out.print(BORDER_CHARS[borderType][BORDER_VERTICAL_LINE]);
        flush();
    }

    private void finishLine() {
        if (boxWidth == 0 || lineLength == 0) {
            return;
        }
        printSpaces(boxWidth - lineLength - 1);
        out.print(BORDER_CHARS[borderType][BORDER_VERTICAL_LINE]);
        flush();
        lineLength = 0;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public void centerOn() {
        setCenter(true);
    }

    public void centerOff() {
        setCenter(false);
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public int getLineLength() {
        return lineLength;
    }

    public int getRemainingWidth() {
        return Math.max(0, boxWidth - 1 - lineLength);
    }

    public void increaseLineLength(String str) {
        lineLength += getVisibleStringLength(str);
    }

    public void increaseLineLength(int len) {
        lineLength += len;
    }

    public BoxWriter printf(Locale locale, String format, Object... args) {
        print(String.format(locale, format, args));
        return this;
    }

    public BoxWriter printf(String format, Object... args) {
        print(String.format(format, args));
        return this;
    }

    public void println(boolean b) {
        println("" + b);
    }

    public void println(char c) {
        println("" + c);
    }

    public void println(char[] s) {
        println(new String(s));
    }

    public void println(double d) {
        println("" + d);
    }

    public void println(float f) {
        println("" + f);
    }

    public void println(int i) {
        println("" + i);
    }

    public void println(long l) {
        println("" + l);
    }

    public void println(Object obj) {
        println(obj.toString());
    }

    public void println(String str) {
        print(str + "\n");
    }

    public void println() {
        print("\n");
    }

    public void print(boolean b) {
        print("" + b);
    }

    public void print(char c) {
        print("" + c);
    }

    public void print(char[] s) {
        print(new String(s));
    }

    public void print(double d) {
        print("" + d);
    }

    public void print(float f) {
        print("" + f);
    }

    public void print(int i) {
        print("" + i);
    }

    public void print(long l) {
        print("" + l);
    }

    public void print(Object obj) {
        print(obj.toString());
    }

    public void print(String str) {
        String[] lines = splitIntoLines(str);
        for (int i = 0; i < lines.length - 1; i++) {
            String line = lines[i];
            startLine();
            if (center && boxWidth != 0) {
                int length = getVisibleStringLength(line);
                printSpaces((boxWidth - length - 2) / 2);
            }
            out.print(line);
            increaseLineLength(line);
            finishLine();
            out.println();
            out.flush();
        }
        String lastLine = lines[lines.length - 1];
        if (!lastLine.isEmpty()) {
            startLine();
            if (center && boxWidth != 0 && lineLength == 1) {
                int length = getVisibleStringLength(lastLine);
                printSpaces((boxWidth - length - 2) / 2);
            }
            out.print(lastLine);
            flush();
            increaseLineLength(lastLine);
        }
    }

    private static String[] splitIntoLines(String str) {
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (c == '\n') {
                lines.add(stringBuilder.toString());
                stringBuilder = new StringBuilder();
            } else if (c != '\r') {
                stringBuilder.append(c);
            }
        }
        lines.add(stringBuilder.toString());
        return lines.toArray(new String[0]);
    }

    public static int getVisibleStringLength(String str) {
        // Remove all terminal control sequences.
        str = str.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
        int length = 0;
        for (char c : str.toCharArray()) {
            int type = Character.getType(c);
            if (type != Character.CONTROL
                    && type != Character.LINE_SEPARATOR
                    && type != Character.MODIFIER_LETTER
                    && type != Character.MODIFIER_SYMBOL
                    && type != Character.NON_SPACING_MARK
                    && type != Character.PARAGRAPH_SEPARATOR) {
                length++;
            }
        }
        return length;
    }

    private void printSpaces(int numSpaces) {
        repeatCharacter(' ', numSpaces);
    }

    private void repeatCharacter(char c, int n) {
        if (n <= 0) {
            return;
        }
        char[] arr = new char[n];
        Arrays.fill(arr, c);
        out.print(arr);
        flush();
        increaseLineLength(n);
    }

    public void flush() {
        out.flush();
    }

    public void close() {
//        out.close();
    }

    @Override
    public void write(char[] arg0, int arg1, int arg2) throws IOException {
        out.write(arg0, arg1, arg2);
    }
}
