package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireHookBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final int WIRE_DIST_MIN = 1;
   protected static final int WIRE_DIST_MAX = 42;
   private static final int RECHECK_PERIOD = 10;
   protected static final int AABB_OFFSET = 3;
   protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
   protected static final VoxelShape WEST_AABB = Block.box(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);

   public TripWireHookBlock(BlockBehaviour.Properties p_57676_) {
      super(p_57676_);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState p_57740_, BlockGetter p_57741_, BlockPos p_57742_, CollisionContext p_57743_) {
      switch((Direction)p_57740_.getValue(FACING)) {
      case EAST:
      default:
         return EAST_AABB;
      case WEST:
         return WEST_AABB;
      case SOUTH:
         return SOUTH_AABB;
      case NORTH:
         return NORTH_AABB;
      }
   }

   public boolean canSurvive(BlockState p_57721_, LevelReader p_57722_, BlockPos p_57723_) {
      Direction direction = p_57721_.getValue(FACING);
      BlockPos blockpos = p_57723_.relative(direction.getOpposite());
      BlockState blockstate = p_57722_.getBlockState(blockpos);
      return direction.getAxis().isHorizontal() && blockstate.isFaceSturdy(p_57722_, blockpos, direction);
   }

   public BlockState updateShape(BlockState p_57731_, Direction p_57732_, BlockState p_57733_, LevelAccessor p_57734_, BlockPos p_57735_, BlockPos p_57736_) {
      return p_57732_.getOpposite() == p_57731_.getValue(FACING) && !p_57731_.canSurvive(p_57734_, p_57735_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_57731_, p_57732_, p_57733_, p_57734_, p_57735_, p_57736_);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext p_57678_) {
      BlockState blockstate = this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false));
      LevelReader levelreader = p_57678_.getLevel();
      BlockPos blockpos = p_57678_.getClickedPos();
      Direction[] adirection = p_57678_.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.setValue(FACING, direction1);
            if (blockstate.canSurvive(levelreader, blockpos)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   public void setPlacedBy(Level p_57680_, BlockPos p_57681_, BlockState p_57682_, LivingEntity p_57683_, ItemStack p_57684_) {
      this.calculateState(p_57680_, p_57681_, p_57682_, false, false, -1, (BlockState)null);
   }

   public void calculateState(Level p_57686_, BlockPos p_57687_, BlockState p_57688_, boolean p_57689_, boolean p_57690_, int p_57691_, @Nullable BlockState p_57692_) {
      Direction direction = p_57688_.getValue(FACING);
      boolean flag = p_57688_.getValue(ATTACHED);
      boolean flag1 = p_57688_.getValue(POWERED);
      boolean flag2 = !p_57689_;
      boolean flag3 = false;
      int i = 0;
      BlockState[] ablockstate = new BlockState[42];

      for(int j = 1; j < 42; ++j) {
         BlockPos blockpos = p_57687_.relative(direction, j);
         BlockState blockstate = p_57686_.getBlockState(blockpos);
         if (blockstate.is(Blocks.TRIPWIRE_HOOK)) {
            if (blockstate.getValue(FACING) == direction.getOpposite()) {
               i = j;
            }
            break;
         }

         if (!blockstate.is(Blocks.TRIPWIRE) && j != p_57691_) {
            ablockstate[j] = null;
            flag2 = false;
         } else {
            if (j == p_57691_) {
               blockstate = MoreObjects.firstNonNull(p_57692_, blockstate);
            }

            boolean flag4 = !blockstate.getValue(TripWireBlock.DISARMED);
            boolean flag5 = blockstate.getValue(TripWireBlock.POWERED);
            flag3 |= flag4 && flag5;
            ablockstate[j] = blockstate;
            if (j == p_57691_) {
               p_57686_.scheduleTick(p_57687_, this, 10);
               flag2 &= flag4;
            }
         }
      }

      flag2 &= i > 1;
      flag3 &= flag2;
      BlockState blockstate1 = this.defaultBlockState().setValue(ATTACHED, Boolean.valueOf(flag2)).setValue(POWERED, Boolean.valueOf(flag3));
      if (i > 0) {
         BlockPos blockpos1 = p_57687_.relative(direction, i);
         Direction direction1 = direction.getOpposite();
         p_57686_.setBlock(blockpos1, blockstate1.setValue(FACING, direction1), 3);
         this.notifyNeighbors(p_57686_, blockpos1, direction1);
         this.playSound(p_57686_, blockpos1, flag2, flag3, flag, flag1);
      }

      this.playSound(p_57686_, p_57687_, flag2, flag3, flag, flag1);
      if (!p_57689_) {
         p_57686_.setBlock(p_57687_, blockstate1.setValue(FACING, direction), 3);
         if (p_57690_) {
            this.notifyNeighbors(p_57686_, p_57687_, direction);
         }
      }

      if (flag != flag2) {
         for(int k = 1; k < i; ++k) {
            BlockPos blockpos2 = p_57687_.relative(direction, k);
            BlockState blockstate2 = ablockstate[k];
            if (blockstate2 != null) {
               if (!p_57686_.getBlockState(blockpos2).isAir()) { // FORGE: fix MC-129055
               p_57686_.setBlock(blockpos2, blockstate2.setValue(ATTACHED, Boolean.valueOf(flag2)), 3);
               }
            }
         }
      }

   }

   public void tick(BlockState p_57705_, ServerLevel p_57706_, BlockPos p_57707_, Random p_57708_) {
      this.calculateState(p_57706_, p_57707_, p_57705_, false, true, -1, (BlockState)null);
   }

   private void playSound(Level p_57698_, BlockPos p_57699_, boolean p_57700_, boolean p_57701_, boolean p_57702_, boolean p_57703_) {
      if (p_57701_ && !p_57703_) {
         p_57698_.playSound((Player)null, p_57699_, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4F, 0.6F);
         p_57698_.gameEvent(GameEvent.BLOCK_PRESS, p_57699_);
      } else if (!p_57701_ && p_57703_) {
         p_57698_.playSound((Player)null, p_57699_, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4F, 0.5F);
         p_57698_.gameEvent(GameEvent.BLOCK_UNPRESS, p_57699_);
      } else if (p_57700_ && !p_57702_) {
         p_57698_.playSound((Player)null, p_57699_, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4F, 0.7F);
         p_57698_.gameEvent(GameEvent.BLOCK_ATTACH, p_57699_);
      } else if (!p_57700_ && p_57702_) {
         p_57698_.playSound((Player)null, p_57699_, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4F, 1.2F / (p_57698_.random.nextFloat() * 0.2F + 0.9F));
         p_57698_.gameEvent(GameEvent.BLOCK_DETACH, p_57699_);
      }

   }

   private void notifyNeighbors(Level p_57694_, BlockPos p_57695_, Direction p_57696_) {
      p_57694_.updateNeighborsAt(p_57695_, this);
      p_57694_.updateNeighborsAt(p_57695_.relative(p_57696_.getOpposite()), this);
   }

   public void onRemove(BlockState p_57715_, Level p_57716_, BlockPos p_57717_, BlockState p_57718_, boolean p_57719_) {
      if (!p_57719_ && !p_57715_.is(p_57718_.getBlock())) {
         boolean flag = p_57715_.getValue(ATTACHED);
         boolean flag1 = p_57715_.getValue(POWERED);
         if (flag || flag1) {
            this.calculateState(p_57716_, p_57717_, p_57715_, true, false, -1, (BlockState)null);
         }

         if (flag1) {
            p_57716_.updateNeighborsAt(p_57717_, this);
            p_57716_.updateNeighborsAt(p_57717_.relative(p_57715_.getValue(FACING).getOpposite()), this);
         }

         super.onRemove(p_57715_, p_57716_, p_57717_, p_57718_, p_57719_);
      }
   }

   public int getSignal(BlockState p_57710_, BlockGetter p_57711_, BlockPos p_57712_, Direction p_57713_) {
      return p_57710_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_57745_, BlockGetter p_57746_, BlockPos p_57747_, Direction p_57748_) {
      if (!p_57745_.getValue(POWERED)) {
         return 0;
      } else {
         return p_57745_.getValue(FACING) == p_57748_ ? 15 : 0;
      }
   }

   public boolean isSignalSource(BlockState p_57750_) {
      return true;
   }

   public BlockState rotate(BlockState p_57728_, Rotation p_57729_) {
      return p_57728_.setValue(FACING, p_57729_.rotate(p_57728_.getValue(FACING)));
   }

   public BlockState mirror(BlockState p_57725_, Mirror p_57726_) {
      return p_57725_.rotate(p_57726_.getRotation(p_57725_.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57738_) {
      p_57738_.add(FACING, POWERED, ATTACHED);
   }
}
