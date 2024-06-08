package net.minecraft.world.entity.animal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class SnowGolem extends AbstractGolem implements Shearable, RangedAttackMob, net.minecraftforge.common.IForgeShearable {
   private static final EntityDataAccessor<Byte> DATA_PUMPKIN_ID = SynchedEntityData.defineId(SnowGolem.class, EntityDataSerializers.BYTE);
   private static final byte PUMPKIN_FLAG = 16;
   private static final float EYE_HEIGHT = 1.7F;

   public SnowGolem(EntityType<? extends SnowGolem> p_29902_, Level p_29903_) {
      super(p_29902_, p_29903_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new RangedAttackGoal(this, 1.25D, 20, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D, 1.0000001E-5F));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false, (p_29932_) -> {
         return p_29932_ instanceof Enemy;
      }));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, (double)0.2F);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_PUMPKIN_ID, (byte)16);
   }

   public void addAdditionalSaveData(CompoundTag p_29923_) {
      super.addAdditionalSaveData(p_29923_);
      p_29923_.putBoolean("Pumpkin", this.hasPumpkin());
   }

   public void readAdditionalSaveData(CompoundTag p_29915_) {
      super.readAdditionalSaveData(p_29915_);
      if (p_29915_.contains("Pumpkin")) {
         this.setPumpkin(p_29915_.getBoolean("Pumpkin"));
      }

   }

   public boolean isSensitiveToWater() {
      return true;
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide) {
         int i = Mth.floor(this.getX());
         int j = Mth.floor(this.getY());
         int k = Mth.floor(this.getZ());
         BlockPos blockpos = new BlockPos(i, j, k);
         Biome biome = this.level.getBiome(blockpos).value();
         if (biome.shouldSnowGolemBurn(blockpos)) {
            this.hurt(DamageSource.ON_FIRE, 1.0F);
         }

         if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this)) {
            return;
         }

         BlockState blockstate = Blocks.SNOW.defaultBlockState();

         for(int l = 0; l < 4; ++l) {
            i = Mth.floor(this.getX() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
            j = Mth.floor(this.getY());
            k = Mth.floor(this.getZ() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos blockpos1 = new BlockPos(i, j, k);
            if (this.level.isEmptyBlock(blockpos1) && blockstate.canSurvive(this.level, blockpos1)) {
               this.level.setBlockAndUpdate(blockpos1, blockstate);
            }
         }
      }

   }

   public void performRangedAttack(LivingEntity p_29912_, float p_29913_) {
      Snowball snowball = new Snowball(this.level, this);
      double d0 = p_29912_.getEyeY() - (double)1.1F;
      double d1 = p_29912_.getX() - this.getX();
      double d2 = d0 - snowball.getY();
      double d3 = p_29912_.getZ() - this.getZ();
      double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double)0.2F;
      snowball.shoot(d1, d2 + d4, d3, 1.6F, 12.0F);
      this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(snowball);
   }

   protected float getStandingEyeHeight(Pose p_29917_, EntityDimensions p_29918_) {
      return 1.7F;
   }

   protected InteractionResult mobInteract(Player p_29920_, InteractionHand p_29921_) {
      ItemStack itemstack = p_29920_.getItemInHand(p_29921_);
      if (false && itemstack.getItem() == Items.SHEARS && this.readyForShearing()) { //Forge: Moved to onSheared
         this.shear(SoundSource.PLAYERS);
         this.gameEvent(GameEvent.SHEAR, p_29920_);
         if (!this.level.isClientSide) {
            itemstack.hurtAndBreak(1, p_29920_, (p_29910_) -> {
               p_29910_.broadcastBreakEvent(p_29921_);
            });
         }

         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public void shear(SoundSource p_29907_) {
      this.level.playSound((Player)null, this, SoundEvents.SNOW_GOLEM_SHEAR, p_29907_, 1.0F, 1.0F);
      if (!this.level.isClientSide()) {
         this.setPumpkin(false);
         this.spawnAtLocation(new ItemStack(Items.CARVED_PUMPKIN), 1.7F);
      }

   }

   public boolean readyForShearing() {
      return this.isAlive() && this.hasPumpkin();
   }

   public boolean hasPumpkin() {
      return (this.entityData.get(DATA_PUMPKIN_ID) & 16) != 0;
   }

   public void setPumpkin(boolean p_29937_) {
      byte b0 = this.entityData.get(DATA_PUMPKIN_ID);
      if (p_29937_) {
         this.entityData.set(DATA_PUMPKIN_ID, (byte)(b0 | 16));
      } else {
         this.entityData.set(DATA_PUMPKIN_ID, (byte)(b0 & -17));
      }

   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return SoundEvents.SNOW_GOLEM_AMBIENT;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_29929_) {
      return SoundEvents.SNOW_GOLEM_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.SNOW_GOLEM_DEATH;
   }

   public Vec3 getLeashOffset() {
      return new Vec3(0.0D, (double)(0.75F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   @Override
   public boolean isShearable(@javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos) {
      return readyForShearing();
   }

   @javax.annotation.Nonnull
   @Override
   public java.util.List<ItemStack> onSheared(@Nullable Player player, @javax.annotation.Nonnull ItemStack item, Level world, BlockPos pos, int fortune) {
      world.playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, player == null ? SoundSource.BLOCKS : SoundSource.PLAYERS, 1.0F, 1.0F);
      this.gameEvent(GameEvent.SHEAR, player);
      if (!world.isClientSide()) {
         setPumpkin(false);
         return java.util.Collections.singletonList(new ItemStack(Items.CARVED_PUMPKIN));
      }
      return java.util.Collections.emptyList();
   }
}
