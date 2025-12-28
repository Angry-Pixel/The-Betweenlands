package thebetweenlands.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import thebetweenlands.common.entity.monster.Pyrad;
import thebetweenlands.common.entity.monster.RockSnot;
import thebetweenlands.common.registries.LootFunctionRegistry;

public record RockSnotPlacedCondition(boolean placed) implements LootItemCondition {

	public static final MapCodec<RockSnotPlacedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("placed").forGetter(RockSnotPlacedCondition::placed))
		.apply(instance, RockSnotPlacedCondition::new));

	@Override
	public LootItemConditionType getType() {
		return LootFunctionRegistry.ROCK_SNOT_PLACED.get();
	}

	@Override
	public boolean test(LootContext context) {
		if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof RockSnot rockSnot) {
			return rockSnot.getPlacedByPlayer() == this.placed();
		}
		return false;
	}

	public static Builder placedByPlayer(boolean placed) {
		return () -> new RockSnotPlacedCondition(placed);
	}
}
