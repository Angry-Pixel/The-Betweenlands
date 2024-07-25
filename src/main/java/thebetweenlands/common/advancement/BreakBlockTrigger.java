package thebetweenlands.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;

import java.util.Optional;

public class BreakBlockTrigger extends SimpleCriterionTrigger<BreakBlockTrigger.TriggerInstance> {

	@Override
	public Codec<BreakBlockTrigger.TriggerInstance> codec() {
		return BreakBlockTrigger.TriggerInstance.CODEC;
	}

	public void trigger(ServerPlayer player, BlockPos pos) {
		this.trigger(player, (instance) -> instance.matches(player.serverLevel(), pos));
	}

	public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<BlockPredicate> block) implements SimpleCriterionTrigger.SimpleInstance {

		public static final Codec<BreakBlockTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(BreakBlockTrigger.TriggerInstance::player),
				BlockPredicate.CODEC.optionalFieldOf("block").forGetter(BreakBlockTrigger.TriggerInstance::block))
			.apply(instance, BreakBlockTrigger.TriggerInstance::new));

		public boolean matches(ServerLevel level, BlockPos pos) {
			return this.block().isEmpty() || this.block().get().matches(level, pos);
		}

		public static Criterion<BreakBlockTrigger.TriggerInstance> breakBlock(BlockPredicate predicate) {
			return AdvancementCriteriaRegistry.BREAK_BLOCK.get().createCriterion(new BreakBlockTrigger.TriggerInstance(Optional.empty(), Optional.of(predicate)));
		}

		public static Criterion<BreakBlockTrigger.TriggerInstance> breakBlock(Block block) {
			return breakBlock(BlockPredicate.Builder.block().of(block).build());
		}
	}
}
