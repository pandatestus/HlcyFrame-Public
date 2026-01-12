package de.panda.hlcyFrame.Message;

import java.util.HashMap;
import java.util.Map;

public class SmallFontConverter {

    private static final Map<Character, Character> SMALL_FONT_MAP = new HashMap<>();

    static {
        String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String small = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘǫʀѕᴛᴜᴠᴡхʏᴢａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ";

        for (int i = 0; i < normal.length(); i++) {
            SMALL_FONT_MAP.put(normal.charAt(i), small.charAt(i));
        }
    }

    public static String toLowerCaseSmallFont(String string) {
        return toSmallFont(string.toLowerCase());
    }

    public static String toSmallFont(String input) {
        StringBuilder sb = new StringBuilder();

        int index = 0;
        while (index < input.length()) {
            int start = input.indexOf("<no-convert>", index);
            if(start == -1) {
                sb.append(convertPart(input.substring(index)));
                break;
            }

            sb.append(convertPart(input.substring(index, start)));

            int end = input.indexOf("</no-convert>", start);
            if(end == -1) {
                sb.append(input.substring(start + "<no-convert>".length()));
                break;
            }

            sb.append(input, start + "<no-convert>".length(), end);

            index = end + "</no-convert>".length();
        }

        return sb.toString();
    }

    private static String convertPart(String text) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if(c == '§' && i + 1 < text.length()) {
                result.append(c).append(text.charAt(i + 1));
                i++;
                continue;
            }

            result.append(SMALL_FONT_MAP.getOrDefault(c, c));
        }

        return result.toString();
    }
}