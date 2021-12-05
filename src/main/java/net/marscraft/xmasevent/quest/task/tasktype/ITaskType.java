package net.marscraft.xmasevent.quest.task.tasktype;

import net.marscraft.xmasevent.quest.listener.EventStorage;
import org.bukkit.entity.Player;

public interface ITaskType {
    /*
    * Initializes Task and creates new Task
    */
    boolean CreateTask();
    /*
    * Loads Task From Database
    */
    boolean LoadTask();
    /*
    * Executes Task
    */
    boolean ExecuteTask(EventStorage eventStorage, Player player);
    /*
    * Check if the Task is finished
    */
    boolean IsTaskFinished(Player player);
    /*
    * Checks if eventStorage has the right event if not --> task not active
    */
    boolean IsTaskActive(EventStorage eventStorage);
    String GetTaskName();
    int GetTaskId();
}
