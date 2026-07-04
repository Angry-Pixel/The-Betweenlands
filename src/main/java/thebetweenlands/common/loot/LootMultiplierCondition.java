package thebetweenlands.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import thebetweenlands.api.entity.EntityWithLootMultiplier;
import thebetweenlands.common.registries.LootFunctionRegistry;

import java.util.Map;

public record LootMultiplierCondition(String key, NumberProvider provider) implements LootItemCondition {

	public static final MapCodec<LootMultiplierCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("key").forGetter(LootMultiplierCondition::key),
			NumberProviders.CODEC.fieldOf("range").forGetter(LootMultiplierCondition::provider))
		.apply(instance, LootMultiplierCondition::new));

	@Override
	public LootItemConditionType getType() {
		return LootFunctionRegistry.LOOT_MULTIPLIER.get();
	}

	public static LootItemCondition.Builder lootMultiplier(String key, float value) {
		return () -> new LootMultiplierCondition(key, ConstantValue.exactly(value));
	}

	public static LootItemCondition.Builder lootMultiplier(String key, float min, float max) {
		return () -> new LootMultiplierCondition(key, UniformGenerator.between(min, max));
	}

	@Override
	public boolean test(LootContext context) {
		if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof EntityWithLootMultiplier multiplier) {
			Map<String, Float> map = multiplier.getLootModifiers(context, false);
			if (map != null && map.containsKey(this.key)) {
				float value = map.get(this.key);
				return this.provider.getFloat(context) >= value;
			}
		}
		return false;
	}
}
