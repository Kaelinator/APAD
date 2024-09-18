package com.kaelkirk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.gson.Gson;
import com.kaelkirk.quests.QuestData;
import com.kaelkirk.quests.QuestType;

public class QuestDataTest {

  @Test
  public void fromStringReturnsNullWithoutQuestType() { 
    String json = "{}";
    QuestData questData = new Gson().fromJson(json, QuestData.class);
    assertNull(questData.getType());
    assertEquals(questData.getType(), QuestType.GATHER_ITEM);
  }

  @Test
  public void fromStringReturnsFromParsedString() {
    String json = "{type:'GATHER_ITEM'}";
    QuestData questData = new Gson().fromJson(json, QuestData.class);
    assertEquals(questData.getType(), QuestType.GATHER_ITEM);
  }
}
