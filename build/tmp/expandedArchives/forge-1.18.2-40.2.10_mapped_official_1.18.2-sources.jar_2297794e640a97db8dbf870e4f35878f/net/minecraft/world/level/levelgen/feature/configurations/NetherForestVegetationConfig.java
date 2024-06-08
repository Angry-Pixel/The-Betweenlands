package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class NetherForestVegetationConfig extends BlockPileConfiguration {
   public static final Codec<NetherForestVegetationConfig> CODEC = RecordCodecBuilder.create((p_191267_) -> {
      return p_191267_.group(BlockStateProvider.CODEC.fieldOf("state_provider").forGetter((p_191273_) -> {
         return p_191273_.stateProvider;
      }), ExtraCodecs.POSITIVE_INT.fieldOf("spread_width").forGetter((p_191271_) -> {
         return p_191271_.spreadWidth;
      }), ExtraCodecs.POSITIVE_INT.fieldOf("spread_height").forGetter((p_191269_) -> {
         return p_191269_.spreadHeight;
      })).apply(p_191267_, NetherForestVegetationConfig::new);
   });
   public final int spreadWidth;
   public final int spreadHeight;

   public NetherForestVegetationConfig(BlockStateProvider p_191263_, int p_191264_, int p_191265_) {
      super(p_191263_);
      this.spreadWidth = p_191264_;
      this.spreadHeight = p_191265_;
   }
}