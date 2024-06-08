package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import org.slf4j.Logger;

public class PredicateManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = Deserializers.createConditionSerializer().create();
   private Map<ResourceLocation, LootItemCondition> conditions = ImmutableMap.of();

   public PredicateManager() {
      super(GSON, "predicates");
   }

   @Nullable
   public LootItemCondition get(ResourceLocation p_79253_) {
      return this.conditions.get(p_79253_);
   }

   protected void apply(Map<ResourceLocation, JsonElement> p_79249_, ResourceManager p_79250_, ProfilerFiller p_79251_) {
      Builder<ResourceLocation, LootItemCondition> builder = ImmutableMap.builder();
      p_79249_.forEach((p_79235_, p_79236_) -> {
         try {
            if (p_79236_.isJsonArray()) {
               LootItemCondition[] alootitemcondition = GSON.fromJson(p_79236_, LootItemCondition[].class);
               builder.put(p_79235_, new PredicateManager.CompositePredicate(alootitemcondition));
            } else {
               LootItemCondition lootitemcondition = GSON.fromJson(p_79236_, LootItemCondition.class);
               builder.put(p_79235_, lootitemcondition);
            }
         } catch (Exception exception) {
            LOGGER.error("Couldn't parse loot table {}", p_79235_, exception);
         }

      });
      Map<ResourceLocation, LootItemCondition> map = builder.build();
      ValidationContext validationcontext = new ValidationContext(LootContextParamSets.ALL_PARAMS, map::get, (p_79255_) -> {
         return null;
      });
      map.forEach((p_79239_, p_79240_) -> {
         p_79240_.validate(validationcontext.enterCondition("{" + p_79239_ + "}", p_79239_));
      });
      validationcontext.getProblems().forEach((p_79246_, p_79247_) -> {
         LOGGER.warn("Found validation problem in {}: {}", p_79246_, p_79247_);
      });
      this.conditions = map;
   }

   public Set<ResourceLocation> getKeys() {
      return Collections.unmodifiableSet(this.conditions.keySet());
   }

   static class CompositePredicate implements LootItemCondition {
      private final LootItemCondition[] terms;
      private final Predicate<LootContext> composedPredicate;

      CompositePredicate(LootItemCondition[] p_79259_) {
         this.terms = p_79259_;
         this.composedPredicate = LootItemConditions.andConditions(p_79259_);
      }

      public final boolean test(LootContext p_79264_) {
         return this.composedPredicate.test(p_79264_);
      }

      public void validate(ValidationContext p_79266_) {
         LootItemCondition.super.validate(p_79266_);

         for(int i = 0; i < this.terms.length; ++i) {
            this.terms[i].validate(p_79266_.forChild(".term[" + i + "]"));
         }

      }

      public LootItemConditionType getType() {
         throw new UnsupportedOperationException();
      }
   }
}