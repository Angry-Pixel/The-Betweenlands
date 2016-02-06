package thebetweenlands.blocks.structure;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.tileentities.TileEntityTarBeastSpawner;

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
		tile.func_145881_a().setEntityName("thebetweenlands.tarBeast");
		return tile;
	}
}