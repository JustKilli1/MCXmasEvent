package net.marscraft.xmasevent.quest.commands.usercommands;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.sql.ResultSet;
import java.util.ArrayList;

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

    @Override
    public CommandState ExecuteCommand(String[] args) {
        ArrayList<Integer> rewardIds = _sql.GetUnclaimedPlayerRewardIds(_player);
        if(rewardIds == null) return NoUnclaimedRewardsFound;
        int inventorySize = (rewardIds.size() / 9) + 1;
        Inventory inv = Bukkit.createInventory(null, inventorySize * 9, "§0Quest Belohnungen");

        for(int i = 0; i < rewardIds.size(); i++) {
            ResultSet rs = _sql.GetQuestRewardByRewardId(rewardIds.get(i));
            try {
                if(!rs.next()) return NoUnclaimedRewardsFound;
                String rewardStr = rs.getString("Reward");
                ItemStackSerializer serializer = new ItemStackSerializer(_logger);
                ItemStack reward = serializer.ItemStackFromBase64(rewardStr);
                ItemMeta rewardMeta = reward.getItemMeta();
                rewardMeta.setLocalizedName(rewardIds.get(i) + "");
                reward.setItemMeta(rewardMeta);
                inv.setItem(i, reward);
            } catch (Exception ex) {
                _logger.Error(ex);
                return FAILED;
            }
        }
        _player.openInventory(inv);
        return UnclaimedRewardsOpened;
    }
}
