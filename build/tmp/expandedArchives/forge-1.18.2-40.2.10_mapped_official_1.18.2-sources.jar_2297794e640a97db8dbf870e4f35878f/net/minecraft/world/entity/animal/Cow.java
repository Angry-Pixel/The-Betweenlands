package net.minecraft.world.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Cow extends Animal {
   public Cow(EntityType<? extends Cow> p_28285_, Level p_28286_) {
      super(p_28285_, p_28286_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.25D, Ingredient.of(Items.WHEAT), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, (double)0.2F);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.COW_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_28306_) {
      return SoundEvents.COW_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.COW_DEATH;
   }

   protected void playStepSound(BlockPos p_28301_, BlockState p_28302_) {
      this.playSound(SoundEvents.COW_STEP, 0.15F, 1.0F);
   }

   protected float getSoundVolume() {
      return 0.4F;
   }

   public InteractionResult mobInteract(Player p_28298_, InteractionHand p_28299_) {
      ItemStack itemstack = p_28298_.getItemInHand(p_28299_);
      if (itemstack.is(Items.BUCKET) && !this.isBaby()) {
         p_28298_.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
         ItemStack itemstack1 = ItemUtils.createFilledResult(itemstack, p_28298_, Items.MILK_BUCKET.getDefaultInstance());
         p_28298_.setItemInHand(p_28299_, itemstack1);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return super.mobInteract(p_28298_, p_28299_);
      }
   }

   public Cow getBreedOffspring(ServerLevel p_148890_, AgeableMob p_148891_) {
      return EntityType.COW.create(p_148890_);
   }

   protected float getStandingEyeHeight(Pose p_28295_, EntityDimensions p_28296_) {
      return this.isBaby() ? p_28296_.height * 0.95F : 1.3F;
   }
}