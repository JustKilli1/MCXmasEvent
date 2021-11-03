package net.marscraft.xmasevent.quest.listener;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.ResultSet;

public class PlaceBlockListener implements Listener {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public PlaceBlockListener(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent event) {

        Player player = event.getPlayer();

        int questId = _sql.GetActivePlayerQuestId(player);

        ResultSet task = _sql.GetTaskByQuestId("PlaceBlockTask", questId);

        try {
            if(!task.next()) return;

            Material blockType = Material.valueOf(task.getString("BlockType").toUpperCase());
            Block eventBlock = event.getBlock();
            if(blockType == eventBlock.getType()) {

                double blockPosX = task.getInt("BlockPositionX");
                double blockPosY = task.getInt("BlockPositionY");
                double blockPosZ = task.getInt("BlockPositionZ");

                if(eventBlock.getLocation().getX() == blockPosX && eventBlock.getLocation().getY() == blockPosY && eventBlock.getLocation().getZ() == blockPosZ) {
                    if(!(_sql.UpdateTaskPlayerBlockPlaced(player))) _logger.Error("PlaceBlockTask von Spieler " + player.getName() + " konnte nicht geupdatet werden!");
                }
            } else return;

        } catch (Exception ex) {
            _logger.Error(ex);
        }

    }
}
