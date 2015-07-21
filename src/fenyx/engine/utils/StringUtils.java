package fenyx.engine.utils;

import java.util.ArrayList;

/**
 *
 * @author KiQDominaN
 */
public class StringUtils {

    public static String make_string() {
        return new String().intern();
    }

    public static String make_string(String s) {
        return s.intern();
    }

    public static String replace(String s, String pattern, String replace) {
        while (s.contains(pattern))
            s = s.replace(pattern, replace);

        return s;
    }

    public static String[] splitString(String str, String separator) {
        ArrayList<String> strings = new ArrayList<>();

        int start = 0, end, skip = separator.length();

        str = str.concat(separator);

        while (start < str.length()) {
            end = str.indexOf(separator, start);
            strings.add(str.substring(start, end));
            start = end + skip;
        }

        return strings.toArray(new String[strings.size()]);
    }
}
