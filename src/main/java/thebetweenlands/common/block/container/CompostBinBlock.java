package thebetweenlands.common.block.container;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.neoforged.neoforge.items.ItemHandlerHelper;
import thebetweenlands.common.block.entity.CompostBinBlockEntity;
import thebetweenlands.common.block.misc.HorizontalBaseEntityBlock;
import thebetweenlands.common.datamap.item.CompostableItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.ItemRegistry;

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
			// setBlockAndUpdate will call level.updateNeighbourForOutputSignal(...) for us
			level.setBlockAndUpdate(pos, state.cycle(OPEN));
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (state.getValue(OPEN)) {
			if (level.getBlockEntity(pos) instanceof CompostBinBlockEntity bin) {
				if (bin.getCompostedAmount() > 0) {
					if (bin.removeCompost(CompostBinBlockEntity.COMPOST_PER_ITEM)) {
						ItemStack compostStack = ItemRegistry.COMPOST.toStack();
						ItemHandlerHelper.giveItemToPlayer(player, compostStack);
						bin.setChanged();
						return ItemInteractionResult.SUCCESS;
					}
				}

				if (!stack.isEmpty()) {
					CompostableItem data = stack.getItemHolder().getData(DataMapRegistry.COMPOSTABLE);
					if (data != null) {
						int amount = data.amount();
						int time = data.time();
						switch (bin.addItemToBin(stack, amount, time, true)) {
							case CompostBinBlockEntity.CompostResult.ADDED:
								bin.addItemToBin(stack, amount, time, false);
								stack.consume(1, player);
								return ItemInteractionResult.SUCCESS;
							case CompostBinBlockEntity.CompostResult.FULL:
							default:
								player.displayClientMessage(Component.translatable("block.thebetweenlands.compost_bin.full"), true);
								return ItemInteractionResult.CONSUME;
						}
					} else {
						player.displayClientMessage(Component.translatable("block.thebetweenlands.compost_bin.not_compostable"), true);
						return ItemInteractionResult.CONSUME;
					}
				}
			}
		}
		
		if(stack.isEmpty() && player.isCreative() && player.isCrouching()) {
			level.setBlockAndUpdate(pos, state.cycle(OPEN));
			return ItemInteractionResult.SUCCESS;
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
	
	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(state.getValue(OPEN)) {
			int itemFullness = AbstractContainerMenu.getRedstoneSignalFromBlockEntity(blockEntity);
			if(blockEntity instanceof CompostBinBlockEntity compostBin) {
				float compostValue = (float)compostBin.getTotalCompostAmount() / (float)compostBin.getMaximimumCompostAmount();
				int compostFullness = Mth.lerpDiscrete(compostValue, 0, 15);
				return Math.max(itemFullness, compostFullness);
			}
			return itemFullness;
		} else if(blockEntity instanceof CompostBinBlockEntity compostBin) {
			float compostValue = (float)compostBin.getCompostedAmount() / (float)compostBin.getTotalCompostAmount();
			return Mth.lerpDiscrete(compostValue, 0, 15);
		}
		return super.getAnalogOutputSignal(state, level, pos);
	}
}
