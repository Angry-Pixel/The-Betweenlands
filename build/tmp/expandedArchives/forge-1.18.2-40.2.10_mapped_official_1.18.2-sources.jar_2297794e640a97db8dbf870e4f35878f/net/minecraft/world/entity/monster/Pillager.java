package net.minecraft.world.entity.monster;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;

public class Pillager extends AbstractIllager implements CrossbowAttackMob, InventoryCarrier {
   private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(Pillager.class, EntityDataSerializers.BOOLEAN);
   private static final int INVENTORY_SIZE = 5;
   private static final int SLOT_OFFSET = 300;
   private static final float CROSSBOW_POWER = 1.6F;
   private final SimpleContainer inventory = new SimpleContainer(5);

   public Pillager(EntityType<? extends Pillager> p_33262_, Level p_33263_) {
      super(p_33262_, p_33263_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
      this.goalSelector.addGoal(3, new RangedCrossbowAttackGoal<>(this, 1.0D, 8.0F));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, (double)0.35F).add(Attributes.MAX_HEALTH, 24.0D).add(Attributes.ATTACK_DAMAGE, 5.0D).add(Attributes.FOLLOW_RANGE, 32.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(IS_CHARGING_CROSSBOW, false);
   }

   public boolean canFireProjectileWeapon(ProjectileWeaponItem p_33280_) {
      return p_33280_ == Items.CROSSBOW;
   }

   public boolean isChargingCrossbow() {
      return this.entityData.get(IS_CHARGING_CROSSBOW);
   }

   public void setChargingCrossbow(boolean p_33302_) {
      this.entityData.set(IS_CHARGING_CROSSBOW, p_33302_);
   }

   public void onCrossbowAttackPerformed() {
      this.noActionTime = 0;
   }

   public void addAdditionalSaveData(CompoundTag p_33300_) {
      super.addAdditionalSaveData(p_33300_);
      ListTag listtag = new ListTag();

      for(int i = 0; i < this.inventory.getContainerSize(); ++i) {
         ItemStack itemstack = this.inventory.getItem(i);
         if (!itemstack.isEmpty()) {
            listtag.add(itemstack.save(new CompoundTag()));
         }
      }

      p_33300_.put("Inventory", listtag);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isChargingCrossbow()) {
         return AbstractIllager.IllagerArmPose.CROSSBOW_CHARGE;
      } else if (this.isHolding(is -> is.getItem() instanceof net.minecraft.world.item.CrossbowItem)) {
         return AbstractIllager.IllagerArmPose.CROSSBOW_HOLD;
      } else {
         return this.isAggressive() ? AbstractIllager.IllagerArmPose.ATTACKING : AbstractIllager.IllagerArmPose.NEUTRAL;
      }
   }

   public void readAdditionalSaveData(CompoundTag p_33291_) {
      super.readAdditionalSaveData(p_33291_);
      ListTag listtag = p_33291_.getList("Inventory", 10);

      for(int i = 0; i < listtag.size(); ++i) {
         ItemStack itemstack = ItemStack.of(listtag.getCompound(i));
         if (!itemstack.isEmpty()) {
            this.inventory.addItem(itemstack);
         }
      }

      this.setCanPickUpLoot(true);
   }

   public float getWalkTargetValue(BlockPos p_33288_, LevelReader p_33289_) {
      return 0.0F;
   }

   public int getMaxSpawnClusterSize() {
      return 1;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33282_, DifficultyInstance p_33283_, MobSpawnType p_33284_, @Nullable SpawnGroupData p_33285_, @Nullable CompoundTag p_33286_) {
      this.populateDefaultEquipmentSlots(p_33283_);
      this.populateDefaultEquipmentEnchantments(p_33283_);
      return super.finalizeSpawn(p_33282_, p_33283_, p_33284_, p_33285_, p_33286_);
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance p_33270_) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
   }

   protected void enchantSpawnedWeapon(float p_33316_) {
      super.enchantSpawnedWeapon(p_33316_);
      if (this.random.nextInt(300) == 0) {
         ItemStack itemstack = this.getMainHandItem();
         if (itemstack.is(Items.CROSSBOW)) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack);
            map.putIfAbsent(Enchantments.PIERCING, 1);
            EnchantmentHelper.setEnchantments(map, itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
         }
      }

   }

   public boolean isAlliedTo(Entity p_33314_) {
      if (super.isAlliedTo(p_33314_)) {
         return true;
      } else if (p_33314_ instanceof LivingEntity && ((LivingEntity)p_33314_).getMobType() == MobType.ILLAGER) {
         return this.getTeam() == null && p_33314_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PILLAGER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PILLAGER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_33306_) {
      return SoundEvents.PILLAGER_HURT;
   }

   public void performRangedAttack(LivingEntity p_33272_, float p_33273_) {
      this.performCrossbowAttack(this, 1.6F);
   }

   public void shootCrossbowProjectile(LivingEntity p_33275_, ItemStack p_33276_, Projectile p_33277_, float p_33278_) {
      this.shootCrossbowProjectile(this, p_33275_, p_33277_, p_33278_, 1.6F);
   }

   public Container getInventory() {
      return this.inventory;
   }

   protected void pickUpItem(ItemEntity p_33296_) {
      ItemStack itemstack = p_33296_.getItem();
      if (itemstack.getItem() instanceof BannerItem) {
         super.pickUpItem(p_33296_);
      } else if (this.wantsItem(itemstack)) {
         this.onItemPickup(p_33296_);
         ItemStack itemstack1 = this.inventory.addItem(itemstack);
         if (itemstack1.isEmpty()) {
            p_33296_.discard();
         } else {
            itemstack.setCount(itemstack1.getCount());
         }
      }

   }

   private boolean wantsItem(ItemStack p_149745_) {
      return this.hasActiveRaid() && p_149745_.is(Items.WHITE_BANNER);
   }

   public SlotAccess getSlot(int p_149743_) {
      int i = p_149743_ - 300;
      return i >= 0 && i < this.inventory.getContainerSize() ? SlotAccess.forContainer(this.inventory, i) : super.getSlot(p_149743_);
   }

   public void applyRaidBuffs(int p_33267_, boolean p_33268_) {
      Raid raid = this.getCurrentRaid();
      boolean flag = this.random.nextFloat() <= raid.getEnchantOdds();
      if (flag) {
         ItemStack itemstack = new ItemStack(Items.CROSSBOW);
         Map<Enchantment, Integer> map = Maps.newHashMap();
         if (p_33267_ > raid.getNumGroups(Difficulty.NORMAL)) {
            map.put(Enchantments.QUICK_CHARGE, 2);
         } else if (p_33267_ > raid.getNumGroups(Difficulty.EASY)) {
            map.put(Enchantments.QUICK_CHARGE, 1);
         }

         map.put(Enchantments.MULTISHOT, 1);
         EnchantmentHelper.setEnchantments(map, itemstack);
         this.setItemSlot(EquipmentSlot.MAINHAND, itemstack);
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.PILLAGER_CELEBRATE;
   }
}
