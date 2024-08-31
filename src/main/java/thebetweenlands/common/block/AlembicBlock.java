package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.AlembicBlockEntity;
import thebetweenlands.common.items.DentrothystVialItem;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class AlembicBlock extends HorizontalBaseEntityBlock {

	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

	public AlembicBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof AlembicBlockEntity alembic) {
			if (player.isCrouching())
				return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

			if (!stack.isEmpty()) {
				/*if (stack.is(ItemRegistry.BL_BUCKET_INFUSION)) {
					if (!alembic.isFull()) {
						alembic.addInfusion(level, stack);
						if (!player.isCreative()) {
							player.setItemInHand(hand, ItemBucketInfusion.getEmptyBucket(stack));
						}
					}
				} else*/ if (stack.getItem() instanceof DentrothystVialItem vial && alembic.hasFinished()) {
					ItemStack result = alembic.getElixir(level, pos, state, vial);
					ItemEntity itemEntity = player.drop(result, false);
					if (itemEntity != null) itemEntity.setNoPickUpDelay();
					stack.consume(1, player);
				}
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (level.getBlockEntity(pos) instanceof AlembicBlockEntity alembic) {
			if (alembic.isRunning()) {
				float xx = (float) pos.getX() + 0.5F;
				float yy = pos.getY() + 0.25F + random.nextFloat() * 0.5F;
				float zz = (float) pos.getZ() + 0.5F;
				float fixedOffset = 0.25F;
				float randomOffset = random.nextFloat() * 0.6F - 0.3F;
//				BLParticles.STEAM_PURIFIER.spawn(level, (double) (xx - fixedOffset), (double) yy + 0.250D, (double) (zz + randomOffset));
//				BLParticles.STEAM_PURIFIER.spawn(level, (double) (xx + fixedOffset), (double) yy + 0.250D, (double) (zz + randomOffset));
//				BLParticles.STEAM_PURIFIER.spawn(level, (double) (xx + randomOffset), (double) yy + 0.250D, (double) (zz - fixedOffset));
//				BLParticles.STEAM_PURIFIER.spawn(level, (double) (xx + randomOffset), (double) yy + 0.250D, (double) (zz + fixedOffset));
				float xOffs = state.getValue(FACING).getAxis() == Direction.Axis.X ? 0.375F : 0.6F;
				float zOffs = state.getValue(FACING).getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 0.375F : 0.6F;
//				BLParticles.FLAME.spawn(level, pos.getX() + xOffs + (random.nextFloat() - 0.5F) * 0.1F, pos.getY(), pos.getZ() + zOffs + (random.nextFloat() - 0.5F) * 0.1F, ParticleFactory.ParticleArgs.get().withMotion((random.nextFloat() - 0.5F) * 0.01F, 0.01F, 0F));
			}
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AlembicBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.ALEMBIC.get(), AlembicBlockEntity::tick);
	}
}
