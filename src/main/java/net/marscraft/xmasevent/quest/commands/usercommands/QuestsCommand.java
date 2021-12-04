package net.marscraft.xmasevent.quest.commands.usercommands;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.Inventorys.IInventoryType;
import net.marscraft.xmasevent.shared.Inventorys.InvPlayerQuests;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static net.marscraft.xmasevent.quest.commands.CommandState.NoUnclaimedRewardsFound;

public class QuestsCommand extends Commandmanager implements CommandExecutor {


    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private IMessagemanager _messages;

    public QuestsCommand(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }
    /*
     * Command: /quests [args]
     * Handles /quests Command
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        _messages = new Messagemanager(_logger, player);
        IInventoryType inventoryType = new InvPlayerQuests(_logger, _sql);

        if (args.length == 0) {
            inventoryType.OpenInventory(player, 0);
        } else if(args.length == 1 && args[0].equalsIgnoreCase("rewards")){
            ICommandType commandType = new CommandTypeQuestRewards(_logger, _sql, player);
            if(commandType.ExecuteCommand(args) == NoUnclaimedRewardsFound) _messages.SendPlayerMessage("Keine Quest Belohnungen zum abholen gefunden");
            return true;
        } else {
            _messages.SendPlayerMessage("Benutze /quests oder /quests rewards");
            return false;
        }
        return false;
    }
}