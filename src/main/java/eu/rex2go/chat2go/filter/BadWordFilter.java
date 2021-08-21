package eu.rex2go.chat2go.filter;

import java.util.ArrayList;
import java.util.List;

public class BadWordFilter extends Filter {

    @Override
    public List<String> filter(String message) {
        ArrayList<String> list = new ArrayList<>();

        // TODO bad word list
        /*
        for(String badWord : Chat2Go.getBadWordManager().getBadWords()) {
            if(message.toLowerCase().contains(badWord)) {
                list.add(badWord);
            }
        }
         */

        return list;
    }
}
