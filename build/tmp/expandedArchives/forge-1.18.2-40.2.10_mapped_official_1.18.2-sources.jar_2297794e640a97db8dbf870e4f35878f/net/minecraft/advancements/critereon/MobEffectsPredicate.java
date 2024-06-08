package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MobEffectsPredicate {
   public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
   private final Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> effects;

   public MobEffectsPredicate(Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> p_56551_) {
      this.effects = p_56551_;
   }

   public static MobEffectsPredicate effects() {
      return new MobEffectsPredicate(Maps.newLinkedHashMap());
   }

   public MobEffectsPredicate and(MobEffect p_56554_) {
      this.effects.put(p_56554_, new MobEffectsPredicate.MobEffectInstancePredicate());
      return this;
   }

   public MobEffectsPredicate and(MobEffect p_154978_, MobEffectsPredicate.MobEffectInstancePredicate p_154979_) {
      this.effects.put(p_154978_, p_154979_);
      return this;
   }

   public boolean matches(Entity p_56556_) {
      if (this == ANY) {
         return true;
      } else {
         return p_56556_ instanceof LivingEntity ? this.matches(((LivingEntity)p_56556_).getActiveEffectsMap()) : false;
      }
   }

   public boolean matches(LivingEntity p_56558_) {
      return this == ANY ? true : this.matches(p_56558_.getActiveEffectsMap());
   }

   public boolean matches(Map<MobEffect, MobEffectInstance> p_56562_) {
      if (this == ANY) {
         return true;
      } else {
         for(Entry<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> entry : this.effects.entrySet()) {
            MobEffectInstance mobeffectinstance = p_56562_.get(entry.getKey());
            if (!entry.getValue().matches(mobeffectinstance)) {
               return false;
            }
         }

         return true;
      }
   }

   public static MobEffectsPredicate fromJson(@Nullable JsonElement p_56560_) {
      if (p_56560_ != null && !p_56560_.isJsonNull()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_56560_, "effects");
         Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> map = Maps.newLinkedHashMap();

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            ResourceLocation resourcelocation = new ResourceLocation(entry.getKey());
            MobEffect mobeffect = Registry.MOB_EFFECT.getOptional(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown effect '" + resourcelocation + "'");
            });
            MobEffectsPredicate.MobEffectInstancePredicate mobeffectspredicate$mobeffectinstancepredicate = MobEffectsPredicate.MobEffectInstancePredicate.fromJson(GsonHelper.convertToJsonObject(entry.getValue(), entry.getKey()));
            map.put(mobeffect, mobeffectspredicate$mobeffectinstancepredicate);
         }

         return new MobEffectsPredicate(map);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();

         for(Entry<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> entry : this.effects.entrySet()) {
            jsonobject.add(Registry.MOB_EFFECT.getKey(entry.getKey()).toString(), entry.getValue().serializeToJson());
         }

         return jsonobject;
      }
   }

   public static class MobEffectInstancePredicate {
      private final MinMaxBounds.Ints amplifier;
      private final MinMaxBounds.Ints duration;
      @Nullable
      private final Boolean ambient;
      @Nullable
      private final Boolean visible;

      public MobEffectInstancePredicate(MinMaxBounds.Ints p_56572_, MinMaxBounds.Ints p_56573_, @Nullable Boolean p_56574_, @Nullable Boolean p_56575_) {
         this.amplifier = p_56572_;
         this.duration = p_56573_;
         this.ambient = p_56574_;
         this.visible = p_56575_;
      }

      public MobEffectInstancePredicate() {
         this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, (Boolean)null, (Boolean)null);
      }

      public boolean matches(@Nullable MobEffectInstance p_56578_) {
         if (p_56578_ == null) {
            return false;
         } else if (!this.amplifier.matches(p_56578_.getAmplifier())) {
            return false;
         } else if (!this.duration.matches(p_56578_.getDuration())) {
            return false;
         } else if (this.ambient != null && this.ambient != p_56578_.isAmbient()) {
            return false;
         } else {
            return this.visible == null || this.visible == p_56578_.isVisible();
         }
      }

      public JsonElement serializeToJson() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("amplifier", this.amplifier.serializeToJson());
         jsonobject.add("duration", this.duration.serializeToJson());
         jsonobject.addProperty("ambient", this.ambient);
         jsonobject.addProperty("visible", this.visible);
         return jsonobject;
      }

      public static MobEffectsPredicate.MobEffectInstancePredicate fromJson(JsonObject p_56580_) {
         MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_56580_.get("amplifier"));
         MinMaxBounds.Ints minmaxbounds$ints1 = MinMaxBounds.Ints.fromJson(p_56580_.get("duration"));
         Boolean obool = p_56580_.has("ambient") ? GsonHelper.getAsBoolean(p_56580_, "ambient") : null;
         Boolean obool1 = p_56580_.has("visible") ? GsonHelper.getAsBoolean(p_56580_, "visible") : null;
         return new MobEffectsPredicate.MobEffectInstancePredicate(minmaxbounds$ints, minmaxbounds$ints1, obool, obool1);
      }
   }
}