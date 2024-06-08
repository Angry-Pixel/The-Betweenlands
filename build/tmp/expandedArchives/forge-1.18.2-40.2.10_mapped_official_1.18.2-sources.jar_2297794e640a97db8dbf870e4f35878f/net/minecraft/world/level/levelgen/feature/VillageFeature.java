package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

public class VillageFeature extends JigsawFeature {
   public VillageFeature(Codec<JigsawConfiguration> p_67333_) {
      super(p_67333_, 0, true, true, (p_197185_) -> {
         return true;
      });
   }
}