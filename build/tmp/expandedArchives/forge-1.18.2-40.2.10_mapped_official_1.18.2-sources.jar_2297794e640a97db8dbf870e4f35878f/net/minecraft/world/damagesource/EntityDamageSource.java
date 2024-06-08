package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class EntityDamageSource extends DamageSource {
   protected final Entity entity;
   private boolean isThorns;

   public EntityDamageSource(String p_19394_, Entity p_19395_) {
      super(p_19394_);
      this.entity = p_19395_;
   }

   public EntityDamageSource setThorns() {
      this.isThorns = true;
      return this;
   }

   public boolean isThorns() {
      return this.isThorns;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public Component getLocalizedDeathMessage(LivingEntity p_19397_) {
      ItemStack itemstack = this.entity instanceof LivingEntity ? ((LivingEntity)this.entity).getMainHandItem() : ItemStack.EMPTY;
      String s = "death.attack." + this.msgId;
      return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? new TranslatableComponent(s + ".item", p_19397_.getDisplayName(), this.entity.getDisplayName(), itemstack.getDisplayName()) : new TranslatableComponent(s, p_19397_.getDisplayName(), this.entity.getDisplayName());
   }

   public boolean scalesWithDifficulty() {
      return this.entity instanceof LivingEntity && !(this.entity instanceof Player);
   }

   @Nullable
   public Vec3 getSourcePosition() {
      return this.entity.position();
   }

   public String toString() {
      return "EntityDamageSource (" + this.entity + ")";
   }
}