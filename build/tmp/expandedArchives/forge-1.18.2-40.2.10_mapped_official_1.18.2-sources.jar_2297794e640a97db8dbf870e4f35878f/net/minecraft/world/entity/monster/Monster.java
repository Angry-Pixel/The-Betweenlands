package net.minecraft.world.entity.monster;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;

public abstract class Monster extends PathfinderMob implements Enemy {
   protected Monster(EntityType<? extends Monster> p_33002_, Level p_33003_) {
      super(p_33002_, p_33003_);
      this.xpReward = 5;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   public void aiStep() {
      this.updateSwingTime();
      this.updateNoActionTime();
      super.aiStep();
   }

   protected void updateNoActionTime() {
      float f = this.getBrightness();
      if (f > 0.5F) {
         this.noActionTime += 2;
      }

   }

   protected boolean shouldDespawnInPeaceful() {
      return true;
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.HOSTILE_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.HOSTILE_SPLASH;
   }

   protected SoundEvent getHurtSound(DamageSource p_33034_) {
      return SoundEvents.HOSTILE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.HOSTILE_DEATH;
   }

   public LivingEntity.Fallsounds getFallSounds() {
      return new LivingEntity.Fallsounds(SoundEvents.HOSTILE_SMALL_FALL, SoundEvents.HOSTILE_BIG_FALL);
   }

   public float getWalkTargetValue(BlockPos p_33013_, LevelReader p_33014_) {
      return 0.5F - p_33014_.getBrightness(p_33013_);
   }

   public static boolean isDarkEnoughToSpawn(ServerLevelAccessor p_33009_, BlockPos p_33010_, Random p_33011_) {
      if (p_33009_.getBrightness(LightLayer.SKY, p_33010_) > p_33011_.nextInt(32)) {
         return false;
      } else if (p_33009_.getBrightness(LightLayer.BLOCK, p_33010_) > 0) {
         return false;
      } else {
         int i = p_33009_.getLevel().isThundering() ? p_33009_.getMaxLocalRawBrightness(p_33010_, 10) : p_33009_.getMaxLocalRawBrightness(p_33010_);
         return i <= p_33011_.nextInt(8);
      }
   }

   public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> p_33018_, ServerLevelAccessor p_33019_, MobSpawnType p_33020_, BlockPos p_33021_, Random p_33022_) {
      return p_33019_.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(p_33019_, p_33021_, p_33022_) && checkMobSpawnRules(p_33018_, p_33019_, p_33020_, p_33021_, p_33022_);
   }

   public static boolean checkAnyLightMonsterSpawnRules(EntityType<? extends Monster> p_33024_, LevelAccessor p_33025_, MobSpawnType p_33026_, BlockPos p_33027_, Random p_33028_) {
      return p_33025_.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(p_33024_, p_33025_, p_33026_, p_33027_, p_33028_);
   }

   public static AttributeSupplier.Builder createMonsterAttributes() {
      return Mob.createMobAttributes().add(Attributes.ATTACK_DAMAGE);
   }

   protected boolean shouldDropExperience() {
      return true;
   }

   protected boolean shouldDropLoot() {
      return true;
   }

   public boolean isPreventingPlayerRest(Player p_33036_) {
      return true;
   }

   public ItemStack getProjectile(ItemStack p_33038_) {
      if (p_33038_.getItem() instanceof ProjectileWeaponItem) {
         Predicate<ItemStack> predicate = ((ProjectileWeaponItem)p_33038_.getItem()).getSupportedHeldProjectiles();
         ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
         return net.minecraftforge.common.ForgeHooks.getProjectile(this, p_33038_, itemstack.isEmpty() ? new ItemStack(Items.ARROW) : itemstack);
      } else {
         return net.minecraftforge.common.ForgeHooks.getProjectile(this, p_33038_, ItemStack.EMPTY);
      }
   }
}
