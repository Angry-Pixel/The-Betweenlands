package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<PlayerInteractTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("player_interacted_with_entity");

   public ResourceLocation getId() {
      return ID;
   }

   protected PlayerInteractTrigger.TriggerInstance createInstance(JsonObject p_61503_, EntityPredicate.Composite p_61504_, DeserializationContext p_61505_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_61503_.get("item"));
      EntityPredicate.Composite entitypredicate$composite = EntityPredicate.Composite.fromJson(p_61503_, "entity", p_61505_);
      return new PlayerInteractTrigger.TriggerInstance(p_61504_, itempredicate, entitypredicate$composite);
   }

   public void trigger(ServerPlayer p_61495_, ItemStack p_61496_, Entity p_61497_) {
      LootContext lootcontext = EntityPredicate.createContext(p_61495_, p_61497_);
      this.trigger(p_61495_, (p_61501_) -> {
         return p_61501_.matches(p_61496_, lootcontext);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final EntityPredicate.Composite entity;

      public TriggerInstance(EntityPredicate.Composite p_61514_, ItemPredicate p_61515_, EntityPredicate.Composite p_61516_) {
         super(PlayerInteractTrigger.ID, p_61514_);
         this.item = p_61515_;
         this.entity = p_61516_;
      }

      public static PlayerInteractTrigger.TriggerInstance itemUsedOnEntity(EntityPredicate.Composite p_61518_, ItemPredicate.Builder p_61519_, EntityPredicate.Composite p_61520_) {
         return new PlayerInteractTrigger.TriggerInstance(p_61518_, p_61519_.build(), p_61520_);
      }

      public boolean matches(ItemStack p_61522_, LootContext p_61523_) {
         return !this.item.matches(p_61522_) ? false : this.entity.matches(p_61523_);
      }

      public JsonObject serializeToJson(SerializationContext p_61525_) {
         JsonObject jsonobject = super.serializeToJson(p_61525_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("entity", this.entity.toJson(p_61525_));
         return jsonobject;
      }
   }
}