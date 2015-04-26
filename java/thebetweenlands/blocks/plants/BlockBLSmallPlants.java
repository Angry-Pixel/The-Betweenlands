package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBLSmallPlants extends BlockTallGrass {
	protected final String name;

	public BlockBLSmallPlants(String type) {
		super();
		name = type;
		setHardness(0.0F);
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockName("thebetweenlands." + name);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if (rand.nextInt(25) == 0) {
			int xx;
			int yy;
			int zz;
			xx = x + rand.nextInt(3) - 1;
			yy = y + rand.nextInt(2) - rand.nextInt(2);
			zz = z + rand.nextInt(3) - 1;
			if (world.isAirBlock(xx, yy, zz) && canBlockStay(world, xx, yy, zz)) {
				if ("nettle".equals(name) && rand.nextInt(3) == 0)
					world.setBlock(x, y, z, BLBlockRegistry.nettleFlowered);
				if ("nettleFlowered".equals(name))
					world.setBlock(xx, yy, zz, BLBlockRegistry.nettle);
			}
			if (rand.nextInt(40) == 0) {
				if ("nettleFlowered".equals(name))
					world.setBlock(x, y, z, BLBlockRegistry.nettle);
			}
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block soil = world.getBlock(x, y - 1, z);
		return soil != null && soil.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this) || soil != null && canPlaceBlockOn(soil);
	}

	@Override	
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass || block == BLBlockRegistry.deadGrass;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		if("nettle".equals(name) || "nettleFlowered".equals(name)) 
			entity.attackEntityFrom(DamageSource.cactus, 1);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if (!"cattail".equals(name) && !"swampPlant".equals(name) && !"softRush".equals(name) && !"arrowArum".equals(name) && !"buttonBush".equals(name) && !"marshHibiscus".equals(name) && !"pickerelWeed".equals(name) && !"swampTallGrass".equals(name)) ret.add(new ItemStack(this));
		return ret;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("thebetweenlands:" + name);
	}

}