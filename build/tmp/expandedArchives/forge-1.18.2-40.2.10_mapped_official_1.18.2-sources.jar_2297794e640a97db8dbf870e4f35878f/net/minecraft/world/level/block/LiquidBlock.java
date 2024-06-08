package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlock extends Block implements BucketPickup {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
   @Deprecated // Use getFluid
   private final FlowingFluid fluid;
   private final List<FluidState> stateCache;
   public static final VoxelShape STABLE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   public static final ImmutableList<Direction> POSSIBLE_FLOW_DIRECTIONS = ImmutableList.of(Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);

   @Deprecated  // Forge: Use the constructor that takes a supplier
   public LiquidBlock(FlowingFluid p_54694_, BlockBehaviour.Properties p_54695_) {
      super(p_54695_);
      this.fluid = p_54694_;
      this.stateCache = Lists.newArrayList();
      this.stateCache.add(p_54694_.getSource(false));

      for(int i = 1; i < 8; ++i) {
         this.stateCache.add(p_54694_.getFlowing(8 - i, false));
      }

      this.stateCache.add(p_54694_.getFlowing(8, true));
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
      fluidStateCacheInitialized = true;
      supplier = p_54694_.delegate;
   }

   /**
    * @param p_54694_ A fluid supplier such as {@link net.minecraftforge.registries.RegistryObject<FlowingFluid>}
    */
   public LiquidBlock(java.util.function.Supplier<? extends FlowingFluid> p_54694_, BlockBehaviour.Properties p_54695_) {
      super(p_54695_);
      this.fluid = null;
      this.stateCache = Lists.newArrayList();
      this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, Integer.valueOf(0)));
      this.supplier = p_54694_;
   }

   public VoxelShape getCollisionShape(BlockState p_54760_, BlockGetter p_54761_, BlockPos p_54762_, CollisionContext p_54763_) {
      return p_54763_.isAbove(STABLE_SHAPE, p_54762_, true) && p_54760_.getValue(LEVEL) == 0 && p_54763_.canStandOnFluid(p_54761_.getFluidState(p_54762_.above()), p_54760_.getFluidState()) ? STABLE_SHAPE : Shapes.empty();
   }

   public boolean isRandomlyTicking(BlockState p_54732_) {
      return p_54732_.getFluidState().isRandomlyTicking();
   }

   public void randomTick(BlockState p_54740_, ServerLevel p_54741_, BlockPos p_54742_, Random p_54743_) {
      p_54740_.getFluidState().randomTick(p_54741_, p_54742_, p_54743_);
   }

   public boolean propagatesSkylightDown(BlockState p_54745_, BlockGetter p_54746_, BlockPos p_54747_) {
      return false;
   }

   public boolean isPathfindable(BlockState p_54704_, BlockGetter p_54705_, BlockPos p_54706_, PathComputationType p_54707_) {
      return !this.fluid.is(FluidTags.LAVA);
   }

   public FluidState getFluidState(BlockState p_54765_) {
      int i = p_54765_.getValue(LEVEL);
      if (!fluidStateCacheInitialized) initFluidStateCache();
      return this.stateCache.get(Math.min(i, 8));
   }

   public boolean skipRendering(BlockState p_54716_, BlockState p_54717_, Direction p_54718_) {
      return p_54717_.getFluidState().getType().isSame(this.fluid);
   }

   public RenderShape getRenderShape(BlockState p_54738_) {
      return RenderShape.INVISIBLE;
   }

   public List<ItemStack> getDrops(BlockState p_54720_, LootContext.Builder p_54721_) {
      return Collections.emptyList();
   }

   public VoxelShape getShape(BlockState p_54749_, BlockGetter p_54750_, BlockPos p_54751_, CollisionContext p_54752_) {
      return Shapes.empty();
   }

   public void onPlace(BlockState p_54754_, Level p_54755_, BlockPos p_54756_, BlockState p_54757_, boolean p_54758_) {
      if (this.shouldSpreadLiquid(p_54755_, p_54756_, p_54754_)) {
         p_54755_.scheduleTick(p_54756_, p_54754_.getFluidState().getType(), this.fluid.getTickDelay(p_54755_));
      }

   }

   public BlockState updateShape(BlockState p_54723_, Direction p_54724_, BlockState p_54725_, LevelAccessor p_54726_, BlockPos p_54727_, BlockPos p_54728_) {
      if (p_54723_.getFluidState().isSource() || p_54725_.getFluidState().isSource()) {
         p_54726_.scheduleTick(p_54727_, p_54723_.getFluidState().getType(), this.fluid.getTickDelay(p_54726_));
      }

      return super.updateShape(p_54723_, p_54724_, p_54725_, p_54726_, p_54727_, p_54728_);
   }

   public void neighborChanged(BlockState p_54709_, Level p_54710_, BlockPos p_54711_, Block p_54712_, BlockPos p_54713_, boolean p_54714_) {
      if (this.shouldSpreadLiquid(p_54710_, p_54711_, p_54709_)) {
         p_54710_.scheduleTick(p_54711_, p_54709_.getFluidState().getType(), this.fluid.getTickDelay(p_54710_));
      }

   }

   private boolean shouldSpreadLiquid(Level p_54697_, BlockPos p_54698_, BlockState p_54699_) {
      if (this.fluid.is(FluidTags.LAVA)) {
         boolean flag = p_54697_.getBlockState(p_54698_.below()).is(Blocks.SOUL_SOIL);

         for(Direction direction : POSSIBLE_FLOW_DIRECTIONS) {
            BlockPos blockpos = p_54698_.relative(direction.getOpposite());
            if (p_54697_.getFluidState(blockpos).is(FluidTags.WATER)) {
               Block block = p_54697_.getFluidState(p_54698_).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
               p_54697_.setBlockAndUpdate(p_54698_, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_54697_, p_54698_, p_54698_, block.defaultBlockState()));
               this.fizz(p_54697_, p_54698_);
               return false;
            }

            if (flag && p_54697_.getBlockState(blockpos).is(Blocks.BLUE_ICE)) {
               p_54697_.setBlockAndUpdate(p_54698_, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_54697_, p_54698_, p_54698_, Blocks.BASALT.defaultBlockState()));
               this.fizz(p_54697_, p_54698_);
               return false;
            }
         }
      }

      return true;
   }

   private void fizz(LevelAccessor p_54701_, BlockPos p_54702_) {
      p_54701_.levelEvent(1501, p_54702_, 0);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54730_) {
      p_54730_.add(LEVEL);
   }

   public ItemStack pickupBlock(LevelAccessor p_153772_, BlockPos p_153773_, BlockState p_153774_) {
      if (p_153774_.getValue(LEVEL) == 0) {
         p_153772_.setBlock(p_153773_, Blocks.AIR.defaultBlockState(), 11);
         return new ItemStack(this.fluid.getBucket());
      } else {
         return ItemStack.EMPTY;
      }
   }

   // Forge start
   private final java.util.function.Supplier<? extends net.minecraft.world.level.material.Fluid> supplier;
   public FlowingFluid getFluid() {
      return (FlowingFluid)supplier.get();
   }

   private boolean fluidStateCacheInitialized = false;
   protected synchronized void initFluidStateCache() {
      if (fluidStateCacheInitialized == false) {
         this.stateCache.add(getFluid().getSource(false));

         for (int i = 1; i < 8; ++i)
            this.stateCache.add(getFluid().getFlowing(8 - i, false));

         this.stateCache.add(getFluid().getFlowing(8, true));
         fluidStateCacheInitialized = true;
      }
   }

   public Optional<SoundEvent> getPickupSound() {
      return this.fluid.getPickupSound();
   }
}
