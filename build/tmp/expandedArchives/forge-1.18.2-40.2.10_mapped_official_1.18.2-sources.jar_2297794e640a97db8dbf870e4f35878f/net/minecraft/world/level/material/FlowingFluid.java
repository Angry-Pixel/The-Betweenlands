package net.minecraft.world.level.material;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class FlowingFluid extends Fluid {
   public static final BooleanProperty FALLING = BlockStateProperties.FALLING;
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_FLOWING;
   private static final int CACHE_SIZE = 200;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>(200) {
         protected void rehash(int p_76102_) {
         }
      };
      object2bytelinkedopenhashmap.defaultReturnValue((byte)127);
      return object2bytelinkedopenhashmap;
   });
   private final Map<FluidState, VoxelShape> shapes = Maps.newIdentityHashMap();

   protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> p_76046_) {
      p_76046_.add(FALLING);
   }

   public Vec3 getFlow(BlockGetter p_75987_, BlockPos p_75988_, FluidState p_75989_) {
      double d0 = 0.0D;
      double d1 = 0.0D;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         blockpos$mutableblockpos.setWithOffset(p_75988_, direction);
         FluidState fluidstate = p_75987_.getFluidState(blockpos$mutableblockpos);
         if (this.affectsFlow(fluidstate)) {
            float f = fluidstate.getOwnHeight();
            float f1 = 0.0F;
            if (f == 0.0F) {
               if (!p_75987_.getBlockState(blockpos$mutableblockpos).getMaterial().blocksMotion()) {
                  BlockPos blockpos = blockpos$mutableblockpos.below();
                  FluidState fluidstate1 = p_75987_.getFluidState(blockpos);
                  if (this.affectsFlow(fluidstate1)) {
                     f = fluidstate1.getOwnHeight();
                     if (f > 0.0F) {
                        f1 = p_75989_.getOwnHeight() - (f - 0.8888889F);
                     }
                  }
               }
            } else if (f > 0.0F) {
               f1 = p_75989_.getOwnHeight() - f;
            }

            if (f1 != 0.0F) {
               d0 += (double)((float)direction.getStepX() * f1);
               d1 += (double)((float)direction.getStepZ() * f1);
            }
         }
      }

      Vec3 vec3 = new Vec3(d0, 0.0D, d1);
      if (p_75989_.getValue(FALLING)) {
         for(Direction direction1 : Direction.Plane.HORIZONTAL) {
            blockpos$mutableblockpos.setWithOffset(p_75988_, direction1);
            if (this.isSolidFace(p_75987_, blockpos$mutableblockpos, direction1) || this.isSolidFace(p_75987_, blockpos$mutableblockpos.above(), direction1)) {
               vec3 = vec3.normalize().add(0.0D, -6.0D, 0.0D);
               break;
            }
         }
      }

      return vec3.normalize();
   }

   private boolean affectsFlow(FluidState p_76095_) {
      return p_76095_.isEmpty() || p_76095_.getType().isSame(this);
   }

   protected boolean isSolidFace(BlockGetter p_75991_, BlockPos p_75992_, Direction p_75993_) {
      BlockState blockstate = p_75991_.getBlockState(p_75992_);
      FluidState fluidstate = p_75991_.getFluidState(p_75992_);
      if (fluidstate.getType().isSame(this)) {
         return false;
      } else if (p_75993_ == Direction.UP) {
         return true;
      } else {
         return blockstate.getMaterial() == Material.ICE ? false : blockstate.isFaceSturdy(p_75991_, p_75992_, p_75993_);
      }
   }

   protected void spread(LevelAccessor p_76011_, BlockPos p_76012_, FluidState p_76013_) {
      if (!p_76013_.isEmpty()) {
         BlockState blockstate = p_76011_.getBlockState(p_76012_);
         BlockPos blockpos = p_76012_.below();
         BlockState blockstate1 = p_76011_.getBlockState(blockpos);
         FluidState fluidstate = this.getNewLiquid(p_76011_, blockpos, blockstate1);
         if (this.canSpreadTo(p_76011_, p_76012_, blockstate, Direction.DOWN, blockpos, blockstate1, p_76011_.getFluidState(blockpos), fluidstate.getType())) {
            this.spreadTo(p_76011_, blockpos, blockstate1, Direction.DOWN, fluidstate);
            if (this.sourceNeighborCount(p_76011_, p_76012_) >= 3) {
               this.spreadToSides(p_76011_, p_76012_, p_76013_, blockstate);
            }
         } else if (p_76013_.isSource() || !this.isWaterHole(p_76011_, fluidstate.getType(), p_76012_, blockstate, blockpos, blockstate1)) {
            this.spreadToSides(p_76011_, p_76012_, p_76013_, blockstate);
         }

      }
   }

   private void spreadToSides(LevelAccessor p_76015_, BlockPos p_76016_, FluidState p_76017_, BlockState p_76018_) {
      int i = p_76017_.getAmount() - this.getDropOff(p_76015_);
      if (p_76017_.getValue(FALLING)) {
         i = 7;
      }

      if (i > 0) {
         Map<Direction, FluidState> map = this.getSpread(p_76015_, p_76016_, p_76018_);

         for(Entry<Direction, FluidState> entry : map.entrySet()) {
            Direction direction = entry.getKey();
            FluidState fluidstate = entry.getValue();
            BlockPos blockpos = p_76016_.relative(direction);
            BlockState blockstate = p_76015_.getBlockState(blockpos);
            if (this.canSpreadTo(p_76015_, p_76016_, p_76018_, direction, blockpos, blockstate, p_76015_.getFluidState(blockpos), fluidstate.getType())) {
               this.spreadTo(p_76015_, blockpos, blockstate, direction, fluidstate);
            }
         }

      }
   }

   protected FluidState getNewLiquid(LevelReader p_76036_, BlockPos p_76037_, BlockState p_76038_) {
      int i = 0;
      int j = 0;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_76037_.relative(direction);
         BlockState blockstate = p_76036_.getBlockState(blockpos);
         FluidState fluidstate = blockstate.getFluidState();
         if (fluidstate.getType().isSame(this) && this.canPassThroughWall(direction, p_76036_, p_76037_, p_76038_, blockpos, blockstate)) {
            if (fluidstate.isSource() && net.minecraftforge.event.ForgeEventFactory.canCreateFluidSource(p_76036_, blockpos, blockstate, this.canConvertToSource())) {
               ++j;
            }

            i = Math.max(i, fluidstate.getAmount());
         }
      }

      if (j >= 2) {
         BlockState blockstate1 = p_76036_.getBlockState(p_76037_.below());
         FluidState fluidstate1 = blockstate1.getFluidState();
         if (blockstate1.getMaterial().isSolid() || this.isSourceBlockOfThisType(fluidstate1)) {
            return this.getSource(false);
         }
      }

      BlockPos blockpos1 = p_76037_.above();
      BlockState blockstate2 = p_76036_.getBlockState(blockpos1);
      FluidState fluidstate2 = blockstate2.getFluidState();
      if (!fluidstate2.isEmpty() && fluidstate2.getType().isSame(this) && this.canPassThroughWall(Direction.UP, p_76036_, p_76037_, p_76038_, blockpos1, blockstate2)) {
         return this.getFlowing(8, true);
      } else {
         int k = i - this.getDropOff(p_76036_);
         return k <= 0 ? Fluids.EMPTY.defaultFluidState() : this.getFlowing(k, false);
      }
   }

   private boolean canPassThroughWall(Direction p_76062_, BlockGetter p_76063_, BlockPos p_76064_, BlockState p_76065_, BlockPos p_76066_, BlockState p_76067_) {
      Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap;
      if (!p_76065_.getBlock().hasDynamicShape() && !p_76067_.getBlock().hasDynamicShape()) {
         object2bytelinkedopenhashmap = OCCLUSION_CACHE.get();
      } else {
         object2bytelinkedopenhashmap = null;
      }

      Block.BlockStatePairKey block$blockstatepairkey;
      if (object2bytelinkedopenhashmap != null) {
         block$blockstatepairkey = new Block.BlockStatePairKey(p_76065_, p_76067_, p_76062_);
         byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block$blockstatepairkey);
         if (b0 != 127) {
            return b0 != 0;
         }
      } else {
         block$blockstatepairkey = null;
      }

      VoxelShape voxelshape1 = p_76065_.getCollisionShape(p_76063_, p_76064_);
      VoxelShape voxelshape = p_76067_.getCollisionShape(p_76063_, p_76066_);
      boolean flag = !Shapes.mergedFaceOccludes(voxelshape1, voxelshape, p_76062_);
      if (object2bytelinkedopenhashmap != null) {
         if (object2bytelinkedopenhashmap.size() == 200) {
            object2bytelinkedopenhashmap.removeLastByte();
         }

         object2bytelinkedopenhashmap.putAndMoveToFirst(block$blockstatepairkey, (byte)(flag ? 1 : 0));
      }

      return flag;
   }

   public abstract Fluid getFlowing();

   public FluidState getFlowing(int p_75954_, boolean p_75955_) {
      return this.getFlowing().defaultFluidState().setValue(LEVEL, Integer.valueOf(p_75954_)).setValue(FALLING, Boolean.valueOf(p_75955_));
   }

   public abstract Fluid getSource();

   public FluidState getSource(boolean p_76069_) {
      return this.getSource().defaultFluidState().setValue(FALLING, Boolean.valueOf(p_76069_));
   }

   protected abstract boolean canConvertToSource();

   protected void spreadTo(LevelAccessor p_76005_, BlockPos p_76006_, BlockState p_76007_, Direction p_76008_, FluidState p_76009_) {
      if (p_76007_.getBlock() instanceof LiquidBlockContainer) {
         ((LiquidBlockContainer)p_76007_.getBlock()).placeLiquid(p_76005_, p_76006_, p_76007_, p_76009_);
      } else {
         if (!p_76007_.isAir()) {
            this.beforeDestroyingBlock(p_76005_, p_76006_, p_76007_);
         }

         p_76005_.setBlock(p_76006_, p_76009_.createLegacyBlock(), 3);
      }

   }

   protected abstract void beforeDestroyingBlock(LevelAccessor p_76002_, BlockPos p_76003_, BlockState p_76004_);

   private static short getCacheKey(BlockPos p_76059_, BlockPos p_76060_) {
      int i = p_76060_.getX() - p_76059_.getX();
      int j = p_76060_.getZ() - p_76059_.getZ();
      return (short)((i + 128 & 255) << 8 | j + 128 & 255);
   }

   protected int getSlopeDistance(LevelReader p_76027_, BlockPos p_76028_, int p_76029_, Direction p_76030_, BlockState p_76031_, BlockPos p_76032_, Short2ObjectMap<Pair<BlockState, FluidState>> p_76033_, Short2BooleanMap p_76034_) {
      int i = 1000;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (direction != p_76030_) {
            BlockPos blockpos = p_76028_.relative(direction);
            short short1 = getCacheKey(p_76032_, blockpos);
            Pair<BlockState, FluidState> pair = p_76033_.computeIfAbsent(short1, (p_192916_) -> {
               BlockState blockstate1 = p_76027_.getBlockState(blockpos);
               return Pair.of(blockstate1, blockstate1.getFluidState());
            });
            BlockState blockstate = pair.getFirst();
            FluidState fluidstate = pair.getSecond();
            if (this.canPassThrough(p_76027_, this.getFlowing(), p_76028_, p_76031_, direction, blockpos, blockstate, fluidstate)) {
               boolean flag = p_76034_.computeIfAbsent(short1, (p_192912_) -> {
                  BlockPos blockpos1 = blockpos.below();
                  BlockState blockstate1 = p_76027_.getBlockState(blockpos1);
                  return this.isWaterHole(p_76027_, this.getFlowing(), blockpos, blockstate, blockpos1, blockstate1);
               });
               if (flag) {
                  return p_76029_;
               }

               if (p_76029_ < this.getSlopeFindDistance(p_76027_)) {
                  int j = this.getSlopeDistance(p_76027_, blockpos, p_76029_ + 1, direction.getOpposite(), blockstate, p_76032_, p_76033_, p_76034_);
                  if (j < i) {
                     i = j;
                  }
               }
            }
         }
      }

      return i;
   }

   private boolean isWaterHole(BlockGetter p_75957_, Fluid p_75958_, BlockPos p_75959_, BlockState p_75960_, BlockPos p_75961_, BlockState p_75962_) {
      if (!this.canPassThroughWall(Direction.DOWN, p_75957_, p_75959_, p_75960_, p_75961_, p_75962_)) {
         return false;
      } else {
         return p_75962_.getFluidState().getType().isSame(this) ? true : this.canHoldFluid(p_75957_, p_75961_, p_75962_, p_75958_);
      }
   }

   private boolean canPassThrough(BlockGetter p_75964_, Fluid p_75965_, BlockPos p_75966_, BlockState p_75967_, Direction p_75968_, BlockPos p_75969_, BlockState p_75970_, FluidState p_75971_) {
      return !this.isSourceBlockOfThisType(p_75971_) && this.canPassThroughWall(p_75968_, p_75964_, p_75966_, p_75967_, p_75969_, p_75970_) && this.canHoldFluid(p_75964_, p_75969_, p_75970_, p_75965_);
   }

   private boolean isSourceBlockOfThisType(FluidState p_76097_) {
      return p_76097_.getType().isSame(this) && p_76097_.isSource();
   }

   protected abstract int getSlopeFindDistance(LevelReader p_76074_);

   private int sourceNeighborCount(LevelReader p_76020_, BlockPos p_76021_) {
      int i = 0;

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_76021_.relative(direction);
         FluidState fluidstate = p_76020_.getFluidState(blockpos);
         if (this.isSourceBlockOfThisType(fluidstate)) {
            ++i;
         }
      }

      return i;
   }

   protected Map<Direction, FluidState> getSpread(LevelReader p_76080_, BlockPos p_76081_, BlockState p_76082_) {
      int i = 1000;
      Map<Direction, FluidState> map = Maps.newEnumMap(Direction.class);
      Short2ObjectMap<Pair<BlockState, FluidState>> short2objectmap = new Short2ObjectOpenHashMap<>();
      Short2BooleanMap short2booleanmap = new Short2BooleanOpenHashMap();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = p_76081_.relative(direction);
         short short1 = getCacheKey(p_76081_, blockpos);
         Pair<BlockState, FluidState> pair = short2objectmap.computeIfAbsent(short1, (p_192907_) -> {
            BlockState blockstate1 = p_76080_.getBlockState(blockpos);
            return Pair.of(blockstate1, blockstate1.getFluidState());
         });
         BlockState blockstate = pair.getFirst();
         FluidState fluidstate = pair.getSecond();
         FluidState fluidstate1 = this.getNewLiquid(p_76080_, blockpos, blockstate);
         if (this.canPassThrough(p_76080_, fluidstate1.getType(), p_76081_, p_76082_, direction, blockpos, blockstate, fluidstate)) {
            BlockPos blockpos1 = blockpos.below();
            boolean flag = short2booleanmap.computeIfAbsent(short1, (p_192903_) -> {
               BlockState blockstate1 = p_76080_.getBlockState(blockpos1);
               return this.isWaterHole(p_76080_, this.getFlowing(), blockpos, blockstate, blockpos1, blockstate1);
            });
            int j;
            if (flag) {
               j = 0;
            } else {
               j = this.getSlopeDistance(p_76080_, blockpos, 1, direction.getOpposite(), blockstate, p_76081_, short2objectmap, short2booleanmap);
            }

            if (j < i) {
               map.clear();
            }

            if (j <= i) {
               map.put(direction, fluidstate1);
               i = j;
            }
         }
      }

      return map;
   }

   private boolean canHoldFluid(BlockGetter p_75973_, BlockPos p_75974_, BlockState p_75975_, Fluid p_75976_) {
      Block block = p_75975_.getBlock();
      if (block instanceof LiquidBlockContainer) {
         return ((LiquidBlockContainer)block).canPlaceLiquid(p_75973_, p_75974_, p_75975_, p_75976_);
      } else if (!(block instanceof DoorBlock) && !p_75975_.is(BlockTags.SIGNS) && !p_75975_.is(Blocks.LADDER) && !p_75975_.is(Blocks.SUGAR_CANE) && !p_75975_.is(Blocks.BUBBLE_COLUMN)) {
         Material material = p_75975_.getMaterial();
         if (material != Material.PORTAL && material != Material.STRUCTURAL_AIR && material != Material.WATER_PLANT && material != Material.REPLACEABLE_WATER_PLANT) {
            return !material.blocksMotion();
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canSpreadTo(BlockGetter p_75978_, BlockPos p_75979_, BlockState p_75980_, Direction p_75981_, BlockPos p_75982_, BlockState p_75983_, FluidState p_75984_, Fluid p_75985_) {
      return p_75984_.canBeReplacedWith(p_75978_, p_75982_, p_75985_, p_75981_) && this.canPassThroughWall(p_75981_, p_75978_, p_75979_, p_75980_, p_75982_, p_75983_) && this.canHoldFluid(p_75978_, p_75982_, p_75983_, p_75985_);
   }

   protected abstract int getDropOff(LevelReader p_76087_);

   protected int getSpreadDelay(Level p_75998_, BlockPos p_75999_, FluidState p_76000_, FluidState p_76001_) {
      return this.getTickDelay(p_75998_);
   }

   public void tick(Level p_75995_, BlockPos p_75996_, FluidState p_75997_) {
      if (!p_75997_.isSource()) {
         FluidState fluidstate = this.getNewLiquid(p_75995_, p_75996_, p_75995_.getBlockState(p_75996_));
         int i = this.getSpreadDelay(p_75995_, p_75996_, p_75997_, fluidstate);
         if (fluidstate.isEmpty()) {
            p_75997_ = fluidstate;
            p_75995_.setBlock(p_75996_, Blocks.AIR.defaultBlockState(), 3);
         } else if (!fluidstate.equals(p_75997_)) {
            p_75997_ = fluidstate;
            BlockState blockstate = fluidstate.createLegacyBlock();
            p_75995_.setBlock(p_75996_, blockstate, 2);
            p_75995_.scheduleTick(p_75996_, fluidstate.getType(), i);
            p_75995_.updateNeighborsAt(p_75996_, blockstate.getBlock());
         }
      }

      this.spread(p_75995_, p_75996_, p_75997_);
   }

   protected static int getLegacyLevel(FluidState p_76093_) {
      return p_76093_.isSource() ? 0 : 8 - Math.min(p_76093_.getAmount(), 8) + (p_76093_.getValue(FALLING) ? 8 : 0);
   }

   private static boolean hasSameAbove(FluidState p_76089_, BlockGetter p_76090_, BlockPos p_76091_) {
      return p_76089_.getType().isSame(p_76090_.getFluidState(p_76091_.above()).getType());
   }

   public float getHeight(FluidState p_76050_, BlockGetter p_76051_, BlockPos p_76052_) {
      return hasSameAbove(p_76050_, p_76051_, p_76052_) ? 1.0F : p_76050_.getOwnHeight();
   }

   public float getOwnHeight(FluidState p_76048_) {
      return (float)p_76048_.getAmount() / 9.0F;
   }

   public abstract int getAmount(FluidState p_164509_);

   public VoxelShape getShape(FluidState p_76084_, BlockGetter p_76085_, BlockPos p_76086_) {
      return p_76084_.getAmount() == 9 && hasSameAbove(p_76084_, p_76085_, p_76086_) ? Shapes.block() : this.shapes.computeIfAbsent(p_76084_, (p_76073_) -> {
         return Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)p_76073_.getHeight(p_76085_, p_76086_), 1.0D);
      });
   }
}
