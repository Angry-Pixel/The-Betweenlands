package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;

public class EndGatewayConfiguration implements FeatureConfiguration {
   public static final Codec<EndGatewayConfiguration> CODEC = RecordCodecBuilder.create((p_67649_) -> {
      return p_67649_.group(BlockPos.CODEC.optionalFieldOf("exit").forGetter((p_160810_) -> {
         return p_160810_.exit;
      }), Codec.BOOL.fieldOf("exact").forGetter((p_160808_) -> {
         return p_160808_.exact;
      })).apply(p_67649_, EndGatewayConfiguration::new);
   });
   private final Optional<BlockPos> exit;
   private final boolean exact;

   private EndGatewayConfiguration(Optional<BlockPos> p_67644_, boolean p_67645_) {
      this.exit = p_67644_;
      this.exact = p_67645_;
   }

   public static EndGatewayConfiguration knownExit(BlockPos p_67651_, boolean p_67652_) {
      return new EndGatewayConfiguration(Optional.of(p_67651_), p_67652_);
   }

   public static EndGatewayConfiguration delayedExitSearch() {
      return new EndGatewayConfiguration(Optional.empty(), false);
   }

   public Optional<BlockPos> getExit() {
      return this.exit;
   }

   public boolean isExitExact() {
      return this.exact;
   }
}