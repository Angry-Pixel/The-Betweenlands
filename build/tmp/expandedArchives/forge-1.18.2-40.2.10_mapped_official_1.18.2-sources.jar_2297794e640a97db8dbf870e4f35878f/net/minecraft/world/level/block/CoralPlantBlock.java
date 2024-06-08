package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoralPlantBlock extends BaseCoralPlantTypeBlock {
   private final Block deadBlock;
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

   public CoralPlantBlock(Block p_52175_, BlockBehaviour.Properties p_52176_) {
      super(p_52176_);
      this.deadBlock = p_52175_;
   }

   public void onPlace(BlockState p_52195_, Level p_52196_, BlockPos p_52197_, BlockState p_52198_, boolean p_52199_) {
      this.tryScheduleDieTick(p_52195_, p_52196_, p_52197_);
   }

   public void tick(BlockState p_52178_, ServerLevel p_52179_, BlockPos p_52180_, Random p_52181_) {
      if (!scanForWater(p_52178_, p_52179_, p_52180_)) {
         p_52179_.setBlock(p_52180_, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)), 2);
      }

   }

   public BlockState updateShape(BlockState p_52183_, Direction p_52184_, BlockState p_52185_, LevelAccessor p_52186_, BlockPos p_52187_, BlockPos p_52188_) {
      if (p_52184_ == Direction.DOWN && !p_52183_.canSurvive(p_52186_, p_52187_)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         this.tryScheduleDieTick(p_52183_, p_52186_, p_52187_);
         if (p_52183_.getValue(WATERLOGGED)) {
            p_52186_.scheduleTick(p_52187_, Fluids.WATER, Fluids.WATER.getTickDelay(p_52186_));
         }

         return super.updateShape(p_52183_, p_52184_, p_52185_, p_52186_, p_52187_, p_52188_);
      }
   }

   public VoxelShape getShape(BlockState p_52190_, BlockGetter p_52191_, BlockPos p_52192_, CollisionContext p_52193_) {
      return SHAPE;
   }
}