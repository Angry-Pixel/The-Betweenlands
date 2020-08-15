package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
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
		public static final OutputPort<BlockPos> BLOCK_2;
		public static final OutputPort<Vec3d> POSITION_2;
		public static final OutputPort<Vec3d> DIRECTION_2;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			CONFIGURATION_1 = builder.build();

			BLOCK_2 = builder.out(RuneTokenDescriptors.BLOCK, BlockPos.class);
			POSITION_2 = builder.out(RuneTokenDescriptors.POSITION, Vec3d.class);
			DIRECTION_2 = builder.out(RuneTokenDescriptors.DIRECTION, Vec3d.class);
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public InitiateRuneRightClick create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new InitiateRuneRightClick(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(InitiateRuneRightClick state, RuneExecutionContext context, INodeIO io) {

			if(state.getConfiguration() == CONFIGURATION_2) {
				if(state.block == null || state.position == null || state.direction == null) {
					io.fail();
				} else {
					BLOCK_2.set(io, state.block);
					POSITION_2.set(io, state.position);
					DIRECTION_2.set(io, state.direction);
					
					return new RuneEffectModifier.Subject(state.block);
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

	private InitiateRuneRightClick(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
