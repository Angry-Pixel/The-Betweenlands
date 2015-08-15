package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.BlockMobSpawner;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
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
		setBlockTextureName("thebetweenlands:betweenstone");
	}

	public static void setMob(World world, int x, int y, int z, String mobName) {
		TileEntity te = world.getTileEntity(x, y, z);
		if(te != null && te instanceof TileEntityBLSpawner) {
			MobSpawnerBaseLogic spawnerLogic = ((TileEntityBLSpawner)te).func_145881_a();
			String prevMob = spawnerLogic.getEntityNameToSpawn();
			spawnerLogic.setEntityName(mobName);
			//resets the rendered entity
			if(world.isRemote && prevMob != mobName) {
				NBTTagCompound nbt = new NBTTagCompound();
				spawnerLogic.writeToNBT(nbt);
				spawnerLogic.readFromNBT(nbt);
			}
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
