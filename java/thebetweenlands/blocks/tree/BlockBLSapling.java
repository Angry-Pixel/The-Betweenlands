package thebetweenlands.blocks.tree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.terraingen.TerrainGen;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.world.feature.trees.WorldGenRubberTree;
import thebetweenlands.world.feature.trees.WorldGenSapTree;
import thebetweenlands.world.feature.trees.WorldGenWeedWoodTree;

import java.util.List;
import java.util.Random;

public class BlockBLSapling extends BlockSapling {
	
	private String type;
	@SideOnly(Side.CLIENT)
	private IIcon icon;

	public BlockBLSapling(String blockName) {
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		setBlockBounds(0.1F, 0.0F, 0.1F, 0.9F, 0.8F, 0.9F);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
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
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		blockIcon = reg.registerIcon(getTextureName());
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block soil = world.getBlock(x, y - 1, z);
		return soil != null && soil.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this) || soil != null && canPlaceBlockOn(soil);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (!world.isRemote) {
			super.updateTick(world, x, y, z, rand);

			if (rand.nextInt(13 - (world.getBlockLightValue(x, y + 1, z) >> 1)) == 0)
				growTree(world, x, y, z, rand);
		}
	}
	
	@Override	
    protected boolean canPlaceBlockOn(Block block) {
        return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass;
    }

	@Override
	public void func_149879_c(World world, int x, int y, int z, Random rand) {
	}

	@Override
	public void func_149878_d(World world, int x, int y, int z, Random rand) {
		growTree(world, x, y, z, rand);
	}

	@Override
	public void func_149853_b(World world, Random rand, int x, int y, int z) {
		growTree(world, x, y, z, rand);
	}

	private void growTree(World world, int x, int y, int z, Random rand) {
		if (!TerrainGen.saplingGrowTree(world, rand, x, y, z))
			return;

		WorldGenerator worldGen = null;

		if(type.equals("saplingWeedwood")) {
			worldGen = new WorldGenWeedWoodTree();
		}
		
		if(type.equals("saplingSapTree")) {
			worldGen = new WorldGenSapTree();
		}
		
		if(type.equals("saplingRubberTree")) {
			worldGen = new WorldGenRubberTree();
		}
		
		if(type.equals("saplingSpiritTree")) {
			System.out.println("Generate Spirit Tree");
		//worldGen = new WorldGenSpiritTree();
		}

		if (worldGen == null)
			return;

		world.setBlockToAir(x, y, z);
		if (!worldGen.generate(world, rand, x, y, z))
			world.setBlock(x, y, z, this);
	}

	public boolean isSameSapling(World world, int x, int y, int z, int meta) {
		return world.getBlock(x, y, z) == this;
	}
}