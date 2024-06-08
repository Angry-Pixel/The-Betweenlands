package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DragonEggBlock extends FallingBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   public DragonEggBlock(BlockBehaviour.Properties p_52911_) {
      super(p_52911_);
   }

   public VoxelShape getShape(BlockState p_52930_, BlockGetter p_52931_, BlockPos p_52932_, CollisionContext p_52933_) {
      return SHAPE;
   }

   public InteractionResult use(BlockState p_52923_, Level p_52924_, BlockPos p_52925_, Player p_52926_, InteractionHand p_52927_, BlockHitResult p_52928_) {
      this.teleport(p_52923_, p_52924_, p_52925_);
      return InteractionResult.sidedSuccess(p_52924_.isClientSide);
   }

   public void attack(BlockState p_52918_, Level p_52919_, BlockPos p_52920_, Player p_52921_) {
      this.teleport(p_52918_, p_52919_, p_52920_);
   }

   private void teleport(BlockState p_52936_, Level p_52937_, BlockPos p_52938_) {
      WorldBorder worldborder = p_52937_.getWorldBorder();

      for(int i = 0; i < 1000; ++i) {
         BlockPos blockpos = p_52938_.offset(p_52937_.random.nextInt(16) - p_52937_.random.nextInt(16), p_52937_.random.nextInt(8) - p_52937_.random.nextInt(8), p_52937_.random.nextInt(16) - p_52937_.random.nextInt(16));
         if (p_52937_.getBlockState(blockpos).isAir() && worldborder.isWithinBounds(blockpos)) {
            if (p_52937_.isClientSide) {
               for(int j = 0; j < 128; ++j) {
                  double d0 = p_52937_.random.nextDouble();
                  float f = (p_52937_.random.nextFloat() - 0.5F) * 0.2F;
                  float f1 = (p_52937_.random.nextFloat() - 0.5F) * 0.2F;
                  float f2 = (p_52937_.random.nextFloat() - 0.5F) * 0.2F;
                  double d1 = Mth.lerp(d0, (double)blockpos.getX(), (double)p_52938_.getX()) + (p_52937_.random.nextDouble() - 0.5D) + 0.5D;
                  double d2 = Mth.lerp(d0, (double)blockpos.getY(), (double)p_52938_.getY()) + p_52937_.random.nextDouble() - 0.5D;
                  double d3 = Mth.lerp(d0, (double)blockpos.getZ(), (double)p_52938_.getZ()) + (p_52937_.random.nextDouble() - 0.5D) + 0.5D;
                  p_52937_.addParticle(ParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2);
               }
            } else {
               p_52937_.setBlock(blockpos, p_52936_, 2);
               p_52937_.removeBlock(p_52938_, false);
            }

            return;
         }
      }

   }

   protected int getDelayAfterPlace() {
      return 5;
   }

   public boolean isPathfindable(BlockState p_52913_, BlockGetter p_52914_, BlockPos p_52915_, PathComputationType p_52916_) {
      return false;
   }
}