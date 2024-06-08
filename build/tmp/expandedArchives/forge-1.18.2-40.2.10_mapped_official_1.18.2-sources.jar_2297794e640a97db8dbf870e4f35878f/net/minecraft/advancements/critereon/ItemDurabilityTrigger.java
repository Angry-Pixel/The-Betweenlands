package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemDurabilityTrigger extends SimpleCriterionTrigger<ItemDurabilityTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

   public ResourceLocation getId() {
      return ID;
   }

   public ItemDurabilityTrigger.TriggerInstance createInstance(JsonObject p_43678_, EntityPredicate.Composite p_43679_, DeserializationContext p_43680_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_43678_.get("item"));
      MinMaxBounds.Ints minmaxbounds$ints = MinMaxBounds.Ints.fromJson(p_43678_.get("durability"));
      MinMaxBounds.Ints minmaxbounds$ints1 = MinMaxBounds.Ints.fromJson(p_43678_.get("delta"));
      return new ItemDurabilityTrigger.TriggerInstance(p_43679_, itempredicate, minmaxbounds$ints, minmaxbounds$ints1);
   }

   public void trigger(ServerPlayer p_43670_, ItemStack p_43671_, int p_43672_) {
      this.trigger(p_43670_, (p_43676_) -> {
         return p_43676_.matches(p_43671_, p_43672_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints durability;
      private final MinMaxBounds.Ints delta;

      public TriggerInstance(EntityPredicate.Composite p_43690_, ItemPredicate p_43691_, MinMaxBounds.Ints p_43692_, MinMaxBounds.Ints p_43693_) {
         super(ItemDurabilityTrigger.ID, p_43690_);
         this.item = p_43691_;
         this.durability = p_43692_;
         this.delta = p_43693_;
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(ItemPredicate p_151287_, MinMaxBounds.Ints p_151288_) {
         return changedDurability(EntityPredicate.Composite.ANY, p_151287_, p_151288_);
      }

      public static ItemDurabilityTrigger.TriggerInstance changedDurability(EntityPredicate.Composite p_43695_, ItemPredicate p_43696_, MinMaxBounds.Ints p_43697_) {
         return new ItemDurabilityTrigger.TriggerInstance(p_43695_, p_43696_, p_43697_, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack p_43699_, int p_43700_) {
         if (!this.item.matches(p_43699_)) {
            return false;
         } else if (!this.durability.matches(p_43699_.getMaxDamage() - p_43700_)) {
            return false;
         } else {
            return this.delta.matches(p_43699_.getDamageValue() - p_43700_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_43702_) {
         JsonObject jsonobject = super.serializeToJson(p_43702_);
         jsonobject.add("item", this.item.serializeToJson());
         jsonobject.add("durability", this.durability.serializeToJson());
         jsonobject.add("delta", this.delta.serializeToJson());
         return jsonobject;
      }
   }
}