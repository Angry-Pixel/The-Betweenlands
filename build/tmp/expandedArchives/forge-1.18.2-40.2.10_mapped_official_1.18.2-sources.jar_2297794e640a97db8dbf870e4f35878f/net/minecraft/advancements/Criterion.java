package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Criterion {
   @Nullable
   private final CriterionTriggerInstance trigger;

   public Criterion(CriterionTriggerInstance p_11415_) {
      this.trigger = p_11415_;
   }

   public Criterion() {
      this.trigger = null;
   }

   public void serializeToNetwork(FriendlyByteBuf p_11424_) {
   }

   public static Criterion criterionFromJson(JsonObject p_11418_, DeserializationContext p_11419_) {
      ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_11418_, "trigger"));
      CriterionTrigger<?> criteriontrigger = CriteriaTriggers.getCriterion(resourcelocation);
      if (criteriontrigger == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + resourcelocation);
      } else {
         CriterionTriggerInstance criteriontriggerinstance = criteriontrigger.createInstance(GsonHelper.getAsJsonObject(p_11418_, "conditions", new JsonObject()), p_11419_);
         return new Criterion(criteriontriggerinstance);
      }
   }

   public static Criterion criterionFromNetwork(FriendlyByteBuf p_11430_) {
      return new Criterion();
   }

   public static Map<String, Criterion> criteriaFromJson(JsonObject p_11427_, DeserializationContext p_11428_) {
      Map<String, Criterion> map = Maps.newHashMap();

      for(Entry<String, JsonElement> entry : p_11427_.entrySet()) {
         map.put(entry.getKey(), criterionFromJson(GsonHelper.convertToJsonObject(entry.getValue(), "criterion"), p_11428_));
      }

      return map;
   }

   public static Map<String, Criterion> criteriaFromNetwork(FriendlyByteBuf p_11432_) {
      return p_11432_.readMap(FriendlyByteBuf::readUtf, Criterion::criterionFromNetwork);
   }

   public static void serializeToNetwork(Map<String, Criterion> p_11421_, FriendlyByteBuf p_11422_) {
      p_11422_.writeMap(p_11421_, FriendlyByteBuf::writeUtf, (p_145258_, p_145259_) -> {
         p_145259_.serializeToNetwork(p_145258_);
      });
   }

   @Nullable
   public CriterionTriggerInstance getTrigger() {
      return this.trigger;
   }

   public JsonElement serializeToJson() {
      if (this.trigger == null) {
         throw new JsonSyntaxException("Missing trigger");
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("trigger", this.trigger.getCriterion().toString());
         JsonObject jsonobject1 = this.trigger.serializeToJson(SerializationContext.INSTANCE);
         if (jsonobject1.size() != 0) {
            jsonobject.add("conditions", jsonobject1);
         }

         return jsonobject;
      }
   }
}