package net.minecraft.client.renderer.block.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemOverride {
   private final ResourceLocation model;
   private final List<ItemOverride.Predicate> predicates;

   public ItemOverride(ResourceLocation p_173447_, List<ItemOverride.Predicate> p_173448_) {
      this.model = p_173447_;
      this.predicates = ImmutableList.copyOf(p_173448_);
   }

   public ResourceLocation getModel() {
      return this.model;
   }

   public Stream<ItemOverride.Predicate> getPredicates() {
      return this.predicates.stream();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemOverride> {
      public ItemOverride deserialize(JsonElement p_111725_, Type p_111726_, JsonDeserializationContext p_111727_) throws JsonParseException {
         JsonObject jsonobject = p_111725_.getAsJsonObject();
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(jsonobject, "model"));
         List<ItemOverride.Predicate> list = this.getPredicates(jsonobject);
         return new ItemOverride(resourcelocation, list);
      }

      protected List<ItemOverride.Predicate> getPredicates(JsonObject p_173451_) {
         Map<ResourceLocation, Float> map = Maps.newLinkedHashMap();
         JsonObject jsonobject = GsonHelper.getAsJsonObject(p_173451_, "predicate");

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            map.put(new ResourceLocation(entry.getKey()), GsonHelper.convertToFloat(entry.getValue(), entry.getKey()));
         }

         return map.entrySet().stream().map((p_173453_) -> {
            return new ItemOverride.Predicate(p_173453_.getKey(), p_173453_.getValue());
         }).collect(ImmutableList.toImmutableList());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Predicate {
      private final ResourceLocation property;
      private final float value;

      public Predicate(ResourceLocation p_173457_, float p_173458_) {
         this.property = p_173457_;
         this.value = p_173458_;
      }

      public ResourceLocation getProperty() {
         return this.property;
      }

      public float getValue() {
         return this.value;
      }
   }
}