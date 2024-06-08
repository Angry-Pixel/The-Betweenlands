package net.minecraft.world.level.storage.loot.entries;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class LootTableReference extends LootPoolSingletonContainer {
   final ResourceLocation name;

   LootTableReference(ResourceLocation p_79756_, int p_79757_, int p_79758_, LootItemCondition[] p_79759_, LootItemFunction[] p_79760_) {
      super(p_79757_, p_79758_, p_79759_, p_79760_);
      this.name = p_79756_;
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.REFERENCE;
   }

   public void createItemStack(Consumer<ItemStack> p_79774_, LootContext p_79775_) {
      LootTable loottable = p_79775_.getLootTable(this.name);
      loottable.getRandomItemsRaw(p_79775_, p_79774_);
   }

   public void validate(ValidationContext p_79770_) {
      if (p_79770_.hasVisitedTable(this.name)) {
         p_79770_.reportProblem("Table " + this.name + " is recursively called");
      } else {
         super.validate(p_79770_);
         LootTable loottable = p_79770_.resolveLootTable(this.name);
         if (loottable == null) {
            p_79770_.reportProblem("Unknown loot table called " + this.name);
         } else {
            loottable.validate(p_79770_.enterTable("->{" + this.name + "}", this.name));
         }

      }
   }

   public static LootPoolSingletonContainer.Builder<?> lootTableReference(ResourceLocation p_79777_) {
      return simpleBuilder((p_79780_, p_79781_, p_79782_, p_79783_) -> {
         return new LootTableReference(p_79777_, p_79780_, p_79781_, p_79782_, p_79783_);
      });
   }

   public static class Serializer extends LootPoolSingletonContainer.Serializer<LootTableReference> {
      public void serializeCustom(JsonObject p_79801_, LootTableReference p_79802_, JsonSerializationContext p_79803_) {
         super.serializeCustom(p_79801_, p_79802_, p_79803_);
         p_79801_.addProperty("name", p_79802_.name.toString());
      }

      protected LootTableReference deserialize(JsonObject p_79786_, JsonDeserializationContext p_79787_, int p_79788_, int p_79789_, LootItemCondition[] p_79790_, LootItemFunction[] p_79791_) {
         ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(p_79786_, "name"));
         return new LootTableReference(resourcelocation, p_79788_, p_79789_, p_79790_, p_79791_);
      }
   }
}