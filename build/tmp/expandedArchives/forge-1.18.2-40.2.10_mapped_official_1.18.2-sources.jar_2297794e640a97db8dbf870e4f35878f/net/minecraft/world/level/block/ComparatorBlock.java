package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.ticks.TickPriority;

public class ComparatorBlock extends DiodeBlock implements EntityBlock {
   public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.MODE_COMPARATOR;

   public ComparatorBlock(BlockBehaviour.Properties p_51857_) {
      super(p_51857_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(MODE, ComparatorMode.COMPARE));
   }

   protected int getDelay(BlockState p_51912_) {
      return 2;
   }

   protected int getOutputSignal(BlockGetter p_51892_, BlockPos p_51893_, BlockState p_51894_) {
      BlockEntity blockentity = p_51892_.getBlockEntity(p_51893_);
      return blockentity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockentity).getOutputSignal() : 0;
   }

   private int calculateOutputSignal(Level p_51904_, BlockPos p_51905_, BlockState p_51906_) {
      int i = this.getInputSignal(p_51904_, p_51905_, p_51906_);
      if (i == 0) {
         return 0;
      } else {
         int j = this.getAlternateSignal(p_51904_, p_51905_, p_51906_);
         if (j > i) {
            return 0;
         } else {
            return p_51906_.getValue(MODE) == ComparatorMode.SUBTRACT ? i - j : i;
         }
      }
   }

   protected boolean shouldTurnOn(Level p_51861_, BlockPos p_51862_, BlockState p_51863_) {
      int i = this.getInputSignal(p_51861_, p_51862_, p_51863_);
      if (i == 0) {
         return false;
      } else {
         int j = this.getAlternateSignal(p_51861_, p_51862_, p_51863_);
         if (i > j) {
            return true;
         } else {
            return i == j && p_51863_.getValue(MODE) == ComparatorMode.COMPARE;
         }
      }
   }

   protected int getInputSignal(Level p_51896_, BlockPos p_51897_, BlockState p_51898_) {
      int i = super.getInputSignal(p_51896_, p_51897_, p_51898_);
      Direction direction = p_51898_.getValue(FACING);
      BlockPos blockpos = p_51897_.relative(direction);
      BlockState blockstate = p_51896_.getBlockState(blockpos);
      if (blockstate.hasAnalogOutputSignal()) {
         i = blockstate.getAnalogOutputSignal(p_51896_, blockpos);
      } else if (i < 15 && blockstate.isRedstoneConductor(p_51896_, blockpos)) {
         blockpos = blockpos.relative(direction);
         blockstate = p_51896_.getBlockState(blockpos);
         ItemFrame itemframe = this.getItemFrame(p_51896_, direction, blockpos);
         int j = Math.max(itemframe == null ? Integer.MIN_VALUE : itemframe.getAnalogOutput(), blockstate.hasAnalogOutputSignal() ? blockstate.getAnalogOutputSignal(p_51896_, blockpos) : Integer.MIN_VALUE);
         if (j != Integer.MIN_VALUE) {
            i = j;
         }
      }

      return i;
   }

   @Nullable
   private ItemFrame getItemFrame(Level p_51865_, Direction p_51866_, BlockPos p_51867_) {
      List<ItemFrame> list = p_51865_.getEntitiesOfClass(ItemFrame.class, new AABB((double)p_51867_.getX(), (double)p_51867_.getY(), (double)p_51867_.getZ(), (double)(p_51867_.getX() + 1), (double)(p_51867_.getY() + 1), (double)(p_51867_.getZ() + 1)), (p_51890_) -> {
         return p_51890_ != null && p_51890_.getDirection() == p_51866_;
      });
      return list.size() == 1 ? list.get(0) : null;
   }

   public InteractionResult use(BlockState p_51880_, Level p_51881_, BlockPos p_51882_, Player p_51883_, InteractionHand p_51884_, BlockHitResult p_51885_) {
      if (!p_51883_.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         p_51880_ = p_51880_.cycle(MODE);
         float f = p_51880_.getValue(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
         p_51881_.playSound(p_51883_, p_51882_, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, f);
         p_51881_.setBlock(p_51882_, p_51880_, 2);
         this.refreshOutputState(p_51881_, p_51882_, p_51880_);
         return InteractionResult.sidedSuccess(p_51881_.isClientSide);
      }
   }

   protected void checkTickOnNeighbor(Level p_51900_, BlockPos p_51901_, BlockState p_51902_) {
      if (!p_51900_.getBlockTicks().willTickThisTick(p_51901_, this)) {
         int i = this.calculateOutputSignal(p_51900_, p_51901_, p_51902_);
         BlockEntity blockentity = p_51900_.getBlockEntity(p_51901_);
         int j = blockentity instanceof ComparatorBlockEntity ? ((ComparatorBlockEntity)blockentity).getOutputSignal() : 0;
         if (i != j || p_51902_.getValue(POWERED) != this.shouldTurnOn(p_51900_, p_51901_, p_51902_)) {
            TickPriority tickpriority = this.shouldPrioritize(p_51900_, p_51901_, p_51902_) ? TickPriority.HIGH : TickPriority.NORMAL;
            p_51900_.scheduleTick(p_51901_, this, 2, tickpriority);
         }

      }
   }

   private void refreshOutputState(Level p_51908_, BlockPos p_51909_, BlockState p_51910_) {
      int i = this.calculateOutputSignal(p_51908_, p_51909_, p_51910_);
      BlockEntity blockentity = p_51908_.getBlockEntity(p_51909_);
      int j = 0;
      if (blockentity instanceof ComparatorBlockEntity) {
         ComparatorBlockEntity comparatorblockentity = (ComparatorBlockEntity)blockentity;
         j = comparatorblockentity.getOutputSignal();
         comparatorblockentity.setOutputSignal(i);
      }

      if (j != i || p_51910_.getValue(MODE) == ComparatorMode.COMPARE) {
         boolean flag1 = this.shouldTurnOn(p_51908_, p_51909_, p_51910_);
         boolean flag = p_51910_.getValue(POWERED);
         if (flag && !flag1) {
            p_51908_.setBlock(p_51909_, p_51910_.setValue(POWERED, Boolean.valueOf(false)), 2);
         } else if (!flag && flag1) {
            p_51908_.setBlock(p_51909_, p_51910_.setValue(POWERED, Boolean.valueOf(true)), 2);
         }

         this.updateNeighborsInFront(p_51908_, p_51909_, p_51910_);
      }

   }

   public void tick(BlockState p_51869_, ServerLevel p_51870_, BlockPos p_51871_, Random p_51872_) {
      this.refreshOutputState(p_51870_, p_51871_, p_51869_);
   }

   public boolean triggerEvent(BlockState p_51874_, Level p_51875_, BlockPos p_51876_, int p_51877_, int p_51878_) {
      super.triggerEvent(p_51874_, p_51875_, p_51876_, p_51877_, p_51878_);
      BlockEntity blockentity = p_51875_.getBlockEntity(p_51876_);
      return blockentity != null && blockentity.triggerEvent(p_51877_, p_51878_);
   }

   public BlockEntity newBlockEntity(BlockPos p_153086_, BlockState p_153087_) {
      return new ComparatorBlockEntity(p_153086_, p_153087_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51887_) {
      p_51887_.add(FACING, MODE, POWERED);
   }

   @Override
   public boolean getWeakChanges(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos) {
      return state.is(Blocks.COMPARATOR);
   }

   @Override
   public void onNeighborChange(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, BlockPos neighbor) {
      if (pos.getY() == neighbor.getY() && world instanceof Level && !((Level)world).isClientSide()) {
         state.neighborChanged((Level)world, pos, world.getBlockState(neighbor).getBlock(), neighbor, false);
      }
   }
}
