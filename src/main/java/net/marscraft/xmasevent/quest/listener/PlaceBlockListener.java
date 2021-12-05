package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Questmanager;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import java.util.HashMap;

public class PlaceBlockListener implements Listener {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;

    public PlaceBlockListener(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        int questId = _sql.GetActivePlayerQuestId(player);
        String taskName = _sql.GetTaskNameByQuestId(questId);
        Questmanager questmanager = new Questmanager(_logger, _sql, _plugin);
        Taskmanager taskmanager = questmanager.GetTaskManager();
        ITaskType taskType = taskmanager.GetTaskTypeByName(questId, taskName);
        if(taskType == null) return;
        EventStorage eventStorage = new EventStorage();
        eventStorage.SetBlockPlaceEvent(event);
        taskType.ExecuteTask(eventStorage, player);
    }
}
