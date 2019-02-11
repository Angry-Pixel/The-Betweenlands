package thebetweenlands.common.herblore.rune;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneUser;
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

		private static final InputPort<?> IN_ENTITY;
		private static final OutputPort<BlockPos> OUT_POSITION;

		static {
			PortNodeConfiguration.Builder builder = PortNodeConfiguration.builder();

			IN_ENTITY = builder.in(RuneMarkDescriptors.ENTITY, Entity.class, IRuneUser.class);
			OUT_POSITION = builder.out(RuneMarkDescriptors.BLOCK, BlockPos.class);

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

				IN_ENTITY.run(io, Entity.class, entity -> {
					run(io, context.getUser().getWorld(), entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null, entity.getPositionEyes(1), entity.getLookVec());
				});
				IN_ENTITY.run(io, IRuneUser.class, user -> {
					run(io, context.getUser().getWorld(), user.getEntity() instanceof EntityLivingBase ? (EntityLivingBase) user.getEntity() : null, user.getEyesPosition(), user.getLook());
				});
			}
		}

		private void run(INodeIO io, World world, EntityLivingBase thrower, Vec3d pos, Vec3d dir) {
			EntitySnowball projectile;
			if(thrower != null) {
				projectile = new EntitySnowball(world, thrower);
				projectile.setPosition(pos.x, pos.y, pos.z);
			} else {
				projectile = new EntitySnowball(world, pos.x, pos.y, pos.z);
			}

			projectile.shoot(dir.x, dir.y, dir.z, 1, 0);

			world.spawnEntity(projectile);

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

	private RuneProjectile(Blueprint blueprint, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
		super(blueprint, composition, configuration);
	}
}
