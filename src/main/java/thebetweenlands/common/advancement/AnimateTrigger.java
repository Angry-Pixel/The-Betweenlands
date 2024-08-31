package thebetweenlands.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;

import java.util.Optional;

public class AnimateTrigger extends SimpleCriterionTrigger<AnimateTrigger.TriggerInstance> {

	@Override
	public Codec<AnimateTrigger.TriggerInstance> codec() {
		return AnimateTrigger.TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, ItemStack input, ItemStack output) {
		this.trigger(player, (instance) -> instance.matches(input, output));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> input, Optional<ItemPredicate> output) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<AnimateTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(AnimateTrigger.TriggerInstance::player),
				ItemPredicate.CODEC.optionalFieldOf("input").forGetter(AnimateTrigger.TriggerInstance::input),
				ItemPredicate.CODEC.optionalFieldOf("output").forGetter(AnimateTrigger.TriggerInstance::output))
			.apply(instance, AnimateTrigger.TriggerInstance::new));

		public boolean matches(ItemStack input, ItemStack output) {
			if (this.input().isEmpty() || this.input().get().test(input)) {
				return this.output().isEmpty() || this.output().get().test(output);
			}
			return false;
		}

		public static Criterion<TriggerInstance> animateItem(ItemPredicate input, ItemPredicate output) {
			return AdvancementCriteriaRegistry.ANIMATE.get().createCriterion(new AnimateTrigger.TriggerInstance(Optional.empty(), Optional.of(input), Optional.of(output)));
		}

		public static Criterion<AnimateTrigger.TriggerInstance> animateItem(ItemLike input, ItemLike output) {
			return animateItem(ItemPredicate.Builder.item().of(input).build(), ItemPredicate.Builder.item().of(output).build());
		}

		public static Criterion<TriggerInstance> animateItem(ItemPredicate input) {
			return AdvancementCriteriaRegistry.ANIMATE.get().createCriterion(new AnimateTrigger.TriggerInstance(Optional.empty(), Optional.of(input), Optional.empty()));
		}

		public static Criterion<AnimateTrigger.TriggerInstance> animateItem(ItemLike input) {
			return animateItem(ItemPredicate.Builder.item().of(input).build());
		}
	}
}
