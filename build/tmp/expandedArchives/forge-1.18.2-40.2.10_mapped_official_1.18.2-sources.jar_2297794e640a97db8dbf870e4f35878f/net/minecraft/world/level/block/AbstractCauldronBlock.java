package net.minecraft.world.level.block;

import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractCauldronBlock extends Block {
   private static final int SIDE_THICKNESS = 2;
   private static final int LEG_WIDTH = 4;
   private static final int LEG_HEIGHT = 3;
   private static final int LEG_DEPTH = 2;
   protected static final int FLOOR_LEVEL = 4;
   private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   protected static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanOp.ONLY_FIRST);
   private final Map<Item, CauldronInteraction> interactions;

   public AbstractCauldronBlock(BlockBehaviour.Properties p_151946_, Map<Item, CauldronInteraction> p_151947_) {
      super(p_151946_);
      this.interactions = p_151947_;
   }

   protected double getContentHeight(BlockState p_151948_) {
      return 0.0D;
   }

   protected boolean isEntityInsideContent(BlockState p_151980_, BlockPos p_151981_, Entity p_151982_) {
      return p_151982_.getY() < (double)p_151981_.getY() + this.getContentHeight(p_151980_) && p_151982_.getBoundingBox().maxY > (double)p_151981_.getY() + 0.25D;
   }

   public InteractionResult use(BlockState p_151969_, Level p_151970_, BlockPos p_151971_, Player p_151972_, InteractionHand p_151973_, BlockHitResult p_151974_) {
      ItemStack itemstack = p_151972_.getItemInHand(p_151973_);
      CauldronInteraction cauldroninteraction = this.interactions.get(itemstack.getItem());
      return cauldroninteraction.interact(p_151969_, p_151970_, p_151971_, p_151972_, p_151973_, itemstack);
   }

   public VoxelShape getShape(BlockState p_151964_, BlockGetter p_151965_, BlockPos p_151966_, CollisionContext p_151967_) {
      return SHAPE;
   }

   public VoxelShape getInteractionShape(BlockState p_151955_, BlockGetter p_151956_, BlockPos p_151957_) {
      return INSIDE;
   }

   public boolean hasAnalogOutputSignal(BlockState p_151986_) {
      return true;
   }

   public boolean isPathfindable(BlockState p_151959_, BlockGetter p_151960_, BlockPos p_151961_, PathComputationType p_151962_) {
      return false;
   }

   public abstract boolean isFull(BlockState p_151984_);

   public void tick(BlockState p_151950_, ServerLevel p_151951_, BlockPos p_151952_, Random p_151953_) {
      BlockPos blockpos = PointedDripstoneBlock.findStalactiteTipAboveCauldron(p_151951_, p_151952_);
      if (blockpos != null) {
         Fluid fluid = PointedDripstoneBlock.getCauldronFillFluidType(p_151951_, blockpos);
         if (fluid != Fluids.EMPTY && this.canReceiveStalactiteDrip(fluid)) {
            this.receiveStalactiteDrip(p_151950_, p_151951_, p_151952_, fluid);
         }

      }
   }

   protected boolean canReceiveStalactiteDrip(Fluid p_151983_) {
      return false;
   }

   protected void receiveStalactiteDrip(BlockState p_151975_, Level p_151976_, BlockPos p_151977_, Fluid p_151978_) {
   }
}