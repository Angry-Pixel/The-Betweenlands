package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FluidState;

public class SpringConfiguration implements FeatureConfiguration {
   public static final Codec<SpringConfiguration> CODEC = RecordCodecBuilder.create((p_68139_) -> {
      return p_68139_.group(FluidState.CODEC.fieldOf("state").forGetter((p_161205_) -> {
         return p_161205_.state;
      }), Codec.BOOL.fieldOf("requires_block_below").orElse(true).forGetter((p_161203_) -> {
         return p_161203_.requiresBlockBelow;
      }), Codec.INT.fieldOf("rock_count").orElse(4).forGetter((p_161201_) -> {
         return p_161201_.rockCount;
      }), Codec.INT.fieldOf("hole_count").orElse(1).forGetter((p_161199_) -> {
         return p_161199_.holeCount;
      }), RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).fieldOf("valid_blocks").forGetter((p_204854_) -> {
         return p_204854_.validBlocks;
      })).apply(p_68139_, SpringConfiguration::new);
   });
   public final FluidState state;
   public final boolean requiresBlockBelow;
   public final int rockCount;
   public final int holeCount;
   public final HolderSet<Block> validBlocks;

   public SpringConfiguration(FluidState p_204848_, boolean p_204849_, int p_204850_, int p_204851_, HolderSet<Block> p_204852_) {
      this.state = p_204848_;
      this.requiresBlockBelow = p_204849_;
      this.rockCount = p_204850_;
      this.holeCount = p_204851_;
      this.validBlocks = p_204852_;
   }
}