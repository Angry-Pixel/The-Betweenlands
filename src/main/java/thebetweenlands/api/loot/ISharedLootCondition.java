package thebetweenlands.api.loot;

import java.util.Random;

import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public interface ISharedLootCondition extends LootCondition {
	public default boolean testCondition(Random rand, LootContext context, ISharedLootPool pool) {
		return this.testCondition(rand, context);
	}
}
