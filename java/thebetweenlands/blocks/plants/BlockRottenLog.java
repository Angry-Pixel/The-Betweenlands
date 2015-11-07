package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.Random;

public class BlockRottenLog extends Block {
	//	public IIcon modelTexture1;
	public IIcon end;
	public IIcon top;
	public IIcon bottom;
	public IIcon sides;

	public BlockRottenLog() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setBlockName("thebetweenlands.rottenLog");
		setCreativeTab(ModCreativeTabs.plants);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		end = reg.registerIcon("thebetweenlands:rottenLog4");
		top = reg.registerIcon("thebetweenlands:rottenLog2");
		bottom = reg.registerIcon("thebetweenlands:rottenLog1");
		sides = reg.registerIcon("thebetweenlands:rottenLog3");
	}



	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ItemGeneric.createStack(EnumItemGeneric.ROTTEN_BARK).getItem();
	}

	@Override
	public int quantityDropped(Random rnd) {
		return 1;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isNormalCube() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isBlockNormalCube(){
		return false;
	}

	@Override
	public boolean shouldSideBeRendered (IBlockAccess iblockaccess, int x, int y, int z, int side) {
		Block block = iblockaccess.getBlock(x, y, z);
		return !(block instanceof BlockRottenLog);
	}



	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess access, int x, int y, int z, int side){
		int meta = access.getBlockMetadata(z, y, z);
		if(meta == 0){
			if(side == 0)
				return bottom;
			if(side == 1)
				return bottom;
			if(side == 2 || side == 3)
				return end;
			else
				return sides;
		} else if(meta == 1) {
			if (side == 0)
				return bottom;
			if (side == 1)
				return bottom;
			if (side == 4 || side == 5)
				return end;
			else
				return sides;
		} else if(meta == 2) {
			if (side == 0)
				return bottom;
			if (side == 1)
				return top;
			if (side == 2 || side == 3)
				return end;
			else
				return sides;
		} else if(meta == 3) {
			if (side == 0)
				return bottom;
			if (side == 1)
				return top;
			if (side == 4 || side == 5)
				return end;
			else
				return sides;
		}
		return bottom;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.rand.nextInt(2000) == 0) {
			BLParticle.MOTH.spawn(world, x, y, z);
		}
	}

}
