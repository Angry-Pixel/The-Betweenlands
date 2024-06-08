package net.minecraft.world.level.storage.loot.predicates;

public interface ConditionUserBuilder<T> {
   T when(LootItemCondition.Builder p_81581_);

   T unwrap();
}