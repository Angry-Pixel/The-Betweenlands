package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Abilities;

public enum GameType {
   SURVIVAL(0, "survival"),
   CREATIVE(1, "creative"),
   ADVENTURE(2, "adventure"),
   SPECTATOR(3, "spectator");

   public static final GameType DEFAULT_MODE = SURVIVAL;
   private static final int NOT_SET = -1;
   private final int id;
   private final String name;
   private final Component shortName;
   private final Component longName;

   private GameType(int p_46390_, String p_46391_) {
      this.id = p_46390_;
      this.name = p_46391_;
      this.shortName = new TranslatableComponent("selectWorld.gameMode." + p_46391_);
      this.longName = new TranslatableComponent("gameMode." + p_46391_);
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public Component getLongDisplayName() {
      return this.longName;
   }

   public Component getShortDisplayName() {
      return this.shortName;
   }

   public void updatePlayerAbilities(Abilities p_46399_) {
      if (this == CREATIVE) {
         p_46399_.mayfly = true;
         p_46399_.instabuild = true;
         p_46399_.invulnerable = true;
      } else if (this == SPECTATOR) {
         p_46399_.mayfly = true;
         p_46399_.instabuild = false;
         p_46399_.invulnerable = true;
         p_46399_.flying = true;
      } else {
         p_46399_.mayfly = false;
         p_46399_.instabuild = false;
         p_46399_.invulnerable = false;
         p_46399_.flying = false;
      }

      p_46399_.mayBuild = !this.isBlockPlacingRestricted();
   }

   public boolean isBlockPlacingRestricted() {
      return this == ADVENTURE || this == SPECTATOR;
   }

   public boolean isCreative() {
      return this == CREATIVE;
   }

   public boolean isSurvival() {
      return this == SURVIVAL || this == ADVENTURE;
   }

   public static GameType byId(int p_46394_) {
      return byId(p_46394_, DEFAULT_MODE);
   }

   public static GameType byId(int p_46396_, GameType p_46397_) {
      for(GameType gametype : values()) {
         if (gametype.id == p_46396_) {
            return gametype;
         }
      }

      return p_46397_;
   }

   public static GameType byName(String p_46401_) {
      return byName(p_46401_, SURVIVAL);
   }

   public static GameType byName(String p_46403_, GameType p_46404_) {
      for(GameType gametype : values()) {
         if (gametype.name.equals(p_46403_)) {
            return gametype;
         }
      }

      return p_46404_;
   }

   public static int getNullableId(@Nullable GameType p_151496_) {
      return p_151496_ != null ? p_151496_.id : -1;
   }

   @Nullable
   public static GameType byNullableId(int p_151498_) {
      return p_151498_ == -1 ? null : byId(p_151498_);
   }
}