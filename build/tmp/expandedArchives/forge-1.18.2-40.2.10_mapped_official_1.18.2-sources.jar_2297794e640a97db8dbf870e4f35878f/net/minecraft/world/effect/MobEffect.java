package net.minecraft.world.effect;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class MobEffect extends net.minecraftforge.registries.ForgeRegistryEntry<MobEffect> implements net.minecraftforge.common.extensions.IForgeMobEffect {
   private final Map<Attribute, AttributeModifier> attributeModifiers = Maps.newHashMap();
   private final MobEffectCategory category;
   private final int color;
   @Nullable
   private String descriptionId;

   @Nullable
   public static MobEffect byId(int p_19454_) {
      return Registry.MOB_EFFECT.byId(p_19454_);
   }

   public static int getId(MobEffect p_19460_) {
      return Registry.MOB_EFFECT.getId(p_19460_);
   }

   protected MobEffect(MobEffectCategory p_19451_, int p_19452_) {
      this.category = p_19451_;
      this.color = p_19452_;
      initClient();
   }

   public void applyEffectTick(LivingEntity p_19467_, int p_19468_) {
      if (this == MobEffects.REGENERATION) {
         if (p_19467_.getHealth() < p_19467_.getMaxHealth()) {
            p_19467_.heal(1.0F);
         }
      } else if (this == MobEffects.POISON) {
         if (p_19467_.getHealth() > 1.0F) {
            p_19467_.hurt(DamageSource.MAGIC, 1.0F);
         }
      } else if (this == MobEffects.WITHER) {
         p_19467_.hurt(DamageSource.WITHER, 1.0F);
      } else if (this == MobEffects.HUNGER && p_19467_ instanceof Player) {
         ((Player)p_19467_).causeFoodExhaustion(0.005F * (float)(p_19468_ + 1));
      } else if (this == MobEffects.SATURATION && p_19467_ instanceof Player) {
         if (!p_19467_.level.isClientSide) {
            ((Player)p_19467_).getFoodData().eat(p_19468_ + 1, 1.0F);
         }
      } else if ((this != MobEffects.HEAL || p_19467_.isInvertedHealAndHarm()) && (this != MobEffects.HARM || !p_19467_.isInvertedHealAndHarm())) {
         if (this == MobEffects.HARM && !p_19467_.isInvertedHealAndHarm() || this == MobEffects.HEAL && p_19467_.isInvertedHealAndHarm()) {
            p_19467_.hurt(DamageSource.MAGIC, (float)(6 << p_19468_));
         }
      } else {
         p_19467_.heal((float)Math.max(4 << p_19468_, 0));
      }

   }

   public void applyInstantenousEffect(@Nullable Entity p_19462_, @Nullable Entity p_19463_, LivingEntity p_19464_, int p_19465_, double p_19466_) {
      if ((this != MobEffects.HEAL || p_19464_.isInvertedHealAndHarm()) && (this != MobEffects.HARM || !p_19464_.isInvertedHealAndHarm())) {
         if (this == MobEffects.HARM && !p_19464_.isInvertedHealAndHarm() || this == MobEffects.HEAL && p_19464_.isInvertedHealAndHarm()) {
            int j = (int)(p_19466_ * (double)(6 << p_19465_) + 0.5D);
            if (p_19462_ == null) {
               p_19464_.hurt(DamageSource.MAGIC, (float)j);
            } else {
               p_19464_.hurt(DamageSource.indirectMagic(p_19462_, p_19463_), (float)j);
            }
         } else {
            this.applyEffectTick(p_19464_, p_19465_);
         }
      } else {
         int i = (int)(p_19466_ * (double)(4 << p_19465_) + 0.5D);
         p_19464_.heal((float)i);
      }

   }

   public boolean isDurationEffectTick(int p_19455_, int p_19456_) {
      if (this == MobEffects.REGENERATION) {
         int k = 50 >> p_19456_;
         if (k > 0) {
            return p_19455_ % k == 0;
         } else {
            return true;
         }
      } else if (this == MobEffects.POISON) {
         int j = 25 >> p_19456_;
         if (j > 0) {
            return p_19455_ % j == 0;
         } else {
            return true;
         }
      } else if (this == MobEffects.WITHER) {
         int i = 40 >> p_19456_;
         if (i > 0) {
            return p_19455_ % i == 0;
         } else {
            return true;
         }
      } else {
         return this == MobEffects.HUNGER;
      }
   }

   public boolean isInstantenous() {
      return false;
   }

   protected String getOrCreateDescriptionId() {
      if (this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("effect", Registry.MOB_EFFECT.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public Component getDisplayName() {
      return new TranslatableComponent(this.getDescriptionId());
   }

   public MobEffectCategory getCategory() {
      return this.category;
   }

   public int getColor() {
      return this.color;
   }

   public MobEffect addAttributeModifier(Attribute p_19473_, String p_19474_, double p_19475_, AttributeModifier.Operation p_19476_) {
      AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(p_19474_), this::getDescriptionId, p_19475_, p_19476_);
      this.attributeModifiers.put(p_19473_, attributemodifier);
      return this;
   }

   public Map<Attribute, AttributeModifier> getAttributeModifiers() {
      return this.attributeModifiers;
   }

   public void removeAttributeModifiers(LivingEntity p_19469_, AttributeMap p_19470_, int p_19471_) {
      for(Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeinstance = p_19470_.getInstance(entry.getKey());
         if (attributeinstance != null) {
            attributeinstance.removeModifier(entry.getValue());
         }
      }

   }

   public void addAttributeModifiers(LivingEntity p_19478_, AttributeMap p_19479_, int p_19480_) {
      for(Entry<Attribute, AttributeModifier> entry : this.attributeModifiers.entrySet()) {
         AttributeInstance attributeinstance = p_19479_.getInstance(entry.getKey());
         if (attributeinstance != null) {
            AttributeModifier attributemodifier = entry.getValue();
            attributeinstance.removeModifier(attributemodifier);
            attributeinstance.addPermanentModifier(new AttributeModifier(attributemodifier.getId(), this.getDescriptionId() + " " + p_19480_, this.getAttributeModifierValue(p_19480_, attributemodifier), attributemodifier.getOperation()));
         }
      }

   }

   public double getAttributeModifierValue(int p_19457_, AttributeModifier p_19458_) {
      return p_19458_.getAmount() * (double)(p_19457_ + 1);
   }

   public boolean isBeneficial() {
      return this.category == MobEffectCategory.BENEFICIAL;
   }

   // FORGE START
   private Object effectRenderer;

   /*
      DO NOT CALL, IT WILL DISAPPEAR IN THE FUTURE
      Call RenderProperties.getEffectRenderer instead
    */
   public Object getEffectRendererInternal() {
      return effectRenderer;
   }

   private void initClient() {
      // Minecraft instance isn't available in datagen, so don't call initializeClient if in datagen
      if (net.minecraftforge.fml.loading.FMLEnvironment.dist == net.minecraftforge.api.distmarker.Dist.CLIENT && !net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData()) {
         initializeClient(properties -> {
            this.effectRenderer = properties;
         });
      }
   }

   public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.EffectRenderer> consumer) {
   }
   // END FORGE

}
