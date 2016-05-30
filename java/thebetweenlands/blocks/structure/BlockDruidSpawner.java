package thebetweenlands.blocks.structure;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.tileentities.spawner.MobSpawnerBaseLogicBL;
import thebetweenlands.tileentities.spawner.TileEntityBLSpawner;

public class BlockDruidSpawner extends BlockMobSpawner {
	public BlockDruidSpawner() {
		setHardness(10.0F);
		setHarvestLevel("pickaxe", 0);
		setBlockName("thebetweenlands.druidSpawner");
		setBlockTextureName("thebetweenlands:druidSpawner");
		setCreativeTab(BLCreativeTabs.blocks);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		TileEntityBLSpawner tile = new TileEntityBLSpawner();
		MobSpawnerBaseLogicBL spawnerLogic = tile.getSpawnerLogic();
		spawnerLogic.setEntityName("thebetweenlands.darkDruid");
		spawnerLogic.setMaxEntities(1);
		spawnerLogic.setCheckRange(24.0D);
		spawnerLogic.setDelay(140, 300);
		return tile;
	}
}
