package net.minecraft.world;

import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum Difficulty {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   private static final Difficulty[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(Difficulty::getId)).toArray((p_19035_) -> {
      return new Difficulty[p_19035_];
   });
   private final int id;
   private final String key;

   private Difficulty(int p_19026_, String p_19027_) {
      this.id = p_19026_;
      this.key = p_19027_;
   }

   public int getId() {
      return this.id;
   }

   public Component getDisplayName() {
      return new TranslatableComponent("options.difficulty." + this.key);
   }

   public static Difficulty byId(int p_19030_) {
      return BY_ID[p_19030_ % BY_ID.length];
   }

   @Nullable
   public static Difficulty byName(String p_19032_) {
      for(Difficulty difficulty : values()) {
         if (difficulty.key.equals(p_19032_)) {
            return difficulty;
         }
      }

      return null;
   }

   public String getKey() {
      return this.key;
   }
}