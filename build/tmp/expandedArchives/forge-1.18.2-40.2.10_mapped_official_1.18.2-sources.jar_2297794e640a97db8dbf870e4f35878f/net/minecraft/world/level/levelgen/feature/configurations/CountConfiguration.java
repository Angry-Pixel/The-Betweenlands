package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class CountConfiguration implements FeatureConfiguration {
   public static final Codec<CountConfiguration> CODEC = IntProvider.codec(0, 256).fieldOf("count").xmap(CountConfiguration::new, CountConfiguration::count).codec();
   private final IntProvider count;

   public CountConfiguration(int p_67572_) {
      this.count = ConstantInt.of(p_67572_);
   }

   public CountConfiguration(IntProvider p_160724_) {
      this.count = p_160724_;
   }

   public IntProvider count() {
      return this.count;
   }
}