package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class BetweenlandsPortal extends Block implements Portal {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	protected static final VoxelShape Y_AXIS_AABB = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
	protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

	public BetweenlandsPortal(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		//TODO search for existing portals in the world storage once implemented
		if (entity.canUsePortal(false)) {
			entity.setAsInsidePortal(this, entity.blockPosition());
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(AXIS)) {
			case Z -> Z_AXIS_AABB;
			case X -> X_AXIS_AABB;
			default -> Y_AXIS_AABB;
		};
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor accessor, BlockPos pos, BlockPos neighborPos) {
		Direction.Axis axis = direction.getAxis();
		Direction.Axis portalAxis = state.getValue(AXIS);
		boolean flag = portalAxis != axis && axis.isHorizontal();
		return !flag && !neighborState.is(this) && !(new PortalShape(accessor, pos, portalAxis)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, accessor, pos, neighborPos);
	}

	@Override
	public void destroy(LevelAccessor accessor, BlockPos pos, BlockState state) {
		//TODO remove from world storage once implemented
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
		return switch (rotation) {
			case COUNTERCLOCKWISE_90, CLOCKWISE_90 -> switch (state.getValue(AXIS)) {
				case Z -> state.setValue(AXIS, Direction.Axis.X);
				case X -> state.setValue(AXIS, Direction.Axis.Z);
				default -> state;
			};
			default -> state;
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		for (int i = 0; i < 4; i++) {
			double particleX = pos.getX() + random.nextFloat();
			double particleY = pos.getY() + random.nextFloat();
			double particleZ = pos.getZ() + random.nextFloat();
			double motionX;
			double motionY;
			double motionZ;
			float multi = (random.nextFloat() * 2.0F - 1.0F) / 4.0F;

			motionX = (random.nextFloat() - 0.5D) * 0.25D;
			motionY = (random.nextFloat() - 0.5D) * 0.25D;
			motionZ = (random.nextFloat() - 0.5D) * 0.25D;

			if (!level.getBlockState(pos.offset(-1, 0, 0)).is(this) && !level.getBlockState(pos.offset(1, 0, 0)).is(this)) {
				particleX = pos.getX() + 0.5D + 0.25D * multi;
				motionX = random.nextFloat() * 2.0F * multi;
			} else {
				particleZ = pos.getZ() + 0.5D + 0.25D * multi;
				motionZ = random.nextFloat() * 2.0F * multi;
			}

			level.addParticle(ParticleRegistry.PORTAL_EFFECT.get(), particleX, particleY, particleZ, motionX, motionY, motionZ);
		}

		// Play portal sound
		if (random.nextInt(20) == 0) {
			level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundRegistry.BETWEENLANDS_PORTAL.get(), SoundSource.BLOCKS, 0.3F, random.nextFloat() * 0.4F + 0.8F, true);
		}
	}

	@Override
	public int getPortalTransitionTime(ServerLevel level, Entity entity) {
		return entity instanceof Player player ? player.getAbilities().invulnerable ? 1 : 80 : 0;
	}

	@Nullable
	@Override
	public DimensionTransition getPortalDestination(ServerLevel level, Entity entity, BlockPos pos) {
		ResourceKey<Level> newDimension = level.dimension() != DimensionRegistries.DIMENSION_KEY ? DimensionRegistries.DIMENSION_KEY : Level.OVERWORLD;
		ServerLevel serverlevel = level.getServer().getLevel(newDimension);
		if (serverlevel == null) {
			return null;
		} else {
			WorldBorder worldborder = serverlevel.getWorldBorder();
			double d0 = DimensionType.getTeleportationScale(level.dimensionType(), serverlevel.dimensionType());
			BlockPos newPos = worldborder.clampToBounds(pos.getX() * d0, pos.getY(), pos.getZ() * d0);
			return this.createTransition(serverlevel, entity, newPos);
		}
	}

	//TODO move to nearest existing portal
	//otherwise, place down a new tree, mark it in storage, and safely place us in the middle of it
	public DimensionTransition createTransition(ServerLevel level, Entity entity, BlockPos pos) {
		return new DimensionTransition(
			level,
			Vec3.atCenterOf(pos),
			Vec3.ZERO,
			entity.getYRot(),
			entity.getXRot(),
			DimensionTransition.PLACE_PORTAL_TICKET
		);
	}
}
