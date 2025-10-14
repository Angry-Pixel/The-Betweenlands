package thebetweenlands.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import thebetweenlands.common.entity.monster.TinySludgeWorm;
import thebetweenlands.common.registries.LootFunctionRegistry;

public record WormSquishedCondition(boolean squished) implements LootItemCondition {

	public static final MapCodec<WormSquishedCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.BOOL.fieldOf("squished").forGetter(WormSquishedCondition::squished))
		.apply(instance, WormSquishedCondition::new));

	@Override
	public LootItemConditionType getType() {
		return LootFunctionRegistry.WORM_SQUISHED.get();
	}

	@Override
	public boolean test(LootContext context) {
		if (context.getParamOrNull(LootContextParams.ATTACKING_ENTITY) instanceof TinySludgeWorm worm) {
			return worm.isSquashed() == this.squished();
		}
		return false;
	}

	public static LootItemCondition.Builder wormSquished() {
		return () -> new WormSquishedCondition(true);
	}
}
