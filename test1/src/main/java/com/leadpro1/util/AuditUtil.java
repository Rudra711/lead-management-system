package com.leadpro1.util;

import java.lang.reflect.Field;
import java.util.*;

public class AuditUtil {

    public static Map<String, Object[]> getChanges(Object oldObj, Object newObj) {

        Map<String, Object[]> changes = new HashMap<>();

        for (Field field : oldObj.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);

                if (!Objects.equals(oldValue, newValue)) {
                    changes.put(field.getName(), new Object[]{oldValue, newValue});
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return changes;
    }
}