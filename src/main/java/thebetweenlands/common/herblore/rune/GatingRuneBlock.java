package thebetweenlands.common.herblore.rune;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class GatingRuneBlock extends AbstractRune<GatingRuneBlock> {

	public static final class Blueprint extends AbstractRune.Blueprint<GatingRuneBlock> {
		private final Block blockType;

		public Blueprint(@Nullable Block block) {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.1f, 0.01f)
					.build());

			this.blockType = block;
		}

		public static final RuneConfiguration CONFIGURATION_1;

		private static final InputPort<BlockPos> IN_POSITION;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			IN_POSITION = builder.in(RuneTokenDescriptors.BLOCK, InputSerializers.BLOCK, BlockPos.class);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public GatingRuneBlock create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new GatingRuneBlock(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(GatingRuneBlock state, RuneExecutionContext context, INodeIO io) {

			if (state.getConfiguration() == CONFIGURATION_1) {
				BlockPos position = IN_POSITION.get(io);

				Block block = context.getUser().getWorld().getBlockState(position).getBlock();
				if(block != this.blockType) {
					io.fail();
				}
			}

			return null;
		}
	}

	private GatingRuneBlock(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
