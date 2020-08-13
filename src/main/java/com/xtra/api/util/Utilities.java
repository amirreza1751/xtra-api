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

    public static Pageable getSortingPageable(int pageNo, int pageSize, String sortBy, String sortDir) {
        Pageable page;
        Sort.Order order;
        if (sortBy != null && !sortBy.equals("")) {
            if (sortDir != null && sortDir.equalsIgnoreCase("desc"))
                order = Sort.Order.desc(sortBy);
            else
                order = Sort.Order.asc(sortBy);
            page = PageRequest.of(pageNo, pageSize, Sort.by(order));
        } else {
            page = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Order.asc("id")));
        }
        return page;
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
