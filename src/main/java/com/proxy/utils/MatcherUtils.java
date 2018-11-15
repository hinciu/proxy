package com.proxy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtils {
    public static String getMatchValue(String regex, String val, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(val);
        matcher.find();
        return matcher.group(group);
    }

    public static boolean isMatches(String regex, String val) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(val);
        return matcher.find();
    }
}
