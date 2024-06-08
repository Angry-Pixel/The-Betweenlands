package net.minecraft.world.entity.decoration;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Rotations;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStand extends LivingEntity {
   public static final int WOBBLE_TIME = 5;
   private static final boolean ENABLE_ARMS = true;
   private static final Rotations DEFAULT_HEAD_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_BODY_POSE = new Rotations(0.0F, 0.0F, 0.0F);
   private static final Rotations DEFAULT_LEFT_ARM_POSE = new Rotations(-10.0F, 0.0F, -10.0F);
   private static final Rotations DEFAULT_RIGHT_ARM_POSE = new Rotations(-15.0F, 0.0F, 10.0F);
   private static final Rotations DEFAULT_LEFT_LEG_POSE = new Rotations(-1.0F, 0.0F, -1.0F);
   private static final Rotations DEFAULT_RIGHT_LEG_POSE = new Rotations(1.0F, 0.0F, 1.0F);
   private static final EntityDimensions MARKER_DIMENSIONS = new EntityDimensions(0.0F, 0.0F, true);
   private static final EntityDimensions BABY_DIMENSIONS = EntityType.ARMOR_STAND.getDimensions().scale(0.5F);
   private static final double FEET_OFFSET = 0.1D;
   private static final double CHEST_OFFSET = 0.9D;
   private static final double LEGS_OFFSET = 0.4D;
   private static final double HEAD_OFFSET = 1.6D;
   public static final int DISABLE_TAKING_OFFSET = 8;
   public static final int DISABLE_PUTTING_OFFSET = 16;
   public static final int CLIENT_FLAG_SMALL = 1;
   public static final int CLIENT_FLAG_SHOW_ARMS = 4;
   public static final int CLIENT_FLAG_NO_BASEPLATE = 8;
   public static final int CLIENT_FLAG_MARKER = 16;
   public static final EntityDataAccessor<Byte> DATA_CLIENT_FLAGS = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.BYTE);
   public static final EntityDataAccessor<Rotations> DATA_HEAD_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_BODY_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_LEFT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_RIGHT_ARM_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_LEFT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   public static final EntityDataAccessor<Rotations> DATA_RIGHT_LEG_POSE = SynchedEntityData.defineId(ArmorStand.class, EntityDataSerializers.ROTATIONS);
   private static final Predicate<Entity> RIDABLE_MINECARTS = (p_31582_) -> {
      return p_31582_ instanceof AbstractMinecart && ((AbstractMinecart)p_31582_).canBeRidden();
   };
   private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
   private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
   private boolean invisible;
   public long lastHit;
   private int disabledSlots;
   private Rotations headPose = DEFAULT_HEAD_POSE;
   private Rotations bodyPose = DEFAULT_BODY_POSE;
   private Rotations leftArmPose = DEFAULT_LEFT_ARM_POSE;
   private Rotations rightArmPose = DEFAULT_RIGHT_ARM_POSE;
   private Rotations leftLegPose = DEFAULT_LEFT_LEG_POSE;
   private Rotations rightLegPose = DEFAULT_RIGHT_LEG_POSE;

   public ArmorStand(EntityType<? extends ArmorStand> p_31553_, Level p_31554_) {
      super(p_31553_, p_31554_);
      this.maxUpStep = 0.0F;
   }

   public ArmorStand(Level p_31556_, double p_31557_, double p_31558_, double p_31559_) {
      this(EntityType.ARMOR_STAND, p_31556_);
      this.setPos(p_31557_, p_31558_, p_31559_);
   }

   public void refreshDimensions() {
      double d0 = this.getX();
      double d1 = this.getY();
      double d2 = this.getZ();
      super.refreshDimensions();
      this.setPos(d0, d1, d2);
   }

   private boolean hasPhysics() {
      return !this.isMarker() && !this.isNoGravity();
   }

   public boolean isEffectiveAi() {
      return super.isEffectiveAi() && this.hasPhysics();
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_CLIENT_FLAGS, (byte)0);
      this.entityData.define(DATA_HEAD_POSE, DEFAULT_HEAD_POSE);
      this.entityData.define(DATA_BODY_POSE, DEFAULT_BODY_POSE);
      this.entityData.define(DATA_LEFT_ARM_POSE, DEFAULT_LEFT_ARM_POSE);
      this.entityData.define(DATA_RIGHT_ARM_POSE, DEFAULT_RIGHT_ARM_POSE);
      this.entityData.define(DATA_LEFT_LEG_POSE, DEFAULT_LEFT_LEG_POSE);
      this.entityData.define(DATA_RIGHT_LEG_POSE, DEFAULT_RIGHT_LEG_POSE);
   }

   public Iterable<ItemStack> getHandSlots() {
      return this.handItems;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return this.armorItems;
   }

   public ItemStack getItemBySlot(EquipmentSlot p_31612_) {
      switch(p_31612_.getType()) {
      case HAND:
         return this.handItems.get(p_31612_.getIndex());
      case ARMOR:
         return this.armorItems.get(p_31612_.getIndex());
      default:
         return ItemStack.EMPTY;
      }
   }

   public void setItemSlot(EquipmentSlot p_31584_, ItemStack p_31585_) {
      this.verifyEquippedItem(p_31585_);
      switch(p_31584_.getType()) {
      case HAND:
         this.equipEventAndSound(p_31585_);
         this.handItems.set(p_31584_.getIndex(), p_31585_);
         break;
      case ARMOR:
         this.equipEventAndSound(p_31585_);
         this.armorItems.set(p_31584_.getIndex(), p_31585_);
      }

   }

   public boolean canTakeItem(ItemStack p_31638_) {
      EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(p_31638_);
      return this.getItemBySlot(equipmentslot).isEmpty() && !this.isDisabled(equipmentslot);
   }

   public void addAdditionalSaveData(CompoundTag p_31619_) {
      super.addAdditionalSaveData(p_31619_);
      ListTag listtag = new ListTag();

      for(ItemStack itemstack : this.armorItems) {
         CompoundTag compoundtag = new CompoundTag();
         if (!itemstack.isEmpty()) {
            itemstack.save(compoundtag);
         }

         listtag.add(compoundtag);
      }

      p_31619_.put("ArmorItems", listtag);
      ListTag listtag1 = new ListTag();

      for(ItemStack itemstack1 : this.handItems) {
         CompoundTag compoundtag1 = new CompoundTag();
         if (!itemstack1.isEmpty()) {
            itemstack1.save(compoundtag1);
         }

         listtag1.add(compoundtag1);
      }

      p_31619_.put("HandItems", listtag1);
      p_31619_.putBoolean("Invisible", this.isInvisible());
      p_31619_.putBoolean("Small", this.isSmall());
      p_31619_.putBoolean("ShowArms", this.isShowArms());
      p_31619_.putInt("DisabledSlots", this.disabledSlots);
      p_31619_.putBoolean("NoBasePlate", this.isNoBasePlate());
      if (this.isMarker()) {
         p_31619_.putBoolean("Marker", this.isMarker());
      }

      p_31619_.put("Pose", this.writePose());
   }

   public void readAdditionalSaveData(CompoundTag p_31600_) {
      super.readAdditionalSaveData(p_31600_);
      if (p_31600_.contains("ArmorItems", 9)) {
         ListTag listtag = p_31600_.getList("ArmorItems", 10);

         for(int i = 0; i < this.armorItems.size(); ++i) {
            this.armorItems.set(i, ItemStack.of(listtag.getCompound(i)));
         }
      }

      if (p_31600_.contains("HandItems", 9)) {
         ListTag listtag1 = p_31600_.getList("HandItems", 10);

         for(int j = 0; j < this.handItems.size(); ++j) {
            this.handItems.set(j, ItemStack.of(listtag1.getCompound(j)));
         }
      }

      this.setInvisible(p_31600_.getBoolean("Invisible"));
      this.setSmall(p_31600_.getBoolean("Small"));
      this.setShowArms(p_31600_.getBoolean("ShowArms"));
      this.disabledSlots = p_31600_.getInt("DisabledSlots");
      this.setNoBasePlate(p_31600_.getBoolean("NoBasePlate"));
      this.setMarker(p_31600_.getBoolean("Marker"));
      this.noPhysics = !this.hasPhysics();
      CompoundTag compoundtag = p_31600_.getCompound("Pose");
      this.readPose(compoundtag);
   }

   private void readPose(CompoundTag p_31658_) {
      ListTag listtag = p_31658_.getList("Head", 5);
      this.setHeadPose(listtag.isEmpty() ? DEFAULT_HEAD_POSE : new Rotations(listtag));
      ListTag listtag1 = p_31658_.getList("Body", 5);
      this.setBodyPose(listtag1.isEmpty() ? DEFAULT_BODY_POSE : new Rotations(listtag1));
      ListTag listtag2 = p_31658_.getList("LeftArm", 5);
      this.setLeftArmPose(listtag2.isEmpty() ? DEFAULT_LEFT_ARM_POSE : new Rotations(listtag2));
      ListTag listtag3 = p_31658_.getList("RightArm", 5);
      this.setRightArmPose(listtag3.isEmpty() ? DEFAULT_RIGHT_ARM_POSE : new Rotations(listtag3));
      ListTag listtag4 = p_31658_.getList("LeftLeg", 5);
      this.setLeftLegPose(listtag4.isEmpty() ? DEFAULT_LEFT_LEG_POSE : new Rotations(listtag4));
      ListTag listtag5 = p_31658_.getList("RightLeg", 5);
      this.setRightLegPose(listtag5.isEmpty() ? DEFAULT_RIGHT_LEG_POSE : new Rotations(listtag5));
   }

   private CompoundTag writePose() {
      CompoundTag compoundtag = new CompoundTag();
      if (!DEFAULT_HEAD_POSE.equals(this.headPose)) {
         compoundtag.put("Head", this.headPose.save());
      }

      if (!DEFAULT_BODY_POSE.equals(this.bodyPose)) {
         compoundtag.put("Body", this.bodyPose.save());
      }

      if (!DEFAULT_LEFT_ARM_POSE.equals(this.leftArmPose)) {
         compoundtag.put("LeftArm", this.leftArmPose.save());
      }

      if (!DEFAULT_RIGHT_ARM_POSE.equals(this.rightArmPose)) {
         compoundtag.put("RightArm", this.rightArmPose.save());
      }

      if (!DEFAULT_LEFT_LEG_POSE.equals(this.leftLegPose)) {
         compoundtag.put("LeftLeg", this.leftLegPose.save());
      }

      if (!DEFAULT_RIGHT_LEG_POSE.equals(this.rightLegPose)) {
         compoundtag.put("RightLeg", this.rightLegPose.save());
      }

      return compoundtag;
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity p_31564_) {
   }

   protected void pushEntities() {
      List<Entity> list = this.level.getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS);

      for(int i = 0; i < list.size(); ++i) {
         Entity entity = list.get(i);
         if (this.distanceToSqr(entity) <= 0.2D) {
            entity.push(this);
         }
      }

   }

   public InteractionResult interactAt(Player p_31594_, Vec3 p_31595_, InteractionHand p_31596_) {
      ItemStack itemstack = p_31594_.getItemInHand(p_31596_);
      if (!this.isMarker() && !itemstack.is(Items.NAME_TAG)) {
         if (p_31594_.isSpectator()) {
            return InteractionResult.SUCCESS;
         } else if (p_31594_.level.isClientSide) {
            return InteractionResult.CONSUME;
         } else {
            EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(itemstack);
            if (itemstack.isEmpty()) {
               EquipmentSlot equipmentslot1 = this.getClickedSlot(p_31595_);
               EquipmentSlot equipmentslot2 = this.isDisabled(equipmentslot1) ? equipmentslot : equipmentslot1;
               if (this.hasItemInSlot(equipmentslot2) && this.swapItem(p_31594_, equipmentslot2, itemstack, p_31596_)) {
                  return InteractionResult.SUCCESS;
               }
            } else {
               if (this.isDisabled(equipmentslot)) {
                  return InteractionResult.FAIL;
               }

               if (equipmentslot.getType() == EquipmentSlot.Type.HAND && !this.isShowArms()) {
                  return InteractionResult.FAIL;
               }

               if (this.swapItem(p_31594_, equipmentslot, itemstack, p_31596_)) {
                  return InteractionResult.SUCCESS;
               }
            }

            return InteractionResult.PASS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   private EquipmentSlot getClickedSlot(Vec3 p_31660_) {
      EquipmentSlot equipmentslot = EquipmentSlot.MAINHAND;
      boolean flag = this.isSmall();
      double d0 = flag ? p_31660_.y * 2.0D : p_31660_.y;
      EquipmentSlot equipmentslot1 = EquipmentSlot.FEET;
      if (d0 >= 0.1D && d0 < 0.1D + (flag ? 0.8D : 0.45D) && this.hasItemInSlot(equipmentslot1)) {
         equipmentslot = EquipmentSlot.FEET;
      } else if (d0 >= 0.9D + (flag ? 0.3D : 0.0D) && d0 < 0.9D + (flag ? 1.0D : 0.7D) && this.hasItemInSlot(EquipmentSlot.CHEST)) {
         equipmentslot = EquipmentSlot.CHEST;
      } else if (d0 >= 0.4D && d0 < 0.4D + (flag ? 1.0D : 0.8D) && this.hasItemInSlot(EquipmentSlot.LEGS)) {
         equipmentslot = EquipmentSlot.LEGS;
      } else if (d0 >= 1.6D && this.hasItemInSlot(EquipmentSlot.HEAD)) {
         equipmentslot = EquipmentSlot.HEAD;
      } else if (!this.hasItemInSlot(EquipmentSlot.MAINHAND) && this.hasItemInSlot(EquipmentSlot.OFFHAND)) {
         equipmentslot = EquipmentSlot.OFFHAND;
      }

      return equipmentslot;
   }

   private boolean isDisabled(EquipmentSlot p_31627_) {
      return (this.disabledSlots & 1 << p_31627_.getFilterFlag()) != 0 || p_31627_.getType() == EquipmentSlot.Type.HAND && !this.isShowArms();
   }

   private boolean swapItem(Player p_31589_, EquipmentSlot p_31590_, ItemStack p_31591_, InteractionHand p_31592_) {
      ItemStack itemstack = this.getItemBySlot(p_31590_);
      if (!itemstack.isEmpty() && (this.disabledSlots & 1 << p_31590_.getFilterFlag() + 8) != 0) {
         return false;
      } else if (itemstack.isEmpty() && (this.disabledSlots & 1 << p_31590_.getFilterFlag() + 16) != 0) {
         return false;
      } else if (p_31589_.getAbilities().instabuild && itemstack.isEmpty() && !p_31591_.isEmpty()) {
         ItemStack itemstack2 = p_31591_.copy();
         itemstack2.setCount(1);
         this.setItemSlot(p_31590_, itemstack2);
         return true;
      } else if (!p_31591_.isEmpty() && p_31591_.getCount() > 1) {
         if (!itemstack.isEmpty()) {
            return false;
         } else {
            ItemStack itemstack1 = p_31591_.copy();
            itemstack1.setCount(1);
            this.setItemSlot(p_31590_, itemstack1);
            p_31591_.shrink(1);
            return true;
         }
      } else {
         this.setItemSlot(p_31590_, p_31591_);
         p_31589_.setItemInHand(p_31592_, itemstack);
         return true;
      }
   }

   public boolean hurt(DamageSource p_31579_, float p_31580_) {
      if (!this.level.isClientSide && !this.isRemoved()) {
         if (DamageSource.OUT_OF_WORLD.equals(p_31579_)) {
            this.kill();
            return false;
         } else if (!this.isInvulnerableTo(p_31579_) && !this.invisible && !this.isMarker()) {
            if (p_31579_.isExplosion()) {
               this.brokenByAnything(p_31579_);
               this.kill();
               return false;
            } else if (DamageSource.IN_FIRE.equals(p_31579_)) {
               if (this.isOnFire()) {
                  this.causeDamage(p_31579_, 0.15F);
               } else {
                  this.setSecondsOnFire(5);
               }

               return false;
            } else if (DamageSource.ON_FIRE.equals(p_31579_) && this.getHealth() > 0.5F) {
               this.causeDamage(p_31579_, 4.0F);
               return false;
            } else {
               boolean flag = p_31579_.getDirectEntity() instanceof AbstractArrow;
               boolean flag1 = flag && ((AbstractArrow)p_31579_.getDirectEntity()).getPierceLevel() > 0;
               boolean flag2 = "player".equals(p_31579_.getMsgId());
               if (!flag2 && !flag) {
                  return false;
               } else if (p_31579_.getEntity() instanceof Player && !((Player)p_31579_.getEntity()).getAbilities().mayBuild) {
                  return false;
               } else if (p_31579_.isCreativePlayer()) {
                  this.playBrokenSound();
                  this.showBreakingParticles();
                  this.kill();
                  return flag1;
               } else {
                  long i = this.level.getGameTime();
                  if (i - this.lastHit > 5L && !flag) {
                     this.level.broadcastEntityEvent(this, (byte)32);
                     this.gameEvent(GameEvent.ENTITY_DAMAGED, p_31579_.getEntity());
                     this.lastHit = i;
                  } else {
                     this.brokenByPlayer(p_31579_);
                     this.showBreakingParticles();
                     this.kill();
                  }

                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void handleEntityEvent(byte p_31568_) {
      if (p_31568_ == 32) {
         if (this.level.isClientSide) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F, false);
            this.lastHit = this.level.getGameTime();
         }
      } else {
         super.handleEntityEvent(p_31568_);
      }

   }

   public boolean shouldRenderAtSqrDistance(double p_31574_) {
      double d0 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(d0) || d0 == 0.0D) {
         d0 = 4.0D;
      }

      d0 *= 64.0D;
      return p_31574_ < d0 * d0;
   }

   private void showBreakingParticles() {
      if (this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (double)(this.getBbWidth() / 4.0F), (double)(this.getBbHeight() / 4.0F), (double)(this.getBbWidth() / 4.0F), 0.05D);
      }

   }

   private void causeDamage(DamageSource p_31649_, float p_31650_) {
      float f = this.getHealth();
      f -= p_31650_;
      if (f <= 0.5F) {
         this.brokenByAnything(p_31649_);
         this.kill();
      } else {
         this.setHealth(f);
         this.gameEvent(GameEvent.ENTITY_DAMAGED, p_31649_.getEntity());
      }

   }

   private void brokenByPlayer(DamageSource p_31647_) {
      Block.popResource(this.level, this.blockPosition(), new ItemStack(Items.ARMOR_STAND));
      this.brokenByAnything(p_31647_);
   }

   private void brokenByAnything(DamageSource p_31654_) {
      this.playBrokenSound();
      this.dropAllDeathLoot(p_31654_);

      for(int i = 0; i < this.handItems.size(); ++i) {
         ItemStack itemstack = this.handItems.get(i);
         if (!itemstack.isEmpty()) {
            Block.popResource(this.level, this.blockPosition().above(), itemstack);
            this.handItems.set(i, ItemStack.EMPTY);
         }
      }

      for(int j = 0; j < this.armorItems.size(); ++j) {
         ItemStack itemstack1 = this.armorItems.get(j);
         if (!itemstack1.isEmpty()) {
            Block.popResource(this.level, this.blockPosition().above(), itemstack1);
            this.armorItems.set(j, ItemStack.EMPTY);
         }
      }

   }

   private void playBrokenSound() {
      this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
   }

   protected float tickHeadTurn(float p_31644_, float p_31645_) {
      this.yBodyRotO = this.yRotO;
      this.yBodyRot = this.getYRot();
      return 0.0F;
   }

   protected float getStandingEyeHeight(Pose p_31614_, EntityDimensions p_31615_) {
      return p_31615_.height * (this.isBaby() ? 0.5F : 0.9F);
   }

   public double getMyRidingOffset() {
      return this.isMarker() ? 0.0D : (double)0.1F;
   }

   public void travel(Vec3 p_31656_) {
      if (this.hasPhysics()) {
         super.travel(p_31656_);
      }
   }

   public void setYBodyRot(float p_31670_) {
      this.yBodyRotO = this.yRotO = p_31670_;
      this.yHeadRotO = this.yHeadRot = p_31670_;
   }

   public void setYHeadRot(float p_31668_) {
      this.yBodyRotO = this.yRotO = p_31668_;
      this.yHeadRotO = this.yHeadRot = p_31668_;
   }

   public void tick() {
      super.tick();
      Rotations rotations = this.entityData.get(DATA_HEAD_POSE);
      if (!this.headPose.equals(rotations)) {
         this.setHeadPose(rotations);
      }

      Rotations rotations1 = this.entityData.get(DATA_BODY_POSE);
      if (!this.bodyPose.equals(rotations1)) {
         this.setBodyPose(rotations1);
      }

      Rotations rotations2 = this.entityData.get(DATA_LEFT_ARM_POSE);
      if (!this.leftArmPose.equals(rotations2)) {
         this.setLeftArmPose(rotations2);
      }

      Rotations rotations3 = this.entityData.get(DATA_RIGHT_ARM_POSE);
      if (!this.rightArmPose.equals(rotations3)) {
         this.setRightArmPose(rotations3);
      }

      Rotations rotations4 = this.entityData.get(DATA_LEFT_LEG_POSE);
      if (!this.leftLegPose.equals(rotations4)) {
         this.setLeftLegPose(rotations4);
      }

      Rotations rotations5 = this.entityData.get(DATA_RIGHT_LEG_POSE);
      if (!this.rightLegPose.equals(rotations5)) {
         this.setRightLegPose(rotations5);
      }

   }

   protected void updateInvisibilityStatus() {
      this.setInvisible(this.invisible);
   }

   public void setInvisible(boolean p_31663_) {
      this.invisible = p_31663_;
      super.setInvisible(p_31663_);
   }

   public boolean isBaby() {
      return this.isSmall();
   }

   public void kill() {
      this.remove(Entity.RemovalReason.KILLED);
   }

   public boolean ignoreExplosion() {
      return this.isInvisible();
   }

   public PushReaction getPistonPushReaction() {
      return this.isMarker() ? PushReaction.IGNORE : super.getPistonPushReaction();
   }

   private void setSmall(boolean p_31604_) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 1, p_31604_));
   }

   public boolean isSmall() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 1) != 0;
   }

   private void setShowArms(boolean p_31676_) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 4, p_31676_));
   }

   public boolean isShowArms() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 4) != 0;
   }

   private void setNoBasePlate(boolean p_31679_) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 8, p_31679_));
   }

   public boolean isNoBasePlate() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 8) != 0;
   }

   private void setMarker(boolean p_31682_) {
      this.entityData.set(DATA_CLIENT_FLAGS, this.setBit(this.entityData.get(DATA_CLIENT_FLAGS), 16, p_31682_));
   }

   public boolean isMarker() {
      return (this.entityData.get(DATA_CLIENT_FLAGS) & 16) != 0;
   }

   private byte setBit(byte p_31570_, int p_31571_, boolean p_31572_) {
      if (p_31572_) {
         p_31570_ = (byte)(p_31570_ | p_31571_);
      } else {
         p_31570_ = (byte)(p_31570_ & ~p_31571_);
      }

      return p_31570_;
   }

   public void setHeadPose(Rotations p_31598_) {
      this.headPose = p_31598_;
      this.entityData.set(DATA_HEAD_POSE, p_31598_);
   }

   public void setBodyPose(Rotations p_31617_) {
      this.bodyPose = p_31617_;
      this.entityData.set(DATA_BODY_POSE, p_31617_);
   }

   public void setLeftArmPose(Rotations p_31624_) {
      this.leftArmPose = p_31624_;
      this.entityData.set(DATA_LEFT_ARM_POSE, p_31624_);
   }

   public void setRightArmPose(Rotations p_31629_) {
      this.rightArmPose = p_31629_;
      this.entityData.set(DATA_RIGHT_ARM_POSE, p_31629_);
   }

   public void setLeftLegPose(Rotations p_31640_) {
      this.leftLegPose = p_31640_;
      this.entityData.set(DATA_LEFT_LEG_POSE, p_31640_);
   }

   public void setRightLegPose(Rotations p_31652_) {
      this.rightLegPose = p_31652_;
      this.entityData.set(DATA_RIGHT_LEG_POSE, p_31652_);
   }

   public Rotations getHeadPose() {
      return this.headPose;
   }

   public Rotations getBodyPose() {
      return this.bodyPose;
   }

   public Rotations getLeftArmPose() {
      return this.leftArmPose;
   }

   public Rotations getRightArmPose() {
      return this.rightArmPose;
   }

   public Rotations getLeftLegPose() {
      return this.leftLegPose;
   }

   public Rotations getRightLegPose() {
      return this.rightLegPose;
   }

   public boolean isPickable() {
      return super.isPickable() && !this.isMarker();
   }

   public boolean skipAttackInteraction(Entity p_31687_) {
      return p_31687_ instanceof Player && !this.level.mayInteract((Player)p_31687_, this.blockPosition());
   }

   public HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

   public LivingEntity.Fallsounds getFallSounds() {
      return new LivingEntity.Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource p_31636_) {
      return SoundEvents.ARMOR_STAND_HIT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ARMOR_STAND_BREAK;
   }

   public void thunderHit(ServerLevel p_31576_, LightningBolt p_31577_) {
   }

   public boolean isAffectedByPotions() {
      return false;
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_31602_) {
      if (DATA_CLIENT_FLAGS.equals(p_31602_)) {
         this.refreshDimensions();
         this.blocksBuilding = !this.isMarker();
      }

      super.onSyncedDataUpdated(p_31602_);
   }

   public boolean attackable() {
      return false;
   }

   public EntityDimensions getDimensions(Pose p_31587_) {
      return this.getDimensionsMarker(this.isMarker());
   }

   private EntityDimensions getDimensionsMarker(boolean p_31684_) {
      if (p_31684_) {
         return MARKER_DIMENSIONS;
      } else {
         return this.isBaby() ? BABY_DIMENSIONS : this.getType().getDimensions();
      }
   }

   public Vec3 getLightProbePosition(float p_31665_) {
      if (this.isMarker()) {
         AABB aabb = this.getDimensionsMarker(false).makeBoundingBox(this.position());
         BlockPos blockpos = this.blockPosition();
         int i = Integer.MIN_VALUE;

         for(BlockPos blockpos1 : BlockPos.betweenClosed(new BlockPos(aabb.minX, aabb.minY, aabb.minZ), new BlockPos(aabb.maxX, aabb.maxY, aabb.maxZ))) {
            int j = Math.max(this.level.getBrightness(LightLayer.BLOCK, blockpos1), this.level.getBrightness(LightLayer.SKY, blockpos1));
            if (j == 15) {
               return Vec3.atCenterOf(blockpos1);
            }

            if (j > i) {
               i = j;
               blockpos = blockpos1.immutable();
            }
         }

         return Vec3.atCenterOf(blockpos);
      } else {
         return super.getLightProbePosition(p_31665_);
      }
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.ARMOR_STAND);
   }

   public boolean canBeSeenByAnyone() {
      return !this.isInvisible() && !this.isMarker();
   }
}
