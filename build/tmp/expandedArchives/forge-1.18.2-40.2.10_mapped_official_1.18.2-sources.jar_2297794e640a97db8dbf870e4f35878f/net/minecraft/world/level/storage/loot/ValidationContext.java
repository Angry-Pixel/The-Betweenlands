package net.minecraft.world.level.storage.loot;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ValidationContext {
   private final Multimap<String, String> problems;
   private final Supplier<String> context;
   private final LootContextParamSet params;
   private final Function<ResourceLocation, LootItemCondition> conditionResolver;
   private final Set<ResourceLocation> visitedConditions;
   private final Function<ResourceLocation, LootTable> tableResolver;
   private final Set<ResourceLocation> visitedTables;
   private String contextCache;

   public ValidationContext(LootContextParamSet p_79349_, Function<ResourceLocation, LootItemCondition> p_79350_, Function<ResourceLocation, LootTable> p_79351_) {
      this(HashMultimap.create(), () -> {
         return "";
      }, p_79349_, p_79350_, ImmutableSet.of(), p_79351_, ImmutableSet.of());
   }

   public ValidationContext(Multimap<String, String> p_79341_, Supplier<String> p_79342_, LootContextParamSet p_79343_, Function<ResourceLocation, LootItemCondition> p_79344_, Set<ResourceLocation> p_79345_, Function<ResourceLocation, LootTable> p_79346_, Set<ResourceLocation> p_79347_) {
      this.problems = p_79341_;
      this.context = p_79342_;
      this.params = p_79343_;
      this.conditionResolver = p_79344_;
      this.visitedConditions = p_79345_;
      this.tableResolver = p_79346_;
      this.visitedTables = p_79347_;
   }

   private String getContext() {
      if (this.contextCache == null) {
         this.contextCache = this.context.get();
      }

      return this.contextCache;
   }

   public void reportProblem(String p_79358_) {
      this.problems.put(this.getContext(), p_79358_);
   }

   public ValidationContext forChild(String p_79366_) {
      return new ValidationContext(this.problems, () -> {
         return this.getContext() + p_79366_;
      }, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
   }

   public ValidationContext enterTable(String p_79360_, ResourceLocation p_79361_) {
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.<ResourceLocation>builder().addAll(this.visitedTables).add(p_79361_).build();
      return new ValidationContext(this.problems, () -> {
         return this.getContext() + p_79360_;
      }, this.params, this.conditionResolver, this.visitedConditions, this.tableResolver, immutableset);
   }

   public ValidationContext enterCondition(String p_79368_, ResourceLocation p_79369_) {
      ImmutableSet<ResourceLocation> immutableset = ImmutableSet.<ResourceLocation>builder().addAll(this.visitedConditions).add(p_79369_).build();
      return new ValidationContext(this.problems, () -> {
         return this.getContext() + p_79368_;
      }, this.params, this.conditionResolver, immutableset, this.tableResolver, this.visitedTables);
   }

   public boolean hasVisitedTable(ResourceLocation p_79363_) {
      return this.visitedTables.contains(p_79363_);
   }

   public boolean hasVisitedCondition(ResourceLocation p_79371_) {
      return this.visitedConditions.contains(p_79371_);
   }

   public Multimap<String, String> getProblems() {
      return ImmutableMultimap.copyOf(this.problems);
   }

   public void validateUser(LootContextUser p_79354_) {
      this.params.validateUser(this, p_79354_);
   }

   @Nullable
   public LootTable resolveLootTable(ResourceLocation p_79376_) {
      return this.tableResolver.apply(p_79376_);
   }

   @Nullable
   public LootItemCondition resolveCondition(ResourceLocation p_79380_) {
      return this.conditionResolver.apply(p_79380_);
   }

   public ValidationContext setParams(LootContextParamSet p_79356_) {
      return new ValidationContext(this.problems, this.context, p_79356_, this.conditionResolver, this.visitedConditions, this.tableResolver, this.visitedTables);
   }
}