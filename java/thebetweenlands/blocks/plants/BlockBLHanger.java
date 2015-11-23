package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.herblore.ItemGenericPlantDrop;
import thebetweenlands.items.herblore.ItemGenericPlantDrop.EnumItemPlantDrop;
import thebetweenlands.items.tools.ISickleHarvestable;
import thebetweenlands.items.tools.ISyrmoriteShearable;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockBLHanger extends BlockBush implements ISickleHarvestable, ISyrmoriteShearable {
	private String type;

	@SideOnly(Side.CLIENT)
	private IIcon spoopyIcon;

	public BlockBLHanger(String blockName) {
		super(Material.plants);
		setHardness(0.0F);
		type = blockName;
		setCreativeTab(ModCreativeTabs.plants);
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
		setStepSound(Block.soundTypeGrass);
		setTickRandomly(true);
		setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int yy = y - 1;
		if (world.isAirBlock(x, yy, z) && canBlockStay(world, x, yy, z) && rand.nextInt(8) == 0)
			world.setBlock(x, yy, z, this);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(int id, Random random, int fortune) {
		return null;
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.HANGER));
		return ret;
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		int meta = world.getBlockMetadata(x, y, z);
		ItemStack item = null;
		if (world.isAirBlock(x, y + 1, z)) {
			world.setBlockToAir(x, y, z);
		}
		canBlockStay(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		super.registerBlockIcons(reg);
		spoopyIcon = reg.registerIcon(this.getTextureName() + "Spoopy");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld)) {
			return spoopyIcon;
		}
		return super.getIcon(side, meta);
	}

	protected boolean isValidBlock(Block block) {
		return block.getMaterial().blocksMovement() || block == BLBlockRegistry.weedwoodLeaves || block instanceof BlockBLHanger;
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
