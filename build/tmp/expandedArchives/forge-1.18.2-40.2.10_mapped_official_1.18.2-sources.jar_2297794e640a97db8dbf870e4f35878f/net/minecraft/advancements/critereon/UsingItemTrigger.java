package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends SimpleCriterionTrigger<UsingItemTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("using_item");

   public ResourceLocation getId() {
      return ID;
   }

   public UsingItemTrigger.TriggerInstance createInstance(JsonObject p_163872_, EntityPredicate.Composite p_163873_, DeserializationContext p_163874_) {
      ItemPredicate itempredicate = ItemPredicate.fromJson(p_163872_.get("item"));
      return new UsingItemTrigger.TriggerInstance(p_163873_, itempredicate);
   }

   public void trigger(ServerPlayer p_163866_, ItemStack p_163867_) {
      this.trigger(p_163866_, (p_163870_) -> {
         return p_163870_.matches(p_163867_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(EntityPredicate.Composite p_163881_, ItemPredicate p_163882_) {
         super(UsingItemTrigger.ID, p_163881_);
         this.item = p_163882_;
      }

      public static UsingItemTrigger.TriggerInstance lookingAt(EntityPredicate.Builder p_163884_, ItemPredicate.Builder p_163885_) {
         return new UsingItemTrigger.TriggerInstance(EntityPredicate.Composite.wrap(p_163884_.build()), p_163885_.build());
      }

      public boolean matches(ItemStack p_163887_) {
         return this.item.matches(p_163887_);
      }

      public JsonObject serializeToJson(SerializationContext p_163889_) {
         JsonObject jsonobject = super.serializeToJson(p_163889_);
         jsonobject.add("item", this.item.serializeToJson());
         return jsonobject;
      }
   }
}