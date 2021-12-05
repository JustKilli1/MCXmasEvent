package net.marscraft.xmasevent.quest.messages;

import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.entity.Player;

public class QuestMessages {

    private IMessagemanager _messagemanager;
    private String _startMessage;
    private String _endMessage;
    private boolean _questFinished;
    private Player _player;
    private ILogmanager _logger;

    public QuestMessages(ILogmanager logger, String startMessage, String endMessage, Player player) {
        _logger = logger;
        _startMessage = startMessage;
        _endMessage = endMessage;
        _player = player;
        _messagemanager = new Messagemanager(_logger, player);
    }

    public boolean SendQuestStartMessage() {
        _messagemanager.SendPlayerMessage(_startMessage);
        return true;
    }
    public boolean SendQuestEndMessage() {
        _messagemanager.SendPlayerMessage(_endMessage);
        return true;
    }
}
