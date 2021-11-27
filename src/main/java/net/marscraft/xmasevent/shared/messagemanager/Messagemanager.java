package net.marscraft.xmasevent.shared.messagemanager;

import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;
import sun.util.resources.cldr.ext.CurrencyNames_lo;

import java.sql.ResultSet;

public class Messagemanager implements IMessagemanager{

    private final String _prefix = "§6§lMarsCraftXmasEvent §7§l> §a";
    private final Player _player;
    private final ILogmanager _logger;

    public Messagemanager(ILogmanager logger, Player player) {
        _logger = logger;
        _player = player;
    }

    @Override
    public void SendPlayerMessage(String msg) {
        _player.sendMessage(_prefix + msg);
    }

    @Override
    public void SendSyntaxErrorMessage(String syntax) { _player.sendMessage(_prefix + "§cSyntax Error use: " + syntax); }

    @Override
    public void SendErrorMessage(String command) { _player.sendMessage(_prefix + "§c" + command + " §acould not be executed!");}

    @Override
    public void SendQuestList(ResultSet values) {

        ResultSet rs = values;
        _player.sendMessage(_prefix + "Erstellte Quests");
        try {
            while(rs.next()){
                int questId = rs.getInt("QuestId");
                int questOrder = rs.getInt("QuestOrder");
                String questName = rs.getString("QuestName");
                String taskName = rs.getString("TaskName");
                String startingMessage = rs.getString("StartingMessage");
                String endMessage = rs.getString("EndMessage");
                boolean SetupFinished = rs.getBoolean("QuestSetupFinished");
                _player.sendMessage(questId + ". " + questName + ": ");
                _player.sendMessage(" - Quest Reihenfolge: " + questOrder);
                _player.sendMessage(" - Aufgabenname: " + taskName);
                _player.sendMessage(" - Anfangs Nachricht: " + startingMessage);
                _player.sendMessage(" - Beendigungs Nachricht: " + endMessage);
            }

        } catch (Exception ex) {
            _logger.Error(ex);
        }




    }


}
