package net.marscraft.xmasevent.shared.messagemanager;

import org.bukkit.entity.Player;

import java.sql.ResultSet;

public interface IMessagemanager {

    public void SendPlayerMessage(String msg);

    public void SendSyntaxErrorMessage(String syntax);

    public void SendErrorMessage(String error);

    public void SendQuestList(ResultSet values);

}
