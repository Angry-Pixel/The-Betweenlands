package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public class DeltaFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<DeltaFeatureConfiguration> CODEC = RecordCodecBuilder.create((p_67607_) -> {
      return p_67607_.group(BlockState.CODEC.fieldOf("contents").forGetter((p_160743_) -> {
         return p_160743_.contents;
      }), BlockState.CODEC.fieldOf("rim").forGetter((p_160740_) -> {
         return p_160740_.rim;
      }), IntProvider.codec(0, 16).fieldOf("size").forGetter((p_160738_) -> {
         return p_160738_.size;
      }), IntProvider.codec(0, 16).fieldOf("rim_size").forGetter((p_160736_) -> {
         return p_160736_.rimSize;
      })).apply(p_67607_, DeltaFeatureConfiguration::new);
   });
   private final BlockState contents;
   private final BlockState rim;
   private final IntProvider size;
   private final IntProvider rimSize;

   public DeltaFeatureConfiguration(BlockState p_160731_, BlockState p_160732_, IntProvider p_160733_, IntProvider p_160734_) {
      this.contents = p_160731_;
      this.rim = p_160732_;
      this.size = p_160733_;
      this.rimSize = p_160734_;
   }

   public BlockState contents() {
      return this.contents;
   }

   public BlockState rim() {
      return this.rim;
   }

   public IntProvider size() {
      return this.size;
   }

   public IntProvider rimSize() {
      return this.rimSize;
   }
}