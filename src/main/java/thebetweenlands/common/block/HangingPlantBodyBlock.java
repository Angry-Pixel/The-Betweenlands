package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HangingPlantBodyBlock extends GrowingPlantBodyBlock {

	private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
	private final Holder<Block> head;

	public HangingPlantBodyBlock(Holder<Block> head, Properties properties) {
		super(properties, Direction.DOWN, SHAPE, false);
		this.head = head;
	}

	@Override
	protected MapCodec<? extends GrowingPlantBodyBlock> codec() {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected BlockState updateHeadAfterConvertedFromBody(BlockState head, BlockState body) {
		return body.setValue(HangingPlantBlock.CAN_GROW, head.getValue(HangingPlantBlock.CAN_GROW));
	}

	@Override
	protected GrowingPlantHeadBlock getHeadBlock() {
		return (GrowingPlantHeadBlock) this.head.value();
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
}
