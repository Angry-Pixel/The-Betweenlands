package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.CompostBinBlockEntity;
import thebetweenlands.common.component.item.CompostData;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class CompostBinBlock extends HorizontalBaseEntityBlock {

	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	//due to wack lighting calculations this block cant be a full block
	public static final VoxelShape ALMOST_FULL = Block.box(0.001D, 0.0D, 0.001D, 15.999D, 16.0D, 15.999D);

	public CompostBinBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return ALMOST_FULL;
	}

	@Override
	protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
		return Shapes.block();
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide()) {
			level.setBlockAndUpdate(pos, state.cycle(OPEN));
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (state.getValue(OPEN)) {
			if (level.getBlockEntity(pos) instanceof CompostBinBlockEntity bin) {
				boolean interacted = false;

				if (bin.getCompostedAmount() > 0) {
					if (bin.removeCompost(level, pos, state, CompostBinBlockEntity.COMPOST_PER_ITEM)) {
						if (!player.getInventory().add(new ItemStack(ItemRegistry.COMPOST.get()))) {
							player.drop(new ItemStack(ItemRegistry.COMPOST.get()), false);
						}
						interacted = true;
					}
				}

				if (!interacted && !stack.isEmpty()) {
					CompostData data = stack.get(DataComponentRegistry.COMPOST);
					if (data != null) {
						int amount = data.compostLevel();
						int time = data.compostTime();
						switch (bin.addItemToBin(level, pos, state, stack, amount, time, true)) {
							case CompostBinBlockEntity.CompostResult.ADDED:
								bin.addItemToBin(level, pos, state, stack, amount, time, false);
								stack.consume(1, player);
								break;
							case CompostBinBlockEntity.CompostResult.FULL:
							default:
								player.displayClientMessage(Component.translatable("chat.compost.full"), true);
								break;
						}
					} else {
						player.displayClientMessage(Component.translatable("chat.compost.not.compostable"), true);
						return ItemInteractionResult.CONSUME;
					}
				}
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		Containers.dropContentsOnDestroy(state, newState, level, pos);
		super.onRemove(state, level, pos, newState, moving);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof CompostBinBlockEntity bin) {
			if (!state.getValue(OPEN) && !bin.isEmpty()) {
//				BLParticles.DIRT_DECAY.spawn(level, pos.getX() + 0.2F + random.nextFloat() * 0.62F, pos.getY() + random.nextFloat() * 0.75F, pos.getZ() + 0.2F + random.nextFloat() * 0.6F);
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CompostBinBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.COMPOST_BIN.get(), CompostBinBlockEntity::tick);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(OPEN));
	}
}
