package net.minecraft.world.entity.projectile;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

public class Arrow extends AbstractArrow {
   private static final int EXPOSED_POTION_DECAY_TIME = 600;
   private static final int NO_EFFECT_COLOR = -1;
   private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR = SynchedEntityData.defineId(Arrow.class, EntityDataSerializers.INT);
   private static final byte EVENT_POTION_PUFF = 0;
   private Potion potion = Potions.EMPTY;
   private final Set<MobEffectInstance> effects = Sets.newHashSet();
   private boolean fixedColor;

   public Arrow(EntityType<? extends Arrow> p_36858_, Level p_36859_) {
      super(p_36858_, p_36859_);
   }

   public Arrow(Level p_36861_, double p_36862_, double p_36863_, double p_36864_) {
      super(EntityType.ARROW, p_36862_, p_36863_, p_36864_, p_36861_);
   }

   public Arrow(Level p_36866_, LivingEntity p_36867_) {
      super(EntityType.ARROW, p_36867_, p_36866_);
   }

   public void setEffectsFromItem(ItemStack p_36879_) {
      if (p_36879_.is(Items.TIPPED_ARROW)) {
         this.potion = PotionUtils.getPotion(p_36879_);
         Collection<MobEffectInstance> collection = PotionUtils.getCustomEffects(p_36879_);
         if (!collection.isEmpty()) {
            for(MobEffectInstance mobeffectinstance : collection) {
               this.effects.add(new MobEffectInstance(mobeffectinstance));
            }
         }

         int i = getCustomColor(p_36879_);
         if (i == -1) {
            this.updateColor();
         } else {
            this.setFixedColor(i);
         }
      } else if (p_36879_.is(Items.ARROW)) {
         this.potion = Potions.EMPTY;
         this.effects.clear();
         this.entityData.set(ID_EFFECT_COLOR, -1);
      }

   }

   public static int getCustomColor(ItemStack p_36885_) {
      CompoundTag compoundtag = p_36885_.getTag();
      return compoundtag != null && compoundtag.contains("CustomPotionColor", 99) ? compoundtag.getInt("CustomPotionColor") : -1;
   }

   private void updateColor() {
      this.fixedColor = false;
      if (this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.entityData.set(ID_EFFECT_COLOR, -1);
      } else {
         this.entityData.set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
      }

   }

   public void addEffect(MobEffectInstance p_36871_) {
      this.effects.add(p_36871_);
      this.getEntityData().set(ID_EFFECT_COLOR, PotionUtils.getColor(PotionUtils.getAllEffects(this.potion, this.effects)));
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(ID_EFFECT_COLOR, -1);
   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         if (this.inGround) {
            if (this.inGroundTime % 5 == 0) {
               this.makeParticle(1);
            }
         } else {
            this.makeParticle(2);
         }
      } else if (this.inGround && this.inGroundTime != 0 && !this.effects.isEmpty() && this.inGroundTime >= 600) {
         this.level.broadcastEntityEvent(this, (byte)0);
         this.potion = Potions.EMPTY;
         this.effects.clear();
         this.entityData.set(ID_EFFECT_COLOR, -1);
      }

   }

   private void makeParticle(int p_36877_) {
      int i = this.getColor();
      if (i != -1 && p_36877_ > 0) {
         double d0 = (double)(i >> 16 & 255) / 255.0D;
         double d1 = (double)(i >> 8 & 255) / 255.0D;
         double d2 = (double)(i >> 0 & 255) / 255.0D;

         for(int j = 0; j < p_36877_; ++j) {
            this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
         }

      }
   }

   public int getColor() {
      return this.entityData.get(ID_EFFECT_COLOR);
   }

   private void setFixedColor(int p_36883_) {
      this.fixedColor = true;
      this.entityData.set(ID_EFFECT_COLOR, p_36883_);
   }

   public void addAdditionalSaveData(CompoundTag p_36881_) {
      super.addAdditionalSaveData(p_36881_);
      if (this.potion != Potions.EMPTY) {
         p_36881_.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if (this.fixedColor) {
         p_36881_.putInt("Color", this.getColor());
      }

      if (!this.effects.isEmpty()) {
         ListTag listtag = new ListTag();

         for(MobEffectInstance mobeffectinstance : this.effects) {
            listtag.add(mobeffectinstance.save(new CompoundTag()));
         }

         p_36881_.put("CustomPotionEffects", listtag);
      }

   }

   public void readAdditionalSaveData(CompoundTag p_36875_) {
      super.readAdditionalSaveData(p_36875_);
      if (p_36875_.contains("Potion", 8)) {
         this.potion = PotionUtils.getPotion(p_36875_);
      }

      for(MobEffectInstance mobeffectinstance : PotionUtils.getCustomEffects(p_36875_)) {
         this.addEffect(mobeffectinstance);
      }

      if (p_36875_.contains("Color", 99)) {
         this.setFixedColor(p_36875_.getInt("Color"));
      } else {
         this.updateColor();
      }

   }

   protected void doPostHurtEffects(LivingEntity p_36873_) {
      super.doPostHurtEffects(p_36873_);
      Entity entity = this.getEffectSource();

      for(MobEffectInstance mobeffectinstance : this.potion.getEffects()) {
         p_36873_.addEffect(new MobEffectInstance(mobeffectinstance.getEffect(), Math.max(mobeffectinstance.getDuration() / 8, 1), mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()), entity);
      }

      if (!this.effects.isEmpty()) {
         for(MobEffectInstance mobeffectinstance1 : this.effects) {
            p_36873_.addEffect(mobeffectinstance1, entity);
         }
      }

   }

   protected ItemStack getPickupItem() {
      if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
         return new ItemStack(Items.ARROW);
      } else {
         ItemStack itemstack = new ItemStack(Items.TIPPED_ARROW);
         PotionUtils.setPotion(itemstack, this.potion);
         PotionUtils.setCustomEffects(itemstack, this.effects);
         if (this.fixedColor) {
            itemstack.getOrCreateTag().putInt("CustomPotionColor", this.getColor());
         }

         return itemstack;
      }
   }

   public void handleEntityEvent(byte p_36869_) {
      if (p_36869_ == 0) {
         int i = this.getColor();
         if (i != -1) {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;

            for(int j = 0; j < 20; ++j) {
               this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), d0, d1, d2);
            }
         }
      } else {
         super.handleEntityEvent(p_36869_);
      }

   }
}