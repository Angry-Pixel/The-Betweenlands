package thebetweenlands.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import thebetweenlands.common.entity.monster.Pyrad;
import thebetweenlands.common.registries.LootFunctionRegistry;

public record PyradChargingCondition(boolean charging) implements LootItemCondition {

	public static final MapCodec<PyradChargingCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("charging").forGetter(PyradChargingCondition::charging))
		.apply(instance, PyradChargingCondition::new));

	@Override
	public LootItemConditionType getType() {
		return LootFunctionRegistry.PYRAD_CHARGING.get();
	}

	@Override
	public boolean test(LootContext context) {
		if (context.getParamOrNull(LootContextParams.ATTACKING_ENTITY) instanceof Pyrad pyrad) {
			return pyrad.isCharging() == this.charging();
		}
		return false;
	}

	public static Builder pyradCharging() {
		return () -> new PyradChargingCondition(true);
	}
}
