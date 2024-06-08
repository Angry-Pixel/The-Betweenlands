package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Selector {
   private final Condition condition;
   private final MultiVariant variant;

   public Selector(Condition p_112018_, MultiVariant p_112019_) {
      if (p_112018_ == null) {
         throw new IllegalArgumentException("Missing condition for selector");
      } else if (p_112019_ == null) {
         throw new IllegalArgumentException("Missing variant for selector");
      } else {
         this.condition = p_112018_;
         this.variant = p_112019_;
      }
   }

   public MultiVariant getVariant() {
      return this.variant;
   }

   public Predicate<BlockState> getPredicate(StateDefinition<Block, BlockState> p_112022_) {
      return this.condition.getPredicate(p_112022_);
   }

   public boolean equals(Object p_112024_) {
      return this == p_112024_;
   }

   public int hashCode() {
      return System.identityHashCode(this);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Selector> {
      public Selector deserialize(JsonElement p_112030_, Type p_112031_, JsonDeserializationContext p_112032_) throws JsonParseException {
         JsonObject jsonobject = p_112030_.getAsJsonObject();
         return new Selector(this.getSelector(jsonobject), p_112032_.deserialize(jsonobject.get("apply"), MultiVariant.class));
      }

      private Condition getSelector(JsonObject p_112040_) {
         return p_112040_.has("when") ? getCondition(GsonHelper.getAsJsonObject(p_112040_, "when")) : Condition.TRUE;
      }

      @VisibleForTesting
      static Condition getCondition(JsonObject p_112034_) {
         Set<Entry<String, JsonElement>> set = p_112034_.entrySet();
         if (set.isEmpty()) {
            throw new JsonParseException("No elements found in selector");
         } else if (set.size() == 1) {
            if (p_112034_.has("OR")) {
               List<Condition> list1 = Streams.stream(GsonHelper.getAsJsonArray(p_112034_, "OR")).map((p_112038_) -> {
                  return getCondition(p_112038_.getAsJsonObject());
               }).collect(Collectors.toList());
               return new OrCondition(list1);
            } else if (p_112034_.has("AND")) {
               List<Condition> list = Streams.stream(GsonHelper.getAsJsonArray(p_112034_, "AND")).map((p_112028_) -> {
                  return getCondition(p_112028_.getAsJsonObject());
               }).collect(Collectors.toList());
               return new AndCondition(list);
            } else {
               return getKeyValueCondition(set.iterator().next());
            }
         } else {
            return new AndCondition(set.stream().map(Selector.Deserializer::getKeyValueCondition).collect(Collectors.toList()));
         }
      }

      private static Condition getKeyValueCondition(Entry<String, JsonElement> p_112036_) {
         return new KeyValueCondition(p_112036_.getKey(), p_112036_.getValue().getAsString());
      }
   }
}