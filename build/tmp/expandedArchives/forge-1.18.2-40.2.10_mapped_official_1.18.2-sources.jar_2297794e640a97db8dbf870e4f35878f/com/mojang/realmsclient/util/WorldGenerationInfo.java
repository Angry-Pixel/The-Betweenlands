package com.mojang.realmsclient.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldGenerationInfo {
   private final String seed;
   private final LevelType levelType;
   private final boolean generateStructures;

   public WorldGenerationInfo(String p_167631_, LevelType p_167632_, boolean p_167633_) {
      this.seed = p_167631_;
      this.levelType = p_167632_;
      this.generateStructures = p_167633_;
   }

   public String getSeed() {
      return this.seed;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }

   public boolean shouldGenerateStructures() {
      return this.generateStructures;
   }
}