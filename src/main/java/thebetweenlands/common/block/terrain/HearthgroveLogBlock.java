package thebetweenlands.common.block.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.Tags;

import java.util.List;

public class HearthgroveLogBlock extends RotatedPillarBlock {
	public HearthgroveLogBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("block.thebetweenlands.good_fuel"));
		tooltip.add(Component.translatable("block.thebetweenlands.tarrable"));
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

		boolean hasWater = false;
		for (Direction offset : Direction.Plane.HORIZONTAL) {
			FluidState offsetState = level.getFluidState(checkPos.set(pos.getX() + offset.getStepX(), pos.getY(), pos.getZ() + offset.getStepZ()));
			FluidState offsetStateDown = level.getFluidState(checkPos.set(pos.getX() + offset.getStepX(), pos.getY() - 1, pos.getZ() + offset.getStepZ()));

			if (offsetStateDown.is(Tags.Fluids.WATER)) {
				if (random.nextInt(8) == 0) {
					for (int i = 0; i < 5; i++) {
						float x = pos.getX() + (offset.getStepX() > 0 ? 1.05F : offset.getStepX() == 0 ? random.nextFloat() : -0.05F);
						float y = pos.getY() - 0.1F;
						float z = pos.getZ() + (offset.getStepZ() > 0 ? 1.05F : offset.getStepZ() == 0 ? random.nextFloat() : -0.05F);

						//BLParticles.PURIFIER_STEAM.spawn(level, x, y, z);
					}
				}
			}

			if (offsetState.is(Tags.Fluids.WATER)) {
				if (random.nextInt(8) == 0) {
					for (int i = 0; i < 5; i++) {
						float x = pos.getX() + (offset.getStepX() > 0 ? 1.1F : offset.getStepX() == 0 ? random.nextFloat() : -0.1F);
						float y = pos.getY() + random.nextFloat();
						float z = pos.getZ() + (offset.getStepZ() > 0 ? 1.1F : offset.getStepZ() == 0 ? random.nextFloat() : -0.1F);

						level.addParticle(ParticleTypes.BUBBLE, x, y, z, 0, 0, 0);
					}
				}
				hasWater = true;
			}
		}
		if (!hasWater) {
			for (Direction offset : Direction.Plane.HORIZONTAL) {
				if (random.nextFloat() < 0.04F) {
					checkPos.set(pos.getX() + offset.getStepX(), pos.getY(), pos.getZ() + offset.getStepZ());
					BlockState offsetState = level.getBlockState(checkPos);
					if (!offsetState.isFaceSturdy(level, checkPos, offset.getOpposite())) {
						float x = pos.getX() + (offset.getStepX() > 0 ? 1.05F : offset.getStepX() == 0 ? random.nextFloat() : -0.05F);
						float y = pos.getY() + random.nextFloat();
						float z = pos.getZ() + (offset.getStepZ() > 0 ? 1.05F : offset.getStepZ() == 0 ? random.nextFloat() : -0.05F);

//						switch(random.nextInt(3)) {
//							default:
//							case 0:
//								BLParticles.EMBER_1.spawn(level, x, y, z);
//								break;
//							case 1:
//								BLParticles.EMBER_2.spawn(level, x, y, z);
//								break;
//							case 2:
//								BLParticles.EMBER_3.spawn(level, x, y, z);
//								break;
//						}
					}
				}
			}
		}
	}
}
