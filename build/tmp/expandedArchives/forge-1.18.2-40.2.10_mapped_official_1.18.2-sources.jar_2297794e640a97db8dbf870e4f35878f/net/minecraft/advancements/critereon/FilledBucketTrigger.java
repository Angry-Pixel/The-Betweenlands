package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class FilledBucketTrigger extends SimpleCriterionTrigger<FilledBucketTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("filled_bucket");

   public ResourceLocation getId() {
      return ID;
   }

   public FilledBucketTrigger.TriggerInstance createInstance(JsonObject p_38779_, EntityPredicate.Composite p_38780_, DeserializationContext p_38781_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_38779_.get("item"));
      return new FilledBucketTrigger.TriggerInstance(p_38780_, itempredicate);
   }

   public void trigger(ServerPlayer p_38773_, ItemStack p_38774_) {
      this.trigger(p_38773_, (p_38777_) -> {
         return p_38777_.matches(p_38774_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite p_38789_, ItemPredicate p_38790_) {
         super(FilledBucketTrigger.ID, p_38789_);
         this.item = p_38790_;
      }

      public static FilledBucketTrigger.TriggerInstance filledBucket(ItemPredicate p_38794_) {
         return new FilledBucketTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_38794_);
      }

      public boolean matches(ItemStack p_38792_) {
         return this.item.matches(p_38792_);
      }

      public JsonObject serializeToJson(SerializationContext p_38796_) {
         JsonObject jsonobject = super.serializeToJson(p_38796_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}