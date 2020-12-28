package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.rune.IBlockTarget;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;
import thebetweenlands.api.rune.IRuneEffect;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.IGetter;
import thebetweenlands.api.rune.impl.ISetter;
import thebetweenlands.api.rune.impl.InputSerializers;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.common.registries.AspectRegistry;

public final class ConductRuneFire extends AbstractRune<ConductRuneFire> {

	public static final class Blueprint extends AbstractRune.Blueprint<ConductRuneFire> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.FERGALAZ, 1)
					.duration(0.1f)
					.build());
		}

		public static final RuneConfiguration CONFIGURATION_1;
		private static final IGetter<IBlockTarget> IN_POSITION_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final IGetter<Entity> IN_ENTITY_2;

		public static final RuneConfiguration CONFIGURATION_3;
		private static final ISetter<IRuneEffect> OUT_EFFECT_3;

		private static final IRuneEffect FIRE_EFFECT = new IRuneEffect() {
			@Override
			public boolean apply(World world, IRuneChainUser user) {
				Entity entity = user.getEntity();
				if(entity != null && !entity.isImmuneToFire()) {
					entity.setFire(3);
					return true;
				}
				return false;
			}

			@Override
			public boolean apply(World world, Entity entity) {
				if(!entity.isImmuneToFire()) {
					entity.setFire(3);
					return true;
				}
				return false;
			}

			@Override
			public boolean apply(World world, BlockPos pos) {
				if(world.isAirBlock(pos.up())) {
					world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
					return true;
				}
				return false;
			}

			@Override
			public boolean apply(World world, Vec3d position) {
				return false;
			}
		};

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			IN_POSITION_1 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			CONFIGURATION_1 = builder.build();

			IN_ENTITY_2 = builder.in(RuneTokenDescriptors.ENTITY).type(Entity.class).serializer(InputSerializers.ENTITY).getter();
			CONFIGURATION_2 = builder.build();

			OUT_EFFECT_3 = builder.out(RuneTokenDescriptors.EFFECT).type(IRuneEffect.class).setter();
			CONFIGURATION_3 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3);
		}

		@Override
		public ConductRuneFire create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new ConductRuneFire(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(ConductRuneFire state, RuneExecutionContext context, INodeIO io) {

			if(state.getConfiguration() == CONFIGURATION_1 && IN_POSITION_1.get(io) != null) {
				FIRE_EFFECT.apply(context.getUser().getWorld(), IN_POSITION_1.get(io).block());
			} else if(state.getConfiguration() == CONFIGURATION_2 && IN_ENTITY_2.get(io) != null) {
				FIRE_EFFECT.apply(context.getUser().getWorld(), IN_ENTITY_2.get(io));
			} else if(state.getConfiguration() == CONFIGURATION_3) {
				OUT_EFFECT_3.set(io, FIRE_EFFECT);
			}

			return null;
		}

		@Override
		protected RuneEffectModifier createRuneEffectModifier(ConductRuneFire state, AbstractRune<?> target, AbstractRune<?> ioNode, int ioIndex) {
			return new RuneEffectModifier() {
				private List<Entity> targets = new ArrayList<>();

				@Override
				public boolean activate(AbstractRune<?> rune, IRuneChainUser user, Subject subject) {
					super.activate(rune, user, subject);

					if(subject != null) {
						if(subject.getEntity() != null) {
							this.targets.add(subject.getEntity());
						} else if(subject.getBlock() != null) {
							BlockPos pos = subject.getBlock();
							World world = this.user.getWorld();
							world.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, (world.rand.nextFloat() - 0.5f) * 0.1f, (world.rand.nextFloat() - 0.5f) * 0.1f, (world.rand.nextFloat() - 0.5f) * 0.1f);
							world.spawnParticle(EnumParticleTypes.LAVA, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, 0, 0, 0);
						}
					}

					return true;
				}

				@Override
				public void update() {
					if(this.user.getWorld().isRemote) {
						Iterator<Entity> targetsIt = this.targets.iterator();
						while(targetsIt.hasNext()) {
							Entity target = targetsIt.next();

							if(target.isEntityAlive()) {
								Random rng = this.user.getWorld().rand;
								target.world.spawnParticle(EnumParticleTypes.FLAME, target.posX, target.posY, target.posZ, (rng.nextFloat() - 0.5f) * 0.1f, (rng.nextFloat() - 0.5f) * 0.1f, (rng.nextFloat() - 0.5f) * 0.1f);
								target.world.spawnParticle(EnumParticleTypes.LAVA, target.posX, target.posY, target.posZ, 0, 0, 0);
							} else {
								targetsIt.remove();
							}
						}
					}
				}
				
				@Override
				public int getColorModifier(RuneEffectModifier.Subject subject, int index) {
					return 0xFFE8440E;
				}
				
				@Override
				public int getColorModifierCount(RuneEffectModifier.Subject subject) {
					return 1;
				}
			};
		}
		
		@Override
		protected boolean isDelegatedRuneEffectModifier(ConductRuneFire state, AbstractRune<?> target, AbstractRune<?> inputRune, int outputIndex) {
			if(state.getConfiguration() == CONFIGURATION_3) {
				return outputIndex == OUT_EFFECT_3.index();
			}
			return false;
		}
	}

	private ConductRuneFire(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
