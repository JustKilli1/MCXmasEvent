package net.marscraft.xmasevent.quest.commands.usercommands;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.Inventorys.InventoryHandler;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import static net.marscraft.xmasevent.quest.commands.CommandState.*;

public class CommandTypeQuestRewards extends Commandmanager implements ICommandType {

    private final ILogmanager _logger;
    private final DatabaseAccessLayer _sql;
    private final Player _player;

    public CommandTypeQuestRewards(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _player = player;
    }
    /*
    * Command: /quest rewards
    * Opens Unclaimed Rewards Inventory
    */
    @Override
    public CommandState ExecuteCommand(String[] args) {
        InventoryHandler invHandler = new InventoryHandler(_logger, _sql, _player);
        Inventory rewardInv = invHandler.CreateUnclaimedRewardsInventory();

        _player.openInventory(rewardInv);
        return UnclaimedRewardsOpened;
    }
}
