package eu.rex2go.chat2go.filter;

import eu.rex2go.chat2go.database.ConnectionWrapper;
import eu.rex2go.chat2go.database.DatabaseManager;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BadWordFilter extends Filter {

    @Getter
    private static final List<String> badWords = new ArrayList<>();

    public BadWordFilter() {
        loadBadWords();
    }

    public static boolean addBadWord(String badWord) {
        if (badWords.contains(badWord)) return false;

        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        if (connectionWrapper == null) return false;

        PreparedStatement preparedStatement = connectionWrapper.prepareStatement(
                "INSERT INTO `badword` (badWord) VALUES (?)"
        );

        try {
            preparedStatement.setString(1, badWord);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            connectionWrapper.close();
            return false;
        }

        connectionWrapper.close();

        badWords.add(badWord);

        return true;
    }

    public static boolean removeBadWord(String badWord) {
        if (!badWords.contains(badWord)) return false;

        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        if (connectionWrapper == null) return false;

        PreparedStatement preparedStatement = connectionWrapper.prepareStatement(
                "DELETE FROM `badword` WHERE badWord = ?"
        );

        try {
            preparedStatement.setString(1, badWord);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            connectionWrapper.close();
            return false;
        }

        connectionWrapper.close();

        badWords.remove(badWord);

        return true;
    }

    public void loadBadWords() {
        ConnectionWrapper connectionWrapper = DatabaseManager.getConnectionWrapper();
        if (connectionWrapper == null) return;

        PreparedStatement preparedStatement = connectionWrapper.prepareStatement("SELECT * FROM `badword`");

        try {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                badWords.add(resultSet.getString("badWord"));
            }

            preparedStatement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        connectionWrapper.close();
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
