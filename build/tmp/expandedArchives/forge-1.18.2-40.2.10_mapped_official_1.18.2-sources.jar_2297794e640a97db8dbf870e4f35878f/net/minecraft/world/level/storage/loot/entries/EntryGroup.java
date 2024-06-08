package net.minecraft.world.level.storage.loot.entries;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class EntryGroup extends CompositeEntryBase {
   EntryGroup(LootPoolEntryContainer[] p_79550_, LootItemCondition[] p_79551_) {
      super(p_79550_, p_79551_);
   }

   public LootPoolEntryType getType() {
      return LootPoolEntries.GROUP;
   }

   protected ComposableEntryContainer compose(ComposableEntryContainer[] p_79559_) {
      switch(p_79559_.length) {
      case 0:
         return ALWAYS_TRUE;
      case 1:
         return p_79559_[0];
      case 2:
         ComposableEntryContainer composableentrycontainer = p_79559_[0];
         ComposableEntryContainer composableentrycontainer1 = p_79559_[1];
         return (p_79556_, p_79557_) -> {
            composableentrycontainer.expand(p_79556_, p_79557_);
            composableentrycontainer1.expand(p_79556_, p_79557_);
            return true;
         };
      default:
         return (p_79562_, p_79563_) -> {
            for(ComposableEntryContainer composableentrycontainer2 : p_79559_) {
               composableentrycontainer2.expand(p_79562_, p_79563_);
            }

            return true;
         };
      }
   }

   public static EntryGroup.Builder list(LootPoolEntryContainer.Builder<?>... p_165138_) {
      return new EntryGroup.Builder(p_165138_);
   }

   public static class Builder extends LootPoolEntryContainer.Builder<EntryGroup.Builder> {
      private final List<LootPoolEntryContainer> entries = Lists.newArrayList();

      public Builder(LootPoolEntryContainer.Builder<?>... p_165141_) {
         for(LootPoolEntryContainer.Builder<?> builder : p_165141_) {
            this.entries.add(builder.build());
         }

      }

      protected EntryGroup.Builder getThis() {
         return this;
      }

      public EntryGroup.Builder append(LootPoolEntryContainer.Builder<?> p_165145_) {
         this.entries.add(p_165145_.build());
         return this;
      }

      public LootPoolEntryContainer build() {
         return new EntryGroup(this.entries.toArray(new LootPoolEntryContainer[0]), this.getConditions());
      }
   }
}