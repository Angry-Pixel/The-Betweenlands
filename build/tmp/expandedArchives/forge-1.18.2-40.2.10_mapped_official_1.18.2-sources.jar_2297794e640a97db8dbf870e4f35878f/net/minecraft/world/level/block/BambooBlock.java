package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooBlock extends Block implements BonemealableBlock, net.minecraftforge.common.IPlantable  {
   protected static final float SMALL_LEAVES_AABB_OFFSET = 3.0F;
   protected static final float LARGE_LEAVES_AABB_OFFSET = 5.0F;
   protected static final float COLLISION_AABB_OFFSET = 1.5F;
   protected static final VoxelShape SMALL_SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
   protected static final VoxelShape LARGE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
   protected static final VoxelShape COLLISION_SHAPE = Block.box(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
   public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
   public static final EnumProperty<BambooLeaves> LEAVES = BlockStateProperties.BAMBOO_LEAVES;
   public static final IntegerProperty STAGE = BlockStateProperties.STAGE;
   public static final int MAX_HEIGHT = 16;
   public static final int STAGE_GROWING = 0;
   public static final int STAGE_DONE_GROWING = 1;
   public static final int AGE_THIN_BAMBOO = 0;
   public static final int AGE_THICK_BAMBOO = 1;

   public BambooBlock(BlockBehaviour.Properties p_48874_) {
      super(p_48874_);
      this.registerDefaultState(this.stateDefinition.any().setValue(AGE, Integer.valueOf(0)).setValue(LEAVES, BambooLeaves.NONE).setValue(STAGE, Integer.valueOf(0)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48928_) {
      p_48928_.add(AGE, LEAVES, STAGE);
   }

   public BlockBehaviour.OffsetType getOffsetType() {
      return BlockBehaviour.OffsetType.XZ;
   }

   public boolean propagatesSkylightDown(BlockState p_48941_, BlockGetter p_48942_, BlockPos p_48943_) {
      return true;
   }

   public VoxelShape getShape(BlockState p_48945_, BlockGetter p_48946_, BlockPos p_48947_, CollisionContext p_48948_) {
      VoxelShape voxelshape = p_48945_.getValue(LEAVES) == BambooLeaves.LARGE ? LARGE_SHAPE : SMALL_SHAPE;
      Vec3 vec3 = p_48945_.getOffset(p_48946_, p_48947_);
      return voxelshape.move(vec3.x, vec3.y, vec3.z);
   }

   public boolean isPathfindable(BlockState p_48906_, BlockGetter p_48907_, BlockPos p_48908_, PathComputationType p_48909_) {
      return false;
   }

   public VoxelShape getCollisionShape(BlockState p_48950_, BlockGetter p_48951_, BlockPos p_48952_, CollisionContext p_48953_) {
      Vec3 vec3 = p_48950_.getOffset(p_48951_, p_48952_);
      return COLLISION_SHAPE.move(vec3.x, vec3.y, vec3.z);
   }

   public boolean isCollisionShapeFullBlock(BlockState p_181159_, BlockGetter p_181160_, BlockPos p_181161_) {
      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_48881_) {
      FluidState fluidstate = p_48881_.getLevel().getFluidState(p_48881_.getClickedPos());
      if (!fluidstate.isEmpty()) {
         return null;
      } else {
         BlockState blockstate = p_48881_.getLevel().getBlockState(p_48881_.getClickedPos().below());
         if (blockstate.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
            if (blockstate.is(Blocks.BAMBOO_SAPLING)) {
               return this.defaultBlockState().setValue(AGE, Integer.valueOf(0));
            } else if (blockstate.is(Blocks.BAMBOO)) {
               int i = blockstate.getValue(AGE) > 0 ? 1 : 0;
               return this.defaultBlockState().setValue(AGE, Integer.valueOf(i));
            } else {
               BlockState blockstate1 = p_48881_.getLevel().getBlockState(p_48881_.getClickedPos().above());
               return blockstate1.is(Blocks.BAMBOO) ? this.defaultBlockState().setValue(AGE, blockstate1.getValue(AGE)) : Blocks.BAMBOO_SAPLING.defaultBlockState();
            }
         } else {
            return null;
         }
      }
   }

   public void tick(BlockState p_48896_, ServerLevel p_48897_, BlockPos p_48898_, Random p_48899_) {
      if (!p_48896_.canSurvive(p_48897_, p_48898_)) {
         p_48897_.destroyBlock(p_48898_, true);
      }

   }

   public boolean isRandomlyTicking(BlockState p_48930_) {
      return p_48930_.getValue(STAGE) == 0;
   }

   public void randomTick(BlockState p_48936_, ServerLevel p_48937_, BlockPos p_48938_, Random p_48939_) {
      if (p_48936_.getValue(STAGE) == 0) {
         if (p_48937_.isEmptyBlock(p_48938_.above()) && p_48937_.getRawBrightness(p_48938_.above(), 0) >= 9) {
            int i = this.getHeightBelowUpToMax(p_48937_, p_48938_) + 1;
            if (i < 16 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_48937_, p_48938_, p_48936_, p_48939_.nextInt(3) == 0)) {
               this.growBamboo(p_48936_, p_48937_, p_48938_, p_48939_, i);
               net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_48937_, p_48938_, p_48936_);
            }
         }

      }
   }

   public boolean canSurvive(BlockState p_48917_, LevelReader p_48918_, BlockPos p_48919_) {
      return p_48918_.getBlockState(p_48919_.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updateShape(BlockState p_48921_, Direction p_48922_, BlockState p_48923_, LevelAccessor p_48924_, BlockPos p_48925_, BlockPos p_48926_) {
      if (!p_48921_.canSurvive(p_48924_, p_48925_)) {
         p_48924_.scheduleTick(p_48925_, this, 1);
      }

      if (p_48922_ == Direction.UP && p_48923_.is(Blocks.BAMBOO) && p_48923_.getValue(AGE) > p_48921_.getValue(AGE)) {
         p_48924_.setBlock(p_48925_, p_48921_.cycle(AGE), 2);
      }

      return super.updateShape(p_48921_, p_48922_, p_48923_, p_48924_, p_48925_, p_48926_);
   }

   public boolean isValidBonemealTarget(BlockGetter p_48886_, BlockPos p_48887_, BlockState p_48888_, boolean p_48889_) {
      int i = this.getHeightAboveUpToMax(p_48886_, p_48887_);
      int j = this.getHeightBelowUpToMax(p_48886_, p_48887_);
      return i + j + 1 < 16 && p_48886_.getBlockState(p_48887_.above(i)).getValue(STAGE) != 1;
   }

   public boolean isBonemealSuccess(Level p_48891_, Random p_48892_, BlockPos p_48893_, BlockState p_48894_) {
      return true;
   }

   public void performBonemeal(ServerLevel p_48876_, Random p_48877_, BlockPos p_48878_, BlockState p_48879_) {
      int i = this.getHeightAboveUpToMax(p_48876_, p_48878_);
      int j = this.getHeightBelowUpToMax(p_48876_, p_48878_);
      int k = i + j + 1;
      int l = 1 + p_48877_.nextInt(2);

      for(int i1 = 0; i1 < l; ++i1) {
         BlockPos blockpos = p_48878_.above(i);
         BlockState blockstate = p_48876_.getBlockState(blockpos);
         if (k >= 16 || blockstate.getValue(STAGE) == 1 || !p_48876_.isEmptyBlock(blockpos.above())) {
            return;
         }

         this.growBamboo(blockstate, p_48876_, blockpos, p_48877_, k);
         ++i;
         ++k;
      }

   }

   public float getDestroyProgress(BlockState p_48901_, Player p_48902_, BlockGetter p_48903_, BlockPos p_48904_) {
      return p_48902_.getMainHandItem().canPerformAction(net.minecraftforge.common.ToolActions.SWORD_DIG) ? 1.0F : super.getDestroyProgress(p_48901_, p_48902_, p_48903_, p_48904_);
   }

   protected void growBamboo(BlockState p_48911_, Level p_48912_, BlockPos p_48913_, Random p_48914_, int p_48915_) {
      BlockState blockstate = p_48912_.getBlockState(p_48913_.below());
      BlockPos blockpos = p_48913_.below(2);
      BlockState blockstate1 = p_48912_.getBlockState(blockpos);
      BambooLeaves bambooleaves = BambooLeaves.NONE;
      if (p_48915_ >= 1) {
         if (blockstate.is(Blocks.BAMBOO) && blockstate.getValue(LEAVES) != BambooLeaves.NONE) {
            if (blockstate.is(Blocks.BAMBOO) && blockstate.getValue(LEAVES) != BambooLeaves.NONE) {
               bambooleaves = BambooLeaves.LARGE;
               if (blockstate1.is(Blocks.BAMBOO)) {
                  p_48912_.setBlock(p_48913_.below(), blockstate.setValue(LEAVES, BambooLeaves.SMALL), 3);
                  p_48912_.setBlock(blockpos, blockstate1.setValue(LEAVES, BambooLeaves.NONE), 3);
               }
            }
         } else {
            bambooleaves = BambooLeaves.SMALL;
         }
      }

      int i = p_48911_.getValue(AGE) != 1 && !blockstate1.is(Blocks.BAMBOO) ? 0 : 1;
      int j = (p_48915_ < 11 || !(p_48914_.nextFloat() < 0.25F)) && p_48915_ != 15 ? 0 : 1;
      p_48912_.setBlock(p_48913_.above(), this.defaultBlockState().setValue(AGE, Integer.valueOf(i)).setValue(LEAVES, bambooleaves).setValue(STAGE, Integer.valueOf(j)), 3);
   }

   protected int getHeightAboveUpToMax(BlockGetter p_48883_, BlockPos p_48884_) {
      int i;
      for(i = 0; i < 16 && p_48883_.getBlockState(p_48884_.above(i + 1)).is(Blocks.BAMBOO); ++i) {
      }

      return i;
   }

   protected int getHeightBelowUpToMax(BlockGetter p_48933_, BlockPos p_48934_) {
      int i;
      for(i = 0; i < 16 && p_48933_.getBlockState(p_48934_.below(i + 1)).is(Blocks.BAMBOO); ++i) {
      }

      return i;
   }

   @Override
   public BlockState getPlant(BlockGetter world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);
      if (state.getBlock() != this) return defaultBlockState();
      return state;
   }
}
