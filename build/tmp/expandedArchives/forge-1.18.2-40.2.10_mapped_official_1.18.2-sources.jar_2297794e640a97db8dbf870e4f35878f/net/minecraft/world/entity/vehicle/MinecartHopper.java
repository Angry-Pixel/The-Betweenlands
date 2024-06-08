package net.minecraft.world.entity.vehicle;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartHopper extends AbstractMinecartContainer implements Hopper {
   public static final int MOVE_ITEM_SPEED = 4;
   private boolean enabled = true;
   private int cooldownTime = -1;
   private final BlockPos lastPosition = BlockPos.ZERO;

   public MinecartHopper(EntityType<? extends MinecartHopper> p_38584_, Level p_38585_) {
      super(p_38584_, p_38585_);
   }

   public MinecartHopper(Level p_38587_, double p_38588_, double p_38589_, double p_38590_) {
      super(EntityType.HOPPER_MINECART, p_38588_, p_38589_, p_38590_, p_38587_);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.HOPPER;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.HOPPER.defaultBlockState();
   }

   public int getDefaultDisplayOffset() {
      return 1;
   }

   public int getContainerSize() {
      return 5;
   }

   public void activateMinecart(int p_38596_, int p_38597_, int p_38598_, boolean p_38599_) {
      boolean flag = !p_38599_;
      if (flag != this.isEnabled()) {
         this.setEnabled(flag);
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean p_38614_) {
      this.enabled = p_38614_;
   }

   public double getLevelX() {
      return this.getX();
   }

   public double getLevelY() {
      return this.getY() + 0.5D;
   }

   public double getLevelZ() {
      return this.getZ();
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide && this.isAlive() && this.isEnabled()) {
         BlockPos blockpos = this.blockPosition();
         if (blockpos.equals(this.lastPosition)) {
            --this.cooldownTime;
         } else {
            this.setCooldown(0);
         }

         if (!this.isOnCooldown()) {
            this.setCooldown(0);
            if (this.suckInItems()) {
               this.setCooldown(4);
               this.setChanged();
            }
         }
      }

   }

   public boolean suckInItems() {
      if (HopperBlockEntity.suckInItems(this.level, this)) {
         return true;
      } else {
         List<ItemEntity> list = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25D, 0.0D, 0.25D), EntitySelector.ENTITY_STILL_ALIVE);
         if (!list.isEmpty()) {
            HopperBlockEntity.addItem(this, list.get(0));
         }

         return false;
      }
   }

   public void destroy(DamageSource p_38604_) {
      super.destroy(p_38604_);
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.HOPPER);
      }

   }

   protected void addAdditionalSaveData(CompoundTag p_38608_) {
      super.addAdditionalSaveData(p_38608_);
      p_38608_.putInt("TransferCooldown", this.cooldownTime);
      p_38608_.putBoolean("Enabled", this.enabled);
   }

   protected void readAdditionalSaveData(CompoundTag p_38606_) {
      super.readAdditionalSaveData(p_38606_);
      this.cooldownTime = p_38606_.getInt("TransferCooldown");
      this.enabled = p_38606_.contains("Enabled") ? p_38606_.getBoolean("Enabled") : true;
   }

   public void setCooldown(int p_38611_) {
      this.cooldownTime = p_38611_;
   }

   public boolean isOnCooldown() {
      return this.cooldownTime > 0;
   }

   public AbstractContainerMenu createMenu(int p_38601_, Inventory p_38602_) {
      return new HopperMenu(p_38601_, p_38602_, this);
   }
}