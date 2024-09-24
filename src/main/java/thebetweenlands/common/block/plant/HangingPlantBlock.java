package thebetweenlands.common.block.plant;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HangingPlantBlock extends GrowingPlantHeadBlock {

	private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
	public static final BooleanProperty CAN_GROW = BooleanProperty.create("can_grow");
	private final Holder<Block> body;

	public HangingPlantBlock(Holder<Block> body, Properties properties) {
		super(properties, Direction.DOWN, SHAPE, false, 0.0625F);
		this.registerDefaultState(this.getStateDefinition().any().setValue(CAN_GROW, true));
		this.body = body;
	}

	@Override
	protected MapCodec<? extends GrowingPlantHeadBlock> codec() {
		return null;
	}

	@Override
	protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
		return 0;
	}

	@Override
	protected boolean canGrowInto(BlockState state) {
		return state.isAir();
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (this.canGrow(level, pos, state)) {
			super.randomTick(state, level, pos, random);
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (random.nextInt(40) == 0) {
			float dripRange = 0.5F;
			float px = random.nextFloat() - 0.5F;
			float py = random.nextFloat();
			float pz = random.nextFloat() - 0.5F;
			float u = Math.max(Math.abs(px), Math.abs(pz));
			px = px / u * dripRange + 0.5F;
			pz = pz / u * dripRange + 0.5F;
			level.addParticle(ParticleTypes.DRIPPING_WATER, pos.getX() + px, pos.getY() + py, pos.getZ() + pz, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(CAN_GROW));
	}

	public boolean canGrow(Level level, BlockPos pos, BlockState state) {
		return state.getValue(CAN_GROW);
	}

	@Override
	protected Block getBodyBlock() {
		return this.body.value();
	}
}
