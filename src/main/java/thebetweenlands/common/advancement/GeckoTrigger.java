package thebetweenlands.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;

import java.util.Optional;

public class GeckoTrigger extends SimpleCriterionTrigger<GeckoTrigger.TriggerInstance> {

	@Override
	public Codec<GeckoTrigger.TriggerInstance> codec() {
		return GeckoTrigger.TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, boolean test, boolean release) {
		this.trigger(player, (instance) -> instance.matches(test, release));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, boolean test, boolean release) implements SimpleInstance {

		public static final Codec<GeckoTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(GeckoTrigger.TriggerInstance::player),
				Codec.BOOL.optionalFieldOf("test_item", false).forGetter(GeckoTrigger.TriggerInstance::test),
				Codec.BOOL.optionalFieldOf("release", false).forGetter(GeckoTrigger.TriggerInstance::test))
			.apply(instance, GeckoTrigger.TriggerInstance::new));

		public boolean matches(boolean test, boolean release) {
			return this.test() == test && this.release() == release;
		}

		public static Criterion<GeckoTrigger.TriggerInstance> gecko(boolean test, boolean release) {
			return AdvancementCriteriaRegistry.GECKO.get().createCriterion(new GeckoTrigger.TriggerInstance(Optional.empty(), test, release));
		}
	}
}
