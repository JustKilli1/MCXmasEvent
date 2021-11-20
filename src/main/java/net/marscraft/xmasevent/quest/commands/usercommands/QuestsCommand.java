package net.marscraft.xmasevent.quest.commands.usercommands;

import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.ItemBuilder;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;

public class QuestsCommand extends Commandmanager implements CommandExecutor {


    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Main _plugin;
    private IMessagemanager _messages;
    private ICommandType _commandType;

    public QuestsCommand(ILogmanager logger, DatabaseAccessLayer sql, Main plugin) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        _messages = new Messagemanager(_logger, player);
        if (args.length == 0) {
            int lastQuestId = _sql.GetLastQuestId();
            int inventorySize = lastQuestId <= 9 ? 2 : lastQuestId + 1;
            Inventory inv = Bukkit.createInventory(null, inventorySize * 9, "§0Quest Fortschritt");
            ResultSet rs = _sql.GetAllQuests();

            try {
                int itemPos = 0;
                while (rs.next()) {
                    int activeQuestId = _sql.GetActivePlayerQuestId(player);
                    int activeQuestOrder = _sql.GetQuestOrder(activeQuestId);
                    int questId = rs.getInt("QuestId");
                    int questOrder = rs.getInt("QuestOrder");
                    boolean questSetupFinished = rs.getBoolean("QuestSetupFinished");
                    if (questSetupFinished) {
                        if (activeQuestOrder == questOrder) {
                            inv.setItem(itemPos, new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c" + rs.getString("QuestName")).SetLore("§aAktiv").SetLocalizedName(questId + "").Build());
                        } else if (activeQuestOrder > questOrder) {
                            inv.setItem(itemPos, new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c" + rs.getString("QuestName")).SetLore("§aAbgeschlossen").SetLocalizedName(questId + "").Build());
                        } else if (activeQuestOrder < questOrder) {
                            inv.setItem(itemPos, new ItemBuilder(Material.WRITABLE_BOOK).SetDisplayname("§c?").SetLore("§aSchließe den vorherigen Quest ab").SetLocalizedName("").Build());
                        }
                        itemPos++;
                    }
                }
                player.openInventory(inv);
                return true;
            } catch (Exception ex) {
                _logger.Error(ex);
                return false;
            }
        } else if(args.length == 1 && args[0].equalsIgnoreCase("rewards")){
            ICommandType commandType = new CommandTypeQuestRewards(_logger, _sql, player);
            commandType.ExecuteCommand(args);
            return true;
        } else {
            _messages.SendPlayerMessage("Benutze /quests oder /quests rewards");
            return false;
        }
    }
}