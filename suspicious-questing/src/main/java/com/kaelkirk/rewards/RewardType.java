package com.kaelkirk.rewards;

public enum RewardType {
  XP,
  LEVEL,
  ITEM,
  EFFECT,
  ATTRIBUTE;

  public static RewardType fromString(String string) {
    try {
      RewardType result = RewardType.valueOf(string);
      return result;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }
}
