package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.herblore.ItemGenericPlantDrop;
import thebetweenlands.items.herblore.ItemGenericPlantDrop.EnumItemPlantDrop;
import thebetweenlands.items.tools.ISickleHarvestable;
import thebetweenlands.items.tools.ISyrmoriteShearable;

public class BlockCaveMoss extends BlockBush implements ISickleHarvestable, ISyrmoriteShearable {
	private IIcon lower;

	public BlockCaveMoss() {
		super(Material.plants);
		setTickRandomly(false);
		setHardness(0);
		setCreativeTab(BLCreativeTabs.plants);
		setBlockName("thebetweenlands.caveMoss");
		setBlockTextureName("thebetweenlands:caveMoss");
		setStepSound(Block.soundTypeGrass);
		setBlockBounds(0.25F, 0, 0.25F, 0.75F, 1, 0.75F);
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return isValidBlock(world.getBlock(x, y + 1, z)) && canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return isValidBlock(world.getBlock(x, y + 1, z));
	}

	@Override
	public int getRenderType() {
		return 6;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return random.nextInt(2);
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float sideHitX, float sideHitY, float sideHitZ, int meta) {
		return world.getBlock(x, y - 1, z) != this ? 1 : 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		if (world.isAirBlock(x, y + 1, z)) {
			world.setBlockToAir(x, y, z);
			dropBlockAsItem(world, x, y, z, 0, 0);
		} else {
			world.setBlockMetadataWithNotify(x, y, z, world.getBlock(x, y - 1, z) != this ? 1 : 0, 2);
		}
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (random.nextInt(40) == 0) {
			float dripRange = 0.5F;
			float px = random.nextFloat() - 0.5F;
			float py = random.nextFloat();
			float pz = random.nextFloat() - 0.5F;
			float u = Math.max(Math.abs(px), Math.abs(pz));
			px = px / u * dripRange + 0.5F;
			pz = pz / u * dripRange + 0.5F;
			BLParticle.CAVE_WATER_DRIP.spawn(world, x + px, y + py, z + pz);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		lower = iconRegister.registerIcon(textureName + "Lower");
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return metadata == 1 ? lower : blockIcon;
	}

	protected boolean isValidBlock(Block block) {
		return block == BLBlockRegistry.betweenstone || block == this;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		return drops;
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return item.getItem() == BLItemRegistry.sickle;
	}

	@Override
	public ArrayList<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> dropList = new ArrayList<ItemStack>();
		dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CAVE_MOSS));
		return dropList;
	}

	@Override
	public ItemStack getSyrmoriteShearableSpecialDrops(Block block, int x, int y, int z, int meta) {
		return null;
	}

	@Override
	public boolean isSyrmoriteShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}
}
