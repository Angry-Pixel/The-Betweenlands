package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.block.entity.DruidAltarBlockEntity;
import thebetweenlands.common.block.entity.MortarBlockEntity;
import thebetweenlands.common.items.LifeCrystalItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class MortarBlock extends HorizontalBaseEntityBlock {

	public static final VoxelShape BASE_SHAPE = Shapes.or(
		Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D),
		Block.box(3.0D, 12.0D, 3.0D, 13.0D, 16.0D, 13.0D)
	);
	public static final VoxelShape INSIDE_SHAPE = Block.box(5.0D, 10.0D, 5.0D, 11.0D, 16.0D, 11.0D);
	public static final VoxelShape SHAPE = Shapes.join(BASE_SHAPE, INSIDE_SHAPE, BooleanOp.ONLY_FIRST);

	public MortarBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
			mortar.manualGrinding = true;
			level.sendBlockUpdated(pos, state, state, 3);
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (level.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
			if (stack.is(ItemRegistry.PESTLE) && mortar.getItem(1).isEmpty()) {
				mortar.setItem(1, stack.copyWithCount(1));
				level.sendBlockUpdated(pos, state, state, 2);
				stack.shrink(1);
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			} else if (stack.getItem() instanceof LifeCrystalItem && mortar.getItem(3).isEmpty()) {
				mortar.setItem(3, stack.copyWithCount(1));
				level.sendBlockUpdated(pos, state, state, 2);
				stack.shrink(1);
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof MortarBlockEntity mortar) {
				player.openMenu(mortar);
			}
			return InteractionResult.CONSUME;
		}
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		MortarBlockEntity tile = (MortarBlockEntity) level.getBlockEntity(pos);
		if (tile.progress > 0 && tile.progress < 84 && random.nextInt(3) == 0) {
			float f = pos.getX() + 0.5F;
			float f1 = pos.getY() + 1.1F + random.nextFloat() * 6.0F / 16.0F;
			float f2 = pos.getZ() + 0.5F;
			level.addParticle(ParticleTypes.HAPPY_VILLAGER, f, f1, f2, 0.0D, 0.0D, 0.0D);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MortarBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.MORTAR.get(), MortarBlockEntity::tick);
	}
}
