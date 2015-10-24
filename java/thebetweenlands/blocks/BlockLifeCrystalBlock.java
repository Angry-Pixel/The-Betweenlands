package thebetweenlands.blocks;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemBlockGeneric;
import thebetweenlands.tileentities.TileEntityLifeCrystal;

public class BlockLifeCrystalBlock extends BlockContainer {

	public BlockLifeCrystalBlock() {
		super(Material.glass);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.lifeCrystalBlock");
		setHardness(1.0F);
		setBlockTextureName("thebetweenlands:betweenstone");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLifeCrystal();
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

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
		return null;
	}

	public void setLife(World world, int x, int y, int z, int life) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityLifeCrystal) {
			((TileEntityLifeCrystal)tile).setLife(life);
		}
	}

	public int getLife(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityLifeCrystal) {
			return ((TileEntityLifeCrystal)tile).getLife();
		}
		return -1;
	}
	
	public void decrLife(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if(tile instanceof TileEntityLifeCrystal) {
			((TileEntityLifeCrystal)tile).decrLife();
		}
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return BLItemRegistry.lifeCrystal;
    }
	
	//TODO: Somebody maybe improve this?
	//Workaround, not sure how to do it otherwise.
	//The tile entity is destroyed before the 'life' value can be retrieved when using getDrops
	private int tmpDamage = 0;
	
	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		int life = this.getLife(world, x, y, z);
		System.out.println(life);
		this.tmpDamage = 4 - (int)Math.floor(life / (float)TileEntityLifeCrystal.MAX_LIFE * 4.0F);
		return this.tmpDamage;
	}
	
	@Override
	public int damageDropped(int p_149692_1_) {
        return this.tmpDamage;
    }
}
