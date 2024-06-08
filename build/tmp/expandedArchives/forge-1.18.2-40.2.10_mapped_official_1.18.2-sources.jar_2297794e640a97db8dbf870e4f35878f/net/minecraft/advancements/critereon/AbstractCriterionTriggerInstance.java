package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractCriterionTriggerInstance implements CriterionTriggerInstance {
   private final ResourceLocation criterion;
   private final EntityPredicate.Composite player;

   public AbstractCriterionTriggerInstance(ResourceLocation p_16975_, EntityPredicate.Composite p_16976_) {
      this.criterion = p_16975_;
      this.player = p_16976_;
   }

   public ResourceLocation getCriterion() {
      return this.criterion;
   }

   protected EntityPredicate.Composite getPlayerPredicate() {
      return this.player;
   }

   public JsonObject serializeToJson(SerializationContext p_16979_) {
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("player", this.player.toJson(p_16979_));
      return jsonobject;
   }

   public String toString() {
      return "AbstractCriterionInstance{criterion=" + this.criterion + "}";
   }
}