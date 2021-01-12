package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.initiation.InitiationPhase;
import thebetweenlands.api.runechain.initiation.InitiationState;
import thebetweenlands.api.runechain.initiation.UseInitiationPhase;
import thebetweenlands.api.runechain.io.ISetter;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class InitiateRuneRightClick extends AbstractRune<InitiateRuneRightClick> {
	public static final class Blueprint extends AbstractRune.Blueprint<InitiateRuneRightClick> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(0.0f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;

		public static final RuneConfiguration CONFIGURATION_2;
		public static final ISetter<BlockPos> BLOCK_2;
		public static final ISetter<Vec3d> POSITION_2;
		public static final ISetter<Vec3d> DIRECTION_2;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			CONFIGURATION_1 = builder.build();

			BLOCK_2 = builder.out(RuneTokenDescriptors.BLOCK).type(BlockPos.class).setter();
			POSITION_2 = builder.out(RuneTokenDescriptors.POSITION).type(Vec3d.class).setter();
			DIRECTION_2 = builder.out(RuneTokenDescriptors.DIRECTION).type(Vec3d.class).setter();
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public InitiateRuneRightClick create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new InitiateRuneRightClick(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(InitiateRuneRightClick state, IRuneExecutionContext context, INodeIO io) {

			if(state.getConfiguration() == CONFIGURATION_2) {
				if(state.block == null || state.position == null || state.direction == null) {
					io.fail();
				} else {
					BLOCK_2.set(io, state.block);
					POSITION_2.set(io, state.position);
					DIRECTION_2.set(io, state.direction);
					
					return new Subject(state.block);
				}
			}

			return null;
		}

		@Override
		public InitiationState<InitiateRuneRightClick> checkInitiation(IRuneChainUser user, InitiationPhase initiationPhase, InitiationState<InitiateRuneRightClick> initiationState) {
			return initiationPhase instanceof UseInitiationPhase ? InitiationState.success(state -> {
				UseInitiationPhase useInitiation = (UseInitiationPhase) initiationPhase;

				state.block = useInitiation.getBlock();
				state.position = useInitiation.getPosition();
				EnumFacing facing = useInitiation.getFacing();
				state.direction = facing != null ? new Vec3d(facing.getXOffset(), facing.getYOffset(), facing.getZOffset()) : null;
			}) : null;
		}
	}

	private BlockPos block;
	private Vec3d position;
	private Vec3d direction;

	private InitiateRuneRightClick(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
