package thebetweenlands.blocks.terrain;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockGenericOre extends Block {
	private String type;
	private EnumMaterialsBL oreDrops;
	private Random rand = new Random();
	
	public BlockGenericOre(String blockName, EnumMaterialsBL blockDrops, String toolClass, int level) {
		super(Material.rock);
		setStepSound(soundTypeStone);
		setHardness(3.0F);
		setResistance(5.0F);
		setHarvestLevel(toolClass, level);
		setCreativeTab(ModCreativeTabs.blocks);
		type = blockName;
		oreDrops = blockDrops;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	private ItemStack getOreDropped() {
		return ItemMaterialsBL.createStack(oreDrops);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		
		if(oreDrops != null)
			for(int dropFortune = 0; dropFortune < 1 + fortune; dropFortune++)
				drops.add(getOreDropped());
		else
			drops.add(new ItemStack(Item.getItemFromBlock(this)));
		
		return drops;
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getExpDrop(IBlockAccess world, int meta, int fortune) {
		int xpAmount = 0;

		if (type.equals("sulfurOre"))
			xpAmount = MathHelper.getRandomIntegerInRange(rand, 2, 5);
		
		else if (type.equals("valoniteOre"))
			xpAmount = MathHelper.getRandomIntegerInRange(rand, 3, 7);
		
		else if (type.equals("lifeCrystalOre"))
			xpAmount = MathHelper.getRandomIntegerInRange(rand, 3, 7);

		return xpAmount;

	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double pixel = 0.0625D;
		if(type.equals("octineOre") && rand.nextInt(3) == 0) {
			for (int l = 0; l < 5; l++) {
				double particleX = x + rand.nextFloat();
				double particleY = y + rand.nextFloat();
				double particleZ = z + rand.nextFloat();

				if (l == 0 && !world.getBlock(x, y + 1, z).isOpaqueCube())
					particleY = y + 1 + pixel;

				if (l == 1 && !world.getBlock(x, y - 1, z).isOpaqueCube())
					particleY = y - pixel;

				if (l == 2 && !world.getBlock(x, y, z + 1).isOpaqueCube())
					particleZ = z + 1 + pixel;

				if (l == 3 && !world.getBlock(x, y, z - 1).isOpaqueCube())
					particleZ = z - pixel;

				if (l == 4 && !world.getBlock(x + 1, y, z).isOpaqueCube())
					particleX = x + 1 + pixel;

				if (l == 5 && !world.getBlock(x - 1, y, z).isOpaqueCube())
					particleX = x - pixel;

				if (particleX < x || particleX > x + 1 || particleY < y || particleY > y + 1 || particleZ < z || particleZ > z + 1)
					TheBetweenlands.proxy.spawnCustomParticle("flame", world, particleX, particleY, particleZ, 0, 0, 0, 0);
			}
		}
	}
}
