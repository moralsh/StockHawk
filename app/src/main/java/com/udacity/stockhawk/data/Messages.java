package com.udacity.stockhawk.data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by morals on 30/3/17.
 */

public final class Messages {
    public static final int UNKNOWN_SYMBOL = 1;
    private static List<Integer> messages = new ArrayList();
    private static int count = 0;

    public static int getCount() {
        return count;
    }

    public  static void setCount(int count_v) {
        count = count_v;
    }

    public static List<Integer> getMessages() {
        return messages;
    }

    public static void clearMessages() {
        messages.clear();
    }

    public static void appendMessage (Integer message) {
        messages.add(message);
        setCount(messages.size());
    }
}
