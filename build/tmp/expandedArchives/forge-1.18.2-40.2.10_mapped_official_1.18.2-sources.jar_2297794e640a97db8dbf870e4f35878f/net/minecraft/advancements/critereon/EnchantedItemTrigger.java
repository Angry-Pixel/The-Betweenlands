package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnchantedItemTrigger extends SimpleCriterionTrigger<EnchantedItemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("enchanted_item");

   public ResourceLocation getId() {
      return ID;
   }

   public EnchantedItemTrigger.TriggerInstance createInstance(JsonObject p_27677_, EntityPredicate.Composite p_27678_, DeserializationContext p_27679_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_27677_.get("item"));
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_27677_.get("levels"));
      return new EnchantedItemTrigger.TriggerInstance(p_27678_, itempredicate, minmaxbounds$ints);
   }

   public void trigger(ServerPlayer p_27669_, ItemStack p_27670_, int p_27671_) {
      this.trigger(p_27669_, (p_27675_) -> {
         return p_27675_.matches(p_27670_, p_27671_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints levels;

      public TriggerInstance(EntityPredicate.Composite p_27688_, ItemPredicate p_27689_, MinMaxBounds.Ints p_27690_) {
         super(EnchantedItemTrigger.ID, p_27688_);
         this.item = p_27689_;
         this.levels = p_27690_;
      }

      public static EnchantedItemTrigger.TriggerInstance enchantedItem() {
         return new EnchantedItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, ItemPredicate.ANY, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack p_27692_, int p_27693_) {
         if (!this.item.matches(p_27692_)) {
            return false;
         } else {
            return this.levels.matches(p_27693_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_27695_) {
         JsonObject jsonobject = super.serializeToJson(p_27695_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("levels", this.levels.serializeToJson());
         return jsonobject;
      }
   }
}