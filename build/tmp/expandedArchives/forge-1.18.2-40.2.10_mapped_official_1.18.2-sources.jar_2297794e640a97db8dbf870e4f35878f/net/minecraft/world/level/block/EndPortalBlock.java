package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalBlock extends BaseEntityBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 6.0D, 0.0D, 16.0D, 12.0D, 16.0D);

   public EndPortalBlock(BlockBehaviour.Properties p_53017_) {
      super(p_53017_);
   }

   public BlockEntity newBlockEntity(BlockPos p_153196_, BlockState p_153197_) {
      return new TheEndPortalBlockEntity(p_153196_, p_153197_);
   }

   public VoxelShape getShape(BlockState p_53038_, BlockGetter p_53039_, BlockPos p_53040_, CollisionContext p_53041_) {
      return SHAPE;
   }

   public void entityInside(BlockState p_53025_, Level p_53026_, BlockPos p_53027_, Entity p_53028_) {
      if (p_53026_ instanceof ServerLevel && !p_53028_.isPassenger() && !p_53028_.isVehicle() && p_53028_.canChangeDimensions() && Shapes.joinIsNotEmpty(Shapes.create(p_53028_.getBoundingBox().move((double)(-p_53027_.getX()), (double)(-p_53027_.getY()), (double)(-p_53027_.getZ()))), p_53025_.getShape(p_53026_, p_53027_), BooleanOp.AND)) {
         ResourceKey<Level> resourcekey = p_53026_.dimension() == Level.END ? Level.OVERWORLD : Level.END;
         ServerLevel serverlevel = ((ServerLevel)p_53026_).getServer().getLevel(resourcekey);
         if (serverlevel == null) {
            return;
         }

         p_53028_.changeDimension(serverlevel);
      }

   }

   public void animateTick(BlockState p_53030_, Level p_53031_, BlockPos p_53032_, Random p_53033_) {
      double d0 = (double)p_53032_.getX() + p_53033_.nextDouble();
      double d1 = (double)p_53032_.getY() + 0.8D;
      double d2 = (double)p_53032_.getZ() + p_53033_.nextDouble();
      p_53031_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   public ItemStack getCloneItemStack(BlockGetter p_53021_, BlockPos p_53022_, BlockState p_53023_) {
      return ItemStack.EMPTY;
   }

   public boolean canBeReplaced(BlockState p_53035_, Fluid p_53036_) {
      return false;
   }
}