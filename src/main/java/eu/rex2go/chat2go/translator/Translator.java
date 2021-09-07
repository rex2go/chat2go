package eu.rex2go.chat2go.translator;

import eu.rex2go.chat2go.Chat2Go;
import lombok.Getter;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {

    @Getter
    private final HashMap<String, String> translations = new HashMap<>();

    public String getTranslation(String key, String... args) {
        String str = translations.get(key);
        if (str == null) str = translations.get(key);

        if (str == null) {
            Chat2Go.getInstance().getLogger().log(Level.WARNING, "Missing translation: " + key);
            return key;
        }

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                String replace = "{" + i + "}";

                str = str.replace(replace, arg);
            }
        }

        str = Chat2Go.parseColor(str);

        return str;
    }

    public void addTranslation(String key, String translation) {
        translations.put(key, translation);
    }

    public void sendMessage(CommandSender sender, String message) {
        String regex = "\\{(.*?)}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String full = matcher.group();
            String expression = matcher.group(1);
            String[] args = null;

            if (expression.contains("(")) {
                String[] parts = expression.split("\\(");
                String pre = parts[0];
                String post = parts[1];

                expression = pre;
                args = extractArgs("(" + post);
            }

            String translated = getTranslation(expression, args);
            message = message.replace(full, translated);
        }

        sender.sendMessage(message);
    }

    private String[] extractArgs(String expression) {
        ArrayList<String> parametersList = new ArrayList<>();

        String regex = "\\((.*?)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(expression);

        while (matcher.find()) {
            String parameters = matcher.group(1);
            if (parameters.contains(",")) {
                parametersList.addAll(Arrays.asList(parameters.split(",")));
            } else {
                parametersList.add(parameters);
            }
        }

        return parametersList.isEmpty() ? null : parametersList.toArray(new String[0]);
    }


}
