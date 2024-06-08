package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.alchemy.Potion;

public class BrewedPotionTrigger extends SimpleCriterionTrigger<BrewedPotionTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("brewed_potion");

   public ResourceLocation getId() {
      return ID;
   }

   public BrewedPotionTrigger.TriggerInstance createInstance(JsonObject p_19127_, EntityPredicate.Composite p_19128_, DeserializationContext p_19129_) {
      Potion potion = null;
      if (p_19127_.has("potion")) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_19127_, "potion"));
         potion = Registry.POTION.getOptional(resourcelocation).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown potion '" + resourcelocation + "'");
         });
      }

      return new BrewedPotionTrigger.TriggerInstance(p_19128_, potion);
   }

   public void trigger(ServerPlayer p_19121_, Potion p_19122_) {
      this.trigger(p_19121_, (p_19125_) -> {
         return p_19125_.matches(p_19122_);
      });
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      @Nullable
      private final Potion potion;

      public TriggerInstance(EntityPredicate.Composite p_19139_, @Nullable Potion p_19140_) {
         super(BrewedPotionTrigger.ID, p_19139_);
         this.potion = p_19140_;
      }

      public static BrewedPotionTrigger.TriggerInstance brewedPotion() {
         return new BrewedPotionTrigger.TriggerInstance(EntityPredicate.Composite.ANY, (Potion)null);
      }

      public boolean matches(Potion p_19142_) {
         return this.potion == null || this.potion == p_19142_;
      }

      public JsonObject serializeToJson(SerializationContext p_19144_) {
         JsonObject jsonobject = super.serializeToJson(p_19144_);
         if (this.potion != null) {
            jsonobject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return jsonobject;
      }
   }
}