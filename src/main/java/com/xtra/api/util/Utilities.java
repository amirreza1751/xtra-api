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

    public static String generateRandomString() {
        String characters = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        characters += characters.toLowerCase();
        characters += "23456789";
        var length = RandomUtils.nextInt(5, 10);
        return RandomStringUtils.random(length, characters);
    }
}
