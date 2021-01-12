package thebetweenlands.common.herblore.rune;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.IGetter;
import thebetweenlands.api.runechain.io.InputSerializers;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
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
		public GatingRuneBlock create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new GatingRuneBlock(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(GatingRuneBlock state, IRuneExecutionContext context, INodeIO io) {

			if (state.getConfiguration() == CONFIGURATION_1) {
				BlockPos position = IN_POSITION.get(io).block();

				Block block = context.getUser().getWorld().getBlockState(position).getBlock();
				if(block != this.blockType) {
					io.fail();
				}
			}

			return null;
		}
	}

	private GatingRuneBlock(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
