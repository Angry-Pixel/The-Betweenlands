package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.IGetter;
import thebetweenlands.api.runechain.io.ISetter;
import thebetweenlands.api.runechain.io.InputSerializers;
import thebetweenlands.api.runechain.io.types.DynamicBlockTarget;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.IVectorTarget;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.io.types.StaticVectorTarget;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
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
		private static final IGetter<?> IN_ENTITY_1;
		private static final ISetter<IVectorTarget> OUT_POSITION_1;
		private static final ISetter<IVectorTarget> OUT_EYE_POSITION_1;
		private static final ISetter<IVectorTarget> OUT_RAY_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final IGetter<IBlockTarget> IN_BLOCK_2;
		private static final ISetter<IVectorTarget> OUT_POSITION_2;

		public static final RuneConfiguration CONFIGURATION_3;
		private static final IGetter<IVectorTarget> IN_POSITION_3;
		private static final ISetter<IBlockTarget> OUT_BLOCK_3;

		public static final RuneConfiguration CONFIGURATION_5;
		private static final IGetter<IVectorTarget> IN_POSITION_5;
		private static final ISetter<IVectorTarget> OUT_POSITION_5;

		public static final RuneConfiguration CONFIGURATION_4;
		private static final IGetter<IBlockTarget> IN_BLOCK_4;
		private static final ISetter<IBlockTarget> OUT_BLOCK_4;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			IN_ENTITY_1 = builder.in(RuneTokenDescriptors.ENTITY).type(Entity.class, IRuneChainUser.class).serializer(InputSerializers.USER).getter();
			OUT_POSITION_1 = builder.out(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).setter();
			OUT_EYE_POSITION_1 = builder.out(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).setter();
			OUT_RAY_1 = builder.out(RuneTokenDescriptors.DIRECTION).type(IVectorTarget.class).setter();
			CONFIGURATION_1 = builder.build();

			IN_BLOCK_2 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			OUT_POSITION_2 = builder.out(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).setter();
			CONFIGURATION_2 = builder.build();

			IN_POSITION_3 = builder.in(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			OUT_BLOCK_3 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).setter();
			CONFIGURATION_3 = builder.build();

			IN_BLOCK_4 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			OUT_BLOCK_4 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).setter();
			CONFIGURATION_4 = builder.build();

			IN_POSITION_5 = builder.in(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			OUT_POSITION_5 = builder.out(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).setter();
			CONFIGURATION_5 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3, CONFIGURATION_4, CONFIGURATION_5);
		}

		@Override
		public RunePinpoint create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RunePinpoint(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(RunePinpoint state, IRuneExecutionContext context, INodeIO io) {
			if (state.getConfiguration() == CONFIGURATION_1) {
				IN_ENTITY_1.run(io, Entity.class, entity -> {
					OUT_POSITION_1.set(io, () -> entity.getPositionVector());
					OUT_EYE_POSITION_1.set(io, () -> entity.getPositionEyes(1));
					OUT_RAY_1.set(io, () -> entity.getLookVec());
				});
				IN_ENTITY_1.run(io, IRuneChainUser.class, user -> {
					OUT_POSITION_1.set(io, () -> user.getPosition());
					OUT_EYE_POSITION_1.set(io, () -> user.getEyesPosition());
					OUT_RAY_1.set(io, () -> user.getLook());
				});
			} else if(state.getConfiguration() == CONFIGURATION_2) {
				IBlockTarget block = IN_BLOCK_2.get(io);
				OUT_POSITION_2.set(io, block);
			} else if(state.getConfiguration() == CONFIGURATION_3) {
				IVectorTarget target = IN_POSITION_3.get(io);
				OUT_BLOCK_3.set(io, target instanceof IBlockTarget ? (IBlockTarget) target : new DynamicBlockTarget(target));
			} else if(state.getConfiguration() == CONFIGURATION_4) {
				IBlockTarget target = IN_BLOCK_4.get(io);
				OUT_BLOCK_4.set(io, !target.isDynamic() ? target : new StaticBlockTarget(target));
			} else if(state.getConfiguration() == CONFIGURATION_5) {
				IVectorTarget target = IN_POSITION_5.get(io);
				OUT_POSITION_5.set(io, !target.isDynamic() ? target : new StaticVectorTarget(target));
			}

			return null;
		}
	}

	private RunePinpoint(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
