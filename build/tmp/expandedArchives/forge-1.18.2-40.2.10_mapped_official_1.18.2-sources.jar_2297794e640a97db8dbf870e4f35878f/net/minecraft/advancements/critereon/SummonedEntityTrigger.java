package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class SummonedEntityTrigger extends SimpleCriterionTrigger<SummonedEntityTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("summoned_entity");

   public ResourceLocation getId() {
      return ID;
   }

   public SummonedEntityTrigger.TriggerInstance createInstance(JsonObject p_68260_, EntityPredicate.Composite p_68261_, DeserializationContext p_68262_) {
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_68260_, "entity", p_68262_);
      return new SummonedEntityTrigger.TriggerInstance(p_68261_, entitypredicate$composite);
   }

   public void trigger(ServerPlayer p_68257_, Entity p_68258_) {
      LootContext lootcontext = EntityPredicate.createContext(p_68257_, p_68258_);
      this.trigger(p_68257_, (p_68265_) -> {
         return p_68265_.matches(lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite entity;

      public TriggerInstance(EntityPredicate.Composite p_68273_, EntityPredicate.Composite p_68274_) {
         super(SummonedEntityTrigger.ID, p_68273_);
         this.entity = p_68274_;
      }

      public static SummonedEntityTrigger.TriggerInstance summonedEntity(EntityPredicate.Builder p_68276_) {
         return new SummonedEntityTrigger.TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_68276_.build()));
      }

      public boolean matches(LootContext p_68280_) {
         return this.entity.matches(p_68280_);
      }

      public JsonObject serializeToJson(SerializationContext p_68278_) {
         JsonObject jsonobject = super.serializeToJson(p_68278_);
         jsonobject.add("entity", this.entity.toJson(p_68278_));
         return jsonobject;
      }
   }
}