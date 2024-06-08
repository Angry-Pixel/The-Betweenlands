package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class EffectsChangedTrigger extends SimpleCriterionTrigger<EffectsChangedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("effects_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public EffectsChangedTrigger.TriggerInstance createInstance(JsonObject p_26766_, EntityPredicate.Composite p_26767_, DeserializationContext p_26768_) {
      MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.fromJson(p_26766_.get("effects"));
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_26766_, "source", p_26768_);
      return new EffectsChangedTrigger.TriggerInstance(p_26767_, mobeffectspredicate, entitypredicate$composite);
   }

   public void trigger(ServerPlayer p_149263_, @Nullable Entity p_149264_) {
      LootContext lootcontext = p_149264_ != null ? EntityPredicate.createContext(p_149263_, p_149264_) : null;
      this.trigger(p_149263_, (p_149268_) -> {
         return p_149268_.matches(p_149263_, lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final MobEffectsPredicate effects;
      private final EntityPredicate.Composite source;

      public TriggerInstance(EntityPredicate.Composite p_149271_, MobEffectsPredicate p_149272_, EntityPredicate.Composite p_149273_) {
         super(EffectsChangedTrigger.ID, p_149271_);
         this.effects = p_149272_;
         this.source = p_149273_;
      }

      public static EffectsChangedTrigger.TriggerInstance hasEffects(MobEffectsPredicate p_26781_) {
         return new EffectsChangedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_26781_, EntityPredicate.Composite.ANY);
      }

      public static EffectsChangedTrigger.TriggerInstance gotEffectsFrom(EntityPredicate p_149278_) {
         return new EffectsChangedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MobEffectsPredicate.ANY, EntityPredicate.Composite.wrap(p_149278_));
      }

      public boolean matches(ServerPlayer p_149275_, @Nullable LootContext p_149276_) {
         if (!this.effects.matches(p_149275_)) {
            return false;
         } else {
            return this.source == EntityPredicate.Composite.ANY || p_149276_ != null && this.source.matches(p_149276_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_26783_) {
         JsonObject jsonobject = super.serializeToJson(p_26783_);
         jsonobject.add("effects", this.effects.serializeToJson());
         jsonobject.add("source", this.source.toJson(p_26783_));
         return jsonobject;
      }
   }
}