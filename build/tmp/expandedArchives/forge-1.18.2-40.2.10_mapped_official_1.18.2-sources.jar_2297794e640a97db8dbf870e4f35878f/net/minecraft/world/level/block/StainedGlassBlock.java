package net.minecraft.world.level.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StainedGlassBlock extends AbstractGlassBlock implements BeaconBeamBlock {
   private final DyeColor color;

   public StainedGlassBlock(DyeColor p_56833_, BlockBehaviour.Properties p_56834_) {
      super(p_56834_);
      this.color = p_56833_;
   }

   public DyeColor getColor() {
      return this.color;
   }
}