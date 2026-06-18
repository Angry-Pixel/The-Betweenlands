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

	public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetAspectFromCropFunction>> SET_ASPECT_FROM_CROP = FUNCTIONS.register("set_aspect_from_crop", () -> new LootItemFunctionType<>(SetAspectFromCropFunction.CODEC));
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> HAS_ITEM = CONDITIONS.register("has_item", () -> new LootItemConditionType(PlayerHasItemCondition.CODEC));
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> EVENT_ACTIVE = CONDITIONS.register("event_active", () -> new LootItemConditionType(EventActiveCondition.CODEC));
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> WORM_SQUISHED = CONDITIONS.register("worm_squished", () -> new LootItemConditionType(WormSquishedCondition.CODEC));
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> PYRAD_CHARGING = CONDITIONS.register("pyrad_charging", () -> new LootItemConditionType(PyradChargingCondition.CODEC));
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> LOOT_MULTIPLIER = CONDITIONS.register("loot_multiplier", () -> new LootItemConditionType(LootMultiplierCondition.CODEC));
	public static final DeferredHolder<LootItemConditionType, LootItemConditionType> ROCK_SNOT_PLACED = CONDITIONS.register("rock_snot_placed", () -> new LootItemConditionType(RockSnotPlacedCondition.CODEC));
}
