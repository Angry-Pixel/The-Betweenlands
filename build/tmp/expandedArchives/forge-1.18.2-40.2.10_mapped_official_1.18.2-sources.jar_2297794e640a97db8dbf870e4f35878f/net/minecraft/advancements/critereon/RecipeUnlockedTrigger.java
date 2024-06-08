package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;

public class RecipeUnlockedTrigger extends SimpleCriterionTrigger<RecipeUnlockedTrigger.TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("recipe_unlocked");

   public ResourceLocation getId() {
      return ID;
   }

   public RecipeUnlockedTrigger.TriggerInstance createInstance(JsonObject p_63725_, EntityPredicate.Composite p_63726_, DeserializationContext p_63727_) {
      ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_63725_, "recipe"));
      return new RecipeUnlockedTrigger.TriggerInstance(p_63726_, resourcelocation);
   }

   public void trigger(ServerPlayer p_63719_, Recipe<?> p_63720_) {
      this.trigger(p_63719_, (p_63723_) -> {
         return p_63723_.matches(p_63720_);
      });
   }

   public static RecipeUnlockedTrigger.TriggerInstance unlocked(ResourceLocation p_63729_) {
      return new RecipeUnlockedTrigger.TriggerInstance(EntityPredicate.Composite.ANY, p_63729_);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ResourceLocation recipe;

      public TriggerInstance(EntityPredicate.Composite p_63737_, ResourceLocation p_63738_) {
         super(RecipeUnlockedTrigger.ID, p_63737_);
         this.recipe = p_63738_;
      }

      public JsonObject serializeToJson(SerializationContext p_63742_) {
         JsonObject jsonobject = super.serializeToJson(p_63742_);
         jsonobject.addProperty("recipe", this.recipe.toString());
         return jsonobject;
      }

      public boolean matches(Recipe<?> p_63740_) {
         return this.recipe.equals(p_63740_.getId());
      }
   }
}