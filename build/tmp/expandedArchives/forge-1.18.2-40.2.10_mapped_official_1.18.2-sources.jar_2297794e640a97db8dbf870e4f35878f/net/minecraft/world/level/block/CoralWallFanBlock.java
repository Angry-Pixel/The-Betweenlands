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

public class CoralWallFanBlock extends BaseCoralWallFanBlock {
   private final Block deadBlock;

   public CoralWallFanBlock(Block p_52202_, BlockBehaviour.Properties p_52203_) {
      super(p_52203_);
      this.deadBlock = p_52202_;
   }

   public void onPlace(BlockState p_52217_, Level p_52218_, BlockPos p_52219_, BlockState p_52220_, boolean p_52221_) {
      this.tryScheduleDieTick(p_52217_, p_52218_, p_52219_);
   }

   public void tick(BlockState p_52205_, ServerLevel p_52206_, BlockPos p_52207_, Random p_52208_) {
      if (!scanForWater(p_52205_, p_52206_, p_52207_)) {
         p_52206_.setBlock(p_52207_, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(FACING, p_52205_.getValue(FACING)), 2);
      }

   }

   public BlockState updateShape(BlockState p_52210_, Direction p_52211_, BlockState p_52212_, LevelAccessor p_52213_, BlockPos p_52214_, BlockPos p_52215_) {
      if (p_52211_.getOpposite() == p_52210_.getValue(FACING) && !p_52210_.canSurvive(p_52213_, p_52214_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (p_52210_.getValue(WATERLOGGED)) {
            p_52213_.scheduleTick(p_52214_, Fluids.WATER, Fluids.WATER.getTickDelay(p_52213_));
         }

         this.tryScheduleDieTick(p_52210_, p_52213_, p_52214_);
         return super.updateShape(p_52210_, p_52211_, p_52212_, p_52213_, p_52214_, p_52215_);
      }
   }
}