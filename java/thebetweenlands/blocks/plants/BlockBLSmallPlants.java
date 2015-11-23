package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.tools.ISickleHarvestable;
import thebetweenlands.items.tools.ISyrmoriteShearable;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockBLSmallPlants extends BlockTallGrass implements ISickleHarvestable, ISyrmoriteShearable {
	protected final String name;

	private boolean harvestable = false;
	private ItemStack harvestItem = null;
	private boolean hasSpoopyTexture = false;

	@SideOnly(Side.CLIENT)
	private IIcon spoopyBlockIcon;

	public BlockBLSmallPlants(String type) {
		super();
		name = type;
		setHardness(0.0F);
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockName("thebetweenlands." + name);
	}

	public BlockBLSmallPlants setHarvestable(boolean harvestable) {
		this.harvestable = harvestable;
		return this;
	}

	public BlockBLSmallPlants setHarvestedItem(ItemStack harvestedItem) {
		this.harvestable = true;
		this.harvestItem = harvestedItem;
		return this;
	}

	public BlockBLSmallPlants setHasSpoopyTexture(boolean spoopyTexture) {
		this.hasSpoopyTexture = spoopyTexture;
		return this;
	}


	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		Block soil = world.getBlock(x, y - 1, z);
		return soil != null && soil.canSustainPlant(world, x, y - 1, z, ForgeDirection.UP, this) || soil != null && canPlaceBlockOn(soil);
	}

	@Override	
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass || block == BLBlockRegistry.deadGrass || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.sludgyDirt || block == BLBlockRegistry.mud;
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
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && this.hasSpoopyTexture) {
			return spoopyBlockIcon;
		}
		return blockIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("thebetweenlands:" + name);
		if(this.hasSpoopyTexture) {
			spoopyBlockIcon = iconRegister.registerIcon("thebetweenlands:" + name + "Spoopy");
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		return drops;
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return this.harvestable && this.harvestItem != null;
	}

	@Override
	public ArrayList<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> dropList = new ArrayList<ItemStack>();
		if(this.harvestItem != null) {
			dropList.add(new ItemStack(this.harvestItem.getItem(), this.harvestItem.stackSize, this.harvestItem.getItemDamage()));
		}
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