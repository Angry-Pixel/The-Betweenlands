package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class TameAnimalTrigger extends SimpleCriterionTrigger<TameAnimalTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("tame_animal");

   public ResourceLocation getId() {
      return ID;
   }

   public TameAnimalTrigger.TriggerInstance createInstance(JsonObject p_68833_, EntityPredicate.Composite p_68834_, DeserializationContext p_68835_) {
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_68833_, "entity", p_68835_);
      return new TameAnimalTrigger.TriggerInstance(p_68834_, entitypredicate$composite);
   }

   public void trigger(ServerPlayer p_68830_, Animal p_68831_) {
      LootContext lootcontext = EntityPredicate.createContext(p_68830_, p_68831_);
      this.trigger(p_68830_, (p_68838_) -> {
         return p_68838_.matches(lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite entity;

      public TriggerInstance(EntityPredicate.Composite p_68846_, EntityPredicate.Composite p_68847_) {
         super(TameAnimalTrigger.ID, p_68846_);
         this.entity = p_68847_;
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal() {
         return new TameAnimalTrigger.TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY);
      }

      public static TameAnimalTrigger.TriggerInstance tamedAnimal(EntityPredicate p_68849_) {
         return new TameAnimalTrigger.TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_68849_));
      }

      public boolean matches(LootContext p_68853_) {
         return this.entity.matches(p_68853_);
      }

      public JsonObject serializeToJson(SerializationContext p_68851_) {
         JsonObject jsonobject = super.serializeToJson(p_68851_);
         jsonobject.add("entity", this.entity.toJson(p_68851_));
         return jsonobject;
      }
   }
}