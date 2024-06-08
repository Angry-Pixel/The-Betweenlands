package net.minecraft.world.level.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireBlock extends Block {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = CrossCollisionBlock.PROPERTY_BY_DIRECTION;
   protected static final VoxelShape AABB = Block.box(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
   protected static final VoxelShape NOT_ATTACHED_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private static final int RECHECK_PERIOD = 10;
   private final TripWireHookBlock hook;

   public TripWireBlock(TripWireHookBlock p_57603_, BlockBehaviour.Properties p_57604_) {
      super(p_57604_);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false)).setValue(DISARMED, Boolean.valueOf(false)).setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)));
      this.hook = p_57603_;
   }

   public VoxelShape getShape(BlockState p_57654_, BlockGetter p_57655_, BlockPos p_57656_, CollisionContext p_57657_) {
      return p_57654_.getValue(ATTACHED) ? AABB : NOT_ATTACHED_AABB;
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_57606_) {
      BlockGetter blockgetter = p_57606_.getLevel();
      BlockPos blockpos = p_57606_.getClickedPos();
      return this.defaultBlockState().setValue(NORTH, Boolean.valueOf(this.shouldConnectTo(blockgetter.getBlockState(blockpos.north()), Direction.NORTH))).setValue(EAST, Boolean.valueOf(this.shouldConnectTo(blockgetter.getBlockState(blockpos.east()), Direction.EAST))).setValue(SOUTH, Boolean.valueOf(this.shouldConnectTo(blockgetter.getBlockState(blockpos.south()), Direction.SOUTH))).setValue(WEST, Boolean.valueOf(this.shouldConnectTo(blockgetter.getBlockState(blockpos.west()), Direction.WEST)));
   }

   public BlockState updateShape(BlockState p_57645_, Direction p_57646_, BlockState p_57647_, LevelAccessor p_57648_, BlockPos p_57649_, BlockPos p_57650_) {
      return p_57646_.getAxis().isHorizontal() ? p_57645_.setValue(PROPERTY_BY_DIRECTION.get(p_57646_), Boolean.valueOf(this.shouldConnectTo(p_57647_, p_57646_))) : super.updateShape(p_57645_, p_57646_, p_57647_, p_57648_, p_57649_, p_57650_);
   }

   public void onPlace(BlockState p_57659_, Level p_57660_, BlockPos p_57661_, BlockState p_57662_, boolean p_57663_) {
      if (!p_57662_.is(p_57659_.getBlock())) {
         this.updateSource(p_57660_, p_57661_, p_57659_);
      }
   }

   public void onRemove(BlockState p_57630_, Level p_57631_, BlockPos p_57632_, BlockState p_57633_, boolean p_57634_) {
      if (!p_57634_ && !p_57630_.is(p_57633_.getBlock())) {
         this.updateSource(p_57631_, p_57632_, p_57630_.setValue(POWERED, Boolean.valueOf(true)));
      }
   }

   public void playerWillDestroy(Level p_57615_, BlockPos p_57616_, BlockState p_57617_, Player p_57618_) {
      if (!p_57615_.isClientSide && !p_57618_.getMainHandItem().isEmpty() && p_57618_.getMainHandItem().canPerformAction(net.minecraftforge.common.ToolActions.SHEARS_DISARM)) {
         p_57615_.setBlock(p_57616_, p_57617_.setValue(DISARMED, Boolean.valueOf(true)), 4);
         p_57615_.gameEvent(p_57618_, GameEvent.SHEAR, p_57616_);
      }

      super.playerWillDestroy(p_57615_, p_57616_, p_57617_, p_57618_);
   }

   private void updateSource(Level p_57611_, BlockPos p_57612_, BlockState p_57613_) {
      for(Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
         for(int i = 1; i < 42; ++i) {
            BlockPos blockpos = p_57612_.relative(direction, i);
            BlockState blockstate = p_57611_.getBlockState(blockpos);
            if (blockstate.is(this.hook)) {
               if (blockstate.getValue(TripWireHookBlock.FACING) == direction.getOpposite()) {
                  this.hook.calculateState(p_57611_, blockpos, blockstate, false, true, i, p_57613_);
               }
               break;
            }

            if (!blockstate.is(this)) {
               break;
            }
         }
      }

   }

   public void entityInside(BlockState p_57625_, Level p_57626_, BlockPos p_57627_, Entity p_57628_) {
      if (!p_57626_.isClientSide) {
         if (!p_57625_.getValue(POWERED)) {
            this.checkPressed(p_57626_, p_57627_);
         }
      }
   }

   public void tick(BlockState p_57620_, ServerLevel p_57621_, BlockPos p_57622_, Random p_57623_) {
      if (p_57621_.getBlockState(p_57622_).getValue(POWERED)) {
         this.checkPressed(p_57621_, p_57622_);
      }
   }

   private void checkPressed(Level p_57608_, BlockPos p_57609_) {
      BlockState blockstate = p_57608_.getBlockState(p_57609_);
      boolean flag = blockstate.getValue(POWERED);
      boolean flag1 = false;
      List<? extends Entity> list = p_57608_.getEntities((Entity)null, blockstate.getShape(p_57608_, p_57609_).bounds().move(p_57609_));
      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.isIgnoringBlockTriggers()) {
               flag1 = true;
               break;
            }
         }
      }

      if (flag1 != flag) {
         blockstate = blockstate.setValue(POWERED, Boolean.valueOf(flag1));
         p_57608_.setBlock(p_57609_, blockstate, 3);
         this.updateSource(p_57608_, p_57609_, blockstate);
      }

      if (flag1) {
         p_57608_.scheduleTick(new BlockPos(p_57609_), this, 10);
      }

   }

   public boolean shouldConnectTo(BlockState p_57642_, Direction p_57643_) {
      if (p_57642_.is(this.hook)) {
         return p_57642_.getValue(TripWireHookBlock.FACING) == p_57643_.getOpposite();
      } else {
         return p_57642_.is(this);
      }
   }

   public BlockState rotate(BlockState p_57639_, Rotation p_57640_) {
      switch(p_57640_) {
      case CLOCKWISE_180:
         return p_57639_.setValue(NORTH, p_57639_.getValue(SOUTH)).setValue(EAST, p_57639_.getValue(WEST)).setValue(SOUTH, p_57639_.getValue(NORTH)).setValue(WEST, p_57639_.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return p_57639_.setValue(NORTH, p_57639_.getValue(EAST)).setValue(EAST, p_57639_.getValue(SOUTH)).setValue(SOUTH, p_57639_.getValue(WEST)).setValue(WEST, p_57639_.getValue(NORTH));
      case CLOCKWISE_90:
         return p_57639_.setValue(NORTH, p_57639_.getValue(WEST)).setValue(EAST, p_57639_.getValue(NORTH)).setValue(SOUTH, p_57639_.getValue(EAST)).setValue(WEST, p_57639_.getValue(SOUTH));
      default:
         return p_57639_;
      }
   }

   public BlockState mirror(BlockState p_57636_, Mirror p_57637_) {
      switch(p_57637_) {
      case LEFT_RIGHT:
         return p_57636_.setValue(NORTH, p_57636_.getValue(SOUTH)).setValue(SOUTH, p_57636_.getValue(NORTH));
      case FRONT_BACK:
         return p_57636_.setValue(EAST, p_57636_.getValue(WEST)).setValue(WEST, p_57636_.getValue(EAST));
      default:
         return super.mirror(p_57636_, p_57637_);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57652_) {
      p_57652_.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }
}
