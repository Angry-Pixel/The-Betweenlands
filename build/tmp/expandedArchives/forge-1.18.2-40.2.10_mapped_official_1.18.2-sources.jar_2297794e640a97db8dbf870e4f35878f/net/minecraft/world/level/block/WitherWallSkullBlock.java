package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WitherWallSkullBlock extends WallSkullBlock {
   public WitherWallSkullBlock(BlockBehaviour.Properties p_58276_) {
      super(SkullBlock.Types.WITHER_SKELETON, p_58276_);
   }

   public void setPlacedBy(Level p_58278_, BlockPos p_58279_, BlockState p_58280_, @Nullable LivingEntity p_58281_, ItemStack p_58282_) {
      Blocks.WITHER_SKELETON_SKULL.setPlacedBy(p_58278_, p_58279_, p_58280_, p_58281_, p_58282_);
   }
}