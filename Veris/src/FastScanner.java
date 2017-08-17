import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class FastScanner {
    private BufferedReader br;
    private StringTokenizer st;

    public FastScanner(InputStream in) {
        br = new BufferedReader(new InputStreamReader(in));
        st = null;
    }

    public FastScanner(File in) {
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
        } catch (FileNotFoundException e) {
        }
        st = null;
    }

    public BufferedReader getBufferedReader() {
        return br;
    }

    public String next() {
        if (hasNext())
            return st.nextToken();
        return null;
    }

    public int nextInt() {
        return Integer.parseInt(next());
    }

    public boolean hasNext() {
        try {
            while (st == null || !st.hasMoreElements()) {
                String line = br.readLine();
                if (line == null)
                    return false;
                st = new StringTokenizer(line);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}