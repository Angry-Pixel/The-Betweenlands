package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.IGetter;
import thebetweenlands.api.runechain.io.ISetter;
import thebetweenlands.api.runechain.io.InputSerializers;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;

public final class TokenRunePattern extends AbstractRune<TokenRunePattern> {

	public static final class Blueprint extends AbstractRune.Blueprint<TokenRunePattern> {
		private final List<BlockPos> pattern;

		public Blueprint(RuneStats stats, List<BlockPos> pattern) {
			super(stats);
			this.pattern = pattern;

			this.setRecursiveRuneEffectModifierCount(3);
		}

		public static final RuneConfiguration CONFIGURATION_1;
		public static final RuneConfiguration CONFIGURATION_2;

		private static final IGetter<IBlockTarget> IN_POSITION_2;
		private static final ISetter<Collection<IBlockTarget>> OUT_POSITIONS_2;

		private static final ISetter<Collection<IBlockTarget>> OUT_POSITIONS;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			OUT_POSITIONS = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();

			CONFIGURATION_1 = builder.build();

			IN_POSITION_2 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			OUT_POSITIONS_2 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();

			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public TokenRunePattern create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new TokenRunePattern(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(TokenRunePattern state, IRuneExecutionContext context, INodeIO io) {

			BlockPos center;
			if(state.getConfiguration() == CONFIGURATION_1) {
				center = new BlockPos(context.getUser().getPosition());
			} else {
				center = IN_POSITION_2.get(io).block();
			}

			List<IBlockTarget> positions = new ArrayList<>(this.pattern.size());
			for(BlockPos block : this.pattern) {
				positions.add(new StaticBlockTarget(block.add(center)));
			}

			if(state.getConfiguration() == CONFIGURATION_1) {
				OUT_POSITIONS.set(io, positions);
			} else {
				OUT_POSITIONS_2.set(io, positions);
			}

			io.schedule(scheduler -> {
				scheduler.sleep(positions.size() * 0.1f);
				scheduler.terminate();
			});

			return new Subject(center);
		}
	}

	private TokenRunePattern(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
