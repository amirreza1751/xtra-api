package com.xtra.api.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.lang.reflect.Field;
import java.text.DecimalFormat;

@Log4j2
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

    public static void copyClassProperties(Object src, Object trg, Iterable<Field> props) {
        BeanWrapper srcWrap = PropertyAccessorFactory.forBeanPropertyAccess(src);
        BeanWrapper trgWrap = PropertyAccessorFactory.forBeanPropertyAccess(trg);

        props.forEach(p -> trgWrap.setPropertyValue(p.getName(), srcWrap.getPropertyValue(p.getName())));
    }

    public static String toReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
