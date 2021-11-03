package net.marscraft.xmasevent.quest.rewards.rewardtype;

import net.marscraft.xmasevent.quest.rewards.RewardState;
import net.marscraft.xmasevent.quest.rewards.Rewardmanager;
import net.marscraft.xmasevent.quest.rewards.request.Requestmanager;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class RewardItems extends Rewardmanager implements IRewardType{

    private ILogmanager _logger;
    private Player _player;
    private HashMap<ItemStack, Integer> _rewards;

    public RewardItems (ILogmanager logger, Player player, HashMap<String, String> commandOptions) {
        super(logger, player);
        _logger = logger;
        _player = player;
        _player.sendMessage(commandOptions.toString());//TODO DEBUG
        if(!setRewardItemsByCommand(commandOptions)) _player.sendMessage("§cEs ist ein Fehler bei der Vergabe der Belohnung aufgetreten. Bitte kontaktiere ein Teammitglied.");
    }
    //NormalItem=Diamond_Sword 1
    private boolean setRewardItemsByCommand(HashMap<String, String> commandOptions) {

        _rewards = new HashMap<>();

        for(String key : commandOptions.keySet()) {
            if(key.equalsIgnoreCase("normalitem")){
                Requestmanager rm = new Requestmanager(_logger);
                ArrayList<String> rewardOptionValues = rm.SplitStringByChar(commandOptions.get(key), ' ');
                if(rewardOptionValues.size() == 2 && IsValidItem(rewardOptionValues.get(0))){

                    Material material = Material.valueOf(rewardOptionValues.get(0).toUpperCase());

                    ItemStack iStack = new ItemStack(material);
                    ItemMeta iMeta = iStack.getItemMeta();

                    ArrayList<String> lore = new ArrayList<>();
                    lore.add("§b§lEventItem");

                    iMeta.setLore(lore);
                    iStack.setItemMeta(iMeta);

                    int itemCount = GetIntFromString(rewardOptionValues.get(1));
                    if(itemCount == 0)return false;
                    _rewards.put(iStack, itemCount);
                } else return false;
            } else return false;
        }
        return true;
    }

    @Override
    public RewardState GivePlayerReward() {
        if(!EnoughSpaceInInventory(_rewards.size())) return RewardState.NotEnoughSpaceInInventory;
        for(ItemStack reward : _rewards.keySet()) {
            for(int i = 0; i <= _rewards.get(reward); i++) {
                _player.getInventory().addItem(reward);
            }
        }
        return RewardState.GIVEN;
    }
}
