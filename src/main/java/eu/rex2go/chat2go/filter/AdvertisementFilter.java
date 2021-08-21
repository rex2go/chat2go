package eu.rex2go.chat2go.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvertisementFilter extends Filter {

    private final Pattern ipRegex = Pattern.compile(
            "(\\d+\\s*\\d*\\.\\s*\\d+\\s*\\d*\\.\\s*\\d+\\s*\\d*\\.\\s*\\d+\\s*\\d*:?\\s*\\d*\\s*\\d*)"
    );

    private final Pattern domainRegex = Pattern.compile(
            "([^ ]*\\.[^0-9 ]{2,4}\\b)"
    );

    @Override
    public List<String> filter(String message) {
        ArrayList<String> ads = new ArrayList<>();

        Matcher matcher = ipRegex.matcher(message);

        while (matcher.find()) {
            String ip = matcher.group();

            if (isWhitelisted(ip)) continue;

            ads.add(ip);
        }

        matcher = domainRegex.matcher(message.toLowerCase());

        while (matcher.find()) {
            String domain = matcher.group().replace(" ", "");

            if (isWhitelisted(domain)) continue;

            ads.add(domain);
        }

        return ads;
    }
}
