package thebetweenlands.common.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MistBridgeBlock extends Block {

	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	public static final BooleanProperty SOLID = BooleanProperty.create("solid");
	private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION.entrySet().stream().filter(directionPropertyPair -> directionPropertyPair.getKey().getAxis().isHorizontal()).collect(Util.toMap());


	public MistBridgeBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(SOLID, true));
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(SOLID) ? Shapes.block() : Shapes.empty();
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState neighborUpdated, LevelAccessor accessor, BlockPos pos, BlockPos posNeighbor) {
		return dir.getAxis().isHorizontal() ? state.setValue(PROPERTY_BY_DIRECTION.get(dir), neighborUpdated.is(this)) : super.updateShape(state, dir, neighborUpdated, accessor, pos, posNeighbor);
	}

	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		if (player.isCreative() && !state.getValue(SOLID))
			super.playerDestroy(level, player, pos, state, blockEntity, tool);
		else if (!level.isClientSide())
			level.setBlockAndUpdate(pos, state.setValue(SOLID, false));
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (level.isEmptyBlock(pos.above())) {
			for(int i = 0; i < 3 + random.nextInt(5); i++) {
//				BatchedParticleRenderer.INSTANCE.addParticle(DefaultParticleBatches.TRANSLUCENT_GLOWING_NEAREST_NEIGHBOR, BLParticles.SMOOTH_SMOKE.create(level, pos.getX() + 0.5F, pos.getY() + 1F, pos.getZ() + 0.5F,
//					ParticleArgs.get()
//						.withMotion((random.nextFloat() - 0.5F) * 0.08F, random.nextFloat() * 0.01F + 0.01F, (random.nextFloat() - 0.5F) * 0.08F)
//						.withScale(1.0F + random.nextFloat() * 8.0F)
//						.withColor(1.0F, 1.0F, 1.0F, 0.05F)
//						.withData(80, true, 0.01F, true)));
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, SOLID);
	}
}
