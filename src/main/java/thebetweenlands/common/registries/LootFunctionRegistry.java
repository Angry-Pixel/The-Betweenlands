package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.loot.*;

public class LootFunctionRegistry {
	public static final DeferredRegister<LootItemFunctionType<?>> FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, TheBetweenlands.ID);
	public static final DeferredRegister<LootItemConditionType> CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetCountFromAnadiaFunction>> SET_ANADIA_COUNT = FUNCTIONS.register("set_count_from_anadia", () -> new LootItemFunctionType<>(SetCountFromAnadiaFunction.CODEC));
	public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetAnadiaPropertiesFunction>> SET_ANADIA_PROPERTIES = FUNCTIONS.register("set_anadia_properties", () -> new LootItemFunctionType<>(SetAnadiaPropertiesFunction.CODEC));

	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> HAS_ITEM = CONDITIONS.register("has_item", () -> new LootItemConditionType(PlayerHasItemCondition.CODEC));
}
