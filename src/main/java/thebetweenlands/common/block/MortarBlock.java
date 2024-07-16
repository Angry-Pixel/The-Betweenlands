package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import thebetweenlands.common.block.entity.MortarBlockEntity;
import thebetweenlands.common.items.LifeCrystalItem;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;

public class MortarBlock extends HorizontalBaseEntityBlock {

	public MortarBlock(Properties properties) {
		super(properties);
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
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
			if (stack.is(ItemRegistry.PESTLE) && mortar.getItem(1).isEmpty()) {
				mortar.setItem(1, stack.copyWithCount(1));
				mortar.hasPestle = true;
				level.sendBlockUpdated(pos, state, state, 2);
				stack.shrink(1);
			} else if (stack.getItem() instanceof LifeCrystalItem && mortar.getItem(3).isEmpty()) {
				mortar.setItem(3, stack.copyWithCount(1));
				stack.shrink(1);
			} else {
				player.openMenu(mortar);
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof MortarBlockEntity mortar) {
			player.openMenu(mortar);
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
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
