package com.kaelkirk.quests;

public enum QuestType {
  GATHER_ITEM,
  KILL_ENTITY,
  CONSTRUCT_SHRINE,
  INTERACT_WITH,
  REACH_LOCATION;

  public static QuestType fromString(String string) {
    try {
      QuestType result = QuestType.valueOf(string);
      return result;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
