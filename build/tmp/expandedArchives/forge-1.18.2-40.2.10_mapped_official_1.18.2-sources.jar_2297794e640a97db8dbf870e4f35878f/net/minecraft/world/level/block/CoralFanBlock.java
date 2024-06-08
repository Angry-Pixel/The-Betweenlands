package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class CoralFanBlock extends BaseCoralFanBlock {
   private final Block deadBlock;

   public CoralFanBlock(Block p_52151_, BlockBehaviour.Properties p_52152_) {
      super(p_52152_);
      this.deadBlock = p_52151_;
   }

   public void onPlace(BlockState p_52166_, Level p_52167_, BlockPos p_52168_, BlockState p_52169_, boolean p_52170_) {
      this.tryScheduleDieTick(p_52166_, p_52167_, p_52168_);
   }

   public void tick(BlockState p_52154_, ServerLevel p_52155_, BlockPos p_52156_, Random p_52157_) {
      if (!scanForWater(p_52154_, p_52155_, p_52156_)) {
         p_52155_.setBlock(p_52156_, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)), 2);
      }

   }

   public BlockState updateShape(BlockState p_52159_, Direction p_52160_, BlockState p_52161_, LevelAccessor p_52162_, BlockPos p_52163_, BlockPos p_52164_) {
      if (p_52160_ == Direction.DOWN && !p_52159_.canSurvive(p_52162_, p_52163_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         this.tryScheduleDieTick(p_52159_, p_52162_, p_52163_);
         if (p_52159_.getValue(WATERLOGGED)) {
            p_52162_.scheduleTick(p_52163_, Fluids.WATER, Fluids.WATER.getTickDelay(p_52162_));
         }

         return super.updateShape(p_52159_, p_52160_, p_52161_, p_52162_, p_52163_, p_52164_);
      }
   }
}