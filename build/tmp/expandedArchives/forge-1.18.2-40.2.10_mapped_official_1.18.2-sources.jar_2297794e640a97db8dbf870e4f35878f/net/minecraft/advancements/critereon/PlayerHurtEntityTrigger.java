package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerHurtEntityTrigger extends SimpleCriterionTrigger<PlayerHurtEntityTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("player_hurt_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public PlayerHurtEntityTrigger.TriggerInstance createInstance(JsonObject p_60128_, EntityPredicate.Composite p_60129_, DeserializationContext p_60130_) {
      DamagePredicate damagepredicate = DamagePredicate.fromJson(p_60128_.get("damage"));
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_60128_, "entity", p_60130_);
      return new PlayerHurtEntityTrigger.TriggerInstance(p_60129_, damagepredicate, entitypredicate$composite);
   }

   public void trigger(ServerPlayer p_60113_, Entity p_60114_, DamageSource p_60115_, float p_60116_, float p_60117_, boolean p_60118_) {
      LootContext lootcontext = EntityPredicate.createContext(p_60113_, p_60114_);
      this.trigger(p_60113_, (p_60126_) -> {
         return p_60126_.matches(p_60113_, lootcontext, p_60115_, p_60116_, p_60117_, p_60118_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final DamagePredicate damage;
      private final EntityPredicate.Composite entity;

      public TriggerInstance(EntityPredicate.Composite p_60139_, DamagePredicate p_60140_, EntityPredicate.Composite p_60141_) {
         super(PlayerHurtEntityTrigger.ID, p_60139_);
         this.damage = p_60140_;
         this.entity = p_60141_;
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity() {
         return new PlayerHurtEntityTrigger.TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY, EntityPredicate.Composite.ANY);
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity(DamagePredicate p_156062_) {
         return new PlayerHurtEntityTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_156062_, EntityPredicate.Composite.ANY);
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity(DamagePredicate.Builder p_60150_) {
         return new PlayerHurtEntityTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_60150_.build(), EntityPredicate.Composite.ANY);
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity(EntityPredicate p_156067_) {
         return new PlayerHurtEntityTrigger.TriggerInstance(EntityPredicate.Composite.ANY, DamagePredicate.ANY, EntityPredicate.Composite.wrap(p_156067_));
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity(DamagePredicate p_156064_, EntityPredicate p_156065_) {
         return new PlayerHurtEntityTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_156064_, EntityPredicate.Composite.wrap(p_156065_));
      }

      public static PlayerHurtEntityTrigger.TriggerInstance playerHurtEntity(DamagePredicate.Builder p_156059_, EntityPredicate p_156060_) {
         return new PlayerHurtEntityTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_156059_.build(), EntityPredicate.Composite.wrap(p_156060_));
      }

      public boolean matches(ServerPlayer p_60143_, LootContext p_60144_, DamageSource p_60145_, float p_60146_, float p_60147_, boolean p_60148_) {
         if (!this.damage.matches(p_60143_, p_60145_, p_60146_, p_60147_, p_60148_)) {
            return false;
         } else {
            return this.entity.matches(p_60144_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_60152_) {
         JsonObject jsonobject = super.serializeToJson(p_60152_);
         jsonobject.add("damage", this.damage.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(p_60152_));
         return jsonobject;
      }
   }
}