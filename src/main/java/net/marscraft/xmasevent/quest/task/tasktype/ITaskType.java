package net.marscraft.xmasevent.quest.task.tasktype;

import org.bukkit.entity.Player;

public interface ITaskType {
    /*
    * Initializes Task and creates new Task
    * */
    boolean InitTask();

    /*
    * Check if the Task is finished
    * */
    boolean IsTaskFinished(Player player);

    String GetTaskName();

    int GetTaskId();

}
