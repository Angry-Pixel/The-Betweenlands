package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class MatchTool implements LootItemCondition {
   final ItemPredicate predicate;

   public MatchTool(ItemPredicate p_81995_) {
      this.predicate = p_81995_;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.MATCH_TOOL;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.TOOL);
   }

   public boolean test(LootContext p_82000_) {
      ItemStack itemstack = p_82000_.getParamOrNull(LootContextParams.TOOL);
      return itemstack != null && this.predicate.matches(itemstack);
   }

   public static LootItemCondition.Builder toolMatches(ItemPredicate.Builder p_81998_) {
      return () -> {
         return new MatchTool(p_81998_.build());
      };
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<MatchTool> {
      public void serialize(JsonObject p_82013_, MatchTool p_82014_, JsonSerializationContext p_82015_) {
         p_82013_.add("predicate", p_82014_.predicate.serializeToJson());
      }

      public MatchTool deserialize(JsonObject p_82021_, JsonDeserializationContext p_82022_) {
         ItemPredicate itempredicate = ItemPredicate.fromJson(p_82021_.get("predicate"));
         return new MatchTool(itempredicate);
      }
   }
}