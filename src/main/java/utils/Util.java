package utils;

import org.apache.commons.lang3.RandomStringUtils;

public class Util {
    public static String randomString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }
}
