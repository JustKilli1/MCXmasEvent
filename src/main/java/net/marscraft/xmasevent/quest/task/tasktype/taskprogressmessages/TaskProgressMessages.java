package net.marscraft.xmasevent.quest.task.tasktype.taskprogressmessages;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.entity.Player;

public class TaskProgressMessages {
    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private IMessagemanager _messages;

    public TaskProgressMessages(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        _logger = logger;
        _sql = sql;
        _messages = new Messagemanager(_logger, player);
    }

    public boolean SendQuestValueIntProgressMsg(int playerValue, int questValue, String taskProgressPrefix) {
        if((playerValue / 2) == questValue) {
            _messages.SendPlayerMessage("Quest zur hälfte abgeschlossen. " + taskProgressPrefix + " " + playerValue + "/" + questValue);
            return true;
        } else return false;
    }
    public boolean SendQuestFinishedMsg() {
        _messages.SendPlayerMessage("Aufgabe erfüllt du kannst deinen Quest jetzt abgeben.");
        return true;
    }
}
