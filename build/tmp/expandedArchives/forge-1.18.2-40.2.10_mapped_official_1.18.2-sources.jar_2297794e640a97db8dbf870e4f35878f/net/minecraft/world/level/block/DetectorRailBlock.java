package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;

public class DetectorRailBlock extends BaseRailBlock {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final int PRESSED_CHECK_PERIOD = 20;

   public DetectorRailBlock(BlockBehaviour.Properties p_52431_) {
      super(true, p_52431_);
      this.registerDefaultState();
   }

   protected void registerDefaultState() {
      this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)).setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   public boolean isSignalSource(BlockState p_52489_) {
      return true;
   }

   public void entityInside(BlockState p_52458_, Level p_52459_, BlockPos p_52460_, Entity p_52461_) {
      if (!p_52459_.isClientSide) {
         if (!p_52458_.getValue(POWERED)) {
            this.checkPressed(p_52459_, p_52460_, p_52458_);
         }
      }
   }

   public void tick(BlockState p_52444_, ServerLevel p_52445_, BlockPos p_52446_, Random p_52447_) {
      if (p_52444_.getValue(POWERED)) {
         this.checkPressed(p_52445_, p_52446_, p_52444_);
      }
   }

   public int getSignal(BlockState p_52449_, BlockGetter p_52450_, BlockPos p_52451_, Direction p_52452_) {
      return p_52449_.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState p_52478_, BlockGetter p_52479_, BlockPos p_52480_, Direction p_52481_) {
      if (!p_52478_.getValue(POWERED)) {
         return 0;
      } else {
         return p_52481_ == Direction.UP ? 15 : 0;
      }
   }

   private void checkPressed(Level p_52433_, BlockPos p_52434_, BlockState p_52435_) {
      if (this.canSurvive(p_52435_, p_52433_, p_52434_)) {
         boolean flag = p_52435_.getValue(POWERED);
         boolean flag1 = false;
         List<AbstractMinecart> list = this.getInteractingMinecartOfType(p_52433_, p_52434_, AbstractMinecart.class, (p_153125_) -> {
            return true;
         });
         if (!list.isEmpty()) {
            flag1 = true;
         }

         if (flag1 && !flag) {
            BlockState blockstate = p_52435_.setValue(POWERED, Boolean.valueOf(true));
            p_52433_.setBlock(p_52434_, blockstate, 3);
            this.updatePowerToConnected(p_52433_, p_52434_, blockstate, true);
            p_52433_.updateNeighborsAt(p_52434_, this);
            p_52433_.updateNeighborsAt(p_52434_.below(), this);
            p_52433_.setBlocksDirty(p_52434_, p_52435_, blockstate);
         }

         if (!flag1 && flag) {
            BlockState blockstate1 = p_52435_.setValue(POWERED, Boolean.valueOf(false));
            p_52433_.setBlock(p_52434_, blockstate1, 3);
            this.updatePowerToConnected(p_52433_, p_52434_, blockstate1, false);
            p_52433_.updateNeighborsAt(p_52434_, this);
            p_52433_.updateNeighborsAt(p_52434_.below(), this);
            p_52433_.setBlocksDirty(p_52434_, p_52435_, blockstate1);
         }

         if (flag1) {
            p_52433_.scheduleTick(p_52434_, this, 20);
         }

         p_52433_.updateNeighbourForOutputSignal(p_52434_, this);
      }
   }

   protected void updatePowerToConnected(Level p_52473_, BlockPos p_52474_, BlockState p_52475_, boolean p_52476_) {
      RailState railstate = new RailState(p_52473_, p_52474_, p_52475_);

      for(BlockPos blockpos : railstate.getConnections()) {
         BlockState blockstate = p_52473_.getBlockState(blockpos);
         blockstate.neighborChanged(p_52473_, blockpos, blockstate.getBlock(), p_52474_, false);
      }

   }

   public void onPlace(BlockState p_52483_, Level p_52484_, BlockPos p_52485_, BlockState p_52486_, boolean p_52487_) {
      if (!p_52486_.is(p_52483_.getBlock())) {
         BlockState blockstate = this.updateState(p_52483_, p_52484_, p_52485_, p_52487_);
         this.checkPressed(p_52484_, p_52485_, blockstate);
      }
   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public boolean hasAnalogOutputSignal(BlockState p_52442_) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState p_52454_, Level p_52455_, BlockPos p_52456_) {
      if (p_52454_.getValue(POWERED)) {
         List<MinecartCommandBlock> list = this.getInteractingMinecartOfType(p_52455_, p_52456_, MinecartCommandBlock.class, (p_153123_) -> {
            return true;
         });
         if (!list.isEmpty()) {
            return list.get(0).getCommandBlock().getSuccessCount();
         }

         List<AbstractMinecart> carts = this.getInteractingMinecartOfType(p_52455_, p_52456_, AbstractMinecart.class, e -> e.isAlive());
         if (!carts.isEmpty() && carts.get(0).getComparatorLevel() > -1) return carts.get(0).getComparatorLevel();
         List<AbstractMinecart> list1 = carts.stream().filter(EntitySelector.CONTAINER_ENTITY_SELECTOR).collect(java.util.stream.Collectors.toList());
         if (!list1.isEmpty()) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)list1.get(0));
         }
      }

      return 0;
   }

   private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level p_52437_, BlockPos p_52438_, Class<T> p_52439_, Predicate<Entity> p_52440_) {
      return p_52437_.getEntitiesOfClass(p_52439_, this.getSearchBB(p_52438_), p_52440_);
   }

   private AABB getSearchBB(BlockPos p_52471_) {
      double d0 = 0.2D;
      return new AABB((double)p_52471_.getX() + 0.2D, (double)p_52471_.getY(), (double)p_52471_.getZ() + 0.2D, (double)(p_52471_.getX() + 1) - 0.2D, (double)(p_52471_.getY() + 1) - 0.2D, (double)(p_52471_.getZ() + 1) - 0.2D);
   }

   public BlockState rotate(BlockState p_52466_, Rotation p_52467_) {
      switch(p_52467_) {
      case CLOCKWISE_180:
         switch((RailShape)p_52466_.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_52466_.setValue(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return p_52466_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return p_52466_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return p_52466_.setValue(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)p_52466_.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return p_52466_.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_52466_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_52466_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_52466_.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return p_52466_.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_52466_.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)p_52466_.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return p_52466_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return p_52466_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_52466_.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_52466_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_52466_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return p_52466_.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_52466_.setValue(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return p_52466_;
      }
   }

   public BlockState mirror(BlockState p_52463_, Mirror p_52464_) {
      RailShape railshape = p_52463_.getValue(SHAPE);
      switch(p_52464_) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return p_52463_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_52463_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_52463_.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_52463_.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_52463_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_52463_.setValue(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(p_52463_, p_52464_);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return p_52463_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_52463_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return p_52463_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_52463_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_52463_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_52463_.setValue(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(p_52463_, p_52464_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52469_) {
      p_52469_.add(getShapeProperty(), POWERED, WATERLOGGED);
   }
}
