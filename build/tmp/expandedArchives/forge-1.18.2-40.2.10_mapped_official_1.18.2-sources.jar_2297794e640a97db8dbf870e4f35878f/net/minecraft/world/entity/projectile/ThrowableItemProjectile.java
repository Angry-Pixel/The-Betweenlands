package net.minecraft.world.entity.projectile;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ThrowableItemProjectile extends ThrowableProjectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(ThrowableItemProjectile.class, EntityDataSerializers.ITEM_STACK);

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> p_37442_, Level p_37443_) {
      super(p_37442_, p_37443_);
   }

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> p_37432_, double p_37433_, double p_37434_, double p_37435_, Level p_37436_) {
      super(p_37432_, p_37433_, p_37434_, p_37435_, p_37436_);
   }

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> p_37438_, LivingEntity p_37439_, Level p_37440_) {
      super(p_37438_, p_37439_, p_37440_);
   }

   public void setItem(ItemStack p_37447_) {
      if (!p_37447_.is(this.getDefaultItem()) || p_37447_.hasTag()) {
         this.getEntityData().set(DATA_ITEM_STACK, Util.make(p_37447_.copy(), (p_37451_) -> {
            p_37451_.setCount(1);
         }));
      }

   }

   protected abstract Item getDefaultItem();

   protected ItemStack getItemRaw() {
      return this.getEntityData().get(DATA_ITEM_STACK);
   }

   public ItemStack getItem() {
      ItemStack itemstack = this.getItemRaw();
      return itemstack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemstack;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public void addAdditionalSaveData(CompoundTag p_37449_) {
      super.addAdditionalSaveData(p_37449_);
      ItemStack itemstack = this.getItemRaw();
      if (!itemstack.isEmpty()) {
         p_37449_.put("Item", itemstack.save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag p_37445_) {
      super.readAdditionalSaveData(p_37445_);
      ItemStack itemstack = ItemStack.of(p_37445_.getCompound("Item"));
      this.setItem(itemstack);
   }
}