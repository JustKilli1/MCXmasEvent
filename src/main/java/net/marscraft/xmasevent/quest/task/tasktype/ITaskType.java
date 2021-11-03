package net.marscraft.xmasevent.quest.task.tasktype;

import org.bukkit.entity.Player;

public interface ITaskType {
    /*
    * Initializes Task and creates new Task
    * */
    public boolean InitTask();

    /*
    * Check if the Task is finished
    * */
    public boolean IsTaskFinished();

    public String GetTaskName();

    public int GetTaskId();

}
