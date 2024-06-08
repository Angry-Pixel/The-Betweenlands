package net.minecraft.world.entity.item;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class ItemEntity extends Entity {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ItemEntity.class, EntityDataSerializers.ITEM_STACK);
   private static final int LIFETIME = 6000;
   private static final int INFINITE_PICKUP_DELAY = 32767;
   private static final int INFINITE_LIFETIME = -32768;
   private int age;
   private int pickupDelay;
   private int health = 5;
   @Nullable
   private UUID thrower;
   @Nullable
   private UUID owner;
   public final float bobOffs;
   /**
    * The maximum age of this EntityItem.  The item is expired once this is reached.
    */
   public int lifespan = 6000;

   public ItemEntity(EntityType<? extends ItemEntity> p_31991_, Level p_31992_) {
      super(p_31991_, p_31992_);
      this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0F;
      this.setYRot(this.random.nextFloat() * 360.0F);
   }

   public ItemEntity(Level p_32001_, double p_32002_, double p_32003_, double p_32004_, ItemStack p_32005_) {
      this(p_32001_, p_32002_, p_32003_, p_32004_, p_32005_, p_32001_.random.nextDouble() * 0.2D - 0.1D, 0.2D, p_32001_.random.nextDouble() * 0.2D - 0.1D);
   }

   public ItemEntity(Level p_149663_, double p_149664_, double p_149665_, double p_149666_, ItemStack p_149667_, double p_149668_, double p_149669_, double p_149670_) {
      this(EntityType.ITEM, p_149663_);
      this.setPos(p_149664_, p_149665_, p_149666_);
      this.setDeltaMovement(p_149668_, p_149669_, p_149670_);
      this.setItem(p_149667_);
      this.lifespan = (p_149667_.getItem() == null ? 6000 : p_149667_.getEntityLifespan(p_149663_));
   }

   private ItemEntity(ItemEntity p_31994_) {
      super(p_31994_.getType(), p_31994_.level);
      this.setItem(p_31994_.getItem().copy());
      this.copyPosition(p_31994_);
      this.age = p_31994_.age;
      this.bobOffs = p_31994_.bobOffs;
   }

   public boolean occludesVibrations() {
      return this.getItem().is(ItemTags.OCCLUDES_VIBRATION_SIGNALS);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.NONE;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
   }

   public void tick() {
      if (getItem().onEntityItemUpdate(this)) return;
      if (this.getItem().isEmpty()) {
         this.discard();
      } else {
         super.tick();
         if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
         }

         this.xo = this.getX();
         this.yo = this.getY();
         this.zo = this.getZ();
         Vec3 vec3 = this.getDeltaMovement();
         float f = this.getEyeHeight() - 0.11111111F;
         if (this.isInWater() && this.getFluidHeight(FluidTags.WATER) > (double)f) {
            this.setUnderwaterMovement();
         } else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)f) {
            this.setUnderLavaMovement();
         } else if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         if (this.level.isClientSide) {
            this.noPhysics = false;
         } else {
            this.noPhysics = !this.level.noCollision(this, this.getBoundingBox().deflate(1.0E-7D));
            if (this.noPhysics) {
               this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getZ());
            }
         }

         if (!this.onGround || this.getDeltaMovement().horizontalDistanceSqr() > (double)1.0E-5F || (this.tickCount + this.getId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getDeltaMovement());
            float f1 = 0.98F;
            if (this.onGround) {
               f1 = this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ())).getFriction(level, new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ()), this) * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply((double)f1, 0.98D, (double)f1));
            if (this.onGround) {
               Vec3 vec31 = this.getDeltaMovement();
               if (vec31.y < 0.0D) {
                  this.setDeltaMovement(vec31.multiply(1.0D, -0.5D, 1.0D));
               }
            }
         }

         boolean flag = Mth.floor(this.xo) != Mth.floor(this.getX()) || Mth.floor(this.yo) != Mth.floor(this.getY()) || Mth.floor(this.zo) != Mth.floor(this.getZ());
         int i = flag ? 2 : 40;
         if (this.tickCount % i == 0 && !this.level.isClientSide && this.isMergable()) {
            this.mergeWithNeighbours();
         }

         if (this.age != -32768) {
            ++this.age;
         }

         this.hasImpulse |= this.updateInWaterStateAndDoFluidPushing();
         if (!this.level.isClientSide) {
            double d0 = this.getDeltaMovement().subtract(vec3).lengthSqr();
            if (d0 > 0.01D) {
               this.hasImpulse = true;
            }
         }

         ItemStack item = this.getItem();
         if (!this.level.isClientSide && this.age >= lifespan) {
             int hook = net.minecraftforge.event.ForgeEventFactory.onItemExpire(this, item);
             if (hook < 0) this.discard();
             else          this.lifespan += hook;
         }

         if (item.isEmpty()) {
            this.discard();
         }

      }
   }

   private void setUnderwaterMovement() {
      Vec3 vec3 = this.getDeltaMovement();
      this.setDeltaMovement(vec3.x * (double)0.99F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.99F);
   }

   private void setUnderLavaMovement() {
      Vec3 vec3 = this.getDeltaMovement();
      this.setDeltaMovement(vec3.x * (double)0.95F, vec3.y + (double)(vec3.y < (double)0.06F ? 5.0E-4F : 0.0F), vec3.z * (double)0.95F);
   }

   private void mergeWithNeighbours() {
      if (this.isMergable()) {
         for(ItemEntity itementity : this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.5D, 0.0D, 0.5D), (p_186268_) -> {
            return p_186268_ != this && p_186268_.isMergable();
         })) {
            if (itementity.isMergable()) {
               this.tryToMerge(itementity);
               if (this.isRemoved()) {
                  break;
               }
            }
         }

      }
   }

   private boolean isMergable() {
      ItemStack itemstack = this.getItem();
      return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && itemstack.getCount() < itemstack.getMaxStackSize();
   }

   private void tryToMerge(ItemEntity p_32016_) {
      ItemStack itemstack = this.getItem();
      ItemStack itemstack1 = p_32016_.getItem();
      if (Objects.equals(this.getOwner(), p_32016_.getOwner()) && areMergable(itemstack, itemstack1)) {
         if (itemstack1.getCount() < itemstack.getCount()) {
            merge(this, itemstack, p_32016_, itemstack1);
         } else {
            merge(p_32016_, itemstack1, this, itemstack);
         }

      }
   }

   public static boolean areMergable(ItemStack p_32027_, ItemStack p_32028_) {
      if (!p_32028_.is(p_32027_.getItem())) {
         return false;
      } else if (p_32028_.getCount() + p_32027_.getCount() > p_32028_.getMaxStackSize()) {
         return false;
      } else if (p_32028_.hasTag() ^ p_32027_.hasTag()) {
         return false;
      } else if (!p_32027_.areCapsCompatible(p_32028_)) {
         return false;
      } else {
         return !p_32028_.hasTag() || p_32028_.getTag().equals(p_32027_.getTag());
      }
   }

   public static ItemStack merge(ItemStack p_32030_, ItemStack p_32031_, int p_32032_) {
      int i = Math.min(Math.min(p_32030_.getMaxStackSize(), p_32032_) - p_32030_.getCount(), p_32031_.getCount());
      ItemStack itemstack = p_32030_.copy();
      itemstack.grow(i);
      p_32031_.shrink(i);
      return itemstack;
   }

   private static void merge(ItemEntity p_32023_, ItemStack p_32024_, ItemStack p_32025_) {
      ItemStack itemstack = merge(p_32024_, p_32025_, 64);
      p_32023_.setItem(itemstack);
   }

   private static void merge(ItemEntity p_32018_, ItemStack p_32019_, ItemEntity p_32020_, ItemStack p_32021_) {
      merge(p_32018_, p_32019_, p_32021_);
      p_32018_.pickupDelay = Math.max(p_32018_.pickupDelay, p_32020_.pickupDelay);
      p_32018_.age = Math.min(p_32018_.age, p_32020_.age);
      if (p_32021_.isEmpty()) {
         p_32020_.discard();
      }

   }

   public boolean fireImmune() {
      return this.getItem().getItem().isFireResistant() || super.fireImmune();
   }

   public boolean hurt(DamageSource p_32013_, float p_32014_) {
      if (this.level.isClientSide || this.isRemoved()) return false; //Forge: Fixes MC-53850
      if (this.isInvulnerableTo(p_32013_)) {
         return false;
      } else if (!this.getItem().isEmpty() && this.getItem().is(Items.NETHER_STAR) && p_32013_.isExplosion()) {
         return false;
      } else if (!this.getItem().getItem().canBeHurtBy(p_32013_)) {
         return false;
      } else if (this.level.isClientSide) {
         return true;
      } else {
         this.markHurt();
         this.health = (int)((float)this.health - p_32014_);
         this.gameEvent(GameEvent.ENTITY_DAMAGED, p_32013_.getEntity());
         if (this.health <= 0) {
            this.getItem().onDestroyed(this, p_32013_);
            this.discard();
         }

         return true;
      }
   }

   public void addAdditionalSaveData(CompoundTag p_32050_) {
      p_32050_.putShort("Health", (short)this.health);
      p_32050_.putShort("Age", (short)this.age);
      p_32050_.putShort("PickupDelay", (short)this.pickupDelay);
      p_32050_.putInt("Lifespan", lifespan);
      if (this.getThrower() != null) {
         p_32050_.putUUID("Thrower", this.getThrower());
      }

      if (this.getOwner() != null) {
         p_32050_.putUUID("Owner", this.getOwner());
      }

      if (!this.getItem().isEmpty()) {
         p_32050_.put("Item", this.getItem().save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag p_32034_) {
      this.health = p_32034_.getShort("Health");
      this.age = p_32034_.getShort("Age");
      if (p_32034_.contains("PickupDelay")) {
         this.pickupDelay = p_32034_.getShort("PickupDelay");
      }
      if (p_32034_.contains("Lifespan")) lifespan = p_32034_.getInt("Lifespan");

      if (p_32034_.hasUUID("Owner")) {
         this.owner = p_32034_.getUUID("Owner");
      }

      if (p_32034_.hasUUID("Thrower")) {
         this.thrower = p_32034_.getUUID("Thrower");
      }

      CompoundTag compoundtag = p_32034_.getCompound("Item");
      this.setItem(ItemStack.of(compoundtag));
      if (this.getItem().isEmpty()) {
         this.discard();
      }

   }

   public void playerTouch(Player p_32040_) {
      if (!this.level.isClientSide) {
         if (this.pickupDelay > 0) return;
         ItemStack itemstack = this.getItem();
         Item item = itemstack.getItem();
         int i = itemstack.getCount();

         int hook = net.minecraftforge.event.ForgeEventFactory.onItemPickup(this, p_32040_);
         if (hook < 0) return;

         ItemStack copy = itemstack.copy();
         if (this.pickupDelay == 0 && (this.owner == null || lifespan - this.age <= 200 || this.owner.equals(p_32040_.getUUID())) && (hook == 1 || i <= 0 || p_32040_.getInventory().add(itemstack))) {
            copy.setCount(copy.getCount() - getItem().getCount());
            net.minecraftforge.event.ForgeEventFactory.firePlayerItemPickupEvent(p_32040_, this, copy);
            p_32040_.take(this, i);
            if (itemstack.isEmpty()) {
               this.discard();
               itemstack.setCount(i);
            }

            p_32040_.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
            p_32040_.onItemPickup(this);
         }

      }
   }

   public Component getName() {
      Component component = this.getCustomName();
      return (Component)(component != null ? component : new TranslatableComponent(this.getItem().getDescriptionId()));
   }

   public boolean isAttackable() {
      return false;
   }

   @Nullable
   public Entity changeDimension(ServerLevel p_32042_, net.minecraftforge.common.util.ITeleporter teleporter) {
      Entity entity = super.changeDimension(p_32042_, teleporter);
      if (!this.level.isClientSide && entity instanceof ItemEntity) {
         ((ItemEntity)entity).mergeWithNeighbours();
      }

      return entity;
   }

   public ItemStack getItem() {
      return this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack p_32046_) {
      this.getEntityData().set(DATA_ITEM, p_32046_);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> p_32036_) {
      super.onSyncedDataUpdated(p_32036_);
      if (DATA_ITEM.equals(p_32036_)) {
         this.getItem().setEntityRepresentation(this);
      }

   }

   @Nullable
   public UUID getOwner() {
      return this.owner;
   }

   public void setOwner(@Nullable UUID p_32048_) {
      this.owner = p_32048_;
   }

   @Nullable
   public UUID getThrower() {
      return this.thrower;
   }

   public void setThrower(@Nullable UUID p_32053_) {
      this.thrower = p_32053_;
   }

   public int getAge() {
      return this.age;
   }

   public void setDefaultPickUpDelay() {
      this.pickupDelay = 10;
   }

   public void setNoPickUpDelay() {
      this.pickupDelay = 0;
   }

   public void setNeverPickUp() {
      this.pickupDelay = 32767;
   }

   public void setPickUpDelay(int p_32011_) {
      this.pickupDelay = p_32011_;
   }

   public boolean hasPickUpDelay() {
      return this.pickupDelay > 0;
   }

   public void setUnlimitedLifetime() {
      this.age = -32768;
   }

   public void setExtendedLifetime() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setNeverPickUp();
      this.age = getItem().getEntityLifespan(level) - 1;
   }

   public float getSpin(float p_32009_) {
      return ((float)this.getAge() + p_32009_) / 20.0F + this.bobOffs;
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   public ItemEntity copy() {
      return new ItemEntity(this);
   }

   public SoundSource getSoundSource() {
      return SoundSource.AMBIENT;
   }
}
