package net.marscraft.xmasevent.quest.commands.mcxmas;

import net.marscraft.xmasevent.quest.commands.CommandState;
import net.marscraft.xmasevent.quest.commands.Commandmanager;
import net.marscraft.xmasevent.quest.commands.ICommandType;
import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;
import org.bukkit.entity.Player;

import java.sql.ResultSet;

import static net.marscraft.xmasevent.quest.commands.CommandState.CantFindQuestId;

public class CommandTypeRewards extends Commandmanager implements ICommandType {

    private ILogmanager _logger;
    private DatabaseAccessLayer _sql;
    private Player _player;

    public CommandTypeRewards(ILogmanager logger, DatabaseAccessLayer sql, Player player) {
        super(logger);
        _logger = logger;
        _sql = sql;
        _player = player;
    }

    @Override
    public CommandState ExecuteCommand(String[] args) {
        int questId = GetIntFromStr(args[1]);
        if(questId == 0) return CantFindQuestId;
        if(questId > _sql.GetLastQuestId()) return CantFindQuestId;

        ResultSet reward = _sql.GetQuestReward(questId);
        try {

        } catch (Exception ex) {
            _logger.Error(ex);
        }
        return null;
    }
}
