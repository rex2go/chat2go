package eu.rex2go.chat2go.filter;

import eu.rex2go.chat2go.Chat2Go;
import eu.rex2go.chat2go.config.FilterConfig;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class BadWordFilter extends Filter {

    @Getter
    private static final List<String> badWords = FilterConfig.getBlockedWords();

    public static boolean addBadWord(String badWord) {
        if (badWords.contains(badWord)) return false;

        badWords.add(badWord);

        Chat2Go.getFilterConfig().save();

        return true;
    }

    public static boolean removeBadWord(String badWord) {
        if (!badWords.contains(badWord)) return false;

        badWords.remove(badWord);

        Chat2Go.getFilterConfig().save();

        return true;
    }

    @Override
    public List<String> filter(String message) {
        ArrayList<String> list = new ArrayList<>();

        for (String badWord : badWords) {
            if (message.toLowerCase().contains(badWord)) {
                list.add(badWord);
            }
        }

        return list;
    }
}
