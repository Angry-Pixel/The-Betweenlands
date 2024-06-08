package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RespawnAnchorBlock extends Block {
   public static final int MIN_CHARGES = 0;
   public static final int MAX_CHARGES = 4;
   public static final IntegerProperty CHARGE = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;
   private static final ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of(new Vec3i(0, 0, -1), new Vec3i(-1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 0), new Vec3i(-1, 0, -1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, 1));
   private static final ImmutableList<Vec3i> RESPAWN_OFFSETS = (new Builder<Vec3i>()).addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add(new Vec3i(0, 1, 0)).build();

   public RespawnAnchorBlock(BlockBehaviour.Properties p_55838_) {
      super(p_55838_);
      this.registerDefaultState(this.stateDefinition.any().setValue(CHARGE, Integer.valueOf(0)));
   }

   public InteractionResult use(BlockState p_55874_, Level p_55875_, BlockPos p_55876_, Player p_55877_, InteractionHand p_55878_, BlockHitResult p_55879_) {
      ItemStack itemstack = p_55877_.getItemInHand(p_55878_);
      if (p_55878_ == InteractionHand.MAIN_HAND && !isRespawnFuel(itemstack) && isRespawnFuel(p_55877_.getItemInHand(InteractionHand.OFF_HAND))) {
         return InteractionResult.PASS;
      } else if (isRespawnFuel(itemstack) && canBeCharged(p_55874_)) {
         charge(p_55875_, p_55876_, p_55874_);
         if (!p_55877_.getAbilities().instabuild) {
            itemstack.shrink(1);
         }

         return InteractionResult.sidedSuccess(p_55875_.isClientSide);
      } else if (p_55874_.getValue(CHARGE) == 0) {
         return InteractionResult.PASS;
      } else if (!canSetSpawn(p_55875_)) {
         if (!p_55875_.isClientSide) {
            this.explode(p_55874_, p_55875_, p_55876_);
         }

         return InteractionResult.sidedSuccess(p_55875_.isClientSide);
      } else {
         if (!p_55875_.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer)p_55877_;
            if (serverplayer.getRespawnDimension() != p_55875_.dimension() || !p_55876_.equals(serverplayer.getRespawnPosition())) {
               serverplayer.setRespawnPosition(p_55875_.dimension(), p_55876_, 0.0F, false, true);
               p_55875_.playSound((Player)null, (double)p_55876_.getX() + 0.5D, (double)p_55876_.getY() + 0.5D, (double)p_55876_.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
               return InteractionResult.SUCCESS;
            }
         }

         return InteractionResult.CONSUME;
      }
   }

   private static boolean isRespawnFuel(ItemStack p_55849_) {
      return p_55849_.is(Items.GLOWSTONE);
   }

   private static boolean canBeCharged(BlockState p_55895_) {
      return p_55895_.getValue(CHARGE) < 4;
   }

   private static boolean isWaterThatWouldFlow(BlockPos p_55888_, Level p_55889_) {
      FluidState fluidstate = p_55889_.getFluidState(p_55888_);
      if (!fluidstate.is(FluidTags.WATER)) {
         return false;
      } else if (fluidstate.isSource()) {
         return true;
      } else {
         float f = (float)fluidstate.getAmount();
         if (f < 2.0F) {
            return false;
         } else {
            FluidState fluidstate1 = p_55889_.getFluidState(p_55888_.below());
            return !fluidstate1.is(FluidTags.WATER);
         }
      }
   }

   private void explode(BlockState p_55891_, Level p_55892_, final BlockPos p_55893_) {
      p_55892_.removeBlock(p_55893_, false);
      boolean flag = Direction.Plane.HORIZONTAL.stream().map(p_55893_::relative).anyMatch((p_55854_) -> {
         return isWaterThatWouldFlow(p_55854_, p_55892_);
      });
      final boolean flag1 = flag || p_55892_.getFluidState(p_55893_.above()).is(FluidTags.WATER);
      ExplosionDamageCalculator explosiondamagecalculator = new ExplosionDamageCalculator() {
         public Optional<Float> getBlockExplosionResistance(Explosion p_55904_, BlockGetter p_55905_, BlockPos p_55906_, BlockState p_55907_, FluidState p_55908_) {
            return p_55906_.equals(p_55893_) && flag1 ? Optional.of(Blocks.WATER.getExplosionResistance()) : super.getBlockExplosionResistance(p_55904_, p_55905_, p_55906_, p_55907_, p_55908_);
         }
      };
      p_55892_.explode((Entity)null, DamageSource.badRespawnPointExplosion(), explosiondamagecalculator, (double)p_55893_.getX() + 0.5D, (double)p_55893_.getY() + 0.5D, (double)p_55893_.getZ() + 0.5D, 5.0F, true, Explosion.BlockInteraction.DESTROY);
   }

   public static boolean canSetSpawn(Level p_55851_) {
      return p_55851_.dimensionType().respawnAnchorWorks();
   }

   public static void charge(Level p_55856_, BlockPos p_55857_, BlockState p_55858_) {
      p_55856_.setBlock(p_55857_, p_55858_.setValue(CHARGE, Integer.valueOf(p_55858_.getValue(CHARGE) + 1)), 3);
      p_55856_.playSound((Player)null, (double)p_55857_.getX() + 0.5D, (double)p_55857_.getY() + 0.5D, (double)p_55857_.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   public void animateTick(BlockState p_55881_, Level p_55882_, BlockPos p_55883_, Random p_55884_) {
      if (p_55881_.getValue(CHARGE) != 0) {
         if (p_55884_.nextInt(100) == 0) {
            p_55882_.playSound((Player)null, (double)p_55883_.getX() + 0.5D, (double)p_55883_.getY() + 0.5D, (double)p_55883_.getZ() + 0.5D, SoundEvents.RESPAWN_ANCHOR_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         double d0 = (double)p_55883_.getX() + 0.5D + (0.5D - p_55884_.nextDouble());
         double d1 = (double)p_55883_.getY() + 1.0D;
         double d2 = (double)p_55883_.getZ() + 0.5D + (0.5D - p_55884_.nextDouble());
         double d3 = (double)p_55884_.nextFloat() * 0.04D;
         p_55882_.addParticle(ParticleTypes.REVERSE_PORTAL, d0, d1, d2, 0.0D, d3, 0.0D);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55886_) {
      p_55886_.add(CHARGE);
   }

   public boolean hasAnalogOutputSignal(BlockState p_55860_) {
      return true;
   }

   public static int getScaledChargeLevel(BlockState p_55862_, int p_55863_) {
      return Mth.floor((float)(p_55862_.getValue(CHARGE) - 0) / 4.0F * (float)p_55863_);
   }

   public int getAnalogOutputSignal(BlockState p_55870_, Level p_55871_, BlockPos p_55872_) {
      return getScaledChargeLevel(p_55870_, 15);
   }

   public static Optional<Vec3> findStandUpPosition(EntityType<?> p_55840_, CollisionGetter p_55841_, BlockPos p_55842_) {
      Optional<Vec3> optional = findStandUpPosition(p_55840_, p_55841_, p_55842_, true);
      return optional.isPresent() ? optional : findStandUpPosition(p_55840_, p_55841_, p_55842_, false);
   }

   private static Optional<Vec3> findStandUpPosition(EntityType<?> p_55844_, CollisionGetter p_55845_, BlockPos p_55846_, boolean p_55847_) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(Vec3i vec3i : RESPAWN_OFFSETS) {
         blockpos$mutableblockpos.set(p_55846_).move(vec3i);
         Vec3 vec3 = DismountHelper.findSafeDismountLocation(p_55844_, p_55845_, blockpos$mutableblockpos, p_55847_);
         if (vec3 != null) {
            return Optional.of(vec3);
         }
      }

      return Optional.empty();
   }

   public boolean isPathfindable(BlockState p_55865_, BlockGetter p_55866_, BlockPos p_55867_, PathComputationType p_55868_) {
      return false;
   }
}