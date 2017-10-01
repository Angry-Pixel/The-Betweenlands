package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.tile.spawner.MobSpawnerLogicBetweenlands;
import thebetweenlands.common.tile.spawner.TileEntityTarBeastSpawner;

public class BlockTarBeastSpawner extends BlockMobSpawner {
	public BlockTarBeastSpawner() {
		this.disableStats();
		this.setHardness(5.0F);
		this.setSoundType(SoundType.STONE);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityTarBeastSpawner tile = new TileEntityTarBeastSpawner();
		MobSpawnerLogicBetweenlands spawnerLogic = tile.getSpawnerLogic();
		spawnerLogic.setNextEntity("thebetweenlands:tar_beast");
		spawnerLogic.setParticles(false);
		spawnerLogic.setMaxEntities(1);
		spawnerLogic.setCheckRange(16.0D);
		spawnerLogic.setDelayRange(1400, 2000);
		return tile;
	}
}