package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

public class PoweredRailBlock extends BaseRailBlock {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private final boolean isActivator;  // TRUE for an Activator Rail, FALSE for Powered Rail

   public PoweredRailBlock(BlockBehaviour.Properties p_55218_) {
      this(p_55218_, false);
   }

   protected PoweredRailBlock(BlockBehaviour.Properties p_55218_, boolean isPoweredRail) {
      super(true, p_55218_);
      this.isActivator = !isPoweredRail;
      this.registerDefaultState();
   }

   protected void registerDefaultState() {
      this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(POWERED, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   protected boolean findPoweredRailSignal(Level p_55220_, BlockPos p_55221_, BlockState p_55222_, boolean p_55223_, int p_55224_) {
      if (p_55224_ >= 8) {
         return false;
      } else {
         int i = p_55221_.getX();
         int j = p_55221_.getY();
         int k = p_55221_.getZ();
         boolean flag = true;
         RailShape railshape = p_55222_.getValue(getShapeProperty());
         switch(railshape) {
         case NORTH_SOUTH:
            if (p_55223_) {
               ++k;
            } else {
               --k;
            }
            break;
         case EAST_WEST:
            if (p_55223_) {
               --i;
            } else {
               ++i;
            }
            break;
         case ASCENDING_EAST:
            if (p_55223_) {
               --i;
            } else {
               ++i;
               ++j;
               flag = false;
            }

            railshape = RailShape.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (p_55223_) {
               --i;
               ++j;
               flag = false;
            } else {
               ++i;
            }

            railshape = RailShape.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (p_55223_) {
               ++k;
            } else {
               --k;
               ++j;
               flag = false;
            }

            railshape = RailShape.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (p_55223_) {
               ++k;
               ++j;
               flag = false;
            } else {
               --k;
            }

            railshape = RailShape.NORTH_SOUTH;
         }

         if (this.isSameRailWithPower(p_55220_, new BlockPos(i, j, k), p_55223_, p_55224_, railshape)) {
            return true;
         } else {
            return flag && this.isSameRailWithPower(p_55220_, new BlockPos(i, j - 1, k), p_55223_, p_55224_, railshape);
         }
      }
   }

   protected boolean isSameRailWithPower(Level p_55226_, BlockPos p_55227_, boolean p_55228_, int p_55229_, RailShape p_55230_) {
      BlockState blockstate = p_55226_.getBlockState(p_55227_);
      if (!(blockstate.getBlock() instanceof PoweredRailBlock other)) {
         return false;
      } else {
         RailShape railshape = other.getRailDirection(blockstate, p_55226_, p_55227_, null);
         if (p_55230_ != RailShape.EAST_WEST || railshape != RailShape.NORTH_SOUTH && railshape != RailShape.ASCENDING_NORTH && railshape != RailShape.ASCENDING_SOUTH) {
            if (p_55230_ != RailShape.NORTH_SOUTH || railshape != RailShape.EAST_WEST && railshape != RailShape.ASCENDING_EAST && railshape != RailShape.ASCENDING_WEST) {
               if (isActivatorRail() == other.isActivatorRail()) {
                  return p_55226_.hasNeighborSignal(p_55227_) ? true : other.findPoweredRailSignal(p_55226_, p_55227_, blockstate, p_55228_, p_55229_ + 1);
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected void updateState(BlockState p_55232_, Level p_55233_, BlockPos p_55234_, Block p_55235_) {
      boolean flag = p_55232_.getValue(POWERED);
      boolean flag1 = p_55233_.hasNeighborSignal(p_55234_) || this.findPoweredRailSignal(p_55233_, p_55234_, p_55232_, true, 0) || this.findPoweredRailSignal(p_55233_, p_55234_, p_55232_, false, 0);
      if (flag1 != flag) {
         p_55233_.setBlock(p_55234_, p_55232_.setValue(POWERED, Boolean.valueOf(flag1)), 3);
         p_55233_.updateNeighborsAt(p_55234_.below(), this);
         if (p_55232_.getValue(getShapeProperty()).isAscending()) {
            p_55233_.updateNeighborsAt(p_55234_.above(), this);
         }
      }

   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public BlockState rotate(BlockState p_55240_, Rotation p_55241_) {
      switch(p_55241_) {
      case CLOCKWISE_180:
         switch((RailShape)p_55240_.getValue(SHAPE)) {
         case ASCENDING_EAST:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_55240_.setValue(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return p_55240_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return p_55240_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return p_55240_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_SOUTH: //Forge fix: MC-196102
         case EAST_WEST:
            return p_55240_;
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)p_55240_.getValue(SHAPE)) {
         case NORTH_SOUTH:
            return p_55240_.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_55240_.setValue(SHAPE, RailShape.NORTH_SOUTH);
         case ASCENDING_EAST:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return p_55240_.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_55240_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_55240_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_55240_.setValue(SHAPE, RailShape.NORTH_WEST);
         }
      case CLOCKWISE_90:
         switch((RailShape)p_55240_.getValue(SHAPE)) {
         case NORTH_SOUTH:
            return p_55240_.setValue(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return p_55240_.setValue(SHAPE, RailShape.NORTH_SOUTH);
         case ASCENDING_EAST:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return p_55240_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return p_55240_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_55240_.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_55240_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_55240_.setValue(SHAPE, RailShape.SOUTH_EAST);
         }
      default:
         return p_55240_;
      }
   }

   public BlockState mirror(BlockState p_55237_, Mirror p_55238_) {
      RailShape railshape = p_55237_.getValue(SHAPE);
      switch(p_55238_) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return p_55237_.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return p_55237_.setValue(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return p_55237_.setValue(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return p_55237_.setValue(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return p_55237_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return p_55237_.setValue(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(p_55237_, p_55238_);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return p_55237_.setValue(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return p_55237_.setValue(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return p_55237_.setValue(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return p_55237_.setValue(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return p_55237_.setValue(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return p_55237_.setValue(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(p_55237_, p_55238_);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55243_) {
      p_55243_.add(getShapeProperty(), POWERED, WATERLOGGED);
   }

   public boolean isActivatorRail() {
      return isActivator;
   }
}
