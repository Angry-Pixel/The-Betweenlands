package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

public class LootTableTrigger extends SimpleCriterionTrigger<LootTableTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("player_generates_container_loot");

   public ResourceLocation getId() {
      return ID;
   }

   protected LootTableTrigger.TriggerInstance createInstance(JsonObject p_54601_, EntityPredicate.Composite p_54602_, DeserializationContext p_54603_) {
      ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_54601_, "loot_table"));
      return new LootTableTrigger.TriggerInstance(p_54602_, resourcelocation);
   }

   public void trigger(ServerPlayer p_54598_, ResourceLocation p_54599_) {
      this.trigger(p_54598_, (p_54606_) -> {
         return p_54606_.matches(p_54599_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation lootTable;

      public TriggerInstance(EntityPredicate.Composite p_54614_, ResourceLocation p_54615_) {
         super(LootTableTrigger.ID, p_54614_);
         this.lootTable = p_54615_;
      }

      public static LootTableTrigger.TriggerInstance lootTableUsed(ResourceLocation p_54619_) {
         return new LootTableTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_54619_);
      }

      public boolean matches(ResourceLocation p_54621_) {
         return this.lootTable.equals(p_54621_);
      }

      public JsonObject serializeToJson(SerializationContext p_54617_) {
         JsonObject jsonobject = super.serializeToJson(p_54617_);
         jsonobject.addProperty("loot_table", this.lootTable.toString());
         return jsonobject;
      }
   }
}