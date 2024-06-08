package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.storage.loot.LootContext;

public class LightningStrikeTrigger extends SimpleCriterionTrigger<LightningStrikeTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("lightning_strike");

   public ResourceLocation getId() {
      return ID;
   }

   public LightningStrikeTrigger.TriggerInstance createInstance(JsonObject p_153396_, EntityPredicate.Composite p_153397_, DeserializationContext p_153398_) {
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_153396_, "lightning", p_153398_);
      EntityPredicate.Composite entitypredicate$composite1 = EntityPredicate.Composite.fromJson(p_153396_, "bystander", p_153398_);
      return new LightningStrikeTrigger.TriggerInstance(p_153397_, entitypredicate$composite, entitypredicate$composite1);
   }

   public void trigger(ServerPlayer p_153392_, LightningBolt p_153393_, List<Entity> p_153394_) {
      List<LootContext> list = p_153394_.stream().map((p_153390_) -> {
         return EntityPredicate.createContext(p_153392_, p_153390_);
      }).collect(Collectors.toList());
      LootContext lootcontext = EntityPredicate.createContext(p_153392_, p_153393_);
      this.trigger(p_153392_, (p_153402_) -> {
         return p_153402_.matches(lootcontext, list);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite lightning;
      private final EntityPredicate.Composite bystander;

      public TriggerInstance(EntityPredicate.Composite p_153410_, EntityPredicate.Composite p_153411_, EntityPredicate.Composite p_153412_) {
         super(LightningStrikeTrigger.ID, p_153410_);
         this.lightning = p_153411_;
         this.bystander = p_153412_;
      }

      public static LightningStrikeTrigger.TriggerInstance lighthingStrike(EntityPredicate p_153414_, EntityPredicate p_153415_) {
         return new LightningStrikeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.wrap(p_153414_), EntityPredicate.Composite.wrap(p_153415_));
      }

      public boolean matches(LootContext p_153419_, List<LootContext> p_153420_) {
         if (!this.lightning.matches(p_153419_)) {
            return false;
         } else {
            return this.bystander == EntityPredicate.Composite.ANY || !p_153420_.stream().noneMatch(this.bystander::matches);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_153417_) {
         JsonObject jsonobject = super.serializeToJson(p_153417_);
         jsonobject.add("lightning", this.lightning.toJson(p_153417_));
         jsonobject.add("bystander", this.bystander.toJson(p_153417_));
         return jsonobject;
      }
   }
}