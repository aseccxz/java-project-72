package hexlet.code.util;

public class Util {
    public static String textCutter(String text) {
        if (text.length() <= 200) {
            return text;
        }
        return text.substring(0, 200) + "...";
    }
}
