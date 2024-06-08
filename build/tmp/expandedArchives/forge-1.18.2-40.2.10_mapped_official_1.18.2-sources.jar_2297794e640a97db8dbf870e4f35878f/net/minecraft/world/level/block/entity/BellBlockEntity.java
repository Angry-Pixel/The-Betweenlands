package net.minecraft.world.level.block.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableInt;

public class BellBlockEntity extends BlockEntity {
   private static final int DURATION = 50;
   private static final int GLOW_DURATION = 60;
   private static final int MIN_TICKS_BETWEEN_SEARCHES = 60;
   private static final int MAX_RESONATION_TICKS = 40;
   private static final int TICKS_BEFORE_RESONATION = 5;
   private static final int SEARCH_RADIUS = 48;
   private static final int HEAR_BELL_RADIUS = 32;
   private static final int HIGHLIGHT_RAIDERS_RADIUS = 48;
   private long lastRingTimestamp;
   public int ticks;
   public boolean shaking;
   public Direction clickDirection;
   private List<LivingEntity> nearbyEntities;
   private boolean resonating;
   private int resonationTicks;

   public BellBlockEntity(BlockPos p_155173_, BlockState p_155174_) {
      super(BlockEntityType.BELL, p_155173_, p_155174_);
   }

   public boolean triggerEvent(int p_58837_, int p_58838_) {
      if (p_58837_ == 1) {
         this.updateEntities();
         this.resonationTicks = 0;
         this.clickDirection = Direction.from3DDataValue(p_58838_);
         this.ticks = 0;
         this.shaking = true;
         return true;
      } else {
         return super.triggerEvent(p_58837_, p_58838_);
      }
   }

   private static void tick(Level p_155181_, BlockPos p_155182_, BlockState p_155183_, BellBlockEntity p_155184_, BellBlockEntity.ResonationEndAction p_155185_) {
      if (p_155184_.shaking) {
         ++p_155184_.ticks;
      }

      if (p_155184_.ticks >= 50) {
         p_155184_.shaking = false;
         p_155184_.ticks = 0;
      }

      if (p_155184_.ticks >= 5 && p_155184_.resonationTicks == 0 && areRaidersNearby(p_155182_, p_155184_.nearbyEntities)) {
         p_155184_.resonating = true;
         p_155181_.playSound((Player)null, p_155182_, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      if (p_155184_.resonating) {
         if (p_155184_.resonationTicks < 40) {
            ++p_155184_.resonationTicks;
         } else {
            p_155185_.run(p_155181_, p_155182_, p_155184_.nearbyEntities);
            p_155184_.resonating = false;
         }
      }

   }

   public static void clientTick(Level p_155176_, BlockPos p_155177_, BlockState p_155178_, BellBlockEntity p_155179_) {
      tick(p_155176_, p_155177_, p_155178_, p_155179_, BellBlockEntity::showBellParticles);
   }

   public static void serverTick(Level p_155203_, BlockPos p_155204_, BlockState p_155205_, BellBlockEntity p_155206_) {
      tick(p_155203_, p_155204_, p_155205_, p_155206_, BellBlockEntity::makeRaidersGlow);
   }

   public void onHit(Direction p_58835_) {
      BlockPos blockpos = this.getBlockPos();
      this.clickDirection = p_58835_;
      if (this.shaking) {
         this.ticks = 0;
      } else {
         this.shaking = true;
      }

      this.level.blockEvent(blockpos, this.getBlockState().getBlock(), 1, p_58835_.get3DDataValue());
   }

   private void updateEntities() {
      BlockPos blockpos = this.getBlockPos();
      if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
         this.lastRingTimestamp = this.level.getGameTime();
         AABB aabb = (new AABB(blockpos)).inflate(48.0D);
         this.nearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, aabb);
      }

      if (!this.level.isClientSide) {
         for(LivingEntity livingentity : this.nearbyEntities) {
            if (livingentity.isAlive() && !livingentity.isRemoved() && blockpos.closerToCenterThan(livingentity.position(), 32.0D)) {
               livingentity.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, this.level.getGameTime());
            }
         }
      }

   }

   private static boolean areRaidersNearby(BlockPos p_155200_, List<LivingEntity> p_155201_) {
      for(LivingEntity livingentity : p_155201_) {
         if (livingentity.isAlive() && !livingentity.isRemoved() && p_155200_.closerToCenterThan(livingentity.position(), 32.0D) && livingentity.getType().is(EntityTypeTags.RAIDERS)) {
            return true;
         }
      }

      return false;
   }

   private static void makeRaidersGlow(Level p_155187_, BlockPos p_155188_, List<LivingEntity> p_155189_) {
      p_155189_.stream().filter((p_155219_) -> {
         return isRaiderWithinRange(p_155188_, p_155219_);
      }).forEach(BellBlockEntity::glow);
   }

   private static void showBellParticles(Level p_155208_, BlockPos p_155209_, List<LivingEntity> p_155210_) {
      MutableInt mutableint = new MutableInt(16700985);
      int i = (int)p_155210_.stream().filter((p_155216_) -> {
         return p_155209_.closerToCenterThan(p_155216_.position(), 48.0D);
      }).count();
      p_155210_.stream().filter((p_155213_) -> {
         return isRaiderWithinRange(p_155209_, p_155213_);
      }).forEach((p_155195_) -> {
         float f = 1.0F;
         double d0 = Math.sqrt((p_155195_.getX() - (double)p_155209_.getX()) * (p_155195_.getX() - (double)p_155209_.getX()) + (p_155195_.getZ() - (double)p_155209_.getZ()) * (p_155195_.getZ() - (double)p_155209_.getZ()));
         double d1 = (double)((float)p_155209_.getX() + 0.5F) + 1.0D / d0 * (p_155195_.getX() - (double)p_155209_.getX());
         double d2 = (double)((float)p_155209_.getZ() + 0.5F) + 1.0D / d0 * (p_155195_.getZ() - (double)p_155209_.getZ());
         int j = Mth.clamp((i - 21) / -2, 3, 15);

         for(int k = 0; k < j; ++k) {
            int l = mutableint.addAndGet(5);
            double d3 = (double)FastColor.ARGB32.red(l) / 255.0D;
            double d4 = (double)FastColor.ARGB32.green(l) / 255.0D;
            double d5 = (double)FastColor.ARGB32.blue(l) / 255.0D;
            p_155208_.addParticle(ParticleTypes.ENTITY_EFFECT, d1, (double)((float)p_155209_.getY() + 0.5F), d2, d3, d4, d5);
         }

      });
   }

   private static boolean isRaiderWithinRange(BlockPos p_155197_, LivingEntity p_155198_) {
      return p_155198_.isAlive() && !p_155198_.isRemoved() && p_155197_.closerToCenterThan(p_155198_.position(), 48.0D) && p_155198_.getType().is(EntityTypeTags.RAIDERS);
   }

   private static void glow(LivingEntity p_58841_) {
      p_58841_.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
   }

   @FunctionalInterface
   interface ResonationEndAction {
      void run(Level p_155221_, BlockPos p_155222_, List<LivingEntity> p_155223_);
   }
}