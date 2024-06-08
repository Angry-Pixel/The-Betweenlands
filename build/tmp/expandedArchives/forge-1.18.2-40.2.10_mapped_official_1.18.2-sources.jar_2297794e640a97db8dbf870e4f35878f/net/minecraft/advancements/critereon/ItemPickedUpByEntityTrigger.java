package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class ItemPickedUpByEntityTrigger extends SimpleCriterionTrigger<ItemPickedUpByEntityTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("thrown_item_picked_up_by_entity");

   public ResourceLocation getId() {
      return ID;
   }

   protected ItemPickedUpByEntityTrigger.TriggerInstance createInstance(JsonObject p_44373_, EntityPredicate.Composite p_44374_, DeserializationContext p_44375_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_44373_.get("item"));
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_44373_, "entity", p_44375_);
      return new ItemPickedUpByEntityTrigger.TriggerInstance(p_44374_, itempredicate, entitypredicate$composite);
   }

   public void trigger(ServerPlayer p_44364_, ItemStack p_44365_, Entity p_44366_) {
      LootContext lootcontext = EntityPredicate.createContext(p_44364_, p_44366_);
      this.trigger(p_44364_, (p_44371_) -> {
         return p_44371_.matches(p_44364_, p_44365_, lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final EntityPredicate.Composite entity;

      public TriggerInstance(EntityPredicate.Composite p_44384_, ItemPredicate p_44385_, EntityPredicate.Composite p_44386_) {
         super(ItemPickedUpByEntityTrigger.ID, p_44384_);
         this.item = p_44385_;
         this.entity = p_44386_;
      }

      public static ItemPickedUpByEntityTrigger.TriggerInstance itemPickedUpByEntity(EntityPredicate.Composite p_44392_, ItemPredicate.Builder p_44393_, EntityPredicate.Composite p_44394_) {
         return new ItemPickedUpByEntityTrigger.TriggerInstance(p_44392_, p_44393_.build(), p_44394_);
      }

      public boolean matches(ServerPlayer p_44388_, ItemStack p_44389_, LootContext p_44390_) {
         if (!this.item.matches(p_44389_)) {
            return false;
         } else {
            return this.entity.matches(p_44390_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_44396_) {
         JsonObject jsonobject = super.serializeToJson(p_44396_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(p_44396_));
         return jsonobject;
      }
   }
}