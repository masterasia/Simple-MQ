package com.xt.simplemq.util;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class UnicodePacker {
    public static String toUnicode(String str) {
        int l = str.length();
        int i = 0;

        StringBuffer sb = new StringBuffer();
        while (i < l) {
            char c = str.charAt(i);
            if (c < 0x7f) {
                switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                default:
                    sb.append(c);
                    break;
                }
            } else {
                sb.append('\\');
                sb.append('u');
                sb.append(toHex((c >> 12) & 0xF));
                sb.append(toHex((c >> 8) & 0xF));
                sb.append(toHex((c >> 4) & 0xF));
                sb.append(toHex(c & 0xF));
            }
            ++i;
        }

        return sb.toString();
    }

    private static final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }
}
