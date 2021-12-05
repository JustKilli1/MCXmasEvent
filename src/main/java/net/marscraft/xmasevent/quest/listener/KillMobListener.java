package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Quest;
import net.marscraft.xmasevent.quest.Questmanager;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;


public class KillMobListener implements Listener {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public KillMobListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if(!(event.getEntity().getKiller() instanceof Player)) return;

        Player player = event.getEntity().getKiller();
        int questId = _sql.GetActivePlayerQuestId(player);
        String taskName = _sql.GetTaskNameByQuestId(questId);
        Questmanager questmanager = new Questmanager(_logger, _sql, _plugin);
        Taskmanager taskmanager = questmanager.GetTaskManager();
        Quest activePlayerQuest = questmanager.GetQuestByQuestId(questId);
        if(activePlayerQuest == null) return;

        ITaskType taskType = taskmanager.GetTaskTypeByName(questId, taskName);
        if(taskType == null) return;
        EventStorage eventStorage = new EventStorage();
        eventStorage.SetEntityDeathEvent(event);
        taskType.ExecuteTask(eventStorage, player);
    }
}
