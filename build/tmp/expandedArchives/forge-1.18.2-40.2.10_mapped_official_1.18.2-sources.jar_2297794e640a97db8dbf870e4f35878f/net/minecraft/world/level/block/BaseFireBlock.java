package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseFireBlock extends Block {
   private static final int SECONDS_ON_FIRE = 8;
   private final float fireDamage;
   protected static final float AABB_OFFSET = 1.0F;
   protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

   public BaseFireBlock(BlockBehaviour.Properties p_49241_, float p_49242_) {
      super(p_49241_);
      this.fireDamage = p_49242_;
   }

   public BlockState getStateForPlacement(BlockPlaceContext p_49244_) {
      return getState(p_49244_.getLevel(), p_49244_.getClickedPos());
   }

   public static BlockState getState(BlockGetter p_49246_, BlockPos p_49247_) {
      BlockPos blockpos = p_49247_.below();
      BlockState blockstate = p_49246_.getBlockState(blockpos);
      return SoulFireBlock.canSurviveOnBlock(blockstate) ? Blocks.SOUL_FIRE.defaultBlockState() : ((FireBlock)Blocks.FIRE).getStateForPlacement(p_49246_, p_49247_);
   }

   public VoxelShape getShape(BlockState p_49274_, BlockGetter p_49275_, BlockPos p_49276_, CollisionContext p_49277_) {
      return DOWN_AABB;
   }

   public void animateTick(BlockState p_49265_, Level p_49266_, BlockPos p_49267_, Random p_49268_) {
      if (p_49268_.nextInt(24) == 0) {
         p_49266_.playLocalSound((double)p_49267_.getX() + 0.5D, (double)p_49267_.getY() + 0.5D, (double)p_49267_.getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1.0F + p_49268_.nextFloat(), p_49268_.nextFloat() * 0.7F + 0.3F, false);
      }

      BlockPos blockpos = p_49267_.below();
      BlockState blockstate = p_49266_.getBlockState(blockpos);
      if (!this.canBurn(blockstate) && !blockstate.isFaceSturdy(p_49266_, blockpos, Direction.UP)) {
         if (this.canBurn(p_49266_.getBlockState(p_49267_.west()))) {
            for(int j = 0; j < 2; ++j) {
               double d3 = (double)p_49267_.getX() + p_49268_.nextDouble() * (double)0.1F;
               double d8 = (double)p_49267_.getY() + p_49268_.nextDouble();
               double d13 = (double)p_49267_.getZ() + p_49268_.nextDouble();
               p_49266_.addParticle(ParticleTypes.LARGE_SMOKE, d3, d8, d13, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(p_49266_.getBlockState(p_49267_.east()))) {
            for(int k = 0; k < 2; ++k) {
               double d4 = (double)(p_49267_.getX() + 1) - p_49268_.nextDouble() * (double)0.1F;
               double d9 = (double)p_49267_.getY() + p_49268_.nextDouble();
               double d14 = (double)p_49267_.getZ() + p_49268_.nextDouble();
               p_49266_.addParticle(ParticleTypes.LARGE_SMOKE, d4, d9, d14, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(p_49266_.getBlockState(p_49267_.north()))) {
            for(int l = 0; l < 2; ++l) {
               double d5 = (double)p_49267_.getX() + p_49268_.nextDouble();
               double d10 = (double)p_49267_.getY() + p_49268_.nextDouble();
               double d15 = (double)p_49267_.getZ() + p_49268_.nextDouble() * (double)0.1F;
               p_49266_.addParticle(ParticleTypes.LARGE_SMOKE, d5, d10, d15, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(p_49266_.getBlockState(p_49267_.south()))) {
            for(int i1 = 0; i1 < 2; ++i1) {
               double d6 = (double)p_49267_.getX() + p_49268_.nextDouble();
               double d11 = (double)p_49267_.getY() + p_49268_.nextDouble();
               double d16 = (double)(p_49267_.getZ() + 1) - p_49268_.nextDouble() * (double)0.1F;
               p_49266_.addParticle(ParticleTypes.LARGE_SMOKE, d6, d11, d16, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.canBurn(p_49266_.getBlockState(p_49267_.above()))) {
            for(int j1 = 0; j1 < 2; ++j1) {
               double d7 = (double)p_49267_.getX() + p_49268_.nextDouble();
               double d12 = (double)(p_49267_.getY() + 1) - p_49268_.nextDouble() * (double)0.1F;
               double d17 = (double)p_49267_.getZ() + p_49268_.nextDouble();
               p_49266_.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(int i = 0; i < 3; ++i) {
            double d0 = (double)p_49267_.getX() + p_49268_.nextDouble();
            double d1 = (double)p_49267_.getY() + p_49268_.nextDouble() * 0.5D + 0.5D;
            double d2 = (double)p_49267_.getZ() + p_49268_.nextDouble();
            p_49266_.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   protected abstract boolean canBurn(BlockState p_49284_);

   public void entityInside(BlockState p_49260_, Level p_49261_, BlockPos p_49262_, Entity p_49263_) {
      if (!p_49263_.fireImmune()) {
         p_49263_.setRemainingFireTicks(p_49263_.getRemainingFireTicks() + 1);
         if (p_49263_.getRemainingFireTicks() == 0) {
            p_49263_.setSecondsOnFire(8);
         }

         p_49263_.hurt(DamageSource.IN_FIRE, this.fireDamage);
      }

      super.entityInside(p_49260_, p_49261_, p_49262_, p_49263_);
   }

   public void onPlace(BlockState p_49279_, Level p_49280_, BlockPos p_49281_, BlockState p_49282_, boolean p_49283_) {
      if (!p_49282_.is(p_49279_.getBlock())) {
         if (inPortalDimension(p_49280_)) {
            Optional<PortalShape> optional = PortalShape.findEmptyPortalShape(p_49280_, p_49281_, Direction.Axis.X);
            optional = net.minecraftforge.event.ForgeEventFactory.onTrySpawnPortal(p_49280_, p_49281_, optional);
            if (optional.isPresent()) {
               optional.get().createPortalBlocks();
               return;
            }
         }

         if (!p_49279_.canSurvive(p_49280_, p_49281_)) {
            p_49280_.removeBlock(p_49281_, false);
         }

      }
   }

   private static boolean inPortalDimension(Level p_49249_) {
      return p_49249_.dimension() == Level.OVERWORLD || p_49249_.dimension() == Level.NETHER;
   }

   protected void spawnDestroyParticles(Level p_152139_, Player p_152140_, BlockPos p_152141_, BlockState p_152142_) {
   }

   public void playerWillDestroy(Level p_49251_, BlockPos p_49252_, BlockState p_49253_, Player p_49254_) {
      if (!p_49251_.isClientSide()) {
         p_49251_.levelEvent((Player)null, 1009, p_49252_, 0);
      }

      super.playerWillDestroy(p_49251_, p_49252_, p_49253_, p_49254_);
   }

   public static boolean canBePlacedAt(Level p_49256_, BlockPos p_49257_, Direction p_49258_) {
      BlockState blockstate = p_49256_.getBlockState(p_49257_);
      if (!blockstate.isAir()) {
         return false;
      } else {
         return getState(p_49256_, p_49257_).canSurvive(p_49256_, p_49257_) || isPortal(p_49256_, p_49257_, p_49258_);
      }
   }

   private static boolean isPortal(Level p_49270_, BlockPos p_49271_, Direction p_49272_) {
      if (!inPortalDimension(p_49270_)) {
         return false;
      } else {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = p_49271_.mutable();
         boolean flag = false;

         for(Direction direction : Direction.values()) {
            if (p_49270_.getBlockState(blockpos$mutableblockpos.set(p_49271_).move(direction)).is(Blocks.OBSIDIAN)) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            return false;
         } else {
            Direction.Axis direction$axis = p_49272_.getAxis().isHorizontal() ? p_49272_.getCounterClockWise().getAxis() : Direction.Plane.HORIZONTAL.getRandomAxis(p_49270_.random);
            return PortalShape.findEmptyPortalShape(p_49270_, p_49271_, direction$axis).isPresent();
         }
      }
   }
}
