package thebetweenlands.blocks.ores;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockGenericOre extends Block {
	private String type;
	private ItemStack oreDrops;
	private Random rand = new Random();

	private int minDropAmount = 1, maxDropAmount = 1;
	private int minXP = 0, maxXP = 0;

	public BlockGenericOre(String blockName, ItemStack oreDrops) {
		super(Material.rock);
		setStepSound(soundTypeStone);
		setHardness(3.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.blocks);
		type = blockName;
		this.oreDrops = oreDrops;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	public BlockGenericOre setDropsAmounts(int min, int max) {
		this.minDropAmount = min;
		this.maxDropAmount = max;
		return this;
	}
	
	public BlockGenericOre setXP(int min, int max) {
		this.minXP = min;
		this.maxXP = max;
		return this;
	}

	private ItemStack getOreDropped() {
		return this.oreDrops != null ? this.oreDrops.copy() : this.oreDrops;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();

		if(oreDrops != null)
			for(int dropFortune = 0; dropFortune < fortune + world.rand.nextInt(this.maxDropAmount - this.minDropAmount + 1) + this.minDropAmount; dropFortune++)
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
		int xpAmount = MathHelper.getRandomIntegerInRange(rand, this.minXP, this.maxXP);
		return xpAmount;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		double pixel = 0.0625D;
		if(rand.nextInt(3) == 0) {
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

				if (particleX < x || particleX > x + 1 || particleY < y || particleY > y + 1 || particleZ < z || particleZ > z + 1) {
					this.spawnParticle(world, particleX, particleY, particleZ);
				}
			}
		}
	}

	public void spawnParticle(World world, double x, double y, double z) { }
}
