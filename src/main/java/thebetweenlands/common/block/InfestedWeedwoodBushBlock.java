package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class InfestedWeedwoodBushBlock extends WeedwoodBushBlock {

	private final int stage;

	public InfestedWeedwoodBushBlock(int stage, Properties properties) {
		super(properties);
		this.stage = stage;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (level.isClientSide())
			return;
		switch (this.stage) {
			case 0 -> level.setBlock(pos, BlockRegistry.MOTH_INFESTED_WEEDWOOD_BUSH.get().defaultBlockState(), 2);
			case 1 -> level.setBlock(pos, BlockRegistry.GRUB_INFESTED_WEEDWOOD_BUSH.get().defaultBlockState(), 2);
			case 2 -> level.setBlock(pos, BlockRegistry.SILK_COCOONED_WEEDWOOD_BUSH.get().defaultBlockState(), 2);
			case 3 -> level.setBlock(pos, BlockRegistry.DECAY_INFESTED_WEEDWOOD_BUSH.get().defaultBlockState(), 2);
			case 4 -> {
				level.setBlock(pos, BlockRegistry.DEAD_WEEDWOOD_BUSH.get().defaultBlockState(), 2);
				//TODO
//				Swarm swarm = new Swarm(level);
//				swarm.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
//				level.addFreshEntity(swarm);
			}
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!level.isClientSide() && stack.canPerformAction(ItemAbilities.SHEARS_HARVEST)) {
			if (state.is(BlockRegistry.GRUB_INFESTED_WEEDWOOD_BUSH) || state.is(BlockRegistry.SILK_COCOONED_WEEDWOOD_BUSH)) {
				ItemStack harvest = new ItemStack(state.is(BlockRegistry.GRUB_INFESTED_WEEDWOOD_BUSH) ? ItemRegistry.SILK_GRUB.get() : ItemRegistry.SILK_COCOON.get(), 2);
				ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, harvest);
				item.setDeltaMovement(Vec3.ZERO);
				level.addFreshEntity(item);
				level.setBlock(pos, BlockRegistry.WEEDWOOD_BUSH.get().defaultBlockState(), 2);
				stack.consume(1, player);
				return ItemInteractionResult.SUCCESS;
			}
		}

		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		double px = pos.getX() + 0.5D;
		double py = pos.getY() + 1.2D;
		double pz = pos.getZ() + 0.5D;
		if (random.nextInt(10) == 0) {
//			switch (this.stage) {
//				case 0 -> BLParticles.SULFUR_TORCH.spawn(level, px, py, pz, ParticleArgs.get().withColor(0.6f, 0.35f, 0.8f, 0.28f));
//				case 1 -> BLParticles.SILK_MOTH.spawn(level, px, py, pz);
//				case 2, 3, 4 -> BLParticles.DIRT_DECAY.spawn(level, px, py, pz);
//			}
		}
	}
}
