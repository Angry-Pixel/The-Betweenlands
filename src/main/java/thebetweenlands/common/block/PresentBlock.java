package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.PresentBlockEntity;
import thebetweenlands.common.registries.LootTableRegistry;

import javax.annotation.Nullable;

public class PresentBlock extends BaseEntityBlock {

	public static final VoxelShape AABB = Shapes.or(
		Block.box(2.0D, 0.0D, 2.0D, 14.0D, 9.0D, 14.0D),
		Block.box(1.0D, 9.0D, 1.0D, 15.0D, 13.0D, 15.0D));
	private final DyeColor color;

	public PresentBlock(DyeColor color, Properties properties) {
		super(properties);
		this.color  = color;
	}

	public DyeColor getColor() {
		return this.color;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return null;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return AABB;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!level.isClientSide()) {
			BlockEntity tile = level.getBlockEntity(pos);
			if (tile instanceof PresentBlockEntity present) {
				present.setLootTable(LootTableRegistry.PRESENT, level.getRandom().nextLong());
				present.setChanged();
			}
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, moving);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (random.nextInt(20) == 0 && !level.hasNearbyAlivePlayer(pos.getX(), pos.getY(), pos.getZ(), 32.0D)) {
			level.removeBlock(pos, false);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PresentBlockEntity(pos, state);
	}
}
