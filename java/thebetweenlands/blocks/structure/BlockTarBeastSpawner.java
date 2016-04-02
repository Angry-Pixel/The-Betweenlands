package thebetweenlands.blocks.structure;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.tileentities.TileEntityTarBeastSpawner;
import thebetweenlands.tileentities.spawner.MobSpawnerBaseLogicBL;

public class BlockTarBeastSpawner extends BlockMobSpawner {

	public BlockTarBeastSpawner() {
		super();
		disableStats();
		setHardness(5.0F);
		setStepSound(soundTypeMetal);
		setHarvestLevel("pickaxe", 0);
		setBlockName("thebetweenlands.tarBeastSpawner");
		setBlockTextureName("thebetweenlands:solidTar");
		setCreativeTab(BLCreativeTabs.blocks);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityTarBeastSpawner tile = new TileEntityTarBeastSpawner();
		MobSpawnerBaseLogicBL spawnerLogic = tile.getSpawnerLogic();
		spawnerLogic.setEntityName("thebetweenlands.tarBeast");
		spawnerLogic.setHasParticles(false);
		spawnerLogic.setMaxEntities(1);
		spawnerLogic.setCheckRange(16.0D);
		spawnerLogic.setDelay(1400, 2000);
		return tile;
	}
}