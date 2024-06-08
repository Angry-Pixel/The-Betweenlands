package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;

public abstract class DiodeBlock extends HorizontalDirectionalBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   protected DiodeBlock(BlockBehaviour.Properties p_52499_) {
      super(p_52499_);
   }

   public VoxelShape getShape(BlockState p_52556_, BlockGetter p_52557_, BlockPos p_52558_, CollisionContext p_52559_) {
      return SHAPE;
   }

   public boolean canSurvive(BlockState p_52538_, LevelReader p_52539_, BlockPos p_52540_) {
      return canSupportRigidBlock(p_52539_, p_52540_.below());
   }

   public void tick(BlockState p_52515_, ServerLevel p_52516_, BlockPos p_52517_, Random p_52518_) {
      if (!this.isLocked(p_52516_, p_52517_, p_52515_)) {
         boolean flag = p_52515_.getValue(POWERED);
         boolean flag1 = this.shouldTurnOn(p_52516_, p_52517_, p_52515_);
         if (flag && !flag1) {
            p_52516_.setBlock(p_52517_, p_52515_.setValue(POWERED, Boolean.valueOf(false)), 2);
         } else if (!flag) {
            p_52516_.setBlock(p_52517_, p_52515_.setValue(POWERED, Boolean.valueOf(true)), 2);
            if (!flag1) {
               p_52516_.scheduleTick(p_52517_, this, this.getDelay(p_52515_), TickPriority.VERY_HIGH);
            }
         }

      }
   }

   public int getDirectSignal(BlockState p_52561_, BlockGetter p_52562_, BlockPos p_52563_, Direction p_52564_) {
      return p_52561_.getSignal(p_52562_, p_52563_, p_52564_);
   }

   public int getSignal(BlockState p_52520_, BlockGetter p_52521_, BlockPos p_52522_, Direction p_52523_) {
      if (!p_52520_.getValue(POWERED)) {
         return 0;
      } else {
         return p_52520_.getValue(FACING) == p_52523_ ? this.getOutputSignal(p_52521_, p_52522_, p_52520_) : 0;
      }
   }

   public void neighborChanged(BlockState p_52525_, Level p_52526_, BlockPos p_52527_, Block p_52528_, BlockPos p_52529_, boolean p_52530_) {
      if (p_52525_.canSurvive(p_52526_, p_52527_)) {
         this.checkTickOnNeighbor(p_52526_, p_52527_, p_52525_);
      } else {
         BlockEntity blockentity = p_52525_.hasBlockEntity() ? p_52526_.getBlockEntity(p_52527_) : null;
         dropResources(p_52525_, p_52526_, p_52527_, blockentity);
         p_52526_.removeBlock(p_52527_, false);

         for(Direction direction : Direction.values()) {
            p_52526_.updateNeighborsAt(p_52527_.relative(direction), this);
         }

      }
   }

   protected void checkTickOnNeighbor(Level p_52577_, BlockPos p_52578_, BlockState p_52579_) {
      if (!this.isLocked(p_52577_, p_52578_, p_52579_)) {
         boolean flag = p_52579_.getValue(POWERED);
         boolean flag1 = this.shouldTurnOn(p_52577_, p_52578_, p_52579_);
         if (flag != flag1 && !p_52577_.getBlockTicks().willTickThisTick(p_52578_, this)) {
            TickPriority tickpriority = TickPriority.HIGH;
            if (this.shouldPrioritize(p_52577_, p_52578_, p_52579_)) {
               tickpriority = TickPriority.EXTREMELY_HIGH;
            } else if (flag) {
               tickpriority = TickPriority.VERY_HIGH;
            }

            p_52577_.scheduleTick(p_52578_, this, this.getDelay(p_52579_), tickpriority);
         }

      }
   }

   public boolean isLocked(LevelReader p_52511_, BlockPos p_52512_, BlockState p_52513_) {
      return false;
   }

   protected boolean shouldTurnOn(Level p_52502_, BlockPos p_52503_, BlockState p_52504_) {
      return this.getInputSignal(p_52502_, p_52503_, p_52504_) > 0;
   }

   protected int getInputSignal(Level p_52544_, BlockPos p_52545_, BlockState p_52546_) {
      Direction direction = p_52546_.getValue(FACING);
      BlockPos blockpos = p_52545_.relative(direction);
      int i = p_52544_.getSignal(blockpos, direction);
      if (i >= 15) {
         return i;
      } else {
         BlockState blockstate = p_52544_.getBlockState(blockpos);
         return Math.max(i, blockstate.is(Blocks.REDSTONE_WIRE) ? blockstate.getValue(RedStoneWireBlock.POWER) : 0);
      }
   }

   protected int getAlternateSignal(LevelReader p_52548_, BlockPos p_52549_, BlockState p_52550_) {
      Direction direction = p_52550_.getValue(FACING);
      Direction direction1 = direction.getClockWise();
      Direction direction2 = direction.getCounterClockWise();
      return Math.max(this.getAlternateSignalAt(p_52548_, p_52549_.relative(direction1), direction1), this.getAlternateSignalAt(p_52548_, p_52549_.relative(direction2), direction2));
   }

   protected int getAlternateSignalAt(LevelReader p_52552_, BlockPos p_52553_, Direction p_52554_) {
      BlockState blockstate = p_52552_.getBlockState(p_52553_);
      if (this.isAlternateInput(blockstate)) {
         if (blockstate.is(Blocks.REDSTONE_BLOCK)) {
            return 15;
         } else {
            return blockstate.is(Blocks.REDSTONE_WIRE) ? blockstate.getValue(RedStoneWireBlock.POWER) : p_52552_.getDirectSignal(p_52553_, p_52554_);
         }
      } else {
         return 0;
      }
   }

   public boolean isSignalSource(BlockState p_52572_) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_52501_) {
      return this.defaultBlockState().setValue(FACING, p_52501_.getHorizontalDirection().getOpposite());
   }

   public void setPlacedBy(Level p_52506_, BlockPos p_52507_, BlockState p_52508_, LivingEntity p_52509_, ItemStack p_52510_) {
      if (this.shouldTurnOn(p_52506_, p_52507_, p_52508_)) {
         p_52506_.scheduleTick(p_52507_, this, 1);
      }

   }

   public void onPlace(BlockState p_52566_, Level p_52567_, BlockPos p_52568_, BlockState p_52569_, boolean p_52570_) {
      this.updateNeighborsInFront(p_52567_, p_52568_, p_52566_);
   }

   public void onRemove(BlockState p_52532_, Level p_52533_, BlockPos p_52534_, BlockState p_52535_, boolean p_52536_) {
      if (!p_52536_ && !p_52532_.is(p_52535_.getBlock())) {
         super.onRemove(p_52532_, p_52533_, p_52534_, p_52535_, p_52536_);
         this.updateNeighborsInFront(p_52533_, p_52534_, p_52532_);
      }
   }

   protected void updateNeighborsInFront(Level p_52581_, BlockPos p_52582_, BlockState p_52583_) {
      Direction direction = p_52583_.getValue(FACING);
      BlockPos blockpos = p_52582_.relative(direction.getOpposite());
      if (net.minecraftforge.event.ForgeEventFactory.onNeighborNotify(p_52581_, p_52582_, p_52581_.getBlockState(p_52582_), java.util.EnumSet.of(direction.getOpposite()), false).isCanceled())
         return;
      p_52581_.neighborChanged(blockpos, this, p_52582_);
      p_52581_.updateNeighborsAtExceptFromFacing(blockpos, this, direction);
   }

   protected boolean isAlternateInput(BlockState p_52585_) {
      return p_52585_.isSignalSource();
   }

   protected int getOutputSignal(BlockGetter p_52541_, BlockPos p_52542_, BlockState p_52543_) {
      return 15;
   }

   public static boolean isDiode(BlockState p_52587_) {
      return p_52587_.getBlock() instanceof DiodeBlock;
   }

   public boolean shouldPrioritize(BlockGetter p_52574_, BlockPos p_52575_, BlockState p_52576_) {
      Direction direction = p_52576_.getValue(FACING).getOpposite();
      BlockState blockstate = p_52574_.getBlockState(p_52575_.relative(direction));
      return isDiode(blockstate) && blockstate.getValue(FACING) != direction;
   }

   protected abstract int getDelay(BlockState p_52584_);
}
