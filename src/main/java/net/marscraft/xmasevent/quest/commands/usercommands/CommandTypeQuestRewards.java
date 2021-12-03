package net.marscraft.xmasevent.quest.commands.usercommands;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.Inventorys.IInventoryType;
import net.marscraft.xmasevent.shared.Inventorys.InvUnclaimedReward;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import static net.marscraft.xmasevent.quest.commands.CommandState.*;

public class CommandTypeQuestRewards extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Player _player;

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
        IInventoryType invType = new InvUnclaimedReward(_logger, _sql);
        invType.OpenInventory(_player);
        return UnclaimedRewardsOpened;
    }
}
