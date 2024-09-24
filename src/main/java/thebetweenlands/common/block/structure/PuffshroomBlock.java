package thebetweenlands.common.block.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import thebetweenlands.common.block.entity.PuffshroomBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

import javax.annotation.Nullable;

public class PuffshroomBlock extends BaseEntityBlock {

	public static final MapCodec<PuffshroomBlock> CODEC = simpleCodec(PuffshroomBlock::new);

	public PuffshroomBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return player.getMainHandItem().canPerformAction(ItemAbilities.SHEARS_HARVEST) ? 1.0F : super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (this.tryHarvestWithShears(level, player, pos, state, level.getBlockEntity(pos), stack, true)) {
			return ItemInteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		this.tryHarvestWithShears(level, player, pos, state, level.getBlockEntity(pos), player.getMainHandItem(), false);
		return super.playerWillDestroy(level, pos, state, player);
	}

	protected boolean tryHarvestWithShears(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool, boolean effects) {
		if (!tool.isEmpty() && tool.canPerformAction(ItemAbilities.SHEARS_HARVEST)) {
			if (blockEntity instanceof PuffshroomBlockEntity puffshroom) {

				if (puffshroom.animation_1 >= 1) {
					if (level instanceof ServerLevel server) {
						LootTable lootTable = server.getServer().reloadableRegistries().getLootTable(LootTableRegistry.PUFFSHROOM);
						LootParams lootparams = new LootParams.Builder(server)
							.withParameter(LootContextParams.ORIGIN, pos.getCenter())
							.withParameter(LootContextParams.THIS_ENTITY, player)
							.withParameter(LootContextParams.BLOCK_ENTITY, puffshroom)
							.withParameter(LootContextParams.TOOL, tool)
							.create(LootContextParamSets.BLOCK);

						lootTable.getRandomItems(lootparams).forEach(stack ->
							level.addFreshEntity(new ItemEntity(level, pos.getX(), pos.getY() + 1, pos.getZ(), stack)));

						level.setBlockAndUpdate(pos, BlockRegistry.CRACKED_MUD_TILES.get().defaultBlockState());

						if (effects) {
							level.levelEvent(null, 2001, pos, Block.getId(state));
						}

						level.sendBlockUpdated(pos, state, state, 3);
					}

					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}

	@Override
	protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (!player.isCreative()) {
			return level.setBlockAndUpdate(pos, BlockRegistry.CRACKED_MUD_TILES.get().defaultBlockState());
		}
		return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PuffshroomBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.PUFFSHROOM.get(), PuffshroomBlockEntity::tick);
	}
}
