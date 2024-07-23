package thebetweenlands.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;

import java.util.Optional;

public class SwatShieldTrigger extends SimpleCriterionTrigger<SwatShieldTrigger.TriggerInstance> {

	@Override
	public Codec<SwatShieldTrigger.TriggerInstance> codec() {
		return SwatShieldTrigger.TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, LivingEntity victim) {
		LootContext lootcontext = EntityPredicate.createContext(player, victim);
		this.trigger(player, (instance) -> instance.matches(lootcontext));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> victim) implements SimpleInstance {

		public static final Codec<SwatShieldTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(SwatShieldTrigger.TriggerInstance::player),
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("victim").forGetter(SwatShieldTrigger.TriggerInstance::victim))
			.apply(instance, SwatShieldTrigger.TriggerInstance::new));

		public boolean matches(LootContext victim) {
			return this.victim().isEmpty() || this.victim().get().matches(victim);
		}

		public static Criterion<SwatShieldTrigger.TriggerInstance> killWithShockwave(EntityPredicate victim) {
			return AdvancementCriteriaRegistry.SWAT_SHIELD.get().createCriterion(new SwatShieldTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(victim))));
		}

		public static Criterion<SwatShieldTrigger.TriggerInstance> killWithShockwave(EntityType<?> victim) {
			return killWithShockwave(EntityPredicate.Builder.entity().of(victim).build());
		}
	}
}
