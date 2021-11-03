package net.marscraft.xmasevent.quest.messages;

import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import net.marscraft.xmasevent.shared.messagemanager.IMessagemanager;
import net.marscraft.xmasevent.shared.messagemanager.Messagemanager;
import org.bukkit.entity.Player;

public class QuestMessages {

    private IMessagemanager _messagemanager;
    private String _startingMessage, _endingMessage;
    private boolean _questFinished;
    private Player _player;
    private ILogmanager _logger;

    public QuestMessages(ILogmanager logger, String startingMessage, String endingMessage, boolean questFinished, Player player) {
        _logger = logger;
        _startingMessage = startingMessage;
        _endingMessage = endingMessage;
        _questFinished = questFinished;
        _player = player;
        _messagemanager = new Messagemanager(_logger, player);
    }

    private void sendQuestMessageToPlayer() {
        if(!_questFinished)
            _messagemanager.SendPlayerMessage(_startingMessage);
        else
            _messagemanager.SendPlayerMessage(_endingMessage);
    }

}
