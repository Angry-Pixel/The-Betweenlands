package net.minecraft.world.level.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class WoolCarpetBlock extends CarpetBlock {
   private final DyeColor color;

   public WoolCarpetBlock(DyeColor p_58291_, BlockBehaviour.Properties p_58292_) {
      super(p_58292_);
      this.color = p_58291_;
   }

   public DyeColor getColor() {
      return this.color;
   }
}