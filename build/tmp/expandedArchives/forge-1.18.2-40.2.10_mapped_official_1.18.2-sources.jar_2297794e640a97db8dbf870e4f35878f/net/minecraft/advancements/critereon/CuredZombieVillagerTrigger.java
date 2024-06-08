package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.loot.LootContext;

public class CuredZombieVillagerTrigger extends SimpleCriterionTrigger<CuredZombieVillagerTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("cured_zombie_villager");

   public ResourceLocation getId() {
      return ID;
   }

   public CuredZombieVillagerTrigger.TriggerInstance createInstance(JsonObject p_24279_, EntityPredicate.Composite p_24280_, DeserializationContext p_24281_) {
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_24279_, "zombie", p_24281_);
      EntityPredicate.Composite entitypredicate$composite1 = EntityPredicate.Composite.fromJson(p_24279_, "villager", p_24281_);
      return new CuredZombieVillagerTrigger.TriggerInstance(p_24280_, entitypredicate$composite, entitypredicate$composite1);
   }

   public void trigger(ServerPlayer p_24275_, Zombie p_24276_, Villager p_24277_) {
      LootContext lootcontext = EntityPredicate.createContext(p_24275_, p_24276_);
      LootContext lootcontext1 = EntityPredicate.createContext(p_24275_, p_24277_);
      this.trigger(p_24275_, (p_24285_) -> {
         return p_24285_.matches(lootcontext, lootcontext1);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final EntityPredicate.Composite zombie;
      private final EntityPredicate.Composite villager;

      public TriggerInstance(EntityPredicate.Composite p_24294_, EntityPredicate.Composite p_24295_, EntityPredicate.Composite p_24296_) {
         super(CuredZombieVillagerTrigger.ID, p_24294_);
         this.zombie = p_24295_;
         this.villager = p_24296_;
      }

      public static CuredZombieVillagerTrigger.TriggerInstance curedZombieVillager() {
         return new CuredZombieVillagerTrigger.TriggerInstance(EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY, EntityPredicate.Composite.ANY);
      }

      public boolean matches(LootContext p_24300_, LootContext p_24301_) {
         if (!this.zombie.matches(p_24300_)) {
            return false;
         } else {
            return this.villager.matches(p_24301_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_24298_) {
         JsonObject jsonobject = super.serializeToJson(p_24298_);
         jsonobject.add("zombie", this.zombie.toJson(p_24298_));
         jsonobject.add("villager", this.villager.toJson(p_24298_));
         return jsonobject;
      }
   }
}