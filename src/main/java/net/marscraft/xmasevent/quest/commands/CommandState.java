package net.marscraft.xmasevent.quest.commands;

public enum CommandState {
    SUCCESS,
    FAILED,
    QuestCreated,
    QuestAlreadyExists,
    QuestIdWrongFormat,
    CommandSyntaxError,
    CommandSyntaxErrorCreate,
    CommandSyntaxErrorEdit,
    CommandSyntaxErrorQuests,
    CommandSyntaxErrorDelete,
    CantFindQuestId,
    InvalidTaskName,
    InvalidBlock,
    InvalidEntityType,
    InvalidEntityAmount,
    RewardSet,
    CouldNotSetReward,
    StartingMessageSet,
    EndMessageSet,
    QuestOrderSet,
    CouldNotDeleteQuestFromQuestTable,
    CouldNotDeleteTask,
    CouldNotUpdateQuestOrder,
    CouldNotUpdateQuestIds
}
