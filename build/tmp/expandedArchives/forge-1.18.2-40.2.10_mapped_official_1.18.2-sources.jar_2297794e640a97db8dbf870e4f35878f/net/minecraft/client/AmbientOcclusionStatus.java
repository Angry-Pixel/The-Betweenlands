package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum AmbientOcclusionStatus {
   OFF(0, "options.ao.off"),
   MIN(1, "options.ao.min"),
   MAX(2, "options.ao.max");

   private static final AmbientOcclusionStatus[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(AmbientOcclusionStatus::getId)).toArray((p_90491_) -> {
      return new AmbientOcclusionStatus[p_90491_];
   });
   private final int id;
   private final String key;

   private AmbientOcclusionStatus(int p_90484_, String p_90485_) {
      this.id = p_90484_;
      this.key = p_90485_;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public static AmbientOcclusionStatus byId(int p_90488_) {
      return BY_ID[Mth.positiveModulo(p_90488_, BY_ID.length)];
   }
}