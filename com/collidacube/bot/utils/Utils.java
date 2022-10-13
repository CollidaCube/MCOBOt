package com.collidacube.bot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

public class Utils {

    public static String parseId(String mentionTag) {
        int start = mentionTag.charAt(2) == '!' ? 3 : 2;
        return mentionTag.substring(start, mentionTag.length() - 1);
    }

    public static String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return fileName.substring(i);
    }

    public static String ifNull(String value, String defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static String generateCode(int len) {
        String code = "";
        for (int i = 0; i < len; i++) code = code + randomStandardChar();
        return code;
    }

    public static final String  lowercaseLetters = "abcdefghijklmnopqrstuvwxyz",
                                uppercaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                                numbers          = "0123456789",
                                standardCharacters = lowercaseLetters + uppercaseLetters + numbers;
    
    public static char randomStandardChar() {
        return randomChar(standardCharacters.toCharArray());
    }

    private static final Random r = new Random();
    public static char randomChar(char... choices) {
        return choices[r.nextInt(choices.length)];
    }

    public static <T> T await(CompletableFuture<T> cf) {
        try {
            return cf.get();
        } catch (InterruptedException | ExecutionException | CancellationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> parseWords(String str) {
        String[] phrases = str.split(" ");
        List<String> words = new ArrayList<>();
        for (String phrase : phrases)
            words.addAll(parsePhrase(phrase));
        return words;
    }

    public static List<String> parsePhrase(String str) {
        List<String> words = new ArrayList<>();
        int start = 0;
        while (start < str.length() - 1 && words.size() < str.length()) {
            String word = parseNextWord(str, start);
            start += word.length();
            words.add(word);
        }
        return words;
    }

    private static String parseNextWord(String str, int start) {
        for (int i = start + 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Detect.capitalLetter(ch)) return str.substring(start, i);
        } return str.substring(start);
    }

    private static final HashMap<String, String> irregularPlurals = parseToMap(
                                                                "sheep:sheep,series:series,species:species,deer:deer,fish:fish,children:child,geese:goose,men:man,women:woman,teeth:tooth,feet:foot,mice:mouse,people:person",
                                                                ",",
                                                                ":");;
    
    public static String singularize(String word) {
        word = word.toLowerCase();

        if (irregularPlurals.containsKey(word)) return irregularPlurals.get(word);
        if (word.endsWith("ies")) return singularize(word, "ies", "y");
        if (word.endsWith("zes")) return singularize(word, "zes");
        if (word.endsWith("ves")) return singularize(word, "fe");
        if (word.endsWith("es")) return singularize(word, "es");
        if (word.endsWith("ays") || word.endsWith("eys") || word.endsWith("oys")) return singularize(word, "s");
        if (word.endsWith("s") && !word.endsWith("ss")) return singularize(word, "s");
        if (word.endsWith("us")) return singularize(word, "us", "i");
        return word;
    }

    private static String singularize(String word, String suffix) {
        return word.substring(0, word.length() - suffix.length());
    }

    private static String singularize(String word, String suffix, String replacement) {
        return singularize(word, suffix) + replacement;
    }

    public static HashMap<String, String> parseToMap(String str, String entryDivider, String keyValueDivider) {
        return parseToMap(str, entryDivider, keyValueDivider, (a,b) -> new Pair<String, String>(a, b));
    }

    public static <T1, T2> HashMap<T1, T2> parseToMap(String str, String entryDivider, String keyValueDivider, BiFunction<String, String, Pair<T1, T2>> entryParser) {
        String[] entries = str.split(entryDivider);
        HashMap<T1, T2> map = new HashMap<>();

        if (entryDivider.equals(keyValueDivider)) for (int i = 0; i < entries.length; i += 2) {
            parseEntry(map, entries[i], entries[i+1], entryParser);
        }
        else for (String entry : entries) {
            String[] keyValue = entry.split(keyValueDivider);
            parseEntry(map, keyValue[0], keyValue[1], entryParser);
        }

        return map;
    }

    private static <T1, T2> void parseEntry(HashMap<T1, T2> map, String value1, String value2, BiFunction<String, String, Pair<T1, T2>> entryParser) {
        Pair<T1, T2> pair = entryParser.apply(value1, value2);
        map.put(pair.value1, pair.value2);
    }

    public static class Pair<T1,T2> {

        public final T1 value1;
        public final T2 value2;
        public Pair(T1 value1, T2 value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

    }

    public static int[] range(int min, int max) {
        int[] rangeArr = new int[(++max) - min];
        for (int i = 0; i < rangeArr.length; i++) rangeArr[i] = i + min;
        return rangeArr;
    }

    public static char[] range(char min, char max) {
        char[] rangeArr = new char[(++max) - min];
        for (int i = 0; i < rangeArr.length; i++) rangeArr[i] = (char)(i + min);
        return rangeArr;
    }

    public static <K,V> void changeKey(HashMap<K,V> map, K oldKey, K newKey) {
        V v = map.get(oldKey);
        map.remove(oldKey);
        if (newKey != null) map.put(newKey, v);
    }
    
}
