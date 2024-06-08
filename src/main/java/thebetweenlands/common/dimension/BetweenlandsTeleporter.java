package thebetweenlands.common.dimension;

import java.util.function.Function;

import net.minecraft.world.level.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.util.ITeleporter;
import thebetweenlands.common.blocks.BetweenlandsPortal;
import thebetweenlands.common.registries.BlockRegistry;

public class BetweenlandsTeleporter implements ITeleporter {
	public static BlockPos blockPos = BlockPos.ZERO;
	public static boolean InDimension = true;

	public BetweenlandsTeleporter(BlockPos blockpos, boolean insideDim) {
		blockPos = blockpos;
		InDimension = insideDim;
	}
	
	// Place entity in new dimension
	@Override
    public Entity placeEntity(Entity entity, ServerLevel currentLevel, ServerLevel destinationLevel, float yaw, Function<Boolean, Entity> repositionEntity) {
        entity = repositionEntity.apply(false);
        double y = 61;

        if (!InDimension) {
            y = blockPos.getY();
        }

        BlockPos destinationPos = new BlockPos(blockPos.getX(), y, blockPos.getZ());

        int tries = 0;
        while ((destinationLevel.getBlockState(destinationPos).getMaterial() != Material.AIR) &&
                !destinationLevel.getBlockState(destinationPos).canBeReplaced(Fluids.WATER) &&
                destinationLevel.getBlockState(destinationPos.above()).getMaterial() != Material.AIR &&
                !destinationLevel.getBlockState(destinationPos.above()).canBeReplaced(Fluids.WATER) && tries < 25) {
            destinationPos = destinationPos.above(2);
            tries++;
        }

        entity.setPos(destinationPos.getX(), destinationPos.getY(), destinationPos.getZ());

        if (InDimension) {
            boolean doSetBlock = true;
            for (BlockPos checkPos : BlockPos.betweenClosed(destinationPos.below(10).west(10), destinationPos.above(10).east(10))) {
                if (destinationLevel.getBlockState(checkPos).getBlock() instanceof BetweenlandsPortal) {
                    doSetBlock = false;
                    break;
                }
            }
            if (doSetBlock) {
            	destinationLevel.setBlockAndUpdate(destinationPos, BlockRegistry.PORTAL.get().defaultBlockState());
            }
        }

        return entity;
    }
}
