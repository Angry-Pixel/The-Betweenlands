package thebetweenlands.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;

import java.util.Optional;

public class EventTrigger extends SimpleCriterionTrigger<EventTrigger.TriggerInstance> {

	@Override
	public Codec<EventTrigger.TriggerInstance> codec() {
		return EventTrigger.TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, EnvironmentEvent event) {
		this.trigger(player, (instance) -> instance.matches(event));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<EnvironmentEvent> event) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<EventTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(EventTrigger.TriggerInstance::player),
				BLRegistries.ENVIRONMENT_EVENTS.byNameCodec().optionalFieldOf("event").forGetter(EventTrigger.TriggerInstance::event))
			.apply(instance, EventTrigger.TriggerInstance::new));

		public boolean matches(EnvironmentEvent event) {
			return this.event().isEmpty() || this.event().get() == event;
		}

		public static Criterion<EventTrigger.TriggerInstance> triggeredEvent(EnvironmentEvent event) {
			return AdvancementCriteriaRegistry.EVENT.get().createCriterion(new EventTrigger.TriggerInstance(Optional.empty(), Optional.of(event)));
		}
	}
}
