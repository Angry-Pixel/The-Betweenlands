package thebetweenlands.common.block.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import javax.annotation.Nullable;

public class DruidStoneBlock extends HorizontalDirectionalBlock {

	public static final MapCodec<DruidStoneBlock> CODEC = simpleCodec(DruidStoneBlock::new);

	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

	public DruidStoneBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(ACTIVE, false));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		double pixel = 0.625D;
		if (!state.getValue(ACTIVE) && random.nextInt(80) == 0) {
			for (Direction facing : Direction.values()) {
				BlockPos side = pos.relative(facing);
				if (!level.getBlockState(side).isSolid()) {
					double dx = random.nextFloat() - 0.5, dy = random.nextFloat() - 0.5, dz = random.nextFloat() - 0.5;
					int vx = facing.getStepX();
					int vy = facing.getStepY();
					int vz = facing.getStepZ();
					dx *= (1 - Math.abs(vx));
					dy *= (1 - Math.abs(vy));
					dz *= (1 - Math.abs(vz));
					double particleX = pos.getX() + 0.5 + dx + vx * pixel;
					double particleY = pos.getY() + 0.5 + dy + vy * pixel;
					double particleZ = pos.getZ() + 0.5 + dz + vz * pixel;
//					BLParticles.DRUID_CASTING_BIG.spawn(world, particleX, particleY, particleZ,
//						ParticleArgs.get()
//							.withMotion((rand.nextFloat() - rand.nextFloat()) * 0.1, 0, (rand.nextFloat() - rand.nextFloat()) * 0.1)
//							.withScale(rand.nextFloat() * 0.5F + 0.5F)
//							.withColor(0.8F + rand.nextFloat() * 0.2F, 0.8F + rand.nextFloat() * 0.2F, 0.8F, 1));
				}
			}
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ACTIVE);
	}
}
