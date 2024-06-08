package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class IndirectEntityDamageSource extends EntityDamageSource {
   @Nullable
   private final Entity owner;

   public IndirectEntityDamageSource(String p_19406_, Entity p_19407_, @Nullable Entity p_19408_) {
      super(p_19406_, p_19407_);
      this.owner = p_19408_;
   }

   @Nullable
   public Entity getDirectEntity() {
      return this.entity;
   }

   @Nullable
   public Entity getEntity() {
      return this.owner;
   }

   public Component getLocalizedDeathMessage(LivingEntity p_19410_) {
      Component component = this.owner == null ? this.entity.getDisplayName() : this.owner.getDisplayName();
      ItemStack itemstack = this.owner instanceof LivingEntity ? ((LivingEntity)this.owner).getMainHandItem() : ItemStack.EMPTY;
      String s = "death.attack." + this.msgId;
      String s1 = s + ".item";
      return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? new TranslatableComponent(s1, p_19410_.getDisplayName(), component, itemstack.getDisplayName()) : new TranslatableComponent(s, p_19410_.getDisplayName(), component);
   }
}