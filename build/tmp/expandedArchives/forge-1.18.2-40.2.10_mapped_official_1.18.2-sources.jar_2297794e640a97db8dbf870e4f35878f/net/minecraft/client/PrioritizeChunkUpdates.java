package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum PrioritizeChunkUpdates {
   NONE(0, "options.prioritizeChunkUpdates.none"),
   PLAYER_AFFECTED(1, "options.prioritizeChunkUpdates.byPlayer"),
   NEARBY(2, "options.prioritizeChunkUpdates.nearby");

   private static final PrioritizeChunkUpdates[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(PrioritizeChunkUpdates::getId)).toArray((p_193791_) -> {
      return new PrioritizeChunkUpdates[p_193791_];
   });
   private final int id;
   private final String key;

   private PrioritizeChunkUpdates(int p_193784_, String p_193785_) {
      this.id = p_193784_;
      this.key = p_193785_;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static PrioritizeChunkUpdates byId(int p_193788_) {
      return BY_ID[Mth.positiveModulo(p_193788_, BY_ID.length)];
   }
}