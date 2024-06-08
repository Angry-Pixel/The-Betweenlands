package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;

public class BastionFeature extends JigsawFeature {
   private static final int BASTION_SPAWN_HEIGHT = 33;

   public BastionFeature(Codec<JigsawConfiguration> p_65226_) {
      super(p_65226_, 33, false, false, (p_209741_) -> {
         return true;
      });
   }
}