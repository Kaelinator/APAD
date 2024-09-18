package com.kaelkirk.quests;

import com.kaelkirk.rewards.Reward;

public class QuestData {

  private QuestType type;
  private Reward reward;

  private QuestData(QuestType type, Reward reward) {
    this.type = type;
    this.reward = reward;
  }

  public QuestType getType() {
    return type;
  }

  public boolean isValid() {
    return type != null;
  }
}
