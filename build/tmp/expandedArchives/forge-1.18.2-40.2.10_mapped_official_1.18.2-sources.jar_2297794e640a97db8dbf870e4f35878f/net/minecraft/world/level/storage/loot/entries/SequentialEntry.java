package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SequentialEntry extends CompositeEntryBase {
   SequentialEntry(LootPoolEntryContainer[] p_79812_, LootItemCondition[] p_79813_) {
      super(p_79812_, p_79813_);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.SEQUENCE;
   }

   protected ComposableEntryContainer compose(ComposableEntryContainer[] p_79816_) {
      switch(p_79816_.length) {
      case 0:
         return ALWAYS_TRUE;
      case 1:
         return p_79816_[0];
      case 2:
         return p_79816_[0].and(p_79816_[1]);
      default:
         return (p_79819_, p_79820_) -> {
            for(ComposableEntryContainer composableentrycontainer : p_79816_) {
               if (!composableentrycontainer.expand(p_79819_, p_79820_)) {
                  return false;
               }
            }

            return true;
         };
      }
   }

   public static SequentialEntry.Builder sequential(LootPoolEntryContainer.Builder<?>... p_165153_) {
      return new SequentialEntry.Builder(p_165153_);
   }

   public static class Builder extends LootPoolEntryContainer.Builder<SequentialEntry.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

      public Builder(LootPoolEntryContainer.Builder<?>... p_165156_) {
         for(LootPoolEntryContainer.Builder<?> builder : p_165156_) {
            this.entries.add(builder.build());
         }

      }

      protected SequentialEntry.Builder getThis() {
         return this;
      }

      public SequentialEntry.Builder then(LootPoolEntryContainer.Builder<?> p_165160_) {
         this.entries.add(p_165160_.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new SequentialEntry(this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
      }
   }
}