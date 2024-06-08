package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class Animal extends AgeableMob {
   static final int PARENT_AGE_AFTER_BREEDING = 6000;
   private int inLove;
   @Nullable
   private UUID loveCause;

   protected Animal(EntityType<? extends Animal> p_27557_, Level p_27558_) {
      super(p_27557_, p_27558_);
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
   }

   protected void customServerAiStep() {
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      super.customServerAiStep();
   }

   public void aiStep() {
      super.aiStep();
      if (this.getAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         --this.inLove;
         if (this.inLove % 10 == 0) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
         }
      }

   }

   public boolean hurt(DamageSource p_27567_, float p_27568_) {
      if (this.isInvulnerableTo(p_27567_)) {
         return false;
      } else {
         this.inLove = 0;
         return super.hurt(p_27567_, p_27568_);
      }
   }

   public float getWalkTargetValue(BlockPos p_27573_, LevelReader p_27574_) {
      return p_27574_.getBlockState(p_27573_.below()).is(Blocks.GRASS_BLOCK) ? 10.0F : p_27574_.getBrightness(p_27573_) - 0.5F;
   }

   public void addAdditionalSaveData(CompoundTag p_27587_) {
      super.addAdditionalSaveData(p_27587_);
      p_27587_.putInt("InLove", this.inLove);
      if (this.loveCause != null) {
         p_27587_.putUUID("LoveCause", this.loveCause);
      }

   }

   public double getMyRidingOffset() {
      return 0.14D;
   }

   public void readAdditionalSaveData(CompoundTag p_27576_) {
      super.readAdditionalSaveData(p_27576_);
      this.inLove = p_27576_.getInt("InLove");
      this.loveCause = p_27576_.hasUUID("LoveCause") ? p_27576_.getUUID("LoveCause") : null;
   }

   public static boolean checkAnimalSpawnRules(EntityType<? extends Animal> p_27578_, LevelAccessor p_27579_, MobSpawnType p_27580_, BlockPos p_27581_, Random p_27582_) {
      return p_27579_.getBlockState(p_27581_.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON) && isBrightEnoughToSpawn(p_27579_, p_27581_);
   }

   protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter p_186210_, BlockPos p_186211_) {
      return p_186210_.getRawBrightness(p_186211_, 0) > 8;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean removeWhenFarAway(double p_27598_) {
      return false;
   }

   protected int getExperienceReward(Player p_27590_) {
      return 1 + this.level.random.nextInt(3);
   }

   public boolean isFood(ItemStack p_27600_) {
      return p_27600_.is(Items.WHEAT);
   }

   public InteractionResult mobInteract(Player p_27584_, InteractionHand p_27585_) {
      ItemStack itemstack = p_27584_.getItemInHand(p_27585_);
      if (this.isFood(itemstack)) {
         int i = this.getAge();
         if (!this.level.isClientSide && i == 0 && this.canFallInLove()) {
            this.usePlayerItem(p_27584_, p_27585_, itemstack);
            this.setInLove(p_27584_);
            this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
            return InteractionResult.SUCCESS;
         }

         if (this.isBaby()) {
            this.usePlayerItem(p_27584_, p_27585_, itemstack);
            this.ageUp((int)((float)(-i / 20) * 0.1F), true);
            this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }

         if (this.level.isClientSide) {
            return InteractionResult.CONSUME;
         }
      }

      return super.mobInteract(p_27584_, p_27585_);
   }

   protected void usePlayerItem(Player p_148715_, InteractionHand p_148716_, ItemStack p_148717_) {
      if (!p_148715_.getAbilities().instabuild) {
         p_148717_.shrink(1);
      }

   }

   public boolean canFallInLove() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable Player p_27596_) {
      this.inLove = 600;
      if (p_27596_ != null) {
         this.loveCause = p_27596_.getUUID();
      }

      this.level.broadcastEntityEvent(this, (byte)18);
   }

   public void setInLoveTime(int p_27602_) {
      this.inLove = p_27602_;
   }

   public int getInLoveTime() {
      return this.inLove;
   }

   @Nullable
   public ServerPlayer getLoveCause() {
      if (this.loveCause == null) {
         return null;
      } else {
         Player player = this.level.getPlayerByUUID(this.loveCause);
         return player instanceof ServerPlayer ? (ServerPlayer)player : null;
      }
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetLove() {
      this.inLove = 0;
   }

   public boolean canMate(Animal p_27569_) {
      if (p_27569_ == this) {
         return false;
      } else if (p_27569_.getClass() != this.getClass()) {
         return false;
      } else {
         return this.isInLove() && p_27569_.isInLove();
      }
   }

   public void spawnChildFromBreeding(ServerLevel p_27564_, Animal p_27565_) {
      AgeableMob ageablemob = this.getBreedOffspring(p_27564_, p_27565_);
      final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(this, p_27565_, ageablemob);
      final boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
      ageablemob = event.getChild();
      if (cancelled) {
         //Reset the "inLove" state for the animals
         this.setAge(6000);
         p_27565_.setAge(6000);
         this.resetLove();
         p_27565_.resetLove();
         return;
      }
      if (ageablemob != null) {
         ServerPlayer serverplayer = this.getLoveCause();
         if (serverplayer == null && p_27565_.getLoveCause() != null) {
            serverplayer = p_27565_.getLoveCause();
         }

         if (serverplayer != null) {
            serverplayer.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this, p_27565_, ageablemob);
         }

         this.setAge(6000);
         p_27565_.setAge(6000);
         this.resetLove();
         p_27565_.resetLove();
         ageablemob.setBaby(true);
         ageablemob.moveTo(this.getX(), this.getY(), this.getZ(), 0.0F, 0.0F);
         p_27564_.addFreshEntityWithPassengers(ageablemob);
         p_27564_.broadcastEntityEvent(this, (byte)18);
         if (p_27564_.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            p_27564_.addFreshEntity(new ExperienceOrb(p_27564_, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
         }

      }
   }

   public void handleEntityEvent(byte p_27562_) {
      if (p_27562_ == 18) {
         for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
         }
      } else {
         super.handleEntityEvent(p_27562_);
      }

   }
}
