package net.solostudio.huntMaster.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.solostudio.huntMaster.HuntMaster;
import net.solostudio.huntMaster.enums.RewardTypes;
import net.solostudio.huntMaster.enums.keys.ConfigKeys;
import net.solostudio.huntMaster.events.BountyCreateEvent;
import net.solostudio.huntMaster.interfaces.HuntMasterDatabase;
import net.solostudio.huntMaster.managers.BountyData;
import net.solostudio.huntMaster.managers.TopData;
import net.solostudio.huntMaster.utils.LoggerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class H2 implements HuntMasterDatabase {
    private final Connection connection;
    private final HikariDataSource dataSource;

    public H2() throws SQLException, ClassNotFoundException {
        Class.forName("net.solostudio.huntMaster.libs.h2.Driver");

        HikariConfig hikariConfig = new HikariConfig();

        String url = "jdbc:h2:" + HuntMaster.getInstance().getDataFolder().getAbsolutePath() + "/database;MODE=MySQL";

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setPoolName("VaultcherPool");

        dataSource = new HikariDataSource(hikariConfig);
        connection = dataSource.getConnection();
    }

    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS huntmaster (ID INT AUTO_INCREMENT PRIMARY KEY, PLAYER VARCHAR(255) NOT NULL, TARGET VARCHAR(255) NOT NULL, REWARD_TYPE VARCHAR(255) NOT NULL, REWARD INT, BOUNTY_DATE DATETIME, STREAK INT DEFAULT 0)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
            preparedStatement.execute();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void createBounty(@NotNull Player player, @NotNull Player target, @NotNull RewardTypes rewardType, int reward) {
        String query = "INSERT INTO huntmaster (PLAYER, TARGET, REWARD_TYPE, REWARD, BOUNTY_DATE) VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            preparedStatement.setString(2, target.getName());
            preparedStatement.setString(3, rewardType.name());
            preparedStatement.setInt(4, reward);

            preparedStatement.executeUpdate();
            HuntMaster.getInstance().getServer().getPluginManager().callEvent(new BountyCreateEvent(player, target, reward, rewardType));
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public void createRandomBounty(@NotNull Player target, @NotNull RewardTypes rewardType, int reward) {
        String query = "INSERT INTO huntmaster (PLAYER, TARGET, REWARD_TYPE, REWARD, BOUNTY_DATE) VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, ConfigKeys.RANDOM_BOUNTY_PLAYER_VALUE.getWebhookString());
            preparedStatement.setString(2, target.getName());
            preparedStatement.setString(3, rewardType.name());
            preparedStatement.setInt(4, reward);

            preparedStatement.executeUpdate();
            HuntMaster.getInstance().getServer().getPluginManager().callEvent(new BountyCreateEvent(null, target, reward, rewardType));
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public List<BountyData> getOwnBounties(@NotNull Player player) {
        List<BountyData> ownBounties = new ArrayList<>();
        String query = "SELECT * FROM huntmaster WHERE PLAYER = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int reward = resultSet.getInt("REWARD");
                String target = resultSet.getString("TARGET");
                String playerName = resultSet.getString("PLAYER");
                String rewardType = resultSet.getString("REWARD_TYPE");

                ownBounties.add(new BountyData(id, playerName, target, RewardTypes.valueOf(rewardType), reward));
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return ownBounties;
    }

    @Override
    public boolean isSenderIsRandom(@NotNull Player player) {
        String query = "SELECT PLAYER FROM huntmaster WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String sender = resultSet.getString("PLAYER");
                return sender.equals(ConfigKeys.RANDOM_BOUNTY_PLAYER_VALUE.getString());
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return false;
    }

    @Override
    public List<BountyData> getBounties() {
        List<BountyData> bounties = new ArrayList<>();

        String query = "SELECT * FROM huntmaster";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int reward = resultSet.getInt("REWARD");
                String target = resultSet.getString("TARGET");
                String player = resultSet.getString("PLAYER");
                String reward_type = resultSet.getString("REWARD_TYPE");
                bounties.add(new BountyData(id, player, target, RewardTypes.valueOf(reward_type), reward));
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return bounties;
    }

    @Override
    public int getStreak(@NotNull OfflinePlayer player) {
        String maxDateQuery = "SELECT MAX(BOUNTY_DATE) AS MAX_DATE FROM huntmaster WHERE TARGET = ?";
        String updateQuery = "UPDATE huntmaster SET STREAK = ? WHERE TARGET = ?";
        String selectQuery = "SELECT STREAK FROM huntmaster WHERE TARGET = ?";

        try (PreparedStatement maxDateStatement = connection.prepareStatement(maxDateQuery);
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {

            maxDateStatement.setString(1, player.getName());
            ResultSet maxDateResult = maxDateStatement.executeQuery();

            if (maxDateResult.next()) {
                Date maxBountyDate = maxDateResult.getDate("MAX_DATE");

                if (maxBountyDate != null) {
                    long diffInMillis = System.currentTimeMillis() - maxBountyDate.getTime();
                    int streak = (int) (diffInMillis / (1000 * 60 * 60 * 24));

                    updateStatement.setInt(1, streak);
                    updateStatement.setString(2, player.getName());
                    updateStatement.executeUpdate();
                }
            }

            selectStatement.setString(1, player.getName());
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) return resultSet.getInt("STREAK");
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return 0;
    }




    @Override
    public boolean isBounty(@NotNull Player player) {
        String query = "SELECT TARGET FROM huntmaster WHERE TARGET = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return false;
    }

    @Override
    public void changeReward(@NotNull Player player, int newReward) {
        String query = "UPDATE huntmaster SET REWARD = ? WHERE TARGET = ?";

        try {
            try (PreparedStatement updateStatement = getConnection().prepareStatement(query)) {
                updateStatement.setInt(1, newReward);
                updateStatement.setString(2, player.getName());
                updateStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public int getReward(@NotNull Player player) {
        String query = "SELECT REWARD FROM huntmaster WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            int reward;

            if (resultSet.next()) {
                reward = resultSet.getInt("REWARD");
                return reward;
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public RewardTypes getRewardType(@NotNull Player player) {
        String query = "SELECT REWARD_TYPE FROM huntmaster WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            RewardTypes rewardTypes;

            if (resultSet.next()) {
                rewardTypes = RewardTypes.valueOf(resultSet.getString("REWARD_TYPE"));
                return rewardTypes;
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return RewardTypes.MONEY;
    }

    @Override
    public Player getSender(@NotNull Player player) {
        String query = "SELECT PLAYER FROM huntmaster WHERE TARGET = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) return Bukkit.getPlayerExact(resultSet.getString("PLAYER"));
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return null;
    }

    @Override
    public void removeBounty(@NotNull Player player) {
        String query = "DELETE FROM huntmaster WHERE TARGET = ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setString(1, player.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public List<TopData> getTop(int number) {
        List<TopData> topStreaks = new ArrayList<>();
        String query = "SELECT TARGET, STREAK FROM huntmaster ORDER BY STREAK DESC LIMIT ?";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, number);

                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(resultSet.getString("TARGET"));
                    int streak = resultSet.getInt("STREAK");

                    topStreaks.add(new TopData(player, streak));
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return topStreaks;
    }

    @Override
    public String getTopStreakPlayer(int top) {
        String playerName = null;
        String query = "SELECT TARGET FROM huntmaster ORDER BY STREAK DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) playerName = resultSet.getString("TARGET");
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return playerName;
    }

    @Override
    public int getTopStreak(int top) {
        String query = "SELECT STREAK FROM huntmaster ORDER BY STREAK DESC LIMIT ?, 1";

        try {
            try (PreparedStatement preparedStatement = getConnection().prepareStatement(query)) {
                preparedStatement.setInt(1, top - 1);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) return resultSet.getInt("STREAK");
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }

        return 0;
    }

    @Override
    public void reconnect() {
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
            new MySQL(Objects.requireNonNull(HuntMaster.getInstance().getConfiguration().getSection("database.mysql")));
        } catch (SQLException | ClassNotFoundException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    @Override
    public boolean reachedMaximumBounty(@NotNull Player player) {
        String query = "SELECT COUNT(*) AS total FROM huntmaster WHERE PLAYER = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, player.getName());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int totalItems = resultSet.getInt("total");
                    return totalItems >= ConfigKeys.MAXIMUM_BOUNTY.getInt();
                }
            }
        } catch (SQLException exception) {
            LoggerUtils.error(exception.getMessage());
        }
        return false;
    }


    @Override
    public boolean isConnected() {
        return connection != null;
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                LoggerUtils.error(exception.getMessage());
            }
        }
    }
}