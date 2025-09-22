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

public class EquipTrigger extends SimpleCriterionTrigger<EquipTrigger.TriggerInstance> {

	@Override
	public Codec<EquipTrigger.TriggerInstance> codec() {
		return EquipTrigger.TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, ItemStack stack) {
		this.trigger(player, (instance) -> instance.matches(stack));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> stack) implements SimpleInstance {

		public static final Codec<EquipTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(EquipTrigger.TriggerInstance::player),
				ItemPredicate.CODEC.optionalFieldOf("stack").forGetter(EquipTrigger.TriggerInstance::stack))
			.apply(instance, EquipTrigger.TriggerInstance::new));

		public boolean matches(ItemStack stack) {
			return this.stack().isEmpty() || this.stack().get().test(stack);
		}

		public static Criterion<EquipTrigger.TriggerInstance> equipItem(ItemLike item) {
			return equipItem(ItemPredicate.Builder.item().of(item).build());
		}

		public static Criterion<TriggerInstance> equipItem(ItemPredicate item) {
			return AdvancementCriteriaRegistry.EQUIP.get().createCriterion(new EquipTrigger.TriggerInstance(Optional.empty(), Optional.of(item)));
		}
	}
}
