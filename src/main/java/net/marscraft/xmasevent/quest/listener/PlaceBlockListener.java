package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.Questmanager;
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

        Questmanager questmanager = new Questmanager(_logger, _sql, _plugin);
        if(!questmanager.GetTaskManager().IsTaskActive("PlaceBlockTask", questId)) return;
        HashMap<Material, Location> blockInfo = questmanager.GetTaskManager().GetPlaceBlockTaskBlockInfo(questId);
        if(blockInfo == null)return;
        if(blockInfo.keySet().size() != 1)return;

        Material blockType = null;
        Location blockLoc = null;
        for(Material key : blockInfo.keySet()) {
            blockType = key;
            blockLoc = blockInfo.get(key);

        }
        Block eventBlock = event.getBlock();
        if(blockType == eventBlock.getType()) {
            if(eventBlock.getWorld() == blockLoc.getWorld() && (int)eventBlock.getLocation().getX() == (int)blockLoc.getX() && (int)eventBlock.getLocation().getY() == (int)blockLoc.getY() && (int)eventBlock.getLocation().getZ() == (int)blockLoc.getZ()) {
                if(_sql.UpdateTaskPlayerBlockPlaced(player)) return;
                else _logger.Error("PlaceBlockTask von Spieler " + player.getName() + " konnte nicht geupdatet werden!");
            }
        } else return;
    }
}
