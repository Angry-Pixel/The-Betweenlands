package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractBannerBlock extends BaseEntityBlock {
   private final DyeColor color;

   protected AbstractBannerBlock(DyeColor p_48659_, BlockBehaviour.Properties p_48660_) {
      super(p_48660_);
      this.color = p_48659_;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public BlockEntity newBlockEntity(BlockPos p_151892_, BlockState p_151893_) {
      return new BannerBlockEntity(p_151892_, p_151893_, this.color);
   }

   public void setPlacedBy(Level p_48668_, BlockPos p_48669_, BlockState p_48670_, @Nullable LivingEntity p_48671_, ItemStack p_48672_) {
      if (p_48668_.isClientSide) {
         p_48668_.getBlockEntity(p_48669_, BlockEntityType.BANNER).ifPresent((p_187404_) -> {
            p_187404_.fromItem(p_48672_);
         });
      } else if (p_48672_.hasCustomHoverName()) {
         p_48668_.getBlockEntity(p_48669_, BlockEntityType.BANNER).ifPresent((p_187401_) -> {
            p_187401_.setCustomName(p_48672_.getHoverName());
         });
      }

   }

   public ItemStack getCloneItemStack(BlockGetter p_48664_, BlockPos p_48665_, BlockState p_48666_) {
      BlockEntity blockentity = p_48664_.getBlockEntity(p_48665_);
      return blockentity instanceof BannerBlockEntity ? ((BannerBlockEntity)blockentity).getItem() : super.getCloneItemStack(p_48664_, p_48665_, p_48666_);
   }

   public DyeColor getColor() {
      return this.color;
   }
}