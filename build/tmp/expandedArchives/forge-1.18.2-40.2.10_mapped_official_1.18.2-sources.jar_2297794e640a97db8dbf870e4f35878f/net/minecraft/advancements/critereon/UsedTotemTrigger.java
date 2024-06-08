package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class UsedTotemTrigger extends SimpleCriterionTrigger<UsedTotemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("used_totem");

   public ResourceLocation getId() {
      return ID;
   }

   public UsedTotemTrigger.TriggerInstance createInstance(JsonObject p_74438_, EntityPredicate.Composite p_74439_, DeserializationContext p_74440_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_74438_.get("item"));
      return new UsedTotemTrigger.TriggerInstance(p_74439_, itempredicate);
   }

   public void trigger(ServerPlayer p_74432_, ItemStack p_74433_) {
      this.trigger(p_74432_, (p_74436_) -> {
         return p_74436_.matches(p_74433_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite p_74448_, ItemPredicate p_74449_) {
         super(UsedTotemTrigger.ID, p_74448_);
         this.item = p_74449_;
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemPredicate p_163725_) {
         return new UsedTotemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_163725_);
      }

      public static UsedTotemTrigger.TriggerInstance usedTotem(ItemLike p_74453_) {
         return new UsedTotemTrigger.TriggerInstance(EntityPredicate.Composite.ANY, ItemPredicate.Builder.item().of(p_74453_).build());
      }

      public boolean matches(ItemStack p_74451_) {
         return this.item.matches(p_74451_);
      }

      public JsonObject serializeToJson(SerializationContext p_74455_) {
         JsonObject jsonobject = super.serializeToJson(p_74455_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}