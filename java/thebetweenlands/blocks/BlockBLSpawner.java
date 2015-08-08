package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.tileentities.TileEntityBLSpawner;

public class BlockBLSpawner extends BlockMobSpawner {
	public BlockBLSpawner() {
		super();
		setHarvestLevel("pickaxe", 0);
		setBlockName("thebetweenlands.blSpawner");
		setCreativeTab(ModCreativeTabs.blocks);
	}

	public void setMob(World world, int x, int y, int z, String mobName) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityBLSpawner) {
			((TileEntityBLSpawner)te).func_145881_a().setEntityName(mobName);
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityBLSpawner();
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rnd) { }
}
