package eu.rex2go.chat2go.filter;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class BadWordFilter extends Filter {

    @Getter
    private List<String> badWords = new ArrayList<>();

    public BadWordFilter() {
        loadBadWords();
    }

    public void loadBadWords() {
        // TODO
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
