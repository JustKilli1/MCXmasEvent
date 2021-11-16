package net.marscraft.xmasevent.quest.rewards;

import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ItemStackSerializer {

    private ILogmanager _logger;

    public ItemStackSerializer(ILogmanager logger) {
        _logger = logger;
    }

    public String ItemStackToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception ex) {
            _logger.Error("Unable to save item stacks.", ex);
            return null;
        }
    }
    public ItemStack ItemStackFromBase64(String data){
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception ex) {
            _logger.Error("Unable to decode class type.", ex);
            return null;
        }
    }
}
