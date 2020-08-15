package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneConfiguration.InputPort;
import thebetweenlands.api.rune.impl.RuneConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RunePinpoint extends AbstractRune<RunePinpoint> {

	public static final class Blueprint extends AbstractRune.Blueprint<RunePinpoint> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.FERGALAZ, 1)
					.duration(0.333f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		private static final InputPort<?> IN_ENTITY_1;
		private static final OutputPort<Vec3d> OUT_POSITION_1;
		private static final OutputPort<Vec3d> OUT_EYE_POSITION_1;
		private static final OutputPort<Vec3d> OUT_RAY_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final InputPort<BlockPos> IN_BLOCK_2;
		private static final OutputPort<Vec3d> OUT_POSITION_2;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			IN_ENTITY_1 = builder.in(RuneTokenDescriptors.ENTITY, InputSerializers.USER, Entity.class, IRuneChainUser.class); //TODO Needs custom serializer
			OUT_POSITION_1 = builder.out(RuneTokenDescriptors.POSITION, Vec3d.class);
			OUT_EYE_POSITION_1 = builder.out(RuneTokenDescriptors.POSITION, Vec3d.class);
			OUT_RAY_1 = builder.out(RuneTokenDescriptors.DIRECTION, Vec3d.class);
			CONFIGURATION_1 = builder.build();

			IN_BLOCK_2 = builder.in(RuneTokenDescriptors.BLOCK, InputSerializers.BLOCK, BlockPos.class);
			OUT_POSITION_2 = builder.out(RuneTokenDescriptors.POSITION, Vec3d.class);
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public RunePinpoint create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RunePinpoint(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RunePinpoint state, RuneExecutionContext context, INodeIO io) {
			if (state.getConfiguration() == CONFIGURATION_1) {
				IN_ENTITY_1.run(io, Entity.class, entity -> {
					OUT_POSITION_1.set(io, entity.getPositionVector());
					OUT_EYE_POSITION_1.set(io, entity.getPositionEyes(1));
					OUT_RAY_1.set(io, entity.getLookVec());
				});
				IN_ENTITY_1.run(io, IRuneChainUser.class, user -> {
					OUT_POSITION_1.set(io, user.getPosition());
					OUT_POSITION_1.set(io, user.getEyesPosition());
					OUT_RAY_1.set(io, user.getLook());
				});
			} else {
				BlockPos block = IN_BLOCK_2.get(io);
				OUT_POSITION_2.set(io, new Vec3d(block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f));
			}

			return null;
		}
	}

	private RunePinpoint(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
