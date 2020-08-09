package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
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
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneFire extends AbstractRune<RuneFire> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneFire> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.FERGALAZ, 1)
					.duration(0.333f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		public static final RuneConfiguration CONFIGURATION_2;

		private static final InputPort<Entity> IN_ENTITY;
		private static final InputPort<BlockPos> IN_POSITION;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			IN_POSITION = builder.in(RuneTokenDescriptors.BLOCK, InputSerializers.BLOCK, BlockPos.class);
			CONFIGURATION_1 = builder.build();

			IN_ENTITY = builder.in(RuneTokenDescriptors.ENTITY, InputSerializers.ENTITY, Entity.class);
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public RuneFire create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneFire(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneFire state, RuneExecutionContext context, INodeIO io) {

			if (state.getConfiguration() == CONFIGURATION_1) {
				BlockPos pos = IN_POSITION.get(io);

				if(context.getUser().getWorld().isAirBlock(pos.up())) {
					context.getUser().getWorld().setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
				}
			}

			if (state.getConfiguration() == CONFIGURATION_2) {
				Entity entity = IN_ENTITY.get(io);

				entity.setFire(3);
			}

			return null;
		}

		@Override
		protected RuneEffectModifier createRuneEffectModifier(AbstractRune<?> target, int output, int input) {
			return new RuneEffectModifier() {
				private List<Entity> targets = new ArrayList<>();

				@Override
				public boolean activate(AbstractRune<?> rune, IRuneChainUser user, Subject subject) {
					super.activate(rune, user, subject);

					if(subject != null && subject.getEntity() != null) {
						this.targets.add(subject.getEntity());
					}

					return true;
				}

				@Override
				public void update() {
					if(user.getWorld().isRemote) {
						Iterator<Entity> targetsIt = this.targets.iterator();
						while(targetsIt.hasNext()) {
							Entity target = targetsIt.next();

							if(target.isEntityAlive()) {
								target.world.spawnParticle(EnumParticleTypes.FLAME, target.posX, target.posY, target.posZ, 0, 0, 0);
							} else {
								targetsIt.remove();
							}
						}
					}
				}
			};
		}
	}

	private RuneFire(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
