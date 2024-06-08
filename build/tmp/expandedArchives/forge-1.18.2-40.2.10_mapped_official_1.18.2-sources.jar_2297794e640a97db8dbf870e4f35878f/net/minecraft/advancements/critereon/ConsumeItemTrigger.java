package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger extends SimpleCriterionTrigger<ConsumeItemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("consume_item");

   public ResourceLocation getId() {
      return ID;
   }

   public ConsumeItemTrigger.TriggerInstance createInstance(JsonObject p_23689_, EntityPredicate.Composite p_23690_, DeserializationContext p_23691_) {
      return new ConsumeItemTrigger.TriggerInstance(p_23690_, ItemPredicate.fromJson(p_23689_.get("item")));
   }

   public void trigger(ServerPlayer p_23683_, ItemStack p_23684_) {
      this.trigger(p_23683_, (p_23687_) -> {
         return p_23687_.matches(p_23684_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite p_23699_, ItemPredicate p_23700_) {
         super(ConsumeItemTrigger.ID, p_23699_);
         this.item = p_23700_;
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem() {
         return new ConsumeItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemPredicate p_148082_) {
         return new ConsumeItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_148082_);
      }

      public static ConsumeItemTrigger.TriggerInstance usedItem(ItemLike p_23704_) {
         return new ConsumeItemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, new ItemPredicate((TagKey<Item>)null, ImmutableSet.of(p_23704_.asItem()), MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, EnchantmentPredicate.NONE, EnchantmentPredicate.NONE, (Potion)null, NbtPredicate.ANY));
      }

      public boolean matches(ItemStack p_23702_) {
         return this.item.matches(p_23702_);
      }

      public JsonObject serializeToJson(SerializationContext p_23706_) {
         JsonObject jsonobject = super.serializeToJson(p_23706_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}