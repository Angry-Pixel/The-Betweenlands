package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;

public class IceBlock extends HalfTransparentBlock {
   public IceBlock(BlockBehaviour.Properties p_54155_) {
      super(p_54155_);
   }

   public void playerDestroy(Level p_54157_, Player p_54158_, BlockPos p_54159_, BlockState p_54160_, @Nullable BlockEntity p_54161_, ItemStack p_54162_) {
      super.playerDestroy(p_54157_, p_54158_, p_54159_, p_54160_, p_54161_, p_54162_);
      if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, p_54162_) == 0) {
         if (p_54157_.dimensionType().ultraWarm()) {
            p_54157_.removeBlock(p_54159_, false);
            return;
         }

         Material material = p_54157_.getBlockState(p_54159_.below()).getMaterial();
         if (material.blocksMotion() || material.isLiquid()) {
            p_54157_.setBlockAndUpdate(p_54159_, Blocks.WATER.defaultBlockState());
         }
      }

   }

   public void randomTick(BlockState p_54164_, ServerLevel p_54165_, BlockPos p_54166_, Random p_54167_) {
      if (p_54165_.getBrightness(LightLayer.BLOCK, p_54166_) > 11 - p_54164_.getLightBlock(p_54165_, p_54166_)) {
         this.melt(p_54164_, p_54165_, p_54166_);
      }

   }

   protected void melt(BlockState p_54169_, Level p_54170_, BlockPos p_54171_) {
      if (p_54170_.dimensionType().ultraWarm()) {
         p_54170_.removeBlock(p_54171_, false);
      } else {
         p_54170_.setBlockAndUpdate(p_54171_, Blocks.WATER.defaultBlockState());
         p_54170_.neighborChanged(p_54171_, Blocks.WATER, p_54171_);
      }
   }

   public PushReaction getPistonPushReaction(BlockState p_54173_) {
      return PushReaction.NORMAL;
   }
}