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

public class ShockwaveKillTrigger extends SimpleCriterionTrigger<ShockwaveKillTrigger.TriggerInstance> {

	@Override
	public Codec<ShockwaveKillTrigger.TriggerInstance> codec() {
		return ShockwaveKillTrigger.TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, LivingEntity victim) {
		LootContext lootcontext = EntityPredicate.createContext(player, victim);
		this.trigger(player, (instance) -> instance.matches(lootcontext));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> victim) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<ShockwaveKillTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ShockwaveKillTrigger.TriggerInstance::player),
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("victim").forGetter(ShockwaveKillTrigger.TriggerInstance::victim))
			.apply(instance, ShockwaveKillTrigger.TriggerInstance::new));

		public boolean matches(LootContext victim) {
			return this.victim().isEmpty() || this.victim().get().matches(victim);
		}

		public static Criterion<ShockwaveKillTrigger.TriggerInstance> killWithShockwave(EntityPredicate victim) {
			return AdvancementCriteriaRegistry.SHOCKWAVE_KILL.get().createCriterion(new ShockwaveKillTrigger.TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(victim))));
		}

		public static Criterion<ShockwaveKillTrigger.TriggerInstance> killWithShockwave(EntityType<?> victim) {
			return killWithShockwave(EntityPredicate.Builder.entity().of(victim).build());
		}
	}
}
