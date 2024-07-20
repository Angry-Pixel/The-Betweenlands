package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.AlembicBlockEntity;
import thebetweenlands.common.block.entity.ItemCageBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;

import javax.annotation.Nullable;

public class ItemCageBlock extends BaseEntityBlock {

	public static final MapCodec<ItemCageBlock> CODEC = simpleCodec(ItemCageBlock::new);

	public ItemCageBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
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

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
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
}
