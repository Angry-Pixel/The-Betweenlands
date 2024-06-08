package net.minecraft.world.level.block.piston;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonMovingBlockEntity extends BlockEntity {
   private static final int TICKS_TO_EXTEND = 2;
   private static final double PUSH_OFFSET = 0.01D;
   public static final double TICK_MOVEMENT = 0.51D;
   private BlockState movedState = Blocks.AIR.defaultBlockState();
   private Direction direction;
   private boolean extending;
   private boolean isSourcePiston;
   private static final ThreadLocal<Direction> NOCLIP = ThreadLocal.withInitial(() -> {
      return null;
   });
   private float progress;
   private float progressO;
   private long lastTicked;
   private int deathTicks;

   public PistonMovingBlockEntity(BlockPos p_155901_, BlockState p_155902_) {
      super(BlockEntityType.PISTON, p_155901_, p_155902_);
   }

   public PistonMovingBlockEntity(BlockPos p_155904_, BlockState p_155905_, BlockState p_155906_, Direction p_155907_, boolean p_155908_, boolean p_155909_) {
      this(p_155904_, p_155905_);
      this.movedState = p_155906_;
      this.direction = p_155907_;
      this.extending = p_155908_;
      this.isSourcePiston = p_155909_;
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public boolean isExtending() {
      return this.extending;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public boolean isSourcePiston() {
      return this.isSourcePiston;
   }

   public float getProgress(float p_60351_) {
      if (p_60351_ > 1.0F) {
         p_60351_ = 1.0F;
      }

      return Mth.lerp(p_60351_, this.progressO, this.progress);
   }

   public float getXOff(float p_60381_) {
      return (float)this.direction.getStepX() * this.getExtendedProgress(this.getProgress(p_60381_));
   }

   public float getYOff(float p_60386_) {
      return (float)this.direction.getStepY() * this.getExtendedProgress(this.getProgress(p_60386_));
   }

   public float getZOff(float p_60389_) {
      return (float)this.direction.getStepZ() * this.getExtendedProgress(this.getProgress(p_60389_));
   }

   private float getExtendedProgress(float p_60391_) {
      return this.extending ? p_60391_ - 1.0F : 1.0F - p_60391_;
   }

   private BlockState getCollisionRelatedBlockState() {
      return !this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof PistonBaseBlock ? Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.progress > 0.25F)).setValue(PistonHeadBlock.TYPE, this.movedState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT).setValue(PistonHeadBlock.FACING, this.movedState.getValue(PistonBaseBlock.FACING)) : this.movedState;
   }

   private static void moveCollidedEntities(Level p_155911_, BlockPos p_155912_, float p_155913_, PistonMovingBlockEntity p_155914_) {
      Direction direction = p_155914_.getMovementDirection();
      double d0 = (double)(p_155913_ - p_155914_.progress);
      VoxelShape voxelshape = p_155914_.getCollisionRelatedBlockState().getCollisionShape(p_155911_, p_155912_);
      if (!voxelshape.isEmpty()) {
         AABB aabb = moveByPositionAndProgress(p_155912_, voxelshape.bounds(), p_155914_);
         List<Entity> list = p_155911_.getEntities((Entity)null, PistonMath.getMovementArea(aabb, direction, d0).minmax(aabb));
         if (!list.isEmpty()) {
            List<AABB> list1 = voxelshape.toAabbs();
            boolean flag = p_155914_.movedState.isSlimeBlock(); //TODO: is this patch really needed the logic of the original seems sound revisit later
            Iterator iterator = list.iterator();

            while(true) {
               Entity entity;
               while(true) {
                  if (!iterator.hasNext()) {
                     return;
                  }

                  entity = (Entity)iterator.next();
                  if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
                     if (!flag) {
                        break;
                     }

                     if (!(entity instanceof ServerPlayer)) {
                        Vec3 vec3 = entity.getDeltaMovement();
                        double d1 = vec3.x;
                        double d2 = vec3.y;
                        double d3 = vec3.z;
                        switch(direction.getAxis()) {
                        case X:
                           d1 = (double)direction.getStepX();
                           break;
                        case Y:
                           d2 = (double)direction.getStepY();
                           break;
                        case Z:
                           d3 = (double)direction.getStepZ();
                        }

                        entity.setDeltaMovement(d1, d2, d3);
                        break;
                     }
                  }
               }

               double d4 = 0.0D;

               for(AABB aabb2 : list1) {
                  AABB aabb1 = PistonMath.getMovementArea(moveByPositionAndProgress(p_155912_, aabb2, p_155914_), direction, d0);
                  AABB aabb3 = entity.getBoundingBox();
                  if (aabb1.intersects(aabb3)) {
                     d4 = Math.max(d4, getMovement(aabb1, direction, aabb3));
                     if (d4 >= d0) {
                        break;
                     }
                  }
               }

               if (!(d4 <= 0.0D)) {
                  d4 = Math.min(d4, d0) + 0.01D;
                  moveEntityByPiston(direction, entity, d4, direction);
                  if (!p_155914_.extending && p_155914_.isSourcePiston) {
                     fixEntityWithinPistonBase(p_155912_, entity, direction, d0);
                  }
               }
            }
         }
      }
   }

   private static void moveEntityByPiston(Direction p_60372_, Entity p_60373_, double p_60374_, Direction p_60375_) {
      NOCLIP.set(p_60372_);
      p_60373_.move(MoverType.PISTON, new Vec3(p_60374_ * (double)p_60375_.getStepX(), p_60374_ * (double)p_60375_.getStepY(), p_60374_ * (double)p_60375_.getStepZ()));
      NOCLIP.set((Direction)null);
   }

   private static void moveStuckEntities(Level p_155932_, BlockPos p_155933_, float p_155934_, PistonMovingBlockEntity p_155935_) {
      if (p_155935_.isStickyForEntities()) {
         Direction direction = p_155935_.getMovementDirection();
         if (direction.getAxis().isHorizontal()) {
            double d0 = p_155935_.movedState.getCollisionShape(p_155932_, p_155933_).max(Direction.Axis.Y);
            AABB aabb = moveByPositionAndProgress(p_155933_, new AABB(0.0D, d0, 0.0D, 1.0D, 1.5000000999999998D, 1.0D), p_155935_);
            double d1 = (double)(p_155934_ - p_155935_.progress);

            for(Entity entity : p_155932_.getEntities((Entity)null, aabb, (p_60384_) -> {
               return matchesStickyCritera(aabb, p_60384_);
            })) {
               moveEntityByPiston(direction, entity, d1, direction);
            }

         }
      }
   }

   private static boolean matchesStickyCritera(AABB p_60365_, Entity p_60366_) {
      return p_60366_.getPistonPushReaction() == PushReaction.NORMAL && p_60366_.isOnGround() && p_60366_.getX() >= p_60365_.minX && p_60366_.getX() <= p_60365_.maxX && p_60366_.getZ() >= p_60365_.minZ && p_60366_.getZ() <= p_60365_.maxZ;
   }

   private boolean isStickyForEntities() {
      return this.movedState.is(Blocks.HONEY_BLOCK);
   }

   public Direction getMovementDirection() {
      return this.extending ? this.direction : this.direction.getOpposite();
   }

   private static double getMovement(AABB p_60368_, Direction p_60369_, AABB p_60370_) {
      switch(p_60369_) {
      case EAST:
         return p_60368_.maxX - p_60370_.minX;
      case WEST:
         return p_60370_.maxX - p_60368_.minX;
      case UP:
      default:
         return p_60368_.maxY - p_60370_.minY;
      case DOWN:
         return p_60370_.maxY - p_60368_.minY;
      case SOUTH:
         return p_60368_.maxZ - p_60370_.minZ;
      case NORTH:
         return p_60370_.maxZ - p_60368_.minZ;
      }
   }

   private static AABB moveByPositionAndProgress(BlockPos p_155926_, AABB p_155927_, PistonMovingBlockEntity p_155928_) {
      double d0 = (double)p_155928_.getExtendedProgress(p_155928_.progress);
      return p_155927_.move((double)p_155926_.getX() + d0 * (double)p_155928_.direction.getStepX(), (double)p_155926_.getY() + d0 * (double)p_155928_.direction.getStepY(), (double)p_155926_.getZ() + d0 * (double)p_155928_.direction.getStepZ());
   }

   private static void fixEntityWithinPistonBase(BlockPos p_155921_, Entity p_155922_, Direction p_155923_, double p_155924_) {
      AABB aabb = p_155922_.getBoundingBox();
      AABB aabb1 = Shapes.block().bounds().move(p_155921_);
      if (aabb.intersects(aabb1)) {
         Direction direction = p_155923_.getOpposite();
         double d0 = getMovement(aabb1, direction, aabb) + 0.01D;
         double d1 = getMovement(aabb1, direction, aabb.intersect(aabb1)) + 0.01D;
         if (Math.abs(d0 - d1) < 0.01D) {
            d0 = Math.min(d0, p_155924_) + 0.01D;
            moveEntityByPiston(p_155923_, p_155922_, d0, direction);
         }
      }

   }

   public BlockState getMovedState() {
      return this.movedState;
   }

   public void finalTick() {
      if (this.level != null && (this.progressO < 1.0F || this.level.isClientSide)) {
         this.progress = 1.0F;
         this.progressO = this.progress;
         this.level.removeBlockEntity(this.worldPosition);
         this.setRemoved();
         if (this.level.getBlockState(this.worldPosition).is(Blocks.MOVING_PISTON)) {
            BlockState blockstate;
            if (this.isSourcePiston) {
               blockstate = Blocks.AIR.defaultBlockState();
            } else {
               blockstate = Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);
            }

            this.level.setBlock(this.worldPosition, blockstate, 3);
            this.level.neighborChanged(this.worldPosition, blockstate.getBlock(), this.worldPosition);
         }
      }

   }

   public static void tick(Level p_155916_, BlockPos p_155917_, BlockState p_155918_, PistonMovingBlockEntity p_155919_) {
      p_155919_.lastTicked = p_155916_.getGameTime();
      p_155919_.progressO = p_155919_.progress;
      if (p_155919_.progressO >= 1.0F) {
         if (p_155916_.isClientSide && p_155919_.deathTicks < 5) {
            ++p_155919_.deathTicks;
         } else {
            p_155916_.removeBlockEntity(p_155917_);
            p_155919_.setRemoved();
            if (p_155916_.getBlockState(p_155917_).is(Blocks.MOVING_PISTON)) {
               BlockState blockstate = Block.updateFromNeighbourShapes(p_155919_.movedState, p_155916_, p_155917_);
               if (blockstate.isAir()) {
                  p_155916_.setBlock(p_155917_, p_155919_.movedState, 84);
                  Block.updateOrDestroy(p_155919_.movedState, blockstate, p_155916_, p_155917_, 3);
               } else {
                  if (blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(BlockStateProperties.WATERLOGGED)) {
                     blockstate = blockstate.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
                  }

                  p_155916_.setBlock(p_155917_, blockstate, 67);
                  p_155916_.neighborChanged(p_155917_, blockstate.getBlock(), p_155917_);
               }
            }

         }
      } else {
         float f = p_155919_.progress + 0.5F;
         moveCollidedEntities(p_155916_, p_155917_, f, p_155919_);
         moveStuckEntities(p_155916_, p_155917_, f, p_155919_);
         p_155919_.progress = f;
         if (p_155919_.progress >= 1.0F) {
            p_155919_.progress = 1.0F;
         }

      }
   }

   public void load(CompoundTag p_155930_) {
      super.load(p_155930_);
      this.movedState = NbtUtils.readBlockState(p_155930_.getCompound("blockState"));
      this.direction = Direction.from3DDataValue(p_155930_.getInt("facing"));
      this.progress = p_155930_.getFloat("progress");
      this.progressO = this.progress;
      this.extending = p_155930_.getBoolean("extending");
      this.isSourcePiston = p_155930_.getBoolean("source");
   }

   protected void saveAdditional(CompoundTag p_187530_) {
      super.saveAdditional(p_187530_);
      p_187530_.put("blockState", NbtUtils.writeBlockState(this.movedState));
      p_187530_.putInt("facing", this.direction.get3DDataValue());
      p_187530_.putFloat("progress", this.progressO);
      p_187530_.putBoolean("extending", this.extending);
      p_187530_.putBoolean("source", this.isSourcePiston);
   }

   public VoxelShape getCollisionShape(BlockGetter p_60357_, BlockPos p_60358_) {
      VoxelShape voxelshape;
      if (!this.extending && this.isSourcePiston && this.movedState.getBlock() instanceof PistonBaseBlock) {
         voxelshape = this.movedState.setValue(PistonBaseBlock.EXTENDED, Boolean.valueOf(true)).getCollisionShape(p_60357_, p_60358_);
      } else {
         voxelshape = Shapes.empty();
      }

      Direction direction = NOCLIP.get();
      if ((double)this.progress < 1.0D && direction == this.getMovementDirection()) {
         return voxelshape;
      } else {
         BlockState blockstate;
         if (this.isSourcePiston()) {
            blockstate = Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, this.direction).setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 0.25F));
         } else {
            blockstate = this.movedState;
         }

         float f = this.getExtendedProgress(this.progress);
         double d0 = (double)((float)this.direction.getStepX() * f);
         double d1 = (double)((float)this.direction.getStepY() * f);
         double d2 = (double)((float)this.direction.getStepZ() * f);
         return Shapes.or(voxelshape, blockstate.getCollisionShape(p_60357_, p_60358_).move(d0, d1, d2));
      }
   }

   public long getLastTicked() {
      return this.lastTicked;
   }
}
