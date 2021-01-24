/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.common;

import me.filoghost.fcommons.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utils {

    /**
     * Converts a generic array to a list of Strings using the method toString().
     */
    public static List<String> toStringList(Object... array) {
        List<String> result = new ArrayList<>(array.length);
        for (Object obj : array) {
            result.add(Objects.toString(obj));
        }
        return result;
    }
    
    
    public static String limitLength(String s, int maxLength) {
        if (s != null && s.length() > maxLength) {
            s = s.substring(0, maxLength);
        }
        return s;
    }
    
    
    public static int floor(double num) {
        int floor = (int) num;
        return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
    }
    
    
    public static double square(double num) {
        return num * num;
    }
    
    
    public static String join(String[] elements, String separator, int startIndex, int endIndex) {
        Preconditions.checkArgument(startIndex >= 0 && startIndex < elements.length, "startIndex out of bounds");
        Preconditions.checkArgument(endIndex >= 0 && endIndex <= elements.length, "endIndex out of bounds");
        Preconditions.checkArgument(startIndex <= endIndex, "startIndex lower than endIndex");
        
        StringBuilder result = new StringBuilder();
        
        while (startIndex < endIndex) {
            if (result.length() != 0) {
                result.append(separator);
            }
            
            if (elements[startIndex] != null) {
                result.append(elements[startIndex]);
            }
            startIndex++;
        }
        
        return result.toString();
    }
    
    public static String sanitize(String s) {
        return s != null ? s : "null";
    }
    
    
    public static boolean isThereNonNull(Object... objects) {
        if (objects == null) {
            return false;
        }

        for (Object object : objects) {
            if (object != null) {
                return true;
            }
        }
        
        return false;
    }
    
    
    public static String uncapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
    
}
