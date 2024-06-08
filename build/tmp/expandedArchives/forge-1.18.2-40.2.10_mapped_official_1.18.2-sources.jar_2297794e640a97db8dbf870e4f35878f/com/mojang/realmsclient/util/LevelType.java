package com.mojang.realmsclient.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum LevelType {
   DEFAULT(0, new TranslatableComponent("generator.default")),
   FLAT(1, new TranslatableComponent("generator.flat")),
   LARGE_BIOMES(2, new TranslatableComponent("generator.large_biomes")),
   AMPLIFIED(3, new TranslatableComponent("generator.amplified"));

   private final int index;
   private final Component name;

   private LevelType(int p_167605_, Component p_167606_) {
      this.index = p_167605_;
      this.name = p_167606_;
   }

   public Component getName() {
      return this.name;
   }

   public int getDtoIndex() {
      return this.index;
   }
}