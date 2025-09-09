package thebetweenlands.common.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.LootFunctionRegistry;

public record EventActiveCondition(ResourceLocation event, boolean active) implements LootItemCondition {

	public static final MapCodec<EventActiveCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("event").forGetter(EventActiveCondition::event),
			Codec.BOOL.fieldOf("active").forGetter(EventActiveCondition::active))
		.apply(instance, EventActiveCondition::new));

	@Override
	public LootItemConditionType getType() {
		return LootFunctionRegistry.EVENT_ACTIVE.get();
	}

	@Override
	public boolean test(LootContext context) {
		if (!context.getLevel().hasData(AttachmentRegistry.WORLD_STORAGE)) return false;
		var event = context.getLevel().getData(AttachmentRegistry.WORLD_STORAGE).getEnvironmentEventRegistry().getEvent(this.event());
		return event != null && event.isActive() == this.active();
	}

	public static LootItemCondition.Builder isEventActive(Holder<EnvironmentEvent> event) {
		return () -> new EventActiveCondition(event.getKey().location(), true);
	}

	public static LootItemCondition.Builder isEventInactive(Holder<EnvironmentEvent> event) {
		return () -> new EventActiveCondition(event.getKey().location(), false);
	}
}
