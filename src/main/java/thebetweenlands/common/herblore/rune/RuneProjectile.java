package thebetweenlands.common.herblore.rune;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.IConfigurationLinkAccess;
import thebetweenlands.api.runechain.base.INodeComposition;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeIO;
import thebetweenlands.api.runechain.chain.IRuneExecutionContext;
import thebetweenlands.api.runechain.io.IGetter;
import thebetweenlands.api.runechain.io.ISetter;
import thebetweenlands.api.runechain.io.InputSerializers;
import thebetweenlands.api.runechain.io.types.IBlockTarget;
import thebetweenlands.api.runechain.io.types.IVectorTarget;
import thebetweenlands.api.runechain.io.types.RuneTokenDescriptors;
import thebetweenlands.api.runechain.io.types.StaticBlockTarget;
import thebetweenlands.api.runechain.modifier.AttachedRuneEffectModifier;
import thebetweenlands.api.runechain.modifier.RuneEffectModifier;
import thebetweenlands.api.runechain.modifier.Subject;
import thebetweenlands.api.runechain.rune.AbstractRune;
import thebetweenlands.api.runechain.rune.RuneConfiguration;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.entity.EntityRunicBeetleProjectile;
import thebetweenlands.common.entity.EntityRunicBeetleWalking;
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

		private static final IGetter<?> IN_ENTITY_1;
		private static final ISetter<IBlockTarget> OUT_POSITION_1;
		private static final ISetter<Entity> OUT_ENTITY_1;
		private static final ISetter<Collection<IBlockTarget>> OUT_TRAIL_1;

		public static final RuneConfiguration CONFIGURATION_2;

		private static final IGetter<?> IN_ENTITY_2;
		private static final IGetter<IVectorTarget> IN_RAY_2;
		private static final ISetter<IBlockTarget> OUT_POSITION_2;
		private static final ISetter<Entity> OUT_ENTITY_2;
		private static final ISetter<Collection<IBlockTarget>> OUT_TRAIL_2;

		public static final RuneConfiguration CONFIGURATION_3;

		private static final IGetter<IVectorTarget> IN_POSITION_3;
		private static final IGetter<IVectorTarget> IN_RAY_3;
		private static final ISetter<IBlockTarget> OUT_POSITION_3;
		private static final ISetter<Entity> OUT_ENTITY_3;
		private static final ISetter<Collection<IBlockTarget>> OUT_TRAIL_3;

		public static final RuneConfiguration CONFIGURATION_4;

		private static final IGetter<IBlockTarget> IN_BLOCK_4;
		private static final IGetter<IVectorTarget> IN_RAY_4;
		private static final ISetter<IBlockTarget> OUT_POSITION_4;
		private static final ISetter<Entity> OUT_ENTITY_4;
		private static final ISetter<Collection<IBlockTarget>> OUT_TRAIL_4;

		public static final RuneConfiguration CONFIGURATION_5;

		private static final IGetter<IVectorTarget> IN_START_POSITION_5;
		private static final IGetter<IVectorTarget> IN_TARGET_POSITION_5;
		private static final ISetter<IBlockTarget> OUT_POSITION_5;
		private static final ISetter<Entity> OUT_ENTITY_5;
		private static final ISetter<Collection<IBlockTarget>> OUT_TRAIL_5;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			IN_ENTITY_1 = builder.in(RuneTokenDescriptors.ENTITY).type(Entity.class, IRuneChainUser.class).serializer(InputSerializers.USER).getter();
			OUT_POSITION_1 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).setter();
			OUT_ENTITY_1 = builder.out(RuneTokenDescriptors.ENTITY).type(Entity.class).setter();
			OUT_TRAIL_1 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();

			CONFIGURATION_1 = builder.build();

			IN_ENTITY_2 = builder.in(RuneTokenDescriptors.ENTITY).type(Entity.class, IRuneChainUser.class).serializer(InputSerializers.USER).getter();
			IN_RAY_2 = builder.in(RuneTokenDescriptors.DIRECTION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			OUT_POSITION_2 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).setter();
			OUT_ENTITY_2 = builder.out(RuneTokenDescriptors.ENTITY).type(Entity.class).setter();
			OUT_TRAIL_2 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();

			CONFIGURATION_2 = builder.build();

			IN_POSITION_3 = builder.in(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			IN_RAY_3 = builder.in(RuneTokenDescriptors.DIRECTION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			OUT_POSITION_3 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).setter();
			OUT_ENTITY_3 = builder.out(RuneTokenDescriptors.ENTITY).type(Entity.class).setter();
			OUT_TRAIL_3 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();

			CONFIGURATION_3 = builder.build();

			IN_BLOCK_4 = builder.in(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).serializer(InputSerializers.BLOCK).getter();
			IN_RAY_4 = builder.in(RuneTokenDescriptors.DIRECTION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			OUT_POSITION_4 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).setter();
			OUT_ENTITY_4 = builder.out(RuneTokenDescriptors.ENTITY).type(Entity.class).setter();
			OUT_TRAIL_4 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();

			CONFIGURATION_4 = builder.build();

			IN_START_POSITION_5 = builder.in(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			IN_TARGET_POSITION_5 = builder.in(RuneTokenDescriptors.POSITION).type(IVectorTarget.class).serializer(InputSerializers.VECTOR).getter();
			OUT_POSITION_5 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).setter();
			OUT_ENTITY_5 = builder.out(RuneTokenDescriptors.ENTITY).type(Entity.class).setter();
			OUT_TRAIL_5 = builder.out(RuneTokenDescriptors.BLOCK).type(IBlockTarget.class).collection().setter();

			CONFIGURATION_5 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2, CONFIGURATION_3, CONFIGURATION_4, CONFIGURATION_5);
		}

		@Override
		public RuneProjectile create(int index, INodeComposition<IRuneExecutionContext> composition, INodeConfiguration configuration) {
			return new RuneProjectile(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected Subject activate(RuneProjectile state, IRuneExecutionContext context, INodeIO io) {
			Entity projectile = null;

			if(state.getConfiguration() == CONFIGURATION_1) {
				projectile = IN_ENTITY_1.run(io, Entity.class, entity -> {
					return spawnProjectile(io, context.getUser().getWorld(), entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null, entity.getPositionEyes(1), entity.getLookVec(), OUT_POSITION_1, OUT_ENTITY_1, OUT_TRAIL_1);
				}, projectile);
				projectile = IN_ENTITY_1.run(io, IRuneChainUser.class, user -> {
					return spawnProjectile(io, context.getUser().getWorld(), user.getEntity() instanceof EntityLivingBase ? (EntityLivingBase) user.getEntity() : null, user.getEyesPosition(), user.getLook(), OUT_POSITION_1, OUT_ENTITY_1, OUT_TRAIL_1);
				}, projectile);
			} else if(state.getConfiguration() == CONFIGURATION_2) {
				projectile = IN_ENTITY_2.run(io, Entity.class, entity -> {
					return spawnProjectile(io, context.getUser().getWorld(), entity instanceof EntityLivingBase ? (EntityLivingBase) entity : null, entity.getPositionEyes(1), IN_RAY_2.get(io).vec(), OUT_POSITION_2, OUT_ENTITY_2, OUT_TRAIL_2);
				}, projectile);
				projectile = IN_ENTITY_2.run(io, IRuneChainUser.class, user -> {
					return spawnProjectile(io, context.getUser().getWorld(), user.getEntity() instanceof EntityLivingBase ? (EntityLivingBase) user.getEntity() : null, user.getEyesPosition(), IN_RAY_2.get(io).vec(), OUT_POSITION_2, OUT_ENTITY_2, OUT_TRAIL_2);
				}, projectile);
			} else if(state.getConfiguration() == CONFIGURATION_3) {
				projectile = spawnProjectile(io, context.getUser().getWorld(), null, IN_POSITION_3.get(io).vec(), IN_RAY_3.get(io).vec(), OUT_POSITION_3, OUT_ENTITY_3, OUT_TRAIL_3);
			} else if(state.getConfiguration() == CONFIGURATION_4) {
				BlockPos block = IN_BLOCK_4.get(io).block();
				projectile = spawnProjectile(io, context.getUser().getWorld(), null, new Vec3d(block.getX() + 0.5f, block.getY() + 0.5f, block.getZ() + 0.5f), IN_RAY_4.get(io).vec(), OUT_POSITION_4, OUT_ENTITY_4, OUT_TRAIL_4);
			} else if(state.getConfiguration() == CONFIGURATION_5) {
				Vec3d start = IN_START_POSITION_5.get(io).vec();
				IVectorTarget target = IN_TARGET_POSITION_5.get(io);
				projectile = spawnWalking(io, context.getUser().getWorld(), null, start, target, OUT_POSITION_4, OUT_ENTITY_4, OUT_TRAIL_4);
			}

			if(projectile != null) {
				return new Subject(projectile);
			}

			return null;
		}

		private Entity spawnProjectile(INodeIO io, World world, EntityLivingBase thrower, Vec3d pos, Vec3d dir, ISetter<IBlockTarget> outPos, ISetter<Entity> outEntity, ISetter<Collection<IBlockTarget>> outTrail) {
			EntityRunicBeetleProjectile projectile;
			if(thrower != null) {
				projectile = new EntityRunicBeetleProjectile(world, thrower);
				projectile.setPosition(pos.x, pos.y, pos.z);
			} else {
				projectile = new EntityRunicBeetleProjectile(world, pos.x, pos.y, pos.z);
			}

			projectile.shoot(dir.x, dir.y, dir.z, 1, 0);

			world.spawnEntity(projectile);

			io.schedule(scheduler -> {
				if(projectile.isEntityAlive()) {
					scheduler.sleep(1);
				} else {
					outPos.set(io, new StaticBlockTarget(projectile.getHitBlock()));
					outEntity.set(io, projectile.getHitEntity());
					outTrail.set(io, projectile.getTrail());
					scheduler.terminate();
				}
			});

			return projectile;
		}

		private Entity spawnWalking(INodeIO io, World world, EntityLivingBase thrower, Vec3d pos, IVectorTarget target, ISetter<IBlockTarget> outPos, ISetter<Entity> outEntity, ISetter<Collection<IBlockTarget>> outTrail) {
			EntityRunicBeetleWalking beetle = new EntityRunicBeetleWalking(world);
			beetle.setLocationAndAngles(pos.x, pos.y, pos.z, 0, 0);

			beetle.setTarget(target);

			world.spawnEntity(beetle);
			
			io.schedule(scheduler -> {
				if(beetle.isEntityAlive()) {
					scheduler.sleep(1);
				} else {
					scheduler.terminate();
				}
			});

			return beetle;
		}

		@Override
		protected RuneEffectModifier createRuneEffectModifier(RuneProjectile state) {
			return new AttachedRuneEffectModifier(true);
		}
	}

	private RuneProjectile(Blueprint blueprint, int index, INodeComposition<IRuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
