package net.marscraft.xmasevent.quest;

import net.marscraft.xmasevent.shared.database.DatabaseAccessLayer;
import net.marscraft.xmasevent.shared.logmanager.ILogmanager;

public class Questmanager {

    private  ILogmanager _logger;
    private DatabaseAccessLayer _sql;

    public Questmanager(ILogmanager logger, DatabaseAccessLayer sql) {
        _logger = logger;
        _sql = sql;
    }

    public boolean CreateNewQuest(String questName, String taskName) {
        Quest quest = new Quest(_logger, _sql, _sql.GetLastQuestId() + 1, questName);

        if(_sql.QuestExists(questName)) return false;

        return _sql.AddNewQuestToDatabase(quest, taskName);
    }

    /*
     * Updates QuestId From Player in PlayerProgressDatabase TODO --> Anhand QuestId wird Task ermittelt
     * */
    public boolean UpdatePlayerQuestId() {

        return false;
    }

    /*
     * Searches for the Next Quest based on QuestOrder
     * */
    private int findNextQuest(int currentQuestId) {

        return 0;
    }

}
