package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class DiskReplaceFeature extends BaseDiskFeature {
   public DiskReplaceFeature(Codec<DiskConfiguration> p_65613_) {
      super(p_65613_);
   }

   public boolean place(FeaturePlaceContext<DiskConfiguration> p_159573_) {
      return !p_159573_.level().getFluidState(p_159573_.origin()).is(FluidTags.WATER) ? false : super.place(p_159573_);
   }
}