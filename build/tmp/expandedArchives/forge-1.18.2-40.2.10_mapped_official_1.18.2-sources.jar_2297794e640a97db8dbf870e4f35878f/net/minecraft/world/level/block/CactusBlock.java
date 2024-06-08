package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CactusBlock extends Block implements net.minecraftforge.common.IPlantable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
   public static final int MAX_AGE = 15;
   protected static final int AABB_OFFSET = 1;
   protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
   protected static final VoxelShape OUTLINE_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public CactusBlock(BlockBehaviour.Properties p_51136_) {
      super(p_51136_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState p_51138_, ServerLevel p_51139_, BlockPos p_51140_, Random p_51141_) {
      if (!p_51139_.isAreaLoaded(p_51140_, 1)) return; // Forge: prevent growing cactus from loading unloaded chunks with block update
      if (!p_51138_.canSurvive(p_51139_, p_51140_)) {
         p_51139_.destroyBlock(p_51140_, true);
      }

   }

   public void randomTick(BlockState p_51166_, ServerLevel p_51167_, BlockPos p_51168_, Random p_51169_) {
      BlockPos blockpos = p_51168_.above();
      if (p_51167_.isEmptyBlock(blockpos)) {
         int i;
         for(i = 1; p_51167_.getBlockState(p_51168_.below(i)).is(this); ++i) {
         }

         if (i < 3) {
            int j = p_51166_.getValue(AGE);
            if(net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_51167_, blockpos, p_51166_, true)) {
            if (j == 15) {
               p_51167_.setBlockAndUpdate(blockpos, this.defaultBlockState());
               BlockState blockstate = p_51166_.setValue(AGE, Integer.valueOf(0));
               p_51167_.setBlock(p_51168_, blockstate, 4);
               blockstate.neighborChanged(p_51167_, blockpos, this, p_51168_, false);
            } else {
               p_51167_.setBlock(p_51168_, p_51166_.setValue(AGE, Integer.valueOf(j + 1)), 4);
            }
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_51167_, p_51168_, p_51166_);
            }
         }
      }
   }

   public VoxelShape getCollisionShape(BlockState p_51176_, BlockGetter p_51177_, BlockPos p_51178_, CollisionContext p_51179_) {
      return COLLISION_SHAPE;
   }

   public VoxelShape getShape(BlockState p_51171_, BlockGetter p_51172_, BlockPos p_51173_, CollisionContext p_51174_) {
      return OUTLINE_SHAPE;
   }

   public BlockState updateShape(BlockState p_51157_, Direction p_51158_, BlockState p_51159_, LevelAccessor p_51160_, BlockPos p_51161_, BlockPos p_51162_) {
      if (!p_51157_.canSurvive(p_51160_, p_51161_)) {
         p_51160_.scheduleTick(p_51161_, this, 1);
      }

      return super.updateShape(p_51157_, p_51158_, p_51159_, p_51160_, p_51161_, p_51162_);
   }

   public boolean canSurvive(BlockState p_51153_, LevelReader p_51154_, BlockPos p_51155_) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockState blockstate = p_51154_.getBlockState(p_51155_.relative(direction));
         Material material = blockstate.getMaterial();
         if (material.isSolid() || p_51154_.getFluidState(p_51155_.relative(direction)).is(FluidTags.LAVA)) {
            return false;
         }
      }

      BlockState blockstate1 = p_51154_.getBlockState(p_51155_.below());
      return blockstate1.canSustainPlant(p_51154_, p_51155_, Direction.UP, this) && !p_51154_.getBlockState(p_51155_.above()).getMaterial().isLiquid();
   }

   public void entityInside(BlockState p_51148_, Level p_51149_, BlockPos p_51150_, Entity p_51151_) {
      p_51151_.hurt(DamageSource.CACTUS, 1.0F);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51164_) {
      p_51164_.add(AGE);
   }

   public boolean isPathfindable(BlockState p_51143_, BlockGetter p_51144_, BlockPos p_51145_, PathComputationType p_51146_) {
      return false;
   }

   @Override
   public net.minecraftforge.common.PlantType getPlantType(BlockGetter world, BlockPos pos) {
      return net.minecraftforge.common.PlantType.DESERT;
   }

   @Override
   public BlockState getPlant(BlockGetter world, BlockPos pos) {
      return defaultBlockState();
   }
}
