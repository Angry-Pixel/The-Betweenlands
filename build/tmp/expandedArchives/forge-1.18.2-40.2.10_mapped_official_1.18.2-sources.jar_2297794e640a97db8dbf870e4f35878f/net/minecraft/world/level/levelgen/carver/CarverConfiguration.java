package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CarverConfiguration extends ProbabilityFeatureConfiguration {
   public static final MapCodec<CarverConfiguration> CODEC = RecordCodecBuilder.mapCodec((p_190635_) -> {
      return p_190635_.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((p_159113_) -> {
         return p_159113_.probability;
      }), HeightProvider.CODEC.fieldOf("y").forGetter((p_159111_) -> {
         return p_159111_.y;
      }), FloatProvider.CODEC.fieldOf("yScale").forGetter((p_159109_) -> {
         return p_159109_.yScale;
      }), VerticalAnchor.CODEC.fieldOf("lava_level").forGetter((p_159107_) -> {
         return p_159107_.lavaLevel;
      }), CarverDebugSettings.CODEC.optionalFieldOf("debug_settings", CarverDebugSettings.DEFAULT).forGetter((p_190637_) -> {
         return p_190637_.debugSettings;
      })).apply(p_190635_, CarverConfiguration::new);
   });
   public final HeightProvider y;
   public final FloatProvider yScale;
   public final VerticalAnchor lavaLevel;
   public final CarverDebugSettings debugSettings;

   public CarverConfiguration(float p_190629_, HeightProvider p_190630_, FloatProvider p_190631_, VerticalAnchor p_190632_, CarverDebugSettings p_190633_) {
      super(p_190629_);
      this.y = p_190630_;
      this.yScale = p_190631_;
      this.lavaLevel = p_190632_;
      this.debugSettings = p_190633_;
   }
}