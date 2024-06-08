package net.minecraft.data.models.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ModelTemplate {
   private final Optional<ResourceLocation> model;
   private final Set<TextureSlot> requiredSlots;
   private final Optional<String> suffix;

   public ModelTemplate(Optional<ResourceLocation> p_125589_, Optional<String> p_125590_, TextureSlot... p_125591_) {
      this.model = p_125589_;
      this.suffix = p_125590_;
      this.requiredSlots = ImmutableSet.copyOf(p_125591_);
   }

   public ResourceLocation create(Block p_125593_, TextureMapping p_125594_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125595_) {
      return this.create(ModelLocationUtils.getModelLocation(p_125593_, this.suffix.orElse("")), p_125594_, p_125595_);
   }

   public ResourceLocation createWithSuffix(Block p_125597_, String p_125598_, TextureMapping p_125599_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125600_) {
      return this.create(ModelLocationUtils.getModelLocation(p_125597_, p_125598_ + (String)this.suffix.orElse("")), p_125599_, p_125600_);
   }

   public ResourceLocation createWithOverride(Block p_125617_, String p_125618_, TextureMapping p_125619_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125620_) {
      return this.create(ModelLocationUtils.getModelLocation(p_125617_, p_125618_), p_125619_, p_125620_);
   }

   public ResourceLocation create(ResourceLocation p_125613_, TextureMapping p_125614_, BiConsumer<ResourceLocation, Supplier<JsonElement>> p_125615_) {
      Map<TextureSlot, ResourceLocation> map = this.createMap(p_125614_);
      p_125615_.accept(p_125613_, () -> {
         JsonObject jsonobject = new JsonObject();
         this.model.ifPresent((p_176461_) -> {
            jsonobject.addProperty("parent", p_176461_.toString());
         });
         if (!map.isEmpty()) {
            JsonObject jsonobject1 = new JsonObject();
            map.forEach((p_176457_, p_176458_) -> {
               jsonobject1.addProperty(p_176457_.getId(), p_176458_.toString());
            });
            jsonobject.add("textures", jsonobject1);
         }

         return jsonobject;
      });
      return p_125613_;
   }

   private Map<TextureSlot, ResourceLocation> createMap(TextureMapping p_125609_) {
      return Streams.concat(this.requiredSlots.stream(), p_125609_.getForced()).collect(ImmutableMap.toImmutableMap(Function.identity(), p_125609_::get));
   }
}