package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public class ReplaceSphereConfiguration implements FeatureConfiguration {
   public static final Codec<ReplaceSphereConfiguration> CODEC = RecordCodecBuilder.create((p_68048_) -> {
      return p_68048_.group(BlockState.CODEC.fieldOf("target").forGetter((p_161100_) -> {
         return p_161100_.targetState;
      }), BlockState.CODEC.fieldOf("state").forGetter((p_161098_) -> {
         return p_161098_.replaceState;
      }), IntProvider.codec(0, 12).fieldOf("radius").forGetter((p_161095_) -> {
         return p_161095_.radius;
      })).apply(p_68048_, ReplaceSphereConfiguration::new);
   });
   public final BlockState targetState;
   public final BlockState replaceState;
   private final IntProvider radius;

   public ReplaceSphereConfiguration(BlockState p_161091_, BlockState p_161092_, IntProvider p_161093_) {
      this.targetState = p_161091_;
      this.replaceState = p_161092_;
      this.radius = p_161093_;
   }

   public IntProvider radius() {
      return this.radius;
   }
}