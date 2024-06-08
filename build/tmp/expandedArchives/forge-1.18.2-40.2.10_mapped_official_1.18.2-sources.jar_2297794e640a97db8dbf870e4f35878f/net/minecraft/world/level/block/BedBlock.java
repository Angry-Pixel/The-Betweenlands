package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.ArrayUtils;

public class BedBlock extends HorizontalDirectionalBlock implements EntityBlock {
   public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
   public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;
   protected static final int HEIGHT = 9;
   protected static final VoxelShape BASE = Block.box(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
   private static final int LEG_WIDTH = 3;
   protected static final VoxelShape LEG_NORTH_WEST = Block.box(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
   protected static final VoxelShape LEG_SOUTH_WEST = Block.box(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
   protected static final VoxelShape LEG_NORTH_EAST = Block.box(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
   protected static final VoxelShape LEG_SOUTH_EAST = Block.box(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape NORTH_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_NORTH_EAST);
   protected static final VoxelShape SOUTH_SHAPE = Shapes.or(BASE, LEG_SOUTH_WEST, LEG_SOUTH_EAST);
   protected static final VoxelShape WEST_SHAPE = Shapes.or(BASE, LEG_NORTH_WEST, LEG_SOUTH_WEST);
   protected static final VoxelShape EAST_SHAPE = Shapes.or(BASE, LEG_NORTH_EAST, LEG_SOUTH_EAST);
   private final DyeColor color;

   public BedBlock(DyeColor p_49454_, BlockBehaviour.Properties p_49455_) {
      super(p_49455_);
      this.color = p_49454_;
      this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.valueOf(false)));
   }

   @Nullable
   public static Direction getBedOrientation(BlockGetter p_49486_, BlockPos p_49487_) {
      BlockState blockstate = p_49486_.getBlockState(p_49487_);
      return blockstate.getBlock() instanceof BedBlock ? blockstate.getValue(FACING) : null;
   }

   public InteractionResult use(BlockState p_49515_, Level p_49516_, BlockPos p_49517_, Player p_49518_, InteractionHand p_49519_, BlockHitResult p_49520_) {
      if (p_49516_.isClientSide) {
         return InteractionResult.CONSUME;
      } else {
         if (p_49515_.getValue(PART) != BedPart.HEAD) {
            p_49517_ = p_49517_.relative(p_49515_.getValue(FACING));
            p_49515_ = p_49516_.getBlockState(p_49517_);
            if (!p_49515_.is(this)) {
               return InteractionResult.CONSUME;
            }
         }

         if (!canSetSpawn(p_49516_)) {
            p_49516_.removeBlock(p_49517_, false);
            BlockPos blockpos = p_49517_.relative(p_49515_.getValue(FACING).getOpposite());
            if (p_49516_.getBlockState(blockpos).is(this)) {
               p_49516_.removeBlock(blockpos, false);
            }

            p_49516_.explode((Entity)null, DamageSource.badRespawnPointExplosion(), (ExplosionDamageCalculator)null, (double)p_49517_.getX() + 0.5D, (double)p_49517_.getY() + 0.5D, (double)p_49517_.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
            return InteractionResult.SUCCESS;
         } else if (p_49515_.getValue(OCCUPIED)) {
            if (!this.kickVillagerOutOfBed(p_49516_, p_49517_)) {
               p_49518_.displayClientMessage(new TranslatableComponent("block.minecraft.bed.occupied"), true);
            }

            return InteractionResult.SUCCESS;
         } else {
            p_49518_.startSleepInBed(p_49517_).ifLeft((p_49477_) -> {
               if (p_49477_ != null) {
                  p_49518_.displayClientMessage(p_49477_.getMessage(), true);
               }

            });
            return InteractionResult.SUCCESS;
         }
      }
   }

   public static boolean canSetSpawn(Level p_49489_) {
      return p_49489_.dimensionType().bedWorks();
   }

   private boolean kickVillagerOutOfBed(Level p_49491_, BlockPos p_49492_) {
      List<Villager> list = p_49491_.getEntitiesOfClass(Villager.class, new AABB(p_49492_), LivingEntity::isSleeping);
      if (list.isEmpty()) {
         return false;
      } else {
         list.get(0).stopSleeping();
         return true;
      }
   }

   public void fallOn(Level p_152169_, BlockState p_152170_, BlockPos p_152171_, Entity p_152172_, float p_152173_) {
      super.fallOn(p_152169_, p_152170_, p_152171_, p_152172_, p_152173_ * 0.5F);
   }

   public void updateEntityAfterFallOn(BlockGetter p_49483_, Entity p_49484_) {
      if (p_49484_.isSuppressingBounce()) {
         super.updateEntityAfterFallOn(p_49483_, p_49484_);
      } else {
         this.bounceUp(p_49484_);
      }

   }

   private void bounceUp(Entity p_49457_) {
      Vec3 vec3 = p_49457_.getDeltaMovement();
      if (vec3.y < 0.0D) {
         double d0 = p_49457_ instanceof LivingEntity ? 1.0D : 0.8D;
         p_49457_.setDeltaMovement(vec3.x, -vec3.y * (double)0.66F * d0, vec3.z);
      }

   }

   public BlockState updateShape(BlockState p_49525_, Direction p_49526_, BlockState p_49527_, LevelAccessor p_49528_, BlockPos p_49529_, BlockPos p_49530_) {
      if (p_49526_ == getNeighbourDirection(p_49525_.getValue(PART), p_49525_.getValue(FACING))) {
         return p_49527_.is(this) && p_49527_.getValue(PART) != p_49525_.getValue(PART) ? p_49525_.setValue(OCCUPIED, p_49527_.getValue(OCCUPIED)) : Blocks.AIR.defaultBlockState();
      } else {
         return super.updateShape(p_49525_, p_49526_, p_49527_, p_49528_, p_49529_, p_49530_);
      }
   }

   private static Direction getNeighbourDirection(BedPart p_49534_, Direction p_49535_) {
      return p_49534_ == BedPart.FOOT ? p_49535_ : p_49535_.getOpposite();
   }

   public void playerWillDestroy(Level p_49505_, BlockPos p_49506_, BlockState p_49507_, Player p_49508_) {
      if (!p_49505_.isClientSide && p_49508_.isCreative()) {
         BedPart bedpart = p_49507_.getValue(PART);
         if (bedpart == BedPart.FOOT) {
            BlockPos blockpos = p_49506_.relative(getNeighbourDirection(bedpart, p_49507_.getValue(FACING)));
            BlockState blockstate = p_49505_.getBlockState(blockpos);
            if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
               p_49505_.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
               p_49505_.levelEvent(p_49508_, 2001, blockpos, Block.getId(blockstate));
            }
         }
      }

      super.playerWillDestroy(p_49505_, p_49506_, p_49507_, p_49508_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_49479_) {
      Direction direction = p_49479_.getHorizontalDirection();
      BlockPos blockpos = p_49479_.getClickedPos();
      BlockPos blockpos1 = blockpos.relative(direction);
      Level level = p_49479_.getLevel();
      return level.getBlockState(blockpos1).canBeReplaced(p_49479_) && level.getWorldBorder().isWithinBounds(blockpos1) ? this.defaultBlockState().setValue(FACING, direction) : null;
   }

   public VoxelShape getShape(BlockState p_49547_, BlockGetter p_49548_, BlockPos p_49549_, CollisionContext p_49550_) {
      Direction direction = getConnectedDirection(p_49547_).getOpposite();
      switch(direction) {
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      default:
         return EAST_SHAPE;
      }
   }

   public static Direction getConnectedDirection(BlockState p_49558_) {
      Direction direction = p_49558_.getValue(FACING);
      return p_49558_.getValue(PART) == BedPart.HEAD ? direction.getOpposite() : direction;
   }

   public static DoubleBlockCombiner.BlockType getBlockType(BlockState p_49560_) {
      BedPart bedpart = p_49560_.getValue(PART);
      return bedpart == BedPart.HEAD ? DoubleBlockCombiner.BlockType.FIRST : DoubleBlockCombiner.BlockType.SECOND;
   }

   private static boolean isBunkBed(BlockGetter p_49542_, BlockPos p_49543_) {
      return p_49542_.getBlockState(p_49543_.below()).getBlock() instanceof BedBlock;
   }

   public static Optional<Vec3> findStandUpPosition(EntityType<?> p_49459_, CollisionGetter p_49460_, BlockPos p_49461_, float p_49462_) {
      Direction direction = p_49460_.getBlockState(p_49461_).getValue(FACING);
      Direction direction1 = direction.getClockWise();
      Direction direction2 = direction1.isFacingAngle(p_49462_) ? direction1.getOpposite() : direction1;
      if (isBunkBed(p_49460_, p_49461_)) {
         return findBunkBedStandUpPosition(p_49459_, p_49460_, p_49461_, direction, direction2);
      } else {
         int[][] aint = bedStandUpOffsets(direction, direction2);
         Optional<Vec3> optional = findStandUpPositionAtOffset(p_49459_, p_49460_, p_49461_, aint, true);
         return optional.isPresent() ? optional : findStandUpPositionAtOffset(p_49459_, p_49460_, p_49461_, aint, false);
      }
   }

   private static Optional<Vec3> findBunkBedStandUpPosition(EntityType<?> p_49464_, CollisionGetter p_49465_, BlockPos p_49466_, Direction p_49467_, Direction p_49468_) {
      int[][] aint = bedSurroundStandUpOffsets(p_49467_, p_49468_);
      Optional<Vec3> optional = findStandUpPositionAtOffset(p_49464_, p_49465_, p_49466_, aint, true);
      if (optional.isPresent()) {
         return optional;
      } else {
         BlockPos blockpos = p_49466_.below();
         Optional<Vec3> optional1 = findStandUpPositionAtOffset(p_49464_, p_49465_, blockpos, aint, true);
         if (optional1.isPresent()) {
            return optional1;
         } else {
            int[][] aint1 = bedAboveStandUpOffsets(p_49467_);
            Optional<Vec3> optional2 = findStandUpPositionAtOffset(p_49464_, p_49465_, p_49466_, aint1, true);
            if (optional2.isPresent()) {
               return optional2;
            } else {
               Optional<Vec3> optional3 = findStandUpPositionAtOffset(p_49464_, p_49465_, p_49466_, aint, false);
               if (optional3.isPresent()) {
                  return optional3;
               } else {
                  Optional<Vec3> optional4 = findStandUpPositionAtOffset(p_49464_, p_49465_, blockpos, aint, false);
                  return optional4.isPresent() ? optional4 : findStandUpPositionAtOffset(p_49464_, p_49465_, p_49466_, aint1, false);
               }
            }
         }
      }
   }

   private static Optional<Vec3> findStandUpPositionAtOffset(EntityType<?> p_49470_, CollisionGetter p_49471_, BlockPos p_49472_, int[][] p_49473_, boolean p_49474_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int[] aint : p_49473_) {
         blockpos$mutableblockpos.set(p_49472_.getX() + aint[0], p_49472_.getY(), p_49472_.getZ() + aint[1]);
         Vec3 vec3 = DismountHelper.findSafeDismountLocation(p_49470_, p_49471_, blockpos$mutableblockpos, p_49474_);
         if (vec3 != null) {
            return Optional.of(vec3);
         }
      }

      return Optional.empty();
   }

   public PushReaction getPistonPushReaction(BlockState p_49556_) {
      return PushReaction.DESTROY;
   }

   public RenderShape getRenderShape(BlockState p_49545_) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49532_) {
      p_49532_.add(FACING, PART, OCCUPIED);
   }

   public BlockEntity newBlockEntity(BlockPos p_152175_, BlockState p_152176_) {
      return new BedBlockEntity(p_152175_, p_152176_, this.color);
   }

   public void setPlacedBy(Level p_49499_, BlockPos p_49500_, BlockState p_49501_, @Nullable LivingEntity p_49502_, ItemStack p_49503_) {
      super.setPlacedBy(p_49499_, p_49500_, p_49501_, p_49502_, p_49503_);
      if (!p_49499_.isClientSide) {
         BlockPos blockpos = p_49500_.relative(p_49501_.getValue(FACING));
         p_49499_.setBlock(blockpos, p_49501_.setValue(PART, BedPart.HEAD), 3);
         p_49499_.blockUpdated(p_49500_, Blocks.AIR);
         p_49501_.updateNeighbourShapes(p_49499_, p_49500_, 3);
      }

   }

   public DyeColor getColor() {
      return this.color;
   }

   public long getSeed(BlockState p_49522_, BlockPos p_49523_) {
      BlockPos blockpos = p_49523_.relative(p_49522_.getValue(FACING), p_49522_.getValue(PART) == BedPart.HEAD ? 0 : 1);
      return Mth.getSeed(blockpos.getX(), p_49523_.getY(), blockpos.getZ());
   }

   public boolean isPathfindable(BlockState p_49510_, BlockGetter p_49511_, BlockPos p_49512_, PathComputationType p_49513_) {
      return false;
   }

   private static int[][] bedStandUpOffsets(Direction p_49539_, Direction p_49540_) {
      return ArrayUtils.addAll((int[][])bedSurroundStandUpOffsets(p_49539_, p_49540_), (int[][])bedAboveStandUpOffsets(p_49539_));
   }

   private static int[][] bedSurroundStandUpOffsets(Direction p_49552_, Direction p_49553_) {
      return new int[][]{{p_49553_.getStepX(), p_49553_.getStepZ()}, {p_49553_.getStepX() - p_49552_.getStepX(), p_49553_.getStepZ() - p_49552_.getStepZ()}, {p_49553_.getStepX() - p_49552_.getStepX() * 2, p_49553_.getStepZ() - p_49552_.getStepZ() * 2}, {-p_49552_.getStepX() * 2, -p_49552_.getStepZ() * 2}, {-p_49553_.getStepX() - p_49552_.getStepX() * 2, -p_49553_.getStepZ() - p_49552_.getStepZ() * 2}, {-p_49553_.getStepX() - p_49552_.getStepX(), -p_49553_.getStepZ() - p_49552_.getStepZ()}, {-p_49553_.getStepX(), -p_49553_.getStepZ()}, {-p_49553_.getStepX() + p_49552_.getStepX(), -p_49553_.getStepZ() + p_49552_.getStepZ()}, {p_49552_.getStepX(), p_49552_.getStepZ()}, {p_49553_.getStepX() + p_49552_.getStepX(), p_49553_.getStepZ() + p_49552_.getStepZ()}};
   }

   private static int[][] bedAboveStandUpOffsets(Direction p_49537_) {
      return new int[][]{{0, 0}, {-p_49537_.getStepX(), -p_49537_.getStepZ()}};
   }
}