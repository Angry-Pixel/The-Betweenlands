package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractArrow extends Projectile {
   private static final double ARROW_BASE_DAMAGE = 2.0D;
   private static final EntityDataAccessor<Byte> ID_FLAGS = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Byte> PIERCE_LEVEL = SynchedEntityData.defineId(AbstractArrow.class, EntityDataSerializers.BYTE);
   private static final int FLAG_CRIT = 1;
   private static final int FLAG_NOPHYSICS = 2;
   private static final int FLAG_CROSSBOW = 4;
   @Nullable
   private BlockState lastState;
   protected boolean inGround;
   protected int inGroundTime;
   public AbstractArrow.Pickup pickup = AbstractArrow.Pickup.DISALLOWED;
   public int shakeTime;
   private int life;
   private double baseDamage = 2.0D;
   private int knockback;
   private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
   @Nullable
   private IntOpenHashSet piercingIgnoreEntityIds;
   @Nullable
   private List<Entity> piercedAndKilledEntities;

   protected AbstractArrow(EntityType<? extends AbstractArrow> p_36721_, Level p_36722_) {
      super(p_36721_, p_36722_);
   }

   protected AbstractArrow(EntityType<? extends AbstractArrow> p_36711_, double p_36712_, double p_36713_, double p_36714_, Level p_36715_) {
      this(p_36711_, p_36715_);
      this.setPos(p_36712_, p_36713_, p_36714_);
   }

   protected AbstractArrow(EntityType<? extends AbstractArrow> p_36717_, LivingEntity p_36718_, Level p_36719_) {
      this(p_36717_, p_36718_.getX(), p_36718_.getEyeY() - (double)0.1F, p_36718_.getZ(), p_36719_);
      this.setOwner(p_36718_);
      if (p_36718_ instanceof Player) {
         this.pickup = AbstractArrow.Pickup.ALLOWED;
      }

   }

   public void setSoundEvent(SoundEvent p_36741_) {
      this.soundEvent = p_36741_;
   }

   public boolean shouldRenderAtSqrDistance(double p_36726_) {
      double d0 = this.getBoundingBox().getSize() * 10.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 *= 64.0D * getViewScale();
      return p_36726_ < d0 * d0;
   }

   protected void defineSynchedData() {
      this.entityData.define(ID_FLAGS, (byte)0);
      this.entityData.define(PIERCE_LEVEL, (byte)0);
   }

   public void shoot(double p_36775_, double p_36776_, double p_36777_, float p_36778_, float p_36779_) {
      super.shoot(p_36775_, p_36776_, p_36777_, p_36778_, p_36779_);
      this.life = 0;
   }

   public void lerpTo(double p_36728_, double p_36729_, double p_36730_, float p_36731_, float p_36732_, int p_36733_, boolean p_36734_) {
      this.setPos(p_36728_, p_36729_, p_36730_);
      this.setRot(p_36731_, p_36732_);
   }

   public void lerpMotion(double p_36786_, double p_36787_, double p_36788_) {
      super.lerpMotion(p_36786_, p_36787_, p_36788_);
      this.life = 0;
   }

   public void tick() {
      super.tick();
      boolean flag = this.isNoPhysics();
      Vec3 vec3 = this.getDeltaMovement();
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         double d0 = vec3.horizontalDistance();
         this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
         this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
      }

      BlockPos blockpos = this.blockPosition();
      BlockState blockstate = this.level.getBlockState(blockpos);
      if (!blockstate.isAir() && !flag) {
         VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
         if (!voxelshape.isEmpty()) {
            Vec3 vec31 = this.position();

            for(AABB aabb : voxelshape.toAabbs()) {
               if (aabb.move(blockpos).contains(vec31)) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.shakeTime > 0) {
         --this.shakeTime;
      }

      if (this.isInWaterOrRain() || blockstate.is(Blocks.POWDER_SNOW)) {
         this.clearFire();
      }

      if (this.inGround && !flag) {
         if (this.lastState != blockstate && this.shouldFall()) {
            this.startFalling();
         } else if (!this.level.isClientSide) {
            this.tickDespawn();
         }

         ++this.inGroundTime;
      } else {
         this.inGroundTime = 0;
         Vec3 vec32 = this.position();
         Vec3 vec33 = vec32.add(vec3);
         HitResult hitresult = this.level.clip(new ClipContext(vec32, vec33, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
         if (hitresult.getType() != HitResult.Type.MISS) {
            vec33 = hitresult.getLocation();
         }

         while(!this.isRemoved()) {
            EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
            if (entityhitresult != null) {
               hitresult = entityhitresult;
            }

            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
               Entity entity = ((EntityHitResult)hitresult).getEntity();
               Entity entity1 = this.getOwner();
               if (entity instanceof Player && entity1 instanceof Player && !((Player)entity1).canHarmPlayer((Player)entity)) {
                  hitresult = null;
                  entityhitresult = null;
               }
            }

            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
               this.onHit(hitresult);
               this.hasImpulse = true;
            }

            if (entityhitresult == null || this.getPierceLevel() <= 0) {
               break;
            }

            hitresult = null;
         }

         vec3 = this.getDeltaMovement();
         double d5 = vec3.x;
         double d6 = vec3.y;
         double d1 = vec3.z;
         if (this.isCritArrow()) {
            for(int i = 0; i < 4; ++i) {
               this.level.addParticle(ParticleTypes.CRIT, this.getX() + d5 * (double)i / 4.0D, this.getY() + d6 * (double)i / 4.0D, this.getZ() + d1 * (double)i / 4.0D, -d5, -d6 + 0.2D, -d1);
            }
         }

         double d7 = this.getX() + d5;
         double d2 = this.getY() + d6;
         double d3 = this.getZ() + d1;
         double d4 = vec3.horizontalDistance();
         if (flag) {
            this.setYRot((float)(Mth.atan2(-d5, -d1) * (double)(180F / (float)Math.PI)));
         } else {
            this.setYRot((float)(Mth.atan2(d5, d1) * (double)(180F / (float)Math.PI)));
         }

         this.setXRot((float)(Mth.atan2(d6, d4) * (double)(180F / (float)Math.PI)));
         this.setXRot(lerpRotation(this.xRotO, this.getXRot()));
         this.setYRot(lerpRotation(this.yRotO, this.getYRot()));
         float f = 0.99F;
         float f1 = 0.05F;
         if (this.isInWater()) {
            for(int j = 0; j < 4; ++j) {
               float f2 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, d7 - d5 * 0.25D, d2 - d6 * 0.25D, d3 - d1 * 0.25D, d5, d6, d1);
            }

            f = this.getWaterInertia();
         }

         this.setDeltaMovement(vec3.scale((double)f));
         if (!this.isNoGravity() && !flag) {
            Vec3 vec34 = this.getDeltaMovement();
            this.setDeltaMovement(vec34.x, vec34.y - (double)0.05F, vec34.z);
         }

         this.setPos(d7, d2, d3);
         this.checkInsideBlocks();
      }
   }

   private boolean shouldFall() {
      return this.inGround && this.level.noCollision((new AABB(this.position(), this.position())).inflate(0.06D));
   }

   private void startFalling() {
      this.inGround = false;
      Vec3 vec3 = this.getDeltaMovement();
      this.setDeltaMovement(vec3.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
      this.life = 0;
   }

   public void move(MoverType p_36749_, Vec3 p_36750_) {
      super.move(p_36749_, p_36750_);
      if (p_36749_ != MoverType.SELF && this.shouldFall()) {
         this.startFalling();
      }

   }

   protected void tickDespawn() {
      ++this.life;
      if (this.life >= 1200) {
         this.discard();
      }

   }

   private void resetPiercedEntities() {
      if (this.piercedAndKilledEntities != null) {
         this.piercedAndKilledEntities.clear();
      }

      if (this.piercingIgnoreEntityIds != null) {
         this.piercingIgnoreEntityIds.clear();
      }

   }

   protected void onHitEntity(EntityHitResult p_36757_) {
      super.onHitEntity(p_36757_);
      Entity entity = p_36757_.getEntity();
      float f = (float)this.getDeltaMovement().length();
      int i = Mth.ceil(Mth.clamp((double)f * this.baseDamage, 0.0D, 2.147483647E9D));
      if (this.getPierceLevel() > 0) {
         if (this.piercingIgnoreEntityIds == null) {
            this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
         }

         if (this.piercedAndKilledEntities == null) {
            this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
         }

         if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
            this.discard();
            return;
         }

         this.piercingIgnoreEntityIds.add(entity.getId());
      }

      if (this.isCritArrow()) {
         long j = (long)this.random.nextInt(i / 2 + 2);
         i = (int)Math.min(j + (long)i, 2147483647L);
      }

      Entity entity1 = this.getOwner();
      DamageSource damagesource;
      if (entity1 == null) {
         damagesource = DamageSource.arrow(this, this);
      } else {
         damagesource = DamageSource.arrow(this, entity1);
         if (entity1 instanceof LivingEntity) {
            ((LivingEntity)entity1).setLastHurtMob(entity);
         }
      }

      boolean flag = entity.getType() == EntityType.ENDERMAN;
      int k = entity.getRemainingFireTicks();
      if (this.isOnFire() && !flag) {
         entity.setSecondsOnFire(5);
      }

      if (entity.hurt(damagesource, (float)i)) {
         if (flag) {
            return;
         }

         if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
               livingentity.setArrowCount(livingentity.getArrowCount() + 1);
            }

            if (this.knockback > 0) {
               Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockback * 0.6D);
               if (vec3.lengthSqr() > 0.0D) {
                  livingentity.push(vec3.x, 0.1D, vec3.z);
               }
            }

            if (!this.level.isClientSide && entity1 instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity);
            }

            this.doPostHurtEffects(livingentity);
            if (entity1 != null && livingentity != entity1 && livingentity instanceof Player && entity1 instanceof ServerPlayer && !this.isSilent()) {
               ((ServerPlayer)entity1).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
            }

            if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
               this.piercedAndKilledEntities.add(livingentity);
            }

            if (!this.level.isClientSide && entity1 instanceof ServerPlayer) {
               ServerPlayer serverplayer = (ServerPlayer)entity1;
               if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayer, this.piercedAndKilledEntities);
               } else if (!entity.isAlive() && this.shotFromCrossbow()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.trigger(serverplayer, Arrays.asList(entity));
               }
            }
         }

         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.discard();
         }
      } else {
         entity.setRemainingFireTicks(k);
         this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
         this.setYRot(this.getYRot() + 180.0F);
         this.yRotO += 180.0F;
         if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.discard();
         }
      }

   }

   protected void onHitBlock(BlockHitResult p_36755_) {
      this.lastState = this.level.getBlockState(p_36755_.getBlockPos());
      super.onHitBlock(p_36755_);
      Vec3 vec3 = p_36755_.getLocation().subtract(this.getX(), this.getY(), this.getZ());
      this.setDeltaMovement(vec3);
      Vec3 vec31 = vec3.normalize().scale((double)0.05F);
      this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
      this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      this.inGround = true;
      this.shakeTime = 7;
      this.setCritArrow(false);
      this.setPierceLevel((byte)0);
      this.setSoundEvent(SoundEvents.ARROW_HIT);
      this.setShotFromCrossbow(false);
      this.resetPiercedEntities();
   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ARROW_HIT;
   }

   protected final SoundEvent getHitGroundSoundEvent() {
      return this.soundEvent;
   }

   protected void doPostHurtEffects(LivingEntity p_36744_) {
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 p_36758_, Vec3 p_36759_) {
      return ProjectileUtil.getEntityHitResult(this.level, this, p_36758_, p_36759_, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), this::canHitEntity);
   }

   protected boolean canHitEntity(Entity p_36743_) {
      return super.canHitEntity(p_36743_) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(p_36743_.getId()));
   }

   public void addAdditionalSaveData(CompoundTag p_36772_) {
      super.addAdditionalSaveData(p_36772_);
      p_36772_.putShort("life", (short)this.life);
      if (this.lastState != null) {
         p_36772_.put("inBlockState", NbtUtils.writeBlockState(this.lastState));
      }

      p_36772_.putByte("shake", (byte)this.shakeTime);
      p_36772_.putBoolean("inGround", this.inGround);
      p_36772_.putByte("pickup", (byte)this.pickup.ordinal());
      p_36772_.putDouble("damage", this.baseDamage);
      p_36772_.putBoolean("crit", this.isCritArrow());
      p_36772_.putByte("PierceLevel", this.getPierceLevel());
      p_36772_.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());
      p_36772_.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
   }

   public void readAdditionalSaveData(CompoundTag p_36761_) {
      super.readAdditionalSaveData(p_36761_);
      this.life = p_36761_.getShort("life");
      if (p_36761_.contains("inBlockState", 10)) {
         this.lastState = NbtUtils.readBlockState(p_36761_.getCompound("inBlockState"));
      }

      this.shakeTime = p_36761_.getByte("shake") & 255;
      this.inGround = p_36761_.getBoolean("inGround");
      if (p_36761_.contains("damage", 99)) {
         this.baseDamage = p_36761_.getDouble("damage");
      }

      this.pickup = AbstractArrow.Pickup.byOrdinal(p_36761_.getByte("pickup"));
      this.setCritArrow(p_36761_.getBoolean("crit"));
      this.setPierceLevel(p_36761_.getByte("PierceLevel"));
      if (p_36761_.contains("SoundEvent", 8)) {
         this.soundEvent = Registry.SOUND_EVENT.getOptional(new ResourceLocation(p_36761_.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
      }

      this.setShotFromCrossbow(p_36761_.getBoolean("ShotFromCrossbow"));
   }

   public void setOwner(@Nullable Entity p_36770_) {
      super.setOwner(p_36770_);
      if (p_36770_ instanceof Player) {
         this.pickup = ((Player)p_36770_).getAbilities().instabuild ? AbstractArrow.Pickup.CREATIVE_ONLY : AbstractArrow.Pickup.ALLOWED;
      }

   }

   public void playerTouch(Player p_36766_) {
      if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
         if (this.tryPickup(p_36766_)) {
            p_36766_.take(this, 1);
            this.discard();
         }

      }
   }

   protected boolean tryPickup(Player p_150121_) {
      switch(this.pickup) {
      case ALLOWED:
         return p_150121_.getInventory().add(this.getPickupItem());
      case CREATIVE_ONLY:
         return p_150121_.getAbilities().instabuild;
      default:
         return false;
      }
   }

   protected abstract ItemStack getPickupItem();

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   public void setBaseDamage(double p_36782_) {
      this.baseDamage = p_36782_;
   }

   public double getBaseDamage() {
      return this.baseDamage;
   }

   public void setKnockback(int p_36736_) {
      this.knockback = p_36736_;
   }

   public int getKnockback() {
      return this.knockback;
   }

   public boolean isAttackable() {
      return false;
   }

   protected float getEyeHeight(Pose p_36752_, EntityDimensions p_36753_) {
      return 0.13F;
   }

   public void setCritArrow(boolean p_36763_) {
      this.setFlag(1, p_36763_);
   }

   public void setPierceLevel(byte p_36768_) {
      this.entityData.set(PIERCE_LEVEL, p_36768_);
   }

   private void setFlag(int p_36738_, boolean p_36739_) {
      byte b0 = this.entityData.get(ID_FLAGS);
      if (p_36739_) {
         this.entityData.set(ID_FLAGS, (byte)(b0 | p_36738_));
      } else {
         this.entityData.set(ID_FLAGS, (byte)(b0 & ~p_36738_));
      }

   }

   public boolean isCritArrow() {
      byte b0 = this.entityData.get(ID_FLAGS);
      return (b0 & 1) != 0;
   }

   public boolean shotFromCrossbow() {
      byte b0 = this.entityData.get(ID_FLAGS);
      return (b0 & 4) != 0;
   }

   public byte getPierceLevel() {
      return this.entityData.get(PIERCE_LEVEL);
   }

   public void setEnchantmentEffectsFromEntity(LivingEntity p_36746_, float p_36747_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, p_36746_);
      int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, p_36746_);
      this.setBaseDamage((double)(p_36747_ * 2.0F) + this.random.nextGaussian() * 0.25D + (double)((float)this.level.getDifficulty().getId() * 0.11F));
      if (i > 0) {
         this.setBaseDamage(this.getBaseDamage() + (double)i * 0.5D + 0.5D);
      }

      if (j > 0) {
         this.setKnockback(j);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, p_36746_) > 0) {
         this.setSecondsOnFire(100);
      }

   }

   protected float getWaterInertia() {
      return 0.6F;
   }

   public void setNoPhysics(boolean p_36791_) {
      this.noPhysics = p_36791_;
      this.setFlag(2, p_36791_);
   }

   public boolean isNoPhysics() {
      if (!this.level.isClientSide) {
         return this.noPhysics;
      } else {
         return (this.entityData.get(ID_FLAGS) & 2) != 0;
      }
   }

   public void setShotFromCrossbow(boolean p_36794_) {
      this.setFlag(4, p_36794_);
   }

   public static enum Pickup {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      public static AbstractArrow.Pickup byOrdinal(int p_36809_) {
         if (p_36809_ < 0 || p_36809_ > values().length) {
            p_36809_ = 0;
         }

         return values()[p_36809_];
      }
   }
}
