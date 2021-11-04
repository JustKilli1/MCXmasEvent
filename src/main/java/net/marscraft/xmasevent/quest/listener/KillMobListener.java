package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Quest;
import net.marscraft.xmasevent.quest.Questmanager;
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
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private IMessagemanager _messageManager;
    private ITaskType _taskType;
    private Questmanager _questManager;

    public KillMobListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if(event.getEntity().getKiller() instanceof Player) {
            Player player = (Player) event.getEntity().getKiller();
            _messageManager = new Messagemanager(_logger, player);
            int questId = _sql.GetActivePlayerQuestId(player);

            Questmanager questmanager = new Questmanager(_logger, _sql, _plugin);
            Quest activePlayerQuest = questmanager.GetQuestByQuestId(questId);

                if(event.getEntityType() == EntityType.valueOf(questmanager.GetTaskManager().GetKillMobsTaskMobType(questId))){
                    if(activePlayerQuest.GetTaskType().IsTaskFinished()) questmanager.FinishQuest(questId, player);
                    else _sql.AddPlayerMobKill(player, questId);
                }
        }
    }
}
