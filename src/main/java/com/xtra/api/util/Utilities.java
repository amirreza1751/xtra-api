package com.xtra.api.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Utilities {
    public static String wrapSearchString(String text) {
        return "%" + text + "%";
    }


    public static String generateRandomString(int minSize, int maxSize, boolean useReadableCharacters) {
        String characters = "ABCDEFGHJKMNPQRSTUVWXYZ";
        if (useReadableCharacters) {
            characters = characters.replace('I', '\0');
            characters = characters.replace('O', '\0');
        }
        characters += characters.toLowerCase();
        characters += "123456789";
        if (useReadableCharacters) {
            characters = characters.replace('0', '\0');
        }
        var length = RandomUtils.nextInt(minSize, maxSize);
        return RandomStringUtils.random(length, characters);
    }
}
