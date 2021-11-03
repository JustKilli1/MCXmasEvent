package net.marscraft.xmasevent.quest.task;

import net.marscraft.xmasevent.quest.task.tasktype.ITaskType;

public class Task {

    private int _taskId;

    private ITaskType _taskType;

    public Task(ITaskType taskType) {
        _taskType = taskType;

        _taskType.InitTask();
    }

    private boolean updateTaskId() {

        return false;
    }


}
