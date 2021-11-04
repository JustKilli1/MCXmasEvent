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
    CantFindQuestId,
    InvalidTaskName,
    InvalidBlock,
    InvalidEntityType,
    RewardSet,
    StartingMessageSet,
    EndMessageSet
}
