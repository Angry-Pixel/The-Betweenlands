package thebetweenlands.common.block.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.spawner.BetweenlandsBaseSpawner;
import thebetweenlands.common.block.entity.spawner.MobSpawnerBlockEntity;
import thebetweenlands.common.registries.EntityRegistry;

public class TarBeastSpawnerBlock extends MobSpawnerBlock {

	public TarBeastSpawnerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		MobSpawnerBlockEntity tile = new MobSpawnerBlockEntity(pos, state);
		BetweenlandsBaseSpawner spawnerLogic = tile.getSpawner();
		spawnerLogic.setNextEntityName(EntityRegistry.TAR_BEAST.get(), tile.getLevel(), RandomSource.create(), pos);
		spawnerLogic.setParticles(false);
		spawnerLogic.setMaxEntities(1);
		spawnerLogic.setCheckRange(16.0D);
		spawnerLogic.setDelayRange(1400, 2000);
		return tile;
	}
}
