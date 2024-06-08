package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class StemGrownBlock extends Block {
   public StemGrownBlock(BlockBehaviour.Properties p_57058_) {
      super(p_57058_);
   }

   public abstract StemBlock getStem();

   public abstract AttachedStemBlock getAttachedStem();
}