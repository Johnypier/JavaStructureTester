package de.tum.cit.fop.structure;

import de.tum.in.test.api.WhitelistPath;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Arrays;
import java.util.List;

@WhitelistPath("target") // For ARES security manager.
public class GetterSetterHelper {
    public static String getRandomValueAsString(Class<?> type) {
        if (type.equals(boolean.class)) {
            return Integer.parseInt(RandomStringUtils.random(1, false, true)) > 4 ? "true" : "false";
        } else {
            return RandomStringUtils.random(5, false, true);
        }
    }

    public static Class<?> getExpectedClass(Class<?> type) {
        Class<?> typeAttr; // type of the returned attribute
        if (type == int.class) {
            typeAttr = Integer.class;
        } else if (type == boolean.class) {
            typeAttr = Boolean.class;
        } else if (type == double.class) {
            typeAttr = Double.class;
        } else {
            typeAttr = Float.class;
        }
        return typeAttr;
    }

    public static String getterSetup(String value) {
        StringBuilder methodName = new StringBuilder("get");
        methodName.append(Character.toUpperCase(value.charAt(0)));
        boolean space = false;

        for (int i = 1; i < value.length(); i++) {
            char temp = value.charAt(i);
            if (temp == ' ') {
                space = true;
                continue;
            }
            if (space) {
                space = false;
                methodName.append(Character.toUpperCase(temp));
            } else {
                methodName.append(temp);
            }
        }

        return methodName.toString();
    }

    public static List<String> setterSetup(String value) {
        StringBuilder methodName = new StringBuilder("set");
        StringBuilder attributeName = new StringBuilder();
        methodName.append(Character.toUpperCase(value.charAt(0)));
        attributeName.append(value.charAt(0));
        boolean space = false;

        for (int i = 1; i < value.length(); i++) {
            char temp = value.charAt(i);
            if (temp == ' ') {
                space = true;
                continue;
            }
            if (space) {
                space = false;
                methodName.append(Character.toUpperCase(temp));
                attributeName.append(Character.toUpperCase(temp));
            } else {
                methodName.append(temp);
                attributeName.append(temp);
            }
        }

        return Arrays.asList(methodName.toString(), attributeName.toString());
    }
}
