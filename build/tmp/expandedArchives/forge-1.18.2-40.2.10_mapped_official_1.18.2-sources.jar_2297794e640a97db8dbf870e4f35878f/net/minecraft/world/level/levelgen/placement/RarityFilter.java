package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;

public class RarityFilter extends PlacementFilter {
   public static final Codec<RarityFilter> CODEC = ExtraCodecs.POSITIVE_INT.fieldOf("chance").xmap(RarityFilter::new, (p_191907_) -> {
      return p_191907_.chance;
   }).codec();
   private final int chance;

   private RarityFilter(int p_191899_) {
      this.chance = p_191899_;
   }

   public static RarityFilter onAverageOnceEvery(int p_191901_) {
      return new RarityFilter(p_191901_);
   }

   protected boolean shouldPlace(PlacementContext p_191903_, Random p_191904_, BlockPos p_191905_) {
      return p_191904_.nextFloat() < 1.0F / (float)this.chance;
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.RARITY_FILTER;
   }
}