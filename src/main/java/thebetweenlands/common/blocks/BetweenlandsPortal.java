package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.dimension.BetweenlandsTeleporter;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.Random;

public class BetweenlandsPortal extends Block {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	protected static final int AABB_OFFSET = 2;
	protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

	// defalt nether portal code for positions and axis
	public BetweenlandsPortal(Properties p_49795_) {
		super(p_49795_);
		this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
	}

	public void entityInside(BlockState blockState, Level levelin, BlockPos blockPos, Entity entity) {

		if (!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {

			MinecraftServer server = levelin.getServer();

			if (server != null) {
				if (levelin.dimension() != DimensionRegistries.BETWEENLANDS_DIMENSION_KEY) {
					ServerLevel betweenlandsDim = server.getLevel(DimensionRegistries.BETWEENLANDS_DIMENSION_KEY);
					if (betweenlandsDim != null) {
						entity.changeDimension(betweenlandsDim, new BetweenlandsTeleporter(blockPos, true));
					}
				}
			}
		}

	}

	public VoxelShape getShape(BlockState p_54942_, BlockGetter p_54943_, BlockPos p_54944_, CollisionContext p_54945_) {
		switch ((Direction.Axis) p_54942_.getValue(AXIS)) {
			case Z:
				return Z_AXIS_AABB;
			case X:
			default:
				return X_AXIS_AABB;
		}
	}

	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState p_54928_, Direction p_54929_, BlockState p_54930_, LevelAccessor p_54931_, BlockPos p_54932_, BlockPos p_54933_) {
		Direction.Axis direction$axis = p_54929_.getAxis();
		Direction.Axis direction$axis1 = p_54928_.getValue(AXIS);
		boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
		return !flag && !p_54930_.is(this) && !(new PortalShape(p_54931_, p_54932_, direction$axis1)).isComplete() ? Blocks.AIR.defaultBlockState() : super.updateShape(p_54928_, p_54929_, p_54930_, p_54931_, p_54932_, p_54933_);
	}

	public BlockState rotate(BlockState p_54925_, Rotation p_54926_) {
		switch (p_54926_) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch ((Direction.Axis) p_54925_.getValue(AXIS)) {
					case Z:
						return p_54925_.setValue(AXIS, Direction.Axis.X);
					case X:
						return p_54925_.setValue(AXIS, Direction.Axis.Z);
					default:
						return p_54925_;
				}
			default:
				return p_54925_;
		}
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_54935_) {
		p_54935_.add(AXIS);
	}

	// Portal effects
	public void animateTick(BlockState p_55479_, Level p_55480_, BlockPos p_55481_, Random p_55482_) {

		// Spawn particles
		if (p_55482_.nextInt(3) == 0) {
			spawnParticles(p_55480_, p_55481_);
		}

		// Play portal sound
		if (p_55482_.nextInt(25) == 0) {
			p_55480_.playLocalSound(p_55481_.getX(), p_55481_.getY(), p_55481_.getZ(), SoundRegistry.BETWEENLANDS_PORTAL.get(), SoundSource.BLOCKS, 1, p_55482_.nextFloat(0.3f) + 0.85f, true);
		}
	}

	private static void spawnParticles(Level level, BlockPos blockpos) {
		Random random = level.random;

		Direction.Axis axis = level.getBlockState(blockpos).getValue(AXIS);

		// Get portal axis and spawn particles in block's rotation plane
		double x = 0;
		double y = 0;
		double z = 0;
		double deltax = 0;
		double deltay = 0;
		double deltaz = 0;

		switch (axis) {
			case X: {
				x = random.nextFloat(1.0f);
				y = random.nextFloat(1.0f);
				z = 0.5d;
				deltax = 0.125D - random.nextFloat(0.25f);
				deltay = 0.125D - random.nextFloat(0.25f);
				deltaz = 0.25D - random.nextFloat(0.5f);
				break;
			}
			case Z: {
				x = 0.5d;
				y = random.nextFloat(1.0f);
				z = random.nextFloat(1.0f);
				deltax = 0.25D - random.nextFloat(0.5f);
				deltay = 0.125D - random.nextFloat(0.25f);
				deltaz = 0.125D - random.nextFloat(0.25f);
				break;
			}
			default:
				break;
		}

		level.addParticle(ParticleRegistry.PORTAL_EFFECT.get(), (double) blockpos.getX() + x, (double) blockpos.getY() + y, (double) blockpos.getZ() + z, deltax, deltay, deltaz);
	}
}
