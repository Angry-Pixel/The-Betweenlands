package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockModelDefinition {
   private final Map<String, MultiVariant> variants = Maps.newLinkedHashMap();
   private MultiPart multiPart;

   public static BlockModelDefinition fromStream(BlockModelDefinition.Context p_111541_, Reader p_111542_) {
      return GsonHelper.fromJson(p_111541_.gson, p_111542_, BlockModelDefinition.class);
   }

   public BlockModelDefinition(Map<String, MultiVariant> p_111537_, MultiPart p_111538_) {
      this.multiPart = p_111538_;
      this.variants.putAll(p_111537_);
   }

   public BlockModelDefinition(List<BlockModelDefinition> p_111535_) {
      BlockModelDefinition blockmodeldefinition = null;

      for(BlockModelDefinition blockmodeldefinition1 : p_111535_) {
         if (blockmodeldefinition1.isMultiPart()) {
            this.variants.clear();
            blockmodeldefinition = blockmodeldefinition1;
         }

         this.variants.putAll(blockmodeldefinition1.variants);
      }

      if (blockmodeldefinition != null) {
         this.multiPart = blockmodeldefinition.multiPart;
      }

   }

   @VisibleForTesting
   public boolean hasVariant(String p_173426_) {
      return this.variants.get(p_173426_) != null;
   }

   @VisibleForTesting
   public MultiVariant getVariant(String p_173429_) {
      MultiVariant multivariant = this.variants.get(p_173429_);
      if (multivariant == null) {
         throw new BlockModelDefinition.MissingVariantException();
      } else {
         return multivariant;
      }
   }

   public boolean equals(Object p_111546_) {
      if (this == p_111546_) {
         return true;
      } else {
         if (p_111546_ instanceof BlockModelDefinition) {
            BlockModelDefinition blockmodeldefinition = (BlockModelDefinition)p_111546_;
            if (this.variants.equals(blockmodeldefinition.variants)) {
               return this.isMultiPart() ? this.multiPart.equals(blockmodeldefinition.multiPart) : !blockmodeldefinition.isMultiPart();
            }
         }

         return false;
      }
   }

   public int hashCode() {
      return 31 * this.variants.hashCode() + (this.isMultiPart() ? this.multiPart.hashCode() : 0);
   }

   public Map<String, MultiVariant> getVariants() {
      return this.variants;
   }

   @VisibleForTesting
   public Set<MultiVariant> getMultiVariants() {
      Set<MultiVariant> set = Sets.newHashSet(this.variants.values());
      if (this.isMultiPart()) {
         set.addAll(this.multiPart.getMultiVariants());
      }

      return set;
   }

   public boolean isMultiPart() {
      return this.multiPart != null;
   }

   public MultiPart getMultiPart() {
      return this.multiPart;
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Context {
      protected final Gson gson = (new GsonBuilder()).registerTypeAdapter(BlockModelDefinition.class, new BlockModelDefinition.Deserializer()).registerTypeAdapter(Variant.class, new Variant.Deserializer()).registerTypeAdapter(MultiVariant.class, new MultiVariant.Deserializer()).registerTypeAdapter(MultiPart.class, new MultiPart.Deserializer(this)).registerTypeAdapter(Selector.class, new Selector.Deserializer()).create();
      private StateDefinition<Block, BlockState> definition;

      public StateDefinition<Block, BlockState> getDefinition() {
         return this.definition;
      }

      public void setDefinition(StateDefinition<Block, BlockState> p_111553_) {
         this.definition = p_111553_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockModelDefinition> {
      public BlockModelDefinition deserialize(JsonElement p_111559_, Type p_111560_, JsonDeserializationContext p_111561_) throws JsonParseException {
         JsonObject jsonobject = p_111559_.getAsJsonObject();
         Map<String, MultiVariant> map = this.getVariants(p_111561_, jsonobject);
         MultiPart multipart = this.getMultiPart(p_111561_, jsonobject);
         if (!map.isEmpty() || multipart != null && !multipart.getMultiVariants().isEmpty()) {
            return new BlockModelDefinition(map, multipart);
         } else {
            throw new JsonParseException("Neither 'variants' nor 'multipart' found");
         }
      }

      protected Map<String, MultiVariant> getVariants(JsonDeserializationContext p_111556_, JsonObject p_111557_) {
         Map<String, MultiVariant> map = Maps.newHashMap();
         if (p_111557_.has("variants")) {
            JsonObject jsonobject = GsonHelper.getAsJsonObject(p_111557_, "variants");

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               map.put(entry.getKey(), p_111556_.deserialize(entry.getValue(), MultiVariant.class));
            }
         }

         return map;
      }

      @Nullable
      protected MultiPart getMultiPart(JsonDeserializationContext p_111563_, JsonObject p_111564_) {
         if (!p_111564_.has("multipart")) {
            return null;
         } else {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(p_111564_, "multipart");
            return p_111563_.deserialize(jsonarray, MultiPart.class);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected class MissingVariantException extends RuntimeException {
   }
}