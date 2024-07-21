package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BLLanternBlock extends LanternBlock {

	public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 7);
	protected static final VoxelShape AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 7.0D, 12.0D);
	protected static final VoxelShape HANGING_AABB = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);

	public BLLanternBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(ROTATION, 0).setValue(HANGING, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(HANGING) ? HANGING_AABB : AABB;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		if (state != null) {
			int rotation = Mth.floor(((context.getPlayer().getYRot() + 180.0F) * 8.0F / 360.0F) + 0.5D) & 7;
			if (context.getLevel().getBlockState(context.getClickedPos().above()).getBlock() instanceof RopeBlock) {
				rotation -= rotation % 2;
			}
			return state.setValue(ROTATION, rotation);
		}
		return null;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		double px = (double) pos.getX() + 0.5D;
		double py = (double) pos.getY() + 0.3D;
		double pz = (double) pos.getZ() + 0.5D;

		if (random.nextInt(20) == 0 && level.canSeeSky(pos)) {
//			if (random.nextBoolean()) {
//				BLParticles.MOTH.spawn(level, px, py, pz);
//			} else {
//				BLParticles.FLY.spawn(level, px, py, pz);
//			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(ROTATION));
	}
}
