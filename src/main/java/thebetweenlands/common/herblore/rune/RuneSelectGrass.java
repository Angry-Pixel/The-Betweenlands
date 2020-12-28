package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.IBlockTarget;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.IGetter;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneSelectGrass extends AbstractRune<RuneSelectGrass> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneSelectGrass> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.1f, 0.01f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;

		private static final IGetter<IBlockTarget> IN_POSITION;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			IN_POSITION = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public RuneSelectGrass create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneSelectGrass(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneSelectGrass state, RuneExecutionContext context, INodeIO io) {

			if (state.getConfiguration() == CONFIGURATION_1) {
				BlockPos position = IN_POSITION.get(io).block();

				Block block = context.getUser().getWorld().getBlockState(position).getBlock();
				if(block != Blocks.GRASS /*&& block != Blocks.DIRT*/) {
					io.fail();
				}
			}

			return null;
		}
	}

	private RuneSelectGrass(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
