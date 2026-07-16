package thebetweenlands.common.block.farming;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.registries.ItemRegistry;

public class MiddleFruitBushBlock extends DecayableCropBlock {

	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 5);
	private static final List<VoxelShape> SHAPES = List.of(
		Block.box(7, 0, 7, 9, 4, 9),
		Block.box(5, 0, 5, 11, 10, 11),
		Block.box(5, 0, 5, 11, 19, 11)
	);

	public MiddleFruitBushBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		int i = state.getValue(this.getAgeProperty());
		if (i >= this.getMaxAge()) {
			popResource(level, pos, new ItemStack(ItemRegistry.MIDDLE_FRUIT.get(), 1 + level.getRandom().nextInt(3)));
			BlockState blockstate = state.setValue(this.getAgeProperty(), 2);
			level.setBlock(pos, blockstate, 2);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, blockstate));
			this.harvestAndUpdateSoil(level, pos, 10);
			return InteractionResult.sidedSuccess(level.isClientSide());
		} else {
			return super.useWithoutItem(state, level, pos, player, hitResult);
		}
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(Math.min(2, state.getValue(this.getAgeProperty())));
	}

	@Override
	public int getMaxAge() {
		return 5;
	}

	@Override
	public int getMaxHeight() {
		return 1;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS;
	}
}
