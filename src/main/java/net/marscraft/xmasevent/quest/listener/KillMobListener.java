package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.quest.rewards.request.RewardRequestmanager;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.quest.task.tasktype.KillMobsTask;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.ResultSet;
import java.util.Locale;


public class KillMobListener implements Listener {

    private ILogmanager _logger;
    private  DatabaseAccessLayer _sql;
    private IMessagemanager _messageManager;
    private ITaskType _taskType;

    public KillMobListener(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if(event.getEntity().getKiller() instanceof Player) {
            Player player = (Player) event.getEntity().getKiller();
            _messageManager = new Messagemanager(_logger, player);
            int questId = _sql.GetActivePlayerQuestId(player);
            int playerMobKills = _sql.GetPlayerQuestValueInt(player);

            ResultSet task = _sql.GetTaskByQuestId("KillMobsTask", questId);

            try {
                if(!task.next()) return;
                String mobType = task.getString("MobType").toUpperCase();
                if(event.getEntityType() == EntityType.valueOf(mobType)){
                    _taskType = new KillMobsTask(_logger, _sql, questId, playerMobKills + 1, mobType);
                    if(_taskType.IsTaskFinished()) {
                        String rewardString = _sql.GetRewardCommand(questId);
                        RewardRequestmanager requestmanager = new RewardRequestmanager(_logger, _sql, player);
                        requestmanager.ExecuteReward(rewardString);
                    }
                    _sql.AddPlayerMobKill(player, questId);
                }
            } catch (Exception ex) {
                _logger.Error(ex);
            }
        }
    }
}
