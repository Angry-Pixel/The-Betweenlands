package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.PortNodeConfiguration;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.InputPort;
import thebetweenlands.api.rune.impl.PortNodeConfiguration.OutputPort;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneMarkDescriptors;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.registries.AspectRegistry;

public final class RuneProjectile extends AbstractRune<RuneProjectile> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneProjectile> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build());
		}

		public static final INodeConfiguration CONFIGURATION_1;

		private static final InputPort<Entity> IN_ENTITY;
		private static final OutputPort<BlockPos> OUT_POSITION;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			IN_ENTITY = builder.in(Entity.class, RuneMarkDescriptors.ENTITY);
			OUT_POSITION = builder.out(BlockPos.class, RuneMarkDescriptors.BLOCK);

			CONFIGURATION_1 = builder.build();
		}

		@Override
		public List<INodeConfiguration> getConfigurations() {
			return ImmutableList.of(CONFIGURATION_1);
		}

		@Override
		public RuneProjectile create(INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneProjectile(this, composition, configuration);
		}

		@Override
		protected void activate(RuneProjectile state, RuneExecutionContext context, INodeIO io) {
			if (state.getConfiguration() == CONFIGURATION_1) {

				Entity thrower = IN_ENTITY.get(io);

				if(thrower instanceof EntityLivingBase) {
					EntitySnowball projectile = new EntitySnowball(context.getUser().getWorld(), (EntityLivingBase) thrower);

					projectile.shoot((EntityLivingBase) thrower, thrower.rotationPitch, thrower.rotationYaw, 0.0F, 1.5F, 1.0F);
					
					context.getUser().getWorld().spawnEntity(projectile);

					io.schedule(scheduler -> {
						if(projectile.isEntityAlive()) {
							scheduler.sleep(1);
						} else {
							OUT_POSITION.set(io, new BlockPos(projectile));
							scheduler.terminate();
						}
					});
				}


			}			
		}
	}

	private RuneProjectile(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
