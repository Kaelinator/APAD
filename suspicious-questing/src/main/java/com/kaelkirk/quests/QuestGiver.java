package com.kaelkirk.quests;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class QuestGiver {

  private net.minecraft.world.entity.Entity nmsEntity;
  private QuestData questData;

  public static QuestGiver fromEntity(Entity entity) {
    net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
    CompoundTag nbtTags = new CompoundTag();

    nmsEntity.saveWithoutId(nbtTags);

    QuestData questData = null;
    for (Tag tag : nbtTags.getList("Tags", Tag.TAG_STRING)) {
      String tagString = tag.getAsString();

      questData = QuestData.fromString(tagString);
      if (questData != null) {
        break;
      }
    }

    if (questData == null) {
      return null;
    }

    return new QuestGiver(nmsEntity, questData);
  }

  private QuestGiver(net.minecraft.world.entity.Entity nmsEntity, QuestData questData) {
    this.questData = questData;
    this.nmsEntity = nmsEntity;

    Bukkit.broadcastMessage(questData.toString());
  }

  public QuestData getQuestData() {
    return questData;
  }

  public String toString() {
    return questData.toString();
  }
}