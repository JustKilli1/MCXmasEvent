package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Quest;
import net.marscraft.xmasevent.quest.Questmanager;
import net.marscraft.xmasevent.quest.task.Taskmanager;
import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;
import net.marscraft.xmasevent.quest.task.tasktype.PlaceBlockTask;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.ResultSet;
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

        Questmanager questmanager = new Questmanager(_logger, _sql, _plugin);
        Quest activePlayerQuest = questmanager.GetQuestByQuestId(questId);

        HashMap<Material, Location> blockInfo = questmanager.GetTaskManager().GetPlaceBlockTaskBlockInfo(questId);
        if(blockInfo.keySet().size() != 1){return;}

        Material blockType = null;
        Location blockLoc = null;
        for(Material key : blockInfo.keySet()) {
            blockType = key;
            blockLoc = blockInfo.get(key);
        }
        Block eventBlock = event.getBlock();
        if(blockType == eventBlock.getType()) {
            if(eventBlock.getLocation().getX() == blockLoc.getX() && eventBlock.getLocation().getY() == blockLoc.getY() && eventBlock.getLocation().getZ() == blockLoc.getZ()) {
                if(_sql.UpdateTaskPlayerBlockPlaced(player)) questmanager.FinishQuest(questId, player);
                else _logger.Error("PlaceBlockTask von Spieler " + player.getName() + " konnte nicht geupdatet werden!");
            }
        } else return;
    }
}
