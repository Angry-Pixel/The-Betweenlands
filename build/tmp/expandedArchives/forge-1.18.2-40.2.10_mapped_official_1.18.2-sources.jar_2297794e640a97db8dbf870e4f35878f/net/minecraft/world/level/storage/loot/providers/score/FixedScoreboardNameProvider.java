package net.minecraft.world.level.storage.loot.providers.score;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class FixedScoreboardNameProvider implements ScoreboardNameProvider {
   final String name;

   FixedScoreboardNameProvider(String p_165842_) {
      this.name = p_165842_;
   }

   public static ScoreboardNameProvider forName(String p_165847_) {
      return new FixedScoreboardNameProvider(p_165847_);
   }

   public LootScoreProviderType getType() {
      return ScoreboardNameProviders.FIXED;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public String getScoreboardName(LootContext p_165845_) {
      return this.name;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<FixedScoreboardNameProvider> {
      public void serialize(JsonObject p_165855_, FixedScoreboardNameProvider p_165856_, JsonSerializationContext p_165857_) {
         p_165855_.addProperty("name", p_165856_.name);
      }

      public FixedScoreboardNameProvider deserialize(JsonObject p_165863_, JsonDeserializationContext p_165864_) {
         String s = GsonHelper.getAsString(p_165863_, "name");
         return new FixedScoreboardNameProvider(s);
      }
   }
}