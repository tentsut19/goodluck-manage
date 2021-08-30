package th.co.infinitait.goodluck.util;

public class AppUtil {

    public static String toLeftPaddingString(String str, int length, Character fillingChar){
        if(str == null){
            str = "";
        }
        return String.format("%-"+length+"s", str).replace(' ', fillingChar).substring(0, length);
    }

    public static String toRightPaddingString(String str, int length, Character fillingChar){
        if(str == null){
            str = "";
        }
        return String.format("%"+length+"s", str).replace(' ', fillingChar).substring(0, length);
    }
}
