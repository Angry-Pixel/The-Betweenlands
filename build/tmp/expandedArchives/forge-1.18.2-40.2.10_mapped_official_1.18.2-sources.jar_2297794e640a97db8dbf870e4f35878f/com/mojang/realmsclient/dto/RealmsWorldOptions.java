package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsWorldOptions extends ValueObject {
   public final boolean pvp;
   public final boolean spawnAnimals;
   public final boolean spawnMonsters;
   public final boolean spawnNPCs;
   public final int spawnProtection;
   public final boolean commandBlocks;
   public final boolean forceGameMode;
   public final int difficulty;
   public final int gameMode;
   @Nullable
   private final String slotName;
   public long templateId;
   @Nullable
   public String templateImage;
   public boolean empty;
   private static final boolean DEFAULT_FORCE_GAME_MODE = false;
   private static final boolean DEFAULT_PVP = true;
   private static final boolean DEFAULT_SPAWN_ANIMALS = true;
   private static final boolean DEFAULT_SPAWN_MONSTERS = true;
   private static final boolean DEFAULT_SPAWN_NPCS = true;
   private static final int DEFAULT_SPAWN_PROTECTION = 0;
   private static final boolean DEFAULT_COMMAND_BLOCKS = false;
   private static final int DEFAULT_DIFFICULTY = 2;
   private static final int DEFAULT_GAME_MODE = 0;
   private static final String DEFAULT_SLOT_NAME = "";
   private static final long DEFAULT_TEMPLATE_ID = -1L;
   private static final String DEFAULT_TEMPLATE_IMAGE = null;

   public RealmsWorldOptions(boolean p_167302_, boolean p_167303_, boolean p_167304_, boolean p_167305_, int p_167306_, boolean p_167307_, int p_167308_, int p_167309_, boolean p_167310_, @Nullable String p_167311_) {
      this.pvp = p_167302_;
      this.spawnAnimals = p_167303_;
      this.spawnMonsters = p_167304_;
      this.spawnNPCs = p_167305_;
      this.spawnProtection = p_167306_;
      this.commandBlocks = p_167307_;
      this.difficulty = p_167308_;
      this.gameMode = p_167309_;
      this.forceGameMode = p_167310_;
      this.slotName = p_167311_;
   }

   public static RealmsWorldOptions createDefaults() {
      return new RealmsWorldOptions(true, true, true, true, 0, false, 2, 0, false, "");
   }

   public static RealmsWorldOptions createEmptyDefaults() {
      RealmsWorldOptions realmsworldoptions = createDefaults();
      realmsworldoptions.setEmpty(true);
      return realmsworldoptions;
   }

   public void setEmpty(boolean p_87631_) {
      this.empty = p_87631_;
   }

   public static RealmsWorldOptions parse(JsonObject p_87629_) {
      RealmsWorldOptions realmsworldoptions = new RealmsWorldOptions(JsonUtils.getBooleanOr("pvp", p_87629_, true), JsonUtils.getBooleanOr("spawnAnimals", p_87629_, true), JsonUtils.getBooleanOr("spawnMonsters", p_87629_, true), JsonUtils.getBooleanOr("spawnNPCs", p_87629_, true), JsonUtils.getIntOr("spawnProtection", p_87629_, 0), JsonUtils.getBooleanOr("commandBlocks", p_87629_, false), JsonUtils.getIntOr("difficulty", p_87629_, 2), JsonUtils.getIntOr("gameMode", p_87629_, 0), JsonUtils.getBooleanOr("forceGameMode", p_87629_, false), JsonUtils.getStringOr("slotName", p_87629_, ""));
      realmsworldoptions.templateId = JsonUtils.getLongOr("worldTemplateId", p_87629_, -1L);
      realmsworldoptions.templateImage = JsonUtils.getStringOr("worldTemplateImage", p_87629_, DEFAULT_TEMPLATE_IMAGE);
      return realmsworldoptions;
   }

   public String getSlotName(int p_87627_) {
      if (this.slotName != null && !this.slotName.isEmpty()) {
         return this.slotName;
      } else {
         return this.empty ? I18n.get("mco.configure.world.slot.empty") : this.getDefaultSlotName(p_87627_);
      }
   }

   public String getDefaultSlotName(int p_87634_) {
      return I18n.get("mco.configure.world.slot", p_87634_);
   }

   public String toJson() {
      JsonObject jsonobject = new JsonObject();
      if (!this.pvp) {
         jsonobject.addProperty("pvp", this.pvp);
      }

      if (!this.spawnAnimals) {
         jsonobject.addProperty("spawnAnimals", this.spawnAnimals);
      }

      if (!this.spawnMonsters) {
         jsonobject.addProperty("spawnMonsters", this.spawnMonsters);
      }

      if (!this.spawnNPCs) {
         jsonobject.addProperty("spawnNPCs", this.spawnNPCs);
      }

      if (this.spawnProtection != 0) {
         jsonobject.addProperty("spawnProtection", this.spawnProtection);
      }

      if (this.commandBlocks) {
         jsonobject.addProperty("commandBlocks", this.commandBlocks);
      }

      if (this.difficulty != 2) {
         jsonobject.addProperty("difficulty", this.difficulty);
      }

      if (this.gameMode != 0) {
         jsonobject.addProperty("gameMode", this.gameMode);
      }

      if (this.forceGameMode) {
         jsonobject.addProperty("forceGameMode", this.forceGameMode);
      }

      if (!Objects.equals(this.slotName, "")) {
         jsonobject.addProperty("slotName", this.slotName);
      }

      return jsonobject.toString();
   }

   public RealmsWorldOptions clone() {
      return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
   }
}