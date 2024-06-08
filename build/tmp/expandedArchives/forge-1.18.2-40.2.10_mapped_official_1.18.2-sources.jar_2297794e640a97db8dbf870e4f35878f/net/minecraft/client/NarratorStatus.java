package net.minecraft.client;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum NarratorStatus {
   OFF(0, "options.narrator.off"),
   ALL(1, "options.narrator.all"),
   CHAT(2, "options.narrator.chat"),
   SYSTEM(3, "options.narrator.system");

   private static final NarratorStatus[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(NarratorStatus::getId)).toArray((p_91623_) -> {
      return new NarratorStatus[p_91623_];
   });
   private final int id;
   private final Component name;

   private NarratorStatus(int p_91616_, String p_91617_) {
      this.id = p_91616_;
      this.name = new TranslatableComponent(p_91617_);
   }

   public int getId() {
      return this.id;
   }

   public Component getName() {
      return this.name;
   }

   public static NarratorStatus byId(int p_91620_) {
      return BY_ID[Mth.positiveModulo(p_91620_, BY_ID.length)];
   }
}