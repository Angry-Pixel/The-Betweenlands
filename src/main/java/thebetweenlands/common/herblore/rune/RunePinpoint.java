package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.InputPort;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RunePinpoint extends AbstractRune<RunePinpoint> {

	public static final class Blueprint extends AbstractRune.Blueprint<RunePinpoint> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.FERGALAZ, 1)
					.duration(0.333f)
					.build());
		}

		public static final INodeConfiguration CONFIGURATION_1;

		private static final InputPort<?> IN_ENTITY;
		private static final OutputPort<Vec3d> OUT_POSITION;
		private static final OutputPort<Vec3d> OUT_EYE_POSITION;
		private static final OutputPort<Vec3d> OUT_RAY;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			IN_ENTITY = builder.in(RuneTokenDescriptors.ENTITY, Entity.class, IRuneChainUser.class);
			OUT_POSITION = builder.out(RuneTokenDescriptors.POSITION, Vec3d.class);
			OUT_EYE_POSITION = builder.out(RuneTokenDescriptors.POSITION, Vec3d.class);
			OUT_RAY = builder.out(RuneTokenDescriptors.DIRECTION, Vec3d.class);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<INodeConfiguration> getConfigurations() {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public RunePinpoint create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RunePinpoint(this, composition, configuration);
		}

		@Override
		protected void activate(RunePinpoint state, RuneExecutionContext context, INodeIO io) {
			if (state.getConfiguration() == CONFIGURATION_1) {
				IN_ENTITY.run(io, Entity.class, entity -> {
					OUT_POSITION.set(io, entity.getPositionVector());
					OUT_EYE_POSITION.set(io, entity.getPositionEyes(1));
					OUT_RAY.set(io, entity.getLookVec());
				});
				IN_ENTITY.run(io, IRuneChainUser.class, user -> {
					OUT_POSITION.set(io, user.getPosition());
					OUT_POSITION.set(io, user.getEyesPosition());
					OUT_RAY.set(io, user.getLook());
				});
			}
		}
	}

	private RunePinpoint(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
