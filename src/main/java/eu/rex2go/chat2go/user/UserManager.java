package eu.rex2go.chat2go.user;


import eu.rex2go.chat2go.Chat2Go;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class UserManager {

    @Getter
    private final ArrayList<User> users = new ArrayList<>();

    public User getUser(Player player) {
        User user = users.stream().filter(u -> u.getPlayer().getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);

        if (user == null) {
            user = loadUser(player.getUniqueId(), player.getName(), false);
        }

        return user;
    }

    public User loadUser(UUID uuid, String username, boolean force) {
        String finalUsername = username;
        UUID finalUuid = uuid;

        User user = users.stream().filter(
                u -> u.getPlayer().getUniqueId().equals(finalUuid)
                        || u.getPlayer().getName().equalsIgnoreCase(finalUsername))
                .findFirst().orElse(null);

        if (user != null) return user;

        try {
            Connection connection = Chat2Go.getDatabaseManager().getDataSource().getConnection();
            PreparedStatement ps;

            // TODO join mute
            if (uuid != null) {
                ps = connection.prepareStatement("SELECT * FROM `user` WHERE `uuid` = ?");
                ps.setString(1, uuid.toString());
            } else if (username != null) {
                ps = connection.prepareStatement("SELECT * FROM `user` WHERE `username` = ?");
                ps.setString(1, username.toLowerCase());
            } else {
                return null;
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // exists

                if (username == null) {
                    username = rs.getString("username");
                }

                if (uuid == null) {
                    uuid = UUID.fromString(rs.getString("uuid"));
                }

                if (!username.equalsIgnoreCase(rs.getString("username"))) {
                    // update remote username

                    PreparedStatement ps1 = connection.prepareStatement("UPDATE `user` WHERE `uuid` = ? SET `username` = ?");
                    ps1.setString(1, uuid.toString());
                    ps1.setString(2, username);

                    ps1.execute();
                    ps1.close();
                }

                user = new User(uuid, username);

            } else if (force) {
                // not existing, create new

                PreparedStatement ps1 = connection.prepareStatement("INSERT INTO `user` (uuid, username) VALUES (?, ?)");
                ps1.setString(1, uuid.toString());
                ps1.setString(2, username);

                ps1.execute();
                ps1.close();

                user = new User(uuid, username);
            }

            rs.close();
            ps.close();

            if (user != null) {
                loadMute(user, connection);
                loadStatistics(user, connection);
            }

            connection.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // add to cache
        if (user != null) users.add(user);

        return user;
    }

    public void saveAll() {
        try {
            Connection connection = Chat2Go.getDatabaseManager().getDataSource().getConnection();

            for (User user : users) {
                saveUser(user, connection);
            }

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void saveUser(User user) {
        try {
            Connection connection = Chat2Go.getDatabaseManager().getDataSource().getConnection();

            saveUser(user, connection);

            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void saveUser(User user, Connection connection) {
        saveMute(user, connection);
    }

    private void loadMute(User user, Connection connection) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `mute` WHERE `user_uuid` = ?");
            ps.setString(1, user.getUuid().toString());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // exists

                long time = rs.getLong("time");
                long unmuteTime = rs.getLong("unmuteTime");
                String reason = rs.getString("reason");
                UUID muter = UUID.fromString(rs.getString("muter_uuid"));

                Mute mute = new Mute(time, unmuteTime, reason, muter);

                user.setMute(mute);
            }

            rs.close();
            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void saveMute(User user, Connection connection) {
        if (user.getMute() == null) return;

        Mute mute = user.getMute();

        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `mute` WHERE user_uuid = ?");
            ps.setString(1, user.getUuid().toString());

            ps.execute();
            ps.close();

            ps = connection.prepareStatement(
                    "INSERT INTO `mute` (user_uuid, 'time', unmuteTime, reason, muter_uuid) " +
                            "VALUES (?, ?, ?, ?, ?)"
            );

            ps.setString(1, user.getUuid().toString());
            ps.setLong(2, mute.getTime());
            ps.setLong(3, mute.getUnmuteTime());
            ps.setString(4, mute.getReason());
            ps.setString(5, mute.getMuter().toString());

            ps.execute();

            ps.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void loadStatistics(User user, Connection connection) {
        // TODO
    }

    private void saveStatistics(User user, Connection connection) {
        // TODO
    }

    public void unloadOffline(User user) {
        if (!users.contains(user)) return;
        if (user.getPlayer() != null) return;

        saveUser(user);

        users.remove(user);
    }
}
