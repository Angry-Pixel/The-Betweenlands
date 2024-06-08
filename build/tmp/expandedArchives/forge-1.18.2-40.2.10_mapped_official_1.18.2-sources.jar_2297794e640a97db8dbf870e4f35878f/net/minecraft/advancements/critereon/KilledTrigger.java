package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledTrigger extends SimpleCriterionTrigger<KilledTrigger.TriggerInstance> {
   final ResourceLocation id;

   public KilledTrigger(ResourceLocation p_48102_) {
      this.id = p_48102_;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public KilledTrigger.TriggerInstance createInstance(JsonObject p_48116_, EntityPredicate.Composite p_48117_, DeserializationContext p_48118_) {
      return new KilledTrigger.TriggerInstance(this.id, p_48117_, EntityPredicate.Composite.fromJson(p_48116_, "entity", p_48118_), DamageSourcePredicate.fromJson(p_48116_.get("killing_blow")));
   }

   public void trigger(ServerPlayer p_48105_, Entity p_48106_, DamageSource p_48107_) {
      LootContext lootcontext = EntityPredicate.createContext(p_48105_, p_48106_);
      this.trigger(p_48105_, (p_48112_) -> {
         return p_48112_.matches(p_48105_, lootcontext, p_48107_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite entityPredicate;
      private final DamageSourcePredicate killingBlow;

      public TriggerInstance(ResourceLocation p_48126_, EntityPredicate.Composite p_48127_, EntityPredicate.Composite p_48128_, DamageSourcePredicate p_48129_) {
         super(p_48126_, p_48127_);
         this.entityPredicate = p_48128_;
         this.killingBlow = p_48129_;
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate p_152109_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152109_), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder p_48135_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_48135_.build()), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate p_152114_, DamageSourcePredicate p_152115_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152114_), p_152115_);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder p_152106_, DamageSourcePredicate p_152107_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152106_.build()), p_152107_);
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate p_152111_, DamageSourcePredicate.Builder p_152112_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152111_), p_152112_.build());
      }

      public static KilledTrigger.TriggerInstance playerKilledEntity(EntityPredicate.Builder p_48137_, DamageSourcePredicate.Builder p_48138_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.PLAYER_KILLED_ENTITY.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_48137_.build()), p_48138_.build());
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate p_152125_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152125_), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder p_152117_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152117_.build()), DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer() {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, DamageSourcePredicate.ANY);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate p_152130_, DamageSourcePredicate p_152131_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152130_), p_152131_);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder p_152122_, DamageSourcePredicate p_152123_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152122_.build()), p_152123_);
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate p_152127_, DamageSourcePredicate.Builder p_152128_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152127_), p_152128_.build());
      }

      public static KilledTrigger.TriggerInstance entityKilledPlayer(EntityPredicate.Builder p_152119_, DamageSourcePredicate.Builder p_152120_) {
         return new KilledTrigger.TriggerInstance(CriteriaTriggers.ENTITY_KILLED_PLAYER.id, EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_152119_.build()), p_152120_.build());
      }

      public boolean matches(ServerPlayer p_48131_, LootContext p_48132_, DamageSource p_48133_) {
         return !this.killingBlow.matches(p_48131_, p_48133_) ? false : this.entityPredicate.matches(p_48132_);
      }

      public JsonObject serializeToJson(SerializationContext p_48140_) {
         JsonObject jsonobject = super.serializeToJson(p_48140_);
         jsonobject.add("entity", this.entityPredicate.toJson(p_48140_));
         jsonobject.add("killing_blow", this.killingBlow.serializeToJson());
         return jsonobject;
      }
   }
}