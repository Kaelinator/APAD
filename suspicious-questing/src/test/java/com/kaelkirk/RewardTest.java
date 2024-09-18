package com.kaelkirk;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;
import com.kaelkirk.rewards.Reward;
import com.kaelkirk.rewards.RewardType;

public class RewardTest {
  
  @Test
  public void deserializesToXPReward() {
    String json = "{type:'XP', amount:5}";
    Reward reward = new Gson().fromJson(json, Reward.class);
    assertEquals(reward.getAmount(), 5);
    assertEquals(reward.getType(), RewardType.XP);
  }
}
