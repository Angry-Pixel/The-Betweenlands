package thebetweenlands.common.block.container;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.ItemCageBlockEntity;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class ItemCageBlock extends BaseEntityBlock implements SwampWaterLoggable {

	public static final MapCodec<ItemCageBlock> CODEC = simpleCodec(ItemCageBlock::new);
	public static final VoxelShape SHAPE = Shapes.or(
		Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D),
		Block.box(2.0D, 2.0D, 2.0D, 14.0D, 4.0D, 14.0D),
		Block.box(0.5D, 8.5D, 0.5D, 15.5D, 10.5D, 15.5D),
		Block.box(2.5D, 10.5D, 2.5D, 13.5D, 12.5D, 13.5D),
		Block.box(6.5D, 12.5D, 6.5D, 9.5D, 14.5D, 9.5D),
		Block.box(6.0D, 14.5D, 6.0D, 10.0D, 15.5D, 10.0D));

	public ItemCageBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATER_TYPE, WaterType.NONE));
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		ItemCageBlockEntity swordStone = (ItemCageBlockEntity) level.getBlockEntity(pos);
		if (swordStone != null && !swordStone.canBreak)
			return -1;
		return super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
		if (!level.isClientSide()) {
			ItemCageBlockEntity swordStone = (ItemCageBlockEntity) level.getBlockEntity(pos);
			if (swordStone != null) {
				//TODO
//				EnergySword energyBall = swordStone.isSwordEnergyBelow(level, pos, state);
//				if (energyBall != null) {
//					level.playSound(null, pos, SoundRegistry.FORTRESS_PUZZLE_CAGE_BREAK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
//					switch (swordStone.type) {
//						case 0:
//							energyBall.setSwordPart1Pos(energyBall.getSwordPart1Pos() - 0.05F);
//							break;
//						case 1:
//							energyBall.setSwordPart2Pos(energyBall.getSwordPart2Pos() - 0.05F);
//							break;
//						case 2:
//							energyBall.setSwordPart3Pos(energyBall.getSwordPart3Pos() - 0.05F);
//							break;
//						case 3:
//							energyBall.setSwordPart4Pos(energyBall.getSwordPart4Pos() - 0.05F);
//							break;
//					}
//				}
			}
		}
		super.destroy(level, pos, state);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemCageBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.ITEM_CAGE.get(), ItemCageBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(WATER_TYPE));
	}
}
