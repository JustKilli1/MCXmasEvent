package net.marscraft.xmasevent.shared.database;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Quest;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.Inventorys.Inventorymanager;
import net.marscraft.xmasevent.shared.configmanager.IConfigmanager;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        String sqlQuery = "CREATE TABLE IF NOT EXISTS PlayerQuestProgress (PlayerUUID VARCHAR(100) NOT NULL, PlayerName VARCHAR(100) NOT NULL, QuestId INT NOT NULL, QuestValueInt INT DEFAULT 0, QuestValueBool BOOLEAN DEFAULT false, QuestValueString LONGTEXT DEFAULT null, QuestFinished BOOLEAN DEFAULT false);";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateQuestsTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS Quests (QuestId INT NOT NULL, QuestName VARCHAR(100) NOT NULL,QuestOrder INT, TaskName VARCHAR(100)," +
                " StartMessage VARCHAR(200) DEFAULT 'Not Set', EndMessage VARCHAR(200) DEFAULT 'Not Set', Description VARCHAR(200) DEFAULT 'Not Set', NpcName VARCHAR(100) DEFAULT 'Trixi', QuestSetupFinished boolean DEFAULT false);";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateRewardsTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS Rewards (RewardId INT NOT NULL, QuestId INT NOT NULL, RewardName VARCHAR(100), Reward LONGTEXT);";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateUnclaimedRewardsTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS UnclaimedRewards (PlayerUUID VARCHAR(100) NOT NULL, PlayerName VARCHAR(100) NOT NULL, RewardIds VARCHAR(100));";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateKillMobsTaskTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS KillMobsTask (TaskId INT NOT NULL, TaskName VARCHAR(100) DEFAULT 'KillMobsTask', QuestId INT NOT NULL, NeededMobs INT, MobType VARCHAR(100), MobTypeGer VARCHAR(100));";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreatePlaceBlockTaskTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS PlaceBlockTask (TaskId INT NOT NULL, TaskName VARCHAR(100) DEFAULT 'PlaceBlockTask', QuestId INT NOT NULL, BlockType VARCHAR(100), BlockTypeGer VARCHAR(100), BlockPositionX INT, BlockPositionY INT, BlockPositionZ INT, WorldName VARCHAR(100));";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreatePlaceBlocksTaskTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS PlaceBlocksTask (TaskId INT NOT NULL, TaskName VARCHAR(100) DEFAULT 'PlaceBlocksTask', QuestId INT NOT NULL, BlockType VARCHAR(100), BlockTypeGer VARCHAR(100), BlockAmount INT);";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateBreakBlocksTaskTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS BreakBlocksTask (TaskId INT NOT NULL, TaskName VARCHAR(100) DEFAULT 'BreakBlocksTask', QuestId INT NOT NULL, BlockType VARCHAR(100), BlockTypeGer VARCHAR(100), BlockAmount INT);";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateCollectItemsTaskTable() {
        String sqlQuery = "CREATE TABLE IF NOT EXISTS CollectItemsTask (TaskId INT NOT NULL, TaskName VARCHAR(100) DEFAULT 'CollectItemsTask', QuestId INT NOT NULL, NeededItems LONGTEXT);";
        return ExecuteSQLRequest(sqlQuery);
    }



    public int GetLastTaskId(String table) {
        String sqlQuery = "SELECT * FROM " + table + " ORDER BY TaskId DESC LIMIT 1;";
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
    public String GetQuestMessage(int questId, String tableColumnName) {
        String sqlQuery ="SELECT * FROM Quests WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return null;
            return rs.getString(tableColumnName);
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public boolean AddQuestNpcName(int questId, String npcName) {
        String sqlQuery = "UPDATE quests SET NpcName='" + npcName + "' WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public String GetQuestNpcName(int questId) {
        ResultSet rs = GetQuest(questId);
        try {
            if(!rs.next()) return null;
            return rs.getString("NpcName");
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
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
    public ArrayList<Integer> GetRewardIds(int questId) {
        String sqlQuery = "SELECT * FROM rewards WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);
        ArrayList<Integer> rewardIds = new ArrayList<>();

        try {
            while(rs.next()) rewardIds.add(rs.getInt("RewardId"));
            return rewardIds;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public boolean DeleteReward(int rewardId) {
        String sqlQuery = "DELETE FROM rewards WHERE RewardId=" + rewardId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean DeleteTaskByTaskId(int taskId, String tableName) {
        String sqlQuery = "DELETE FROM " + tableName + " WHERE TaskId=" + taskId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean AddUnclaimedPlayerReward(int rewardId, Player player) {
        String rewardIds = "";
        String sqlQuery = "SELECT * FROM UnclaimedRewards WHERE PlayerUUID='" + player.getUniqueId() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);
        try {
            if(!rs.next()) {
                sqlQuery = "INSERT INTO UnclaimedRewards (PlayerUUID, PlayerName, RewardIds) VALUES ('" + player.getUniqueId() + "', '" + player.getName() + "', '" + rewardId + "')";
                return ExecuteSQLRequest(sqlQuery);
            }
            rewardIds = rs.getString("RewardIds");
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
        rewardIds += "," + rewardId;
        sqlQuery = "UPDATE UnclaimedRewards Set RewardIds='" + rewardIds +"'";
        return ExecuteSQLRequest(sqlQuery);
    }
    public ArrayList<Integer> GetUnclaimedPlayerRewardIds(Player player) {
        String sqlQuery = "SELECT * FROM UnclaimedRewards WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);
        ArrayList<Integer> rewardIds = new ArrayList<>();
        try {
            if(!rs.next()) return null;
            String[] rewardIdStr = rs.getString("RewardIds").split(",");
            for(String str : rewardIdStr) { rewardIds.add(Integer.parseInt(str)); }
            return rewardIds;
        }catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public boolean DeleteUnclaimedPlayerReward(Player player) {
        String sqlQuery = "DELETE FROM UnclaimedRewards WHERE PlayerUUID='" + player.getUniqueId() + "'";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateKillMobsTask(int taskId, int questId, int neededMobs, String mobType, String mobTypeGer) {

        String sqlQuery = "INSERT INTO KillMobsTask " +
                "(TaskId, QuestId, NeededMobs, MobType, MobTypeGer) " +
                "VALUES (" + taskId + "," +
                questId + "," +
                neededMobs + ", '" +
                mobType + "', '" +
                mobTypeGer + "');";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdateKillMobsTask(int questId, int neededMobs, String mobType, String mobTypeGer) {
        String sqlQuery = "UPDATE KillMobsTask SET NeededMobs=" + neededMobs + ", MobType='" + mobType + "', MobTypeGer='" + mobTypeGer + "' WHERE QuestId=" + questId;
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
    public boolean UpdatePlaceBlockTask(int questId, String blockType, String blockTypeGer, Location location) {
        String sqlQuery = "UPDATE PlaceBlockTask SET " +
                "BlockType='" + blockType + "', " +
                "BlockTypeGer='" + blockTypeGer + "', " +
                "BlockPositionX='" + location.getBlockX() + "', " +
                "BlockPositionY='" + location.getBlockY() + "', " +
                "BlockPositionZ='" + location.getBlockZ() + "', " +
                "WorldName='" + location.getWorld().getName() + "' " +
                "WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreatePlaceBlockTask(int taskId, int questId, String blockType, String blockTypeGer, Location location) {
        String sqlQuery = "INSERT INTO PlaceBlockTask (TaskId, QuestId, BlockType, BlockTypeGer, BlockPositionX, BlockPositionY, BlockPositionZ, WorldName)" +
                "VALUES (" + taskId + ", " +
                questId + ", " +
                "'" + blockType + "'," +
                "'" + blockTypeGer + "'," +
                "'" + location.getBlockX() + "', " +
                "'" + location.getBlockY() + "', " +
                "'" + location.getBlockZ() + "', " +
                "'" + location.getWorld().getName() + "')";
        return ExecuteSQLRequest(sqlQuery);
    }

    public boolean CreatePlaceBlocksTask(int questId, int blockAmount, String blockType, String blockTypeGer) {
        int taskId = GetLastTaskId("PlaceBlocksTask");
        String sqlQuery = "INSERT INTO PlaceBlocksTask " +
                "(TaskId, QuestId, BlockType, BlockTypeGer, BlockAmount) " +
                "VALUES (" + taskId + ", " + questId + ", '" + blockType + "', '" + blockTypeGer + "', " + blockAmount + ")";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateBreakBlocksTask(int questId, int blockAmount, String blockType, String blockTypeGer) {
        int taskId = GetLastTaskId("BreakBlocksTask");
        String sqlQuery = "INSERT INTO BreakBlocksTask " +
                "(TaskId, QuestId, BlockType, BlockTypeGer, BlockAmount) " +
                "VALUES (" + taskId + ", " + questId + ", '" + blockType + "', '" + blockTypeGer + "', " + blockAmount + ")";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean CreateCollectItemsTask(int questId, String neededItemsStr) {
        int taskId = GetLastTaskId("CollectItemsTask") + 1;
        String sqlQuery = "INSERT INTO CollectItemsTask " +
                "(TaskId, QuestId, NeededItems) " +
                "VALUES (" + taskId + ", " + questId + ", '" + neededItemsStr + "')";
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdatePlaceBlocksTask(int questId, int blockAmount, String blockType, String blockTypeGer) {
        String sqlQuery = "UPDATE PlaceBlocksTask SET " +
                "QuestId=" + questId + ", " +
                "BlockType='" + blockType + "', " +
                "BlockTypeGer='" + blockTypeGer + "', " +
                "BlockAmount=" + blockAmount + " " +
                "WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdateBreakBlocksTask(int questId, int blockAmount, String blockType, String blockTypeGer) {
        String sqlQuery = "UPDATE BreakBlocksTask SET " +
                "QuestId=" + questId + ", " +
                "BlockType='" + blockType + "', " +
                "BlockTypeGer='" + blockTypeGer + "', " +
                "BlockAmount=" + blockAmount + " " +
                "WHERE QuestId=" + questId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public boolean UpdateCollectItemsTask(int questId, String neededItems) {
        String sqlQuery = "UPDATE CollectItemsTask SET " +
                "QuestId=" + questId + ", " +
                "NeededItems='" + neededItems + "' " +
                "WHERE QuestId=" + questId;
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
    public boolean GetPlayerQuestValueBool(Player player) {

        String sqlQuery = "SELECT * FROM PlayerQuestProgress WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return false;
            return rs.getBoolean("QuestValueBool");
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
    }
    public ArrayList<String> GetQuestRewardStr(int questId) {
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
    public int GetBlocksTaskBlockAmount(int questId, String tableName) {
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return 0;
            return rs.getInt("BlockAmount");
        } catch (Exception ex) {
            _logger.Error(ex);
            return 0;
        }
    }
    public ArrayList<ItemStack> GetCollectItemsTaskNeededItems(int questId) {
        ResultSet rs = GetTaskByQuestId("CollectItemsTask", questId);


        try {
            ArrayList<ItemStack> neededItems = new ArrayList<>();
            while(rs.next()) {
                String neededItemStr = rs.getString("NeededItems");
                ItemStackSerializer serializer = new ItemStackSerializer(_logger);
                ItemStack neededItem = serializer.ItemStackFromBase64(neededItemStr);
                if (neededItem == null) return null;
                neededItems.add(neededItem);
            }
            return neededItems;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public ItemStack GetCollectItemByTaskId(int taskId) {
        String sqlQuery = "SELECT * FROM collectitemstask WHERE TaskId=" + taskId;
        ResultSet rs = QuerySQLRequest(sqlQuery);
        ItemStackSerializer serializer = new ItemStackSerializer(_logger);

        try {
            if(!rs.next()) return null;
            ItemStack itemStack = serializer.ItemStackFromBase64(rs.getString("NeededItems"));
            if(itemStack == null) return null;
            return itemStack;
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public String GetPlayerQuestValueString(Player player) {
        String sqlQuery = "SELECT * FROM PlayerQuestProgress WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return null;
            return rs.getString("QuestValueString");
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    //TODO ändern wegen SqlInjections
    public boolean SetPlayerQuestValueString(Player player, String value) {
        String sqlQuery = "UPDATE PlayerQuestProgress SET QuestValueString='" + value + "' WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        return ExecuteSQLRequest(sqlQuery);
    }
    public ResultSet GetQuestReward(int questId) {
        String sqlQuery = "SELECT * FROM Rewards WHERE QuestId=" + questId;
        return QuerySQLRequest(sqlQuery);
    }
    public boolean SetReward(int rewardId, String rewardStr) {
        String sqlQuery = "UPDATE rewards Set Reward='" + rewardStr + "' WHERE RewardId=" + rewardId;
        return ExecuteSQLRequest(sqlQuery);
    }
    public ResultSet GetQuestRewardByRewardId(int rewardId) {
        String sqlQuery = "SELECT * FROM Rewards WHERE RewardId=" + rewardId;
        return QuerySQLRequest(sqlQuery);
    }
    public boolean SetUnclaimedPlayerRewards(Player player, String unclaimedRewardIds) {
        String sqlQuery = "UPDATE UnclaimedRewards Set RewardIds='" + unclaimedRewardIds + "' WHERE PlayerUUID='" + player.getUniqueId() + "'";
        return ExecuteSQLRequest(sqlQuery);
    }
    public int GetQuestOrder(int questId) {
        String sqlQuery = "SELECT * FROM quests WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next())return GetLastQuestOrder() + 1;
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
    public String GetQuestName(int questId) {
        ResultSet rs = GetQuest(questId);

        try {
            if(!rs.next()) return null;
            return rs.getString("QuestName");
        } catch (Exception ex) {
            _logger.Error(ex);
            return null;
        }
    }
    public int GetNextQuestQuestID(int questOrder) {
        String sqlQuery="SELECT * FROM quests WHERE QuestOrder>" + questOrder + " AND QuestSetupFinished=true ORDER BY QuestOrder";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return GetLastQuestId() + 1;
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
    public boolean PlayerQuestFinished(Player player) {
        String sqlQuery = "SELECT * FROM playerquestprogress WHERE PlayerUUID='" + player .getUniqueId().toString() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            if(!rs.next()) return false;
            return rs.getBoolean("QuestFinished");
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
    }
    public boolean SetPlayerQuestFinished(Player player, boolean questFinished) {
        String sqlQuery = "UPDATE PlayerQuestProgress SET QuestFinished=" + questFinished + " WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        return ExecuteSQLRequest(sqlQuery);
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

    public boolean AddNewPlayerToDatabase(Player player) {
        String sqlQuery = "SELECT * FROM PlayerQuestProgress WHERE PlayerUUID='" + player.getUniqueId().toString() + "'";
        ResultSet rs = QuerySQLRequest(sqlQuery);
        try {
            if(rs.next()) return false;
        } catch (Exception ex) {
            _logger.Error(ex);
            return false;
        }
        int questId = GetNextQuestQuestID(0);
        if (questId == 0) return false;
        sqlQuery  = "INSERT INTO PlayerQuestProgress (PlayerUUID, PlayerName, QuestId, QuestValueInt) VALUES ('" +
                player.getUniqueId().toString() + "', '" +
                player.getName() + "', " +
                questId + ", 0);";

        return ExecuteSQLRequest(sqlQuery);
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
            return rs.next();
        } catch (Exception ex) {
            _logger.Error(ex);
            return true;
        }
    }

    public boolean TaskExists(int questId, String taskName) {

        String sqlQuery = "SELECT * FROM " + taskName + " WHERE QuestId=" + questId;
        ResultSet rs = QuerySQLRequest(sqlQuery);

        try {
            return rs.next();
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
