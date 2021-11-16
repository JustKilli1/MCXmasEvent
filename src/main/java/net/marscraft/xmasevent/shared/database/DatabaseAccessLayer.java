package net.marscraft.xmasevent.shared.database;

import net.marscraft.xmasevent.quest.Quest;
import net.marscraft.xmasevent.shared.configmanager.IConfigmanager;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseAccessLayer {

    private ILogmanager _logger;
    private MySQL _mySql;

    public DatabaseAccessLayer(ILogmanager logger, IConfigmanager cm) {
        _logger = logger;
        _mySql = new MySQL(_logger, cm);
        _mySql.connect();
    }
    public void disable() {
        _mySql.Disconnect();
    }

    public boolean CreatePlayerQuestProgressTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS PlayerQuestProgress (PlayerUUID VARCHAR(100) NOT NULL, PlayerName VARCHAR(100) NOT NULL, QuestId INT NOT NULL, QuestValueInt INT, QuestValueBool BOOLEAN);";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateQuestsTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS Quests (QuestId INT NOT NULL, QuestName VARCHAR(100) NOT NULL,QuestOrder INT, TaskName VARCHAR(100)," +
                " StartingMessage VARCHAR(100) DEFAULT 'Not Set', EndMessage VARCHAR(100) DEFAULT 'Not Set', QuestSetupFinished boolean DEFAULT false);";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateRewardsTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS Rewards (RewardId INT NOT NULL, QuestId INT NOT NULL, RewardName VARCHAR(100), Reward LONGTEXT);";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateKillMobsTaskTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS KillMobsTask (TaskId INT NOT NULL, TaskName VARCHAR(100) DEFAULT 'KillMobsTask', QuestId INT NOT NULL, NeededMobs INT, MobType VARCHAR(100));";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreatePlaceBlockTaskTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS PlaceBlockTask (TaskId INT NOT NULL, TaskName VARCHAR(100) DEFAULT 'PlaceBlockTask', QuestId INT NOT NULL, BlockType VARCHAR(100), BlockPositionX DOUBLE, BlockPositionY DOUBLE, BlockPositionZ DOUBLE, WorldName VARCHAR(100));";
        return ExecuteSQLRequest(sqlQuery);
    }

    public int GetLastTaskId(String table) {
        String sqlQuery = "SELECT * FROM KillMobsTask ORDER BY TaskId DESC LIMIT 1;";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return 0;
            else return rs.getInt("TaskId");
        } catch (Exception ex) {
            _logger.Error(ex);
            return 0;
        }
    }
    public String GetTaskNameByQuestId(int questId) {
        String sqlQuery = "SELECT * FROM Quests WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return null;
            return rs.getString("TaskName");
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public int GetLastQuestId() {
        String sqlQuery = "SELECT * FROM Quests ORDER BY QuestId DESC LIMIT 1";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return 0;
            else return rs.getInt("QuestId");
        } catch (Exception ex) {
            _logger.Error(ex);
            return -1;
        }

    }
    public int GetLastQuestOrder() {
        String sqlQuery = "SELECT * FROM quests ORDER BY QuestOrder DESC LIMIT 1";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return 0;
            else return rs.getInt("QuestOrder");
        } catch (Exception ex) {
            _logger.Error(ex);
            return 0;
        }
    }
    public int GetLastRewardId() {
        String sqlQuery = "SELECT * FROM rewards ORDER BY RewardId DESC LIMIT 1";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return 0;
            else return rs.getInt("RewardId");
        } catch (Exception ex) {
            _logger.Error(ex);
            return 0;
        }
    }
    public boolean AddNewReward(String rewardName, String reward, int questId) {
        int rewardId = GetLastRewardId() + 1;
        String sqlQuery = "INSERT INTO rewards (RewardId, QuestId, RewardName, Reward)" +
                "VALUES (" +
                rewardId + "," +
                questId + ",'" +
                rewardName + "','" +
                reward + "')";
        return ExecuteSQLRequest(sqlQuery);
    }

    public boolean CreateKillMobsTask(int taskId, int questId, int neededMobs, String mobType) {

        String sqlQuery = "INSERT INTO KillMobsTask " +
                "(TaskId, QuestId, NeededMobs, MobType) " +
                "VALUES (" + taskId + "," +
                questId + "," +
                neededMobs + "," +
                "'" + mobType + "');";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdateKillMobsTask(int questId, int neededMobs, String mobType) {
        String sqlQuery = "UPDATE KillMobsTask SET NeededMobs=" + neededMobs + ", MobType='" + mobType + "' WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdateQuestTaskName(int questId, String taskName) {
        String sqlQuery = "UPDATE Quests SET TaskName='" + taskName + "' WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean QuestSetupFinished(int questId) {
        String sqlQuery = "UPDATE Quests SET QuestSetupFinished=true WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdatePlaceBlockTask(int questId, String blockType, Location location) {
        String sqlQuery = "UPDATE PlaceBlockTask SET " +
                "BlockType='" + blockType + "', " +
                "BlockPositionX='" + location.getX() + "', " +
                "BlockPositionY='" + location.getY() + "', " +
                "BlockPositionZ='" + location.getZ() + "', " +
                "WorldName='" + location.getWorld().getName() + "' " +
                "WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreatePlaceBlockTask(int taskId, int questId, String blockType, Location location) {

        String sqlQuery = "INSERT INTO PlaceBlockTask (TaskId, QuestId, BlockType, BlockPositionX, BlockPositionY, BlockPositionZ, WorldName)" +
                "VALUES (" + taskId + ", " +
                questId + ", " +
                "'" + blockType + "'," +
                "'" + location.getX() + "', " +
                "'" + location.getY() + "', " +
                "'" + location.getZ() + "', " +
                "'" + location.getWorld().getName() + "')";
        return ExecuteSQLRequest(sqlQuery);
    }
    public int GetPlayerQuestValueInt(Player player) {

        String sqlQuery = "SELECT * FROM PlayerQuestProgress WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return 0;
            int questValueInt = rs.getInt("QuestValueInt");
            return questValueInt;
        } catch (Exception ex) {
            _logger.Error(ex);
        }
        return -1;
    }
    public ArrayList<String> GetQuestReward(int questId) {
        ArrayList<String> rewards = new ArrayList<>();
        String sqlQuery = "SELECT * FROM Rewards WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);
        try {
            while(rs.next()) { rewards.add(rs.getString("Reward")); }
            return rewards;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public ArrayList<String> GetQuestRewardNames(int questId) {
        ArrayList<String> rewards = new ArrayList<>();
        String sqlQuery = "SELECT * FROM Rewards WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);
        try {
            while(rs.next()) { rewards.add(rs.getString("RewardName")); }
            return rewards;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public int GetQuestOrder(int questId) {
        String sqlQuery = "SELECT * FROM quests WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next())return 0;
            else return rs.getInt("QuestOrder");
        } catch (Exception ex) {
            _logger.Error(ex);
            return 0;
        }
    }
    public boolean AddPlayerMobKill(Player player, int questId) {
        int oldCount = GetPlayerQuestValueInt(player);
        int newCount =  oldCount + 1;

        String sqlQuery = "UPDATE PlayerQuestProgress SET QuestValueInt=" + newCount + " WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdateQuestMessage(int questId, String commandStr, String field) {
        String sqlQuery = "UPDATE quests SET " + field + "='" + commandStr + "' WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdateQuestOrder(int questId, int newQuestOrder, int oldQuestOrder) {
        if(newQuestOrder == oldQuestOrder) return true;
        int lastQuestOrder = GetLastQuestOrder();
        int questOrder = newQuestOrder;
        if(questOrder > lastQuestOrder) questOrder = lastQuestOrder;

        String sqlQuery = "";
        if(questOrder > oldQuestOrder) {
            for(int i = oldQuestOrder; i <= questOrder; i++) {
                int newQOrder = i-1;
                sqlQuery = "UPDATE quests SET QuestOrder=" + newQOrder + " WHERE QuestOrder=" + i;
                if(!ExecuteSQLRequest(sqlQuery)) return false;
            }
        } else if(newQuestOrder < oldQuestOrder) {
            for(int i = oldQuestOrder; i >= questOrder; i--) {
                int newQOrder = i+1;
                sqlQuery = "UPDATE quests SET QuestOrder=" + newQOrder + " WHERE QuestOrder=" + i;
                if(!ExecuteSQLRequest(sqlQuery)) return false;
            }
        }
        sqlQuery = "UPDATE quests SET QuestOrder=" + questOrder + " WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdateALlQuestIds() {
        String sqlQuery = "SELECT * FROM quests ORDER BY QuestId";
        int lastQuestId = GetLastQuestId();
        ResultSet rs = QuerySQLRequest(sqlQuery);
        try {
            int newQuestId = 1;
            while (rs.next()){
                    sqlQuery = "UPDATE quests Set QuestId=" + newQuestId + " WHERE QuestName='" + rs.getString("QuestName") + "'";
                    if (!ExecuteSQLRequest(sqlQuery)) _logger.Error("Could Not Update quests Table");
                    int oldQuestID = rs.getInt("QuestId");
                    sqlQuery = "UPDATE " + rs.getString("TaskName") + " SET QuestId=" + newQuestId + " WHERE QuestId=" + oldQuestID;
                    if (!ExecuteSQLRequest(sqlQuery)) _logger.Error("Could Not Update task Table");
                    sqlQuery = "UPDATE Rewards SET QuestId=" + newQuestId + " WHERE QuestId=" + oldQuestID;
                    if (!ExecuteSQLRequest(sqlQuery)) _logger.Error("Could Not Update Rewards Table");
                    newQuestId++;
            }
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
        return true;
    }
    public boolean SetNextPlayerQuest(String playerUUID, int questId) {
        int questOrder = GetQuestOrder(questId);
        int nextQuestId = GetNextQuestQuestID(questOrder);
        String sqlQuery = "UPDATE PlayerQuestProgress SET QuestId=" + nextQuestId + " WHERE PlayerUUID='" + playerUUID + "'";
        return ExecuteSQLRequest(sqlQuery);
    }
    public int GetNextQuestQuestID(int questOrder) {
        String sqlQuery="SELECT * FROM quests WHERE QuestOrder>" + questOrder + " AND QuestSetupFinished=true ORDER BY QuestOrder";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return 0;
            else return rs.getInt("QuestId");
        } catch (Exception ex) {
            _logger.Error(ex);
            return 0;
        }
    }
    public ResultSet GetQuest(int questId) {
        String sqlQuery = "SELECT * FROM quests WHERE QuestId=" + questId;
        return QuerySQLRequest(sqlQuery);
    }
    public boolean UpdateTaskPlayerBlockPlaced(Player player) {

        String sqlQuery = "UPDATE PlayerQuestProgress SET QuestValueBool=true WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        return ExecuteSQLRequest(sqlQuery);
    }

    public ResultSet GetTaskByQuestId(String tableName, int questId) {
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE QuestId=" + questId;
        return QuerySQLRequest(sqlQuery);
    }
    public int GetActivePlayerQuestId(Player player) {
        String sqlQuery = "SELECT * FROM PlayerQuestProgress WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) { return 0;}
            return rs.getInt("QuestId");
        } catch(Exception ex) {
            _logger.Error(ex);
        }
        return -1;
    }

    public void AddNewPlayerToDatabase(Player player) {
        String sqlQuery = "SELECT * FROM PlayerQuestProgress WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);
        try {
            if(rs.next()) return;
        } catch (Exception ex) {
            _logger.Error(ex);
            return;
        }
        int questId = GetNextQuestQuestID(0);
        if (questId == 0) return;
        sqlQuery  = "INSERT INTO PlayerQuestProgress (PlayerUUID, PlayerName, QuestId, QuestValueInt) VALUES ('" +
                player.getUniqueId().toString() + "', '" +
                player.getName() + "', " +
                questId + ", 0);";

        ExecuteSQLRequest(sqlQuery);
    }
    public boolean AddNewQuestToDatabase(Quest quest, String taskName) {
        String sqlQuery = "INSERT INTO Quests " +
                "(QuestId, QuestName, QuestOrder, TaskName) " +
                "VALUES (" + quest.GetQuestId() + "," +
                " '" + quest.GetQuestName() + "'," +
                (GetLastQuestOrder() + 1) + "," +
                " '" + taskName + "')";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean QuestExists(String questName) {
        String sqlQuery = "SELECT * FROM Quests WHERE QuestName='" + questName + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(rs.next()) return true;
            else return false;
        } catch (Exception ex) {
            _logger.Error(ex);
            return true;
        }
    }

    public boolean TaskExists(int questId, String taskName) {

        String sqlQuery = "SELECT * FROM " + taskName + " WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(rs.next()) return true;
            else return false;
        } catch (Exception ex) {
            _logger.Error(ex);
        }
        return false;
    }
    public ResultSet GetAllQuests() {
        String sqlQuery = "SELECT * FROM Quests";
        return QuerySQLRequest(sqlQuery);
    }

    public boolean DeleteTaskByQuestId(int questId, String table) {
        String sqlQuery = "DELETE FROM " + table + " WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean DeleteQuestFromQuestsTable(int questId) {
        String sqlQuery = "DELETE FROM quests WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean DeleteQuestRewards(int questId) {
        String sqlQuery = "DELETE FROM rewards WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean ResetProgressValues(int questId) {
        String sqlQuery = "UPDATE PlayerQuestProgress SET QuestValueInt=0, QuestValueBool=false WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }

    private void checkAndReconnectConnection() {
        if (!_mySql.IsConnected()) {
            _logger.Info("Connection lost! Reconnecting...");
            _mySql.connect();
        }
    }
    public boolean ExecuteSQLRequest(String sqlQuery) {
        checkAndReconnectConnection();
        if (_mySql.IsConnected()) {
            Connection connection = _mySql.GetConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(sqlQuery);
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                _logger.Error(e);
                return false;
            }
        } else {
            return false;
        }
    }
    public ResultSet QuerySQLRequest(String sqlQuery) {
        checkAndReconnectConnection();
        if (_mySql.IsConnected()) {
            Connection connection = _mySql.GetConnection();
            try {
                PreparedStatement ps = connection.prepareStatement(sqlQuery);
                ResultSet rs = ps.executeQuery();
                return rs;
            } catch (SQLException e) {
                _logger.Error(e);
                return null;
            }
        } else {
            return null;
        }
    }
}
