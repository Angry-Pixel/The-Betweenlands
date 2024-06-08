package net.minecraft.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;

public interface CriterionTriggerInstance {
   ResourceLocation getCriterion();

   JsonObject serializeToJson(SerializationContext p_14485_);
}