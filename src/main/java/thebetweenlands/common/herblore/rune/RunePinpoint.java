package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeCompositionBlueprint;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.INodeBlueprint.IConfigurationLinkAccess;
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

		private static final InputPort<?> IN_ENTITY;
		private static final OutputPort<Vec3d> OUT_POSITION;
		private static final OutputPort<Vec3d> OUT_EYE_POSITION;
		private static final OutputPort<Vec3d> OUT_RAY;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			IN_ENTITY = builder.in(RuneTokenDescriptors.ENTITY, InputSerializers.USER, Entity.class, IRuneChainUser.class); //TODO Needs custom serializer
			OUT_POSITION = builder.out(RuneTokenDescriptors.POSITION, Vec3d.class);
			OUT_EYE_POSITION = builder.out(RuneTokenDescriptors.POSITION, Vec3d.class);
			OUT_RAY = builder.out(RuneTokenDescriptors.DIRECTION, Vec3d.class);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess) {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public RunePinpoint create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RunePinpoint(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RunePinpoint state, RuneExecutionContext context, INodeIO io) {
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
			
			return null;
		}
	}

	private RunePinpoint(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
