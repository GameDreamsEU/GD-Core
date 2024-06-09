package eu.gamedreams.gdcore.modules.charfilter.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isInvalidString(String message, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        return !matcher.matches();
    }

}
