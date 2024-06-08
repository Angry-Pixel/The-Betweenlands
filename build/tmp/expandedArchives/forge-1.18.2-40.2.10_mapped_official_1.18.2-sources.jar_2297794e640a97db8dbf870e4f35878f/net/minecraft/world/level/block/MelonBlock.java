package net.minecraft.world.level.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class MelonBlock extends StemGrownBlock {
   public MelonBlock(BlockBehaviour.Properties p_54829_) {
      super(p_54829_);
   }

   public StemBlock getStem() {
      return (StemBlock)Blocks.MELON_STEM;
   }

   public AttachedStemBlock getAttachedStem() {
      return (AttachedStemBlock)Blocks.ATTACHED_MELON_STEM;
   }
}