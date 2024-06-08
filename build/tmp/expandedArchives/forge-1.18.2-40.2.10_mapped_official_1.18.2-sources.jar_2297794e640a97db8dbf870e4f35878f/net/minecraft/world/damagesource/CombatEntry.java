package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class CombatEntry {
   private final DamageSource source;
   private final int time;
   private final float damage;
   private final float health;
   @Nullable
   private final String location;
   private final float fallDistance;

   public CombatEntry(DamageSource p_19257_, int p_19258_, float p_19259_, float p_19260_, @Nullable String p_19261_, float p_19262_) {
      this.source = p_19257_;
      this.time = p_19258_;
      this.damage = p_19260_;
      this.health = p_19259_;
      this.location = p_19261_;
      this.fallDistance = p_19262_;
   }

   public DamageSource getSource() {
      return this.source;
   }

   public int getTime() {
      return this.time;
   }

   public float getDamage() {
      return this.damage;
   }

   public float getHealthBeforeDamage() {
      return this.health;
   }

   public float getHealthAfterDamage() {
      return this.health - this.damage;
   }

   public boolean isCombatRelated() {
      return this.source.getEntity() instanceof LivingEntity;
   }

   @Nullable
   public String getLocation() {
      return this.location;
   }

   @Nullable
   public Component getAttackerName() {
      return this.getSource().getEntity() == null ? null : this.getSource().getEntity().getDisplayName();
   }

   @Nullable
   public Entity getAttacker() {
      return this.getSource().getEntity();
   }

   public float getFallDistance() {
      return this.source == DamageSource.OUT_OF_WORLD ? Float.MAX_VALUE : this.fallDistance;
   }
}