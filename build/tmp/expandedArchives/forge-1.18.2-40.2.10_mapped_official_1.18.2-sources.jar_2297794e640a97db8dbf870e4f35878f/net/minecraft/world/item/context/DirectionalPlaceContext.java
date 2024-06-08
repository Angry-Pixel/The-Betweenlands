package net.minecraft.world.item.context;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DirectionalPlaceContext extends BlockPlaceContext {
   private final Direction direction;

   public DirectionalPlaceContext(Level p_43650_, BlockPos p_43651_, Direction p_43652_, ItemStack p_43653_, Direction p_43654_) {
      super(p_43650_, (Player)null, InteractionHand.MAIN_HAND, p_43653_, new BlockHitResult(Vec3.atBottomCenterOf(p_43651_), p_43654_, p_43651_, false));
      this.direction = p_43652_;
   }

   public BlockPos getClickedPos() {
      return this.getHitResult().getBlockPos();
   }

   public boolean canPlace() {
      return this.getLevel().getBlockState(this.getHitResult().getBlockPos()).canBeReplaced(this);
   }

   public boolean replacingClickedOnBlock() {
      return this.canPlace();
   }

   public Direction getNearestLookingDirection() {
      return Direction.DOWN;
   }

   public Direction[] getNearestLookingDirections() {
      switch(this.direction) {
      case DOWN:
      default:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP};
      case UP:
         return new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
      case NORTH:
         return new Direction[]{Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.SOUTH};
      case SOUTH:
         return new Direction[]{Direction.DOWN, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.NORTH};
      case WEST:
         return new Direction[]{Direction.DOWN, Direction.WEST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.EAST};
      case EAST:
         return new Direction[]{Direction.DOWN, Direction.EAST, Direction.SOUTH, Direction.UP, Direction.NORTH, Direction.WEST};
      }
   }

   public Direction getHorizontalDirection() {
      return this.direction.getAxis() == Direction.Axis.Y ? Direction.NORTH : this.direction;
   }

   public boolean isSecondaryUseActive() {
      return false;
   }

   public float getRotation() {
      return (float)(this.direction.get2DDataValue() * 90);
   }
}