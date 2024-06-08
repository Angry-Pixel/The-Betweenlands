package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ConduitBlockEntity extends BlockEntity {
   private static final int BLOCK_REFRESH_RATE = 2;
   private static final int EFFECT_DURATION = 13;
   private static final float ROTATION_SPEED = -0.0375F;
   private static final int MIN_ACTIVE_SIZE = 16;
   private static final int MIN_KILL_SIZE = 42;
   private static final int KILL_RANGE = 8;
   private static final Block[] VALID_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
   public int tickCount;
   private float activeRotation;
   private boolean isActive;
   private boolean isHunting;
   private final List<BlockPos> effectBlocks = Lists.newArrayList();
   @Nullable
   private LivingEntity destroyTarget;
   @Nullable
   private UUID destroyTargetUUID;
   private long nextAmbientSoundActivation;

   public ConduitBlockEntity(BlockPos p_155397_, BlockState p_155398_) {
      super(BlockEntityType.CONDUIT, p_155397_, p_155398_);
   }

   public void load(CompoundTag p_155437_) {
      super.load(p_155437_);
      if (p_155437_.hasUUID("Target")) {
         this.destroyTargetUUID = p_155437_.getUUID("Target");
      } else {
         this.destroyTargetUUID = null;
      }

   }

   protected void saveAdditional(CompoundTag p_187495_) {
      super.saveAdditional(p_187495_);
      if (this.destroyTarget != null) {
         p_187495_.putUUID("Target", this.destroyTarget.getUUID());
      }

   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag() {
      return this.saveWithoutMetadata();
   }

   public static void clientTick(Level p_155404_, BlockPos p_155405_, BlockState p_155406_, ConduitBlockEntity p_155407_) {
      ++p_155407_.tickCount;
      long i = p_155404_.getGameTime();
      List<BlockPos> list = p_155407_.effectBlocks;
      if (i % 40L == 0L) {
         p_155407_.isActive = updateShape(p_155404_, p_155405_, list);
         updateHunting(p_155407_, list);
      }

      updateClientTarget(p_155404_, p_155405_, p_155407_);
      animationTick(p_155404_, p_155405_, list, p_155407_.destroyTarget, p_155407_.tickCount);
      if (p_155407_.isActive()) {
         ++p_155407_.activeRotation;
      }

   }

   public static void serverTick(Level p_155439_, BlockPos p_155440_, BlockState p_155441_, ConduitBlockEntity p_155442_) {
      ++p_155442_.tickCount;
      long i = p_155439_.getGameTime();
      List<BlockPos> list = p_155442_.effectBlocks;
      if (i % 40L == 0L) {
         boolean flag = updateShape(p_155439_, p_155440_, list);
         if (flag != p_155442_.isActive) {
            SoundEvent soundevent = flag ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE;
            p_155439_.playSound((Player)null, p_155440_, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         p_155442_.isActive = flag;
         updateHunting(p_155442_, list);
         if (flag) {
            applyEffects(p_155439_, p_155440_, list);
            updateDestroyTarget(p_155439_, p_155440_, p_155441_, list, p_155442_);
         }
      }

      if (p_155442_.isActive()) {
         if (i % 80L == 0L) {
            p_155439_.playSound((Player)null, p_155440_, SoundEvents.CONDUIT_AMBIENT, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         if (i > p_155442_.nextAmbientSoundActivation) {
            p_155442_.nextAmbientSoundActivation = i + 60L + (long)p_155439_.getRandom().nextInt(40);
            p_155439_.playSound((Player)null, p_155440_, SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   private static void updateHunting(ConduitBlockEntity p_155429_, List<BlockPos> p_155430_) {
      p_155429_.setHunting(p_155430_.size() >= 42);
   }

   private static boolean updateShape(Level p_155415_, BlockPos p_155416_, List<BlockPos> p_155417_) {
      p_155417_.clear();

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            for(int k = -1; k <= 1; ++k) {
               BlockPos blockpos = p_155416_.offset(i, j, k);
               if (!p_155415_.isWaterAt(blockpos)) {
                  return false;
               }
            }
         }
      }

      for(int j1 = -2; j1 <= 2; ++j1) {
         for(int k1 = -2; k1 <= 2; ++k1) {
            for(int l1 = -2; l1 <= 2; ++l1) {
               int i2 = Math.abs(j1);
               int l = Math.abs(k1);
               int i1 = Math.abs(l1);
               if ((i2 > 1 || l > 1 || i1 > 1) && (j1 == 0 && (l == 2 || i1 == 2) || k1 == 0 && (i2 == 2 || i1 == 2) || l1 == 0 && (i2 == 2 || l == 2))) {
                  BlockPos blockpos1 = p_155416_.offset(j1, k1, l1);
                  BlockState blockstate = p_155415_.getBlockState(blockpos1);

                  if (blockstate.isConduitFrame(p_155415_, blockpos1, p_155416_)) {
                     p_155417_.add(blockpos1);
                  }
               }
            }
         }
      }

      return p_155417_.size() >= 16;
   }

   private static void applyEffects(Level p_155444_, BlockPos p_155445_, List<BlockPos> p_155446_) {
      int i = p_155446_.size();
      int j = i / 7 * 16;
      int k = p_155445_.getX();
      int l = p_155445_.getY();
      int i1 = p_155445_.getZ();
      AABB aabb = (new AABB((double)k, (double)l, (double)i1, (double)(k + 1), (double)(l + 1), (double)(i1 + 1))).inflate((double)j).expandTowards(0.0D, (double)p_155444_.getHeight(), 0.0D);
      List<Player> list = p_155444_.getEntitiesOfClass(Player.class, aabb);
      if (!list.isEmpty()) {
         for(Player player : list) {
            if (p_155445_.closerThan(player.blockPosition(), (double)j) && player.isInWaterOrRain()) {
               player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
            }
         }

      }
   }

   private static void updateDestroyTarget(Level p_155409_, BlockPos p_155410_, BlockState p_155411_, List<BlockPos> p_155412_, ConduitBlockEntity p_155413_) {
      LivingEntity livingentity = p_155413_.destroyTarget;
      int i = p_155412_.size();
      if (i < 42) {
         p_155413_.destroyTarget = null;
      } else if (p_155413_.destroyTarget == null && p_155413_.destroyTargetUUID != null) {
         p_155413_.destroyTarget = findDestroyTarget(p_155409_, p_155410_, p_155413_.destroyTargetUUID);
         p_155413_.destroyTargetUUID = null;
      } else if (p_155413_.destroyTarget == null) {
         List<LivingEntity> list = p_155409_.getEntitiesOfClass(LivingEntity.class, getDestroyRangeAABB(p_155410_), (p_59213_) -> {
            return p_59213_ instanceof Enemy && p_59213_.isInWaterOrRain();
         });
         if (!list.isEmpty()) {
            p_155413_.destroyTarget = list.get(p_155409_.random.nextInt(list.size()));
         }
      } else if (!p_155413_.destroyTarget.isAlive() || !p_155410_.closerThan(p_155413_.destroyTarget.blockPosition(), 8.0D)) {
         p_155413_.destroyTarget = null;
      }

      if (p_155413_.destroyTarget != null) {
         p_155409_.playSound((Player)null, p_155413_.destroyTarget.getX(), p_155413_.destroyTarget.getY(), p_155413_.destroyTarget.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0F, 1.0F);
         p_155413_.destroyTarget.hurt(DamageSource.MAGIC, 4.0F);
      }

      if (livingentity != p_155413_.destroyTarget) {
         p_155409_.sendBlockUpdated(p_155410_, p_155411_, p_155411_, 2);
      }

   }

   private static void updateClientTarget(Level p_155400_, BlockPos p_155401_, ConduitBlockEntity p_155402_) {
      if (p_155402_.destroyTargetUUID == null) {
         p_155402_.destroyTarget = null;
      } else if (p_155402_.destroyTarget == null || !p_155402_.destroyTarget.getUUID().equals(p_155402_.destroyTargetUUID)) {
         p_155402_.destroyTarget = findDestroyTarget(p_155400_, p_155401_, p_155402_.destroyTargetUUID);
         if (p_155402_.destroyTarget == null) {
            p_155402_.destroyTargetUUID = null;
         }
      }

   }

   private static AABB getDestroyRangeAABB(BlockPos p_155432_) {
      int i = p_155432_.getX();
      int j = p_155432_.getY();
      int k = p_155432_.getZ();
      return (new AABB((double)i, (double)j, (double)k, (double)(i + 1), (double)(j + 1), (double)(k + 1))).inflate(8.0D);
   }

   @Nullable
   private static LivingEntity findDestroyTarget(Level p_155425_, BlockPos p_155426_, UUID p_155427_) {
      List<LivingEntity> list = p_155425_.getEntitiesOfClass(LivingEntity.class, getDestroyRangeAABB(p_155426_), (p_155435_) -> {
         return p_155435_.getUUID().equals(p_155427_);
      });
      return list.size() == 1 ? list.get(0) : null;
   }

   private static void animationTick(Level p_155419_, BlockPos p_155420_, List<BlockPos> p_155421_, @Nullable Entity p_155422_, int p_155423_) {
      Random random = p_155419_.random;
      double d0 = (double)(Mth.sin((float)(p_155423_ + 35) * 0.1F) / 2.0F + 0.5F);
      d0 = (d0 * d0 + d0) * (double)0.3F;
      Vec3 vec3 = new Vec3((double)p_155420_.getX() + 0.5D, (double)p_155420_.getY() + 1.5D + d0, (double)p_155420_.getZ() + 0.5D);

      for(BlockPos blockpos : p_155421_) {
         if (random.nextInt(50) == 0) {
            BlockPos blockpos1 = blockpos.subtract(p_155420_);
            float f = -0.5F + random.nextFloat() + (float)blockpos1.getX();
            float f1 = -2.0F + random.nextFloat() + (float)blockpos1.getY();
            float f2 = -0.5F + random.nextFloat() + (float)blockpos1.getZ();
            p_155419_.addParticle(ParticleTypes.NAUTILUS, vec3.x, vec3.y, vec3.z, (double)f, (double)f1, (double)f2);
         }
      }

      if (p_155422_ != null) {
         Vec3 vec31 = new Vec3(p_155422_.getX(), p_155422_.getEyeY(), p_155422_.getZ());
         float f3 = (-0.5F + random.nextFloat()) * (3.0F + p_155422_.getBbWidth());
         float f4 = -1.0F + random.nextFloat() * p_155422_.getBbHeight();
         float f5 = (-0.5F + random.nextFloat()) * (3.0F + p_155422_.getBbWidth());
         Vec3 vec32 = new Vec3((double)f3, (double)f4, (double)f5);
         p_155419_.addParticle(ParticleTypes.NAUTILUS, vec31.x, vec31.y, vec31.z, vec32.x, vec32.y, vec32.z);
      }

   }

   public boolean isActive() {
      return this.isActive;
   }

   public boolean isHunting() {
      return this.isHunting;
   }

   private void setHunting(boolean p_59215_) {
      this.isHunting = p_59215_;
   }

   public float getActiveRotation(float p_59198_) {
      return (this.activeRotation + p_59198_) * -0.0375F;
   }
}
