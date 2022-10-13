package com.collidacube.bot.utils;

import java.util.List;

public class Detect {

    private static final String[] profaneWords = new String[] { "arse", "ass", "asshole", "bitch", "bitching", "bastard", "boobs", "cock", "cum", "cumming", "cummed", "cunt", "damn", "damnit", "dick", "dumbass", "faggot", "fuck", "fucking", "fucker", "fucked", "hell", "jackass", "jizz", "nigger", "pussy", "shit", "shitter", "shitted", "whore" };
    public static boolean profanity(String str) {
        return Detect.word(str, profaneWords);
    }

    public static boolean word(String str, String... wordsToDetect) {
        List<String> words = Utils.parseWords(str);
        
        for (String word : words) {
            for (String wordToDetect : wordsToDetect) {
                if (Utils.singularize(word).equals(wordToDetect)) return true;
            }
        }

        return false;
    }

    public static boolean capitalLetter(char ch) {
        return Utils.uppercaseLetters.contains(String.valueOf(ch));
    }
    
}
