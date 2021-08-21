package eu.rex2go.chat2go.chat;

import eu.rex2go.chat2go.config.ChatConfig;
import eu.rex2go.chat2go.user.User;
import lombok.Getter;

public class AntiSpam {

    public static CheckResult check(String message, User sender) {
        double diff = ((System.currentTimeMillis() - sender.getLastMessageTime()) / 1000D);

        if (diff < 0.5) {
            return CheckResult.TOO_FAST;
        }

        if (diff < 30) {
            if (sender.getLastMessage().equalsIgnoreCase(message)) {
                return CheckResult.REPEATING;
            }
        }

        if (message.replace(" ", "").length() > 3) {
            double spaceCount = 0;
            char lastChar = ' ';
            int duplicates = 0;

            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);

                if (c == lastChar) {
                    duplicates++;

                    if (duplicates >= ChatConfig.getAntiSpamMaxCharRepetitions()) {
                        return CheckResult.SUSPICIOUS;
                    }
                } else {
                    duplicates = 0;
                }

                if (Character.isSpaceChar(c)) {
                    spaceCount++;
                }

                lastChar = c;
            }

            if (spaceCount / message.length() > ChatConfig.getAntiSpamSpaceThreshold()) {
                return CheckResult.SUSPICIOUS;
            }
        }

        return CheckResult.OK;
    }

    public static String preventCaps(String message) {
        if (message.replace(" ", "").length() > 3) {
            double upperCount = 0;

            for (int i = 0; i < message.length(); i++) {
                char c = message.charAt(i);

                if (Character.isUpperCase(c)) {
                    upperCount++;
                }
            }

            if (upperCount / message.length() > ChatConfig.getAntiSpamCapsThreshold()) {
                message = message.toLowerCase();
            }
        }

        return message;
    }

    public enum CheckResult {
        OK(null, false),
        SUSPICIOUS("chat.antispam.suspicious", true),
        TOO_FAST("chat.antispam.too_fast", true),
        REPEATING("chat.antispam.repeating", true);

        @Getter
        private final String message;

        @Getter
        private final boolean blockMessage;

        CheckResult(String message, boolean blockMessage) {
            this.message = message;
            this.blockMessage = blockMessage;
        }
    }
}
