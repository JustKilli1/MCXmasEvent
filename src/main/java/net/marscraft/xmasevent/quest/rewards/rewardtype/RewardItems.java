package net.marscraft.xmasevent.quest.rewards.rewardtype;

import com.google.gson.Gson;
import net.marscraft.xmasevent.Main;
import net.marscraft.xmasevent.quest.rewards.ItemStackSerializer;
import net.marscraft.xmasevent.quest.rewards.RewardState;
import net.marscraft.xmasevent.quest.rewards.Rewardmanager;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import static net.marscraft.xmasevent.quest.rewards.RewardState.*;

public class RewardItems extends Rewardmanager implements IRewardType{

    private DatabaseAccessLayer _sql;
    private Player _player;
    private int _rewardId;
    private ItemStack _reward;
    private ILogmanager _logger;

    public RewardItems(ILogmanager logger, DatabaseAccessLayer sql, Player player, int rewardId, String rewardStr) {
        super(logger, sql, player);
        _logger = logger;
        _sql = sql;
        _player = player;
        _rewardId = rewardId;
        ItemStackSerializer serializer = new ItemStackSerializer(logger);
        _reward = serializer.ItemStackFromBase64(rewardStr);
    }

    @Override
    public RewardState GivePlayerReward() {
        int neededSpace = 1;
        if(!EnoughSpaceInInventory(neededSpace)) {
            if(!_sql.AddUnclaimedPlayerReward(_rewardId, _player)) return CouldNotAddUnclaimedPlayerReward;
            return NotEnoughSpaceInInventory;
        }
        NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), "rewardId");
        ItemMeta iMeta = _reward.getItemMeta();
        if(iMeta.getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
            iMeta.getPersistentDataContainer().remove(key);
        _reward.setItemMeta(iMeta);
        _player.getInventory().addItem(_reward);
        return GIVEN;
    }
}
