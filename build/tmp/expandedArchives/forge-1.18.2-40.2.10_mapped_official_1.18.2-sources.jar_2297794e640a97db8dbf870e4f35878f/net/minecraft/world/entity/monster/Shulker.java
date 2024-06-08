package net.minecraft.world.entity.monster;

import com.mojang.math.Vector3f;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Shulker extends AbstractGolem implements Enemy {
   private static final UUID COVERED_ARMOR_MODIFIER_UUID = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   private static final AttributeModifier COVERED_ARMOR_MODIFIER = new AttributeModifier(COVERED_ARMOR_MODIFIER_UUID, "Covered armor bonus", 20.0D, AttributeModifier.Operation.ADDITION);
   protected static final EntityDataAccessor<Direction> DATA_ATTACH_FACE_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.DIRECTION);
   protected static final EntityDataAccessor<Byte> DATA_PEEK_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
   protected static final EntityDataAccessor<Byte> DATA_COLOR_ID = SynchedEntityData.defineId(Shulker.class, EntityDataSerializers.BYTE);
   private static final int TELEPORT_STEPS = 6;
   private static final byte NO_COLOR = 16;
   private static final byte DEFAULT_COLOR = 16;
   private static final int MAX_TELEPORT_DISTANCE = 8;
   private static final int OTHER_SHULKER_SCAN_RADIUS = 8;
   private static final int OTHER_SHULKER_LIMIT = 5;
   private static final float PEEK_PER_TICK = 0.05F;
   static final Vector3f FORWARD = Util.make(() -> {
      Vec3i vec3i = Direction.SOUTH.getNormal();
      return new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
   });
   private float currentPeekAmountO;
   private float currentPeekAmount;
   @Nullable
   private BlockPos clientOldAttachPosition;
   private int clientSideTeleportInterpolation;
   private static final float MAX_LID_OPEN = 1.0F;

   public Shulker(EntityType<? extends Shulker> p_33404_, Level p_33405_) {
      super(p_33404_, p_33405_);
      this.xpReward = 5;
      this.lookControl = new Shulker.ShulkerLookControl(this);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F, 0.02F, true));
      this.goalSelector.addGoal(4, new Shulker.ShulkerAttackGoal());
      this.goalSelector.addGoal(7, new Shulker.ShulkerPeekGoal());
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, this.getClass())).setAlertOthers());
      this.targetSelector.addGoal(2, new Shulker.ShulkerNearestAttackGoal(this));
      this.targetSelector.addGoal(3, new Shulker.ShulkerDefenseAttackGoal(this));
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHULKER_AMBIENT;
   }

   public void playAmbientSound() {
      if (!this.isClosed()) {
         super.playAmbientSound();
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHULKER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_33457_) {
      return this.isClosed() ? SoundEvents.SHULKER_HURT_CLOSED : SoundEvents.SHULKER_HURT;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ATTACH_FACE_ID, Direction.DOWN);
      this.entityData.define(DATA_PEEK_ID, (byte)0);
      this.entityData.define(DATA_COLOR_ID, (byte)16);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D);
   }

   protected BodyRotationControl createBodyControl() {
      return new Shulker.ShulkerBodyRotationControl(this);
   }

   public void readAdditionalSaveData(CompoundTag p_33432_) {
      super.readAdditionalSaveData(p_33432_);
      this.setAttachFace(Direction.from3DDataValue(p_33432_.getByte("AttachFace")));
      this.entityData.set(DATA_PEEK_ID, p_33432_.getByte("Peek"));
      if (p_33432_.contains("Color", 99)) {
         this.entityData.set(DATA_COLOR_ID, p_33432_.getByte("Color"));
      }

   }

   public void addAdditionalSaveData(CompoundTag p_33443_) {
      super.addAdditionalSaveData(p_33443_);
      p_33443_.putByte("AttachFace", (byte)this.getAttachFace().get3DDataValue());
      p_33443_.putByte("Peek", this.entityData.get(DATA_PEEK_ID));
      p_33443_.putByte("Color", this.entityData.get(DATA_COLOR_ID));
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide && !this.isPassenger() && !this.canStayAt(this.blockPosition(), this.getAttachFace())) {
         this.findNewAttachment();
      }

      if (this.updatePeekAmount()) {
         this.onPeekAmountChange();
      }

      if (this.level.isClientSide) {
         if (this.clientSideTeleportInterpolation > 0) {
            --this.clientSideTeleportInterpolation;
         } else {
            this.clientOldAttachPosition = null;
         }
      }

   }

   private void findNewAttachment() {
      Direction direction = this.findAttachableSurface(this.blockPosition());
      if (direction != null) {
         this.setAttachFace(direction);
      } else {
         this.teleportSomewhere();
      }

   }

   protected AABB makeBoundingBox() {
      float f = getPhysicalPeek(this.currentPeekAmount);
      Direction direction = this.getAttachFace().getOpposite();
      float f1 = this.getType().getWidth() / 2.0F;
      return getProgressAabb(direction, f).move(this.getX() - (double)f1, this.getY(), this.getZ() - (double)f1);
   }

   private static float getPhysicalPeek(float p_149769_) {
      return 0.5F - Mth.sin((0.5F + p_149769_) * (float)Math.PI) * 0.5F;
   }

   private boolean updatePeekAmount() {
      this.currentPeekAmountO = this.currentPeekAmount;
      float f = (float)this.getRawPeekAmount() * 0.01F;
      if (this.currentPeekAmount == f) {
         return false;
      } else {
         if (this.currentPeekAmount > f) {
            this.currentPeekAmount = Mth.clamp(this.currentPeekAmount - 0.05F, f, 1.0F);
         } else {
            this.currentPeekAmount = Mth.clamp(this.currentPeekAmount + 0.05F, 0.0F, f);
         }

         return true;
      }
   }

   private void onPeekAmountChange() {
      this.reapplyPosition();
      float f = getPhysicalPeek(this.currentPeekAmount);
      float f1 = getPhysicalPeek(this.currentPeekAmountO);
      Direction direction = this.getAttachFace().getOpposite();
      float f2 = f - f1;
      if (!(f2 <= 0.0F)) {
         for(Entity entity : this.level.getEntities(this, getProgressDeltaAabb(direction, f1, f).move(this.getX() - 0.5D, this.getY(), this.getZ() - 0.5D), EntitySelector.NO_SPECTATORS.and((p_149771_) -> {
            return !p_149771_.isPassengerOfSameVehicle(this);
         }))) {
            if (!(entity instanceof Shulker) && !entity.noPhysics) {
               entity.move(MoverType.SHULKER, new Vec3((double)(f2 * (float)direction.getStepX()), (double)(f2 * (float)direction.getStepY()), (double)(f2 * (float)direction.getStepZ())));
            }
         }

      }
   }

   public static AABB getProgressAabb(Direction p_149791_, float p_149792_) {
      return getProgressDeltaAabb(p_149791_, -1.0F, p_149792_);
   }

   public static AABB getProgressDeltaAabb(Direction p_149794_, float p_149795_, float p_149796_) {
      double d0 = (double)Math.max(p_149795_, p_149796_);
      double d1 = (double)Math.min(p_149795_, p_149796_);
      return (new AABB(BlockPos.ZERO)).expandTowards((double)p_149794_.getStepX() * d0, (double)p_149794_.getStepY() * d0, (double)p_149794_.getStepZ() * d0).contract((double)(-p_149794_.getStepX()) * (1.0D + d1), (double)(-p_149794_.getStepY()) * (1.0D + d1), (double)(-p_149794_.getStepZ()) * (1.0D + d1));
   }

   public double getMyRidingOffset() {
      EntityType<?> entitytype = this.getVehicle().getType();
      return entitytype != EntityType.BOAT && entitytype != EntityType.MINECART ? super.getMyRidingOffset() : 0.1875D - this.getVehicle().getPassengersRidingOffset();
   }

   public boolean startRiding(Entity p_149773_, boolean p_149774_) {
      if (this.level.isClientSide()) {
         this.clientOldAttachPosition = null;
         this.clientSideTeleportInterpolation = 0;
      }

      this.setAttachFace(Direction.DOWN);
      return super.startRiding(p_149773_, p_149774_);
   }

   public void stopRiding() {
      super.stopRiding();
      if (this.level.isClientSide) {
         this.clientOldAttachPosition = this.blockPosition();
      }

      this.yBodyRotO = 0.0F;
      this.yBodyRot = 0.0F;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_149780_, DifficultyInstance p_149781_, MobSpawnType p_149782_, @Nullable SpawnGroupData p_149783_, @Nullable CompoundTag p_149784_) {
      this.setYRot(0.0F);
      this.yHeadRot = this.getYRot();
      this.setOldPosAndRot();
      return super.finalizeSpawn(p_149780_, p_149781_, p_149782_, p_149783_, p_149784_);
   }

   public void move(MoverType p_33424_, Vec3 p_33425_) {
      if (p_33424_ == MoverType.SHULKER_BOX) {
         this.teleportSomewhere();
      } else {
         super.move(p_33424_, p_33425_);
      }

   }

   public Vec3 getDeltaMovement() {
      return Vec3.ZERO;
   }

   public void setDeltaMovement(Vec3 p_149804_) {
   }

   public void setPos(double p_33449_, double p_33450_, double p_33451_) {
      BlockPos blockpos = this.blockPosition();
      if (this.isPassenger()) {
         super.setPos(p_33449_, p_33450_, p_33451_);
      } else {
         super.setPos((double)Mth.floor(p_33449_) + 0.5D, (double)Mth.floor(p_33450_ + 0.5D), (double)Mth.floor(p_33451_) + 0.5D);
      }

      if (this.tickCount != 0) {
         BlockPos blockpos1 = this.blockPosition();
         if (!blockpos1.equals(blockpos)) {
            this.entityData.set(DATA_PEEK_ID, (byte)0);
            this.hasImpulse = true;
            if (this.level.isClientSide && !this.isPassenger() && !blockpos1.equals(this.clientOldAttachPosition)) {
               this.clientOldAttachPosition = blockpos;
               this.clientSideTeleportInterpolation = 6;
               this.xOld = this.getX();
               this.yOld = this.getY();
               this.zOld = this.getZ();
            }
         }

      }
   }

   @Nullable
   protected Direction findAttachableSurface(BlockPos p_149811_) {
      for(Direction direction : Direction.values()) {
         if (this.canStayAt(p_149811_, direction)) {
            return direction;
         }
      }

      return null;
   }

   boolean canStayAt(BlockPos p_149786_, Direction p_149787_) {
      if (this.isPositionBlocked(p_149786_)) {
         return false;
      } else {
         Direction direction = p_149787_.getOpposite();
         if (!this.level.loadedAndEntityCanStandOnFace(p_149786_.relative(p_149787_), this, direction)) {
            return false;
         } else {
            AABB aabb = getProgressAabb(direction, 1.0F).move(p_149786_).deflate(1.0E-6D);
            return this.level.noCollision(this, aabb);
         }
      }
   }

   private boolean isPositionBlocked(BlockPos p_149813_) {
      BlockState blockstate = this.level.getBlockState(p_149813_);
      if (blockstate.isAir()) {
         return false;
      } else {
         boolean flag = blockstate.is(Blocks.MOVING_PISTON) && p_149813_.equals(this.blockPosition());
         return !flag;
      }
   }

   protected boolean teleportSomewhere() {
      if (!this.isNoAi() && this.isAlive()) {
         BlockPos blockpos = this.blockPosition();

         for(int i = 0; i < 5; ++i) {
            BlockPos blockpos1 = blockpos.offset(Mth.randomBetweenInclusive(this.random, -8, 8), Mth.randomBetweenInclusive(this.random, -8, 8), Mth.randomBetweenInclusive(this.random, -8, 8));
            if (blockpos1.getY() > this.level.getMinBuildHeight() && this.level.isEmptyBlock(blockpos1) && this.level.getWorldBorder().isWithinBounds(blockpos1) && this.level.noCollision(this, (new AABB(blockpos1)).deflate(1.0E-6D))) {
               Direction direction = this.findAttachableSurface(blockpos1);
               if (direction != null) {
                  net.minecraftforge.event.entity.EntityTeleportEvent.EnderEntity event = net.minecraftforge.event.ForgeEventFactory.onEnderTeleport(this, blockpos1.getX(), blockpos1.getY(), blockpos1.getZ());
                  if (event.isCanceled()) direction = null;
                  blockpos1 = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
               }

               if (direction != null) {
                  this.unRide();
                  this.setAttachFace(direction);
                  this.playSound(SoundEvents.SHULKER_TELEPORT, 1.0F, 1.0F);
                  this.setPos((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY(), (double)blockpos1.getZ() + 0.5D);
                  this.entityData.set(DATA_PEEK_ID, (byte)0);
                  this.setTarget((LivingEntity)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public void lerpTo(double p_33411_, double p_33412_, double p_33413_, float p_33414_, float p_33415_, int p_33416_, boolean p_33417_) {
      this.lerpSteps = 0;
      this.setPos(p_33411_, p_33412_, p_33413_);
      this.setRot(p_33414_, p_33415_);
   }

   public boolean hurt(DamageSource p_33421_, float p_33422_) {
      if (this.isClosed()) {
         Entity entity = p_33421_.getDirectEntity();
         if (entity instanceof AbstractArrow) {
            return false;
         }
      }

      if (!super.hurt(p_33421_, p_33422_)) {
         return false;
      } else {
         if ((double)this.getHealth() < (double)this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
            this.teleportSomewhere();
         } else if (p_33421_.isProjectile()) {
            Entity entity1 = p_33421_.getDirectEntity();
            if (entity1 != null && entity1.getType() == EntityType.SHULKER_BULLET) {
               this.hitByShulkerBullet();
            }
         }

         return true;
      }
   }

   private boolean isClosed() {
      return this.getRawPeekAmount() == 0;
   }

   private void hitByShulkerBullet() {
      Vec3 vec3 = this.position();
      AABB aabb = this.getBoundingBox();
      if (!this.isClosed() && this.teleportSomewhere()) {
         int i = this.level.getEntities(EntityType.SHULKER, aabb.inflate(8.0D), Entity::isAlive).size();
         float f = (float)(i - 1) / 5.0F;
         if (!(this.level.random.nextFloat() < f)) {
            Shulker shulker = EntityType.SHULKER.create(this.level);
            DyeColor dyecolor = this.getColor();
            if (dyecolor != null) {
               shulker.setColor(dyecolor);
            }

            shulker.moveTo(vec3);
            this.level.addFreshEntity(shulker);
         }
      }
   }

   public boolean canBeCollidedWith() {
      return this.isAlive();
   }

   public Direction getAttachFace() {
      return this.entityData.get(DATA_ATTACH_FACE_ID);
   }

   private void setAttachFace(Direction p_149789_) {
      this.entityData.set(DATA_ATTACH_FACE_ID, p_149789_);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_33434_) {
      if (DATA_ATTACH_FACE_ID.equals(p_33434_)) {
         this.setBoundingBox(this.makeBoundingBox());
      }

      super.onSyncedDataUpdated(p_33434_);
   }

   private int getRawPeekAmount() {
      return this.entityData.get(DATA_PEEK_ID);
   }

   void setRawPeekAmount(int p_33419_) {
      if (!this.level.isClientSide) {
         this.getAttribute(Attributes.ARMOR).removeModifier(COVERED_ARMOR_MODIFIER);
         if (p_33419_ == 0) {
            this.getAttribute(Attributes.ARMOR).addPermanentModifier(COVERED_ARMOR_MODIFIER);
            this.playSound(SoundEvents.SHULKER_CLOSE, 1.0F, 1.0F);
            this.gameEvent(GameEvent.SHULKER_CLOSE);
         } else {
            this.playSound(SoundEvents.SHULKER_OPEN, 1.0F, 1.0F);
            this.gameEvent(GameEvent.SHULKER_OPEN);
         }
      }

      this.entityData.set(DATA_PEEK_ID, (byte)p_33419_);
   }

   public float getClientPeekAmount(float p_33481_) {
      return Mth.lerp(p_33481_, this.currentPeekAmountO, this.currentPeekAmount);
   }

   protected float getStandingEyeHeight(Pose p_33438_, EntityDimensions p_33439_) {
      return 0.5F;
   }

   public void recreateFromPacket(ClientboundAddMobPacket p_149798_) {
      super.recreateFromPacket(p_149798_);
      this.yBodyRot = 0.0F;
      this.yBodyRotO = 0.0F;
   }

   public int getMaxHeadXRot() {
      return 180;
   }

   public int getMaxHeadYRot() {
      return 180;
   }

   public void push(Entity p_33474_) {
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public Optional<Vec3> getRenderPosition(float p_149767_) {
      if (this.clientOldAttachPosition != null && this.clientSideTeleportInterpolation > 0) {
         double d0 = (double)((float)this.clientSideTeleportInterpolation - p_149767_) / 6.0D;
         d0 *= d0;
         BlockPos blockpos = this.blockPosition();
         double d1 = (double)(blockpos.getX() - this.clientOldAttachPosition.getX()) * d0;
         double d2 = (double)(blockpos.getY() - this.clientOldAttachPosition.getY()) * d0;
         double d3 = (double)(blockpos.getZ() - this.clientOldAttachPosition.getZ()) * d0;
         return Optional.of(new Vec3(-d1, -d2, -d3));
      } else {
         return Optional.empty();
      }
   }

   private void setColor(DyeColor p_149778_) {
      this.entityData.set(DATA_COLOR_ID, (byte)p_149778_.getId());
   }

   @Nullable
   public DyeColor getColor() {
      byte b0 = this.entityData.get(DATA_COLOR_ID);
      return b0 != 16 && b0 <= 15 ? DyeColor.byId(b0) : null;
   }

   class ShulkerAttackGoal extends Goal {
      private int attackTime;

      public ShulkerAttackGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         LivingEntity livingentity = Shulker.this.getTarget();
         if (livingentity != null && livingentity.isAlive()) {
            return Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL;
         } else {
            return false;
         }
      }

      public void start() {
         this.attackTime = 20;
         Shulker.this.setRawPeekAmount(100);
      }

      public void stop() {
         Shulker.this.setRawPeekAmount(0);
      }

      public boolean requiresUpdateEveryTick() {
         return true;
      }

      public void tick() {
         if (Shulker.this.level.getDifficulty() != Difficulty.PEACEFUL) {
            --this.attackTime;
            LivingEntity livingentity = Shulker.this.getTarget();
            if (livingentity != null) {
               Shulker.this.getLookControl().setLookAt(livingentity, 180.0F, 180.0F);
               double d0 = Shulker.this.distanceToSqr(livingentity);
               if (d0 < 400.0D) {
                  if (this.attackTime <= 0) {
                     this.attackTime = 20 + Shulker.this.random.nextInt(10) * 20 / 2;
                     Shulker.this.level.addFreshEntity(new ShulkerBullet(Shulker.this.level, Shulker.this, livingentity, Shulker.this.getAttachFace().getAxis()));
                     Shulker.this.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, (Shulker.this.random.nextFloat() - Shulker.this.random.nextFloat()) * 0.2F + 1.0F);
                  }
               } else {
                  Shulker.this.setTarget((LivingEntity)null);
               }

               super.tick();
            }
         }
      }
   }

   static class ShulkerBodyRotationControl extends BodyRotationControl {
      public ShulkerBodyRotationControl(Mob p_149816_) {
         super(p_149816_);
      }

      public void clientTick() {
      }
   }

   static class ShulkerDefenseAttackGoal extends NearestAttackableTargetGoal<LivingEntity> {
      public ShulkerDefenseAttackGoal(Shulker p_33496_) {
         super(p_33496_, LivingEntity.class, 10, true, false, (p_33501_) -> {
            return p_33501_ instanceof Enemy;
         });
      }

      public boolean canUse() {
         return this.mob.getTeam() == null ? false : super.canUse();
      }

      protected AABB getTargetSearchArea(double p_33499_) {
         Direction direction = ((Shulker)this.mob).getAttachFace();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().inflate(4.0D, p_33499_, p_33499_);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().inflate(p_33499_, p_33499_, 4.0D) : this.mob.getBoundingBox().inflate(p_33499_, 4.0D, p_33499_);
         }
      }
   }

   class ShulkerLookControl extends LookControl {
      public ShulkerLookControl(Mob p_149820_) {
         super(p_149820_);
      }

      protected void clampHeadRotationToBody() {
      }

      protected Optional<Float> getYRotD() {
         Direction direction = Shulker.this.getAttachFace().getOpposite();
         Vector3f vector3f = Shulker.FORWARD.copy();
         vector3f.transform(direction.getRotation());
         Vec3i vec3i = direction.getNormal();
         Vector3f vector3f1 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
         vector3f1.cross(vector3f);
         double d0 = this.wantedX - this.mob.getX();
         double d1 = this.wantedY - this.mob.getEyeY();
         double d2 = this.wantedZ - this.mob.getZ();
         Vector3f vector3f2 = new Vector3f((float)d0, (float)d1, (float)d2);
         float f = vector3f1.dot(vector3f2);
         float f1 = vector3f.dot(vector3f2);
         return !(Math.abs(f) > 1.0E-5F) && !(Math.abs(f1) > 1.0E-5F) ? Optional.empty() : Optional.of((float)(Mth.atan2((double)(-f), (double)f1) * (double)(180F / (float)Math.PI)));
      }

      protected Optional<Float> getXRotD() {
         return Optional.of(0.0F);
      }
   }

   class ShulkerNearestAttackGoal extends NearestAttackableTargetGoal<Player> {
      public ShulkerNearestAttackGoal(Shulker p_33505_) {
         super(p_33505_, Player.class, true);
      }

      public boolean canUse() {
         return Shulker.this.level.getDifficulty() == Difficulty.PEACEFUL ? false : super.canUse();
      }

      protected AABB getTargetSearchArea(double p_33508_) {
         Direction direction = ((Shulker)this.mob).getAttachFace();
         if (direction.getAxis() == Direction.Axis.X) {
            return this.mob.getBoundingBox().inflate(4.0D, p_33508_, p_33508_);
         } else {
            return direction.getAxis() == Direction.Axis.Z ? this.mob.getBoundingBox().inflate(p_33508_, p_33508_, 4.0D) : this.mob.getBoundingBox().inflate(p_33508_, 4.0D, p_33508_);
         }
      }
   }

   class ShulkerPeekGoal extends Goal {
      private int peekTime;

      public boolean canUse() {
         return Shulker.this.getTarget() == null && Shulker.this.random.nextInt(reducedTickDelay(40)) == 0 && Shulker.this.canStayAt(Shulker.this.blockPosition(), Shulker.this.getAttachFace());
      }

      public boolean canContinueToUse() {
         return Shulker.this.getTarget() == null && this.peekTime > 0;
      }

      public void start() {
         this.peekTime = this.adjustedTickDelay(20 * (1 + Shulker.this.random.nextInt(3)));
         Shulker.this.setRawPeekAmount(30);
      }

      public void stop() {
         if (Shulker.this.getTarget() == null) {
            Shulker.this.setRawPeekAmount(0);
         }

      }

      public void tick() {
         --this.peekTime;
      }
   }
}
