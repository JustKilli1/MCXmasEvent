package net.marscraft.xmasevent.shared.messagemanager;

import org.bukkit.entity.Player;
import java.sql.ResultSet;

public interface IMessagemanager {
    void SendPlayerMessage(String msg);
    void SendNpcMessage(String npcName, String msg);
    void SendSyntaxErrorMessage(String syntax);
    void SendErrorMessage(String error);
    void SendQuestList(ResultSet values);
}
