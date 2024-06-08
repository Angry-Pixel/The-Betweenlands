package net.minecraft.world.entity.projectile;

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class Fireball extends AbstractHurtingProjectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.ITEM_STACK);

   public Fireball(EntityType<? extends Fireball> p_37006_, Level p_37007_) {
      super(p_37006_, p_37007_);
   }

   public Fireball(EntityType<? extends Fireball> p_36990_, double p_36991_, double p_36992_, double p_36993_, double p_36994_, double p_36995_, double p_36996_, Level p_36997_) {
      super(p_36990_, p_36991_, p_36992_, p_36993_, p_36994_, p_36995_, p_36996_, p_36997_);
   }

   public Fireball(EntityType<? extends Fireball> p_36999_, LivingEntity p_37000_, double p_37001_, double p_37002_, double p_37003_, Level p_37004_) {
      super(p_36999_, p_37000_, p_37001_, p_37002_, p_37003_, p_37004_);
   }

   public void setItem(ItemStack p_37011_) {
      if (!p_37011_.is(Items.FIRE_CHARGE) || p_37011_.hasTag()) {
         this.getEntityData().set(DATA_ITEM_STACK, Util.make(p_37011_.copy(), (p_37015_) -> {
            p_37015_.setCount(1);
         }));
      }

   }

   protected ItemStack getItemRaw() {
      return this.getEntityData().get(DATA_ITEM_STACK);
   }

   public ItemStack getItem() {
      ItemStack itemstack = this.getItemRaw();
      return itemstack.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : itemstack;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public void addAdditionalSaveData(CompoundTag p_37013_) {
      super.addAdditionalSaveData(p_37013_);
      ItemStack itemstack = this.getItemRaw();
      if (!itemstack.isEmpty()) {
         p_37013_.put("Item", itemstack.save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag p_37009_) {
      super.readAdditionalSaveData(p_37009_);
      ItemStack itemstack = ItemStack.of(p_37009_.getCompound("Item"));
      this.setItem(itemstack);
   }
}