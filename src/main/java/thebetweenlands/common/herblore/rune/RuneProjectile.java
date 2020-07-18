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

public final class RuneProjectile extends AbstractRune<RuneProjectile> {

	public static final class Blueprint extends AbstractRune.Blueprint<RuneProjectile> {
		public Blueprint() {
			super(RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build());

			this.setRecursiveRuneEffectModifierCount(3);
		}

		public static final RuneConfiguration CONFIGURATION_1;

		private static final InputPort<?> IN_ENTITY_1;
		private static final OutputPort<BlockPos> OUT_POSITION_1;

		public static final RuneConfiguration CONFIGURATION_2;

		private static final InputPort<?> IN_ENTITY_2;
		private static final InputPort<Vec3d> IN_RAY_2;
		private static final OutputPort<BlockPos> OUT_POSITION_2;

		public static final RuneConfiguration CONFIGURATION_3;

		private static final InputPort<Vec3d> IN_POSITION_3;
		private static final InputPort<Vec3d> IN_RAY_3;
		private static final OutputPort<BlockPos> OUT_POSITION_3;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.builder();

			IN_ENTITY_1 = builder.in(RuneTokenDescriptors.ENTITY, InputSerializers.USER, Entity.class, IRuneChainUser.class);
			OUT_POSITION_1 = builder.out(RuneTokenDescriptors.BLOCK, BlockPos.class);

			CONFIGURATION_1 = builder.build();

			IN_ENTITY_2 = builder.in(RuneTokenDescriptors.ENTITY, InputSerializers.USER, Entity.class, IRuneChainUser.class);
			IN_RAY_2 = builder.in(RuneTokenDescriptors.DIRECTION, InputSerializers.VECTOR, Vec3d.class);
			OUT_POSITION_2 = builder.out(RuneTokenDescriptors.BLOCK, BlockPos.class);

			CONFIGURATION_2 = builder.build();

			IN_POSITION_3 = builder.in(RuneTokenDescriptors.POSITION, InputSerializers.VECTOR, Vec3d.class);
			IN_RAY_3 = builder.in(RuneTokenDescriptors.DIRECTION, InputSerializers.VECTOR, Vec3d.class);
			OUT_POSITION_3 = builder.out(RuneTokenDescriptors.BLOCK, BlockPos.class);

			CONFIGURATION_3 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations() {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3);
		}

		@Override
		public RuneProjectile create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneProjectile(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(RuneProjectile state, RuneExecutionContext context, INodeIO io) {
			EntitySnowball projectile = null;

			if(state.getConfiguration() == CONFIGURATION_1) {
				projectile = IN_ENTITY_1.run(io, Entity.class, entity -> {
					return run(io, context.getUser().getWorld(), entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null, entity.getPositionEyes(1), entity.getLookVec(), OUT_POSITION_1);
				}, projectile);
				projectile = IN_ENTITY_1.run(io, IRuneChainUser.class, user -> {
					return run(io, context.getUser().getWorld(), user.getEntity() instanceof EntityLivingBase ? (EntityLivingBase) user.getEntity() : null, user.getEyesPosition(), user.getLook(), OUT_POSITION_1);
				}, projectile);
			} else if(state.getConfiguration() == CONFIGURATION_2) {
				projectile = IN_ENTITY_2.run(io, Entity.class, entity -> {
					return run(io, context.getUser().getWorld(), entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null, entity.getPositionEyes(1), IN_RAY_2.get(io), OUT_POSITION_2);
				}, projectile);
				projectile = IN_ENTITY_2.run(io, IRuneChainUser.class, user -> {
					return run(io, context.getUser().getWorld(), user.getEntity() instanceof EntityLivingBase ? (EntityLivingBase) user.getEntity() : null, user.getEyesPosition(), IN_RAY_2.get(io), OUT_POSITION_2);
				}, projectile);
			} else if(state.getConfiguration() == CONFIGURATION_3) {
				projectile = run(io, context.getUser().getWorld(), null, IN_POSITION_3.get(io), IN_RAY_3.get(io), OUT_POSITION_3);
			}

			if(projectile != null) {
				return new RuneEffectModifier.Subject(null, null, projectile);
			}

			return null;
		}

		private EntitySnowball run(INodeIO io, World world, EntityLivingBase thrower, Vec3d pos, Vec3d dir, OutputPort<BlockPos> out) {
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
					out.set(io, new BlockPos(projectile));
					scheduler.terminate();
				}
			});

			return projectile;
		}
	}

	private RuneProjectile(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
