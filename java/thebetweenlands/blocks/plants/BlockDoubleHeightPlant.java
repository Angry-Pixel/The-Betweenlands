package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.tools.IHarvestable;
import thebetweenlands.items.tools.ISyrmoriteShearable;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockDoubleHeightPlant extends BlockDoublePlant implements IHarvestable, ISyrmoriteShearable {
	@SideOnly(Side.CLIENT)
	public IIcon topIcon, bottomIcon, spoopyTopIcon, spoopyBottomIcon;
	protected final String name;
	Random rnd = new Random();
	private boolean harvestable = false;
	private ItemStack harvestItem = null;
	private boolean hasSpoopyTexture = false;

	private int renderType = -1;

	public BlockDoubleHeightPlant(String name) {
		this(name, 1);
	}

	public BlockDoubleHeightPlant(String name, float width) {
		this.name = name;
		setCreativeTab(ModCreativeTabs.plants);
		setStepSound(Block.soundTypeGrass);
		this.setTickRandomly(true);
		float w = (1F - width) / 2F;
		setBlockBounds(w, 0, w, width + w, 1, width + w);
		setBlockName("thebetweenlands." + name.substring(0, 1).toLowerCase() + name.substring(1));
	}

	public BlockDoubleHeightPlant setRenderType(int renderType) {
		this.renderType = renderType;
		return this;
	}

	public BlockDoubleHeightPlant setHarvestable(boolean harvestable) {
		this.harvestable = harvestable;
		return this;
	}

	public BlockDoubleHeightPlant setHarvestedItem(ItemStack stack) {
		this.harvestable = true;
		this.harvestItem = stack;
		return this;
	}

	public BlockDoubleHeightPlant setHasSpoopyTexture(boolean spoopyTexture) {
		this.hasSpoopyTexture = spoopyTexture;
		return this;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
	}

	@Override
	protected boolean canPlaceBlockOn(Block block) {
		return block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland || block == BLBlockRegistry.swampDirt || block == BLBlockRegistry.swampGrass || block == BLBlockRegistry.deadGrass;
	}

	@Override
	public int getRenderType() {
		return this.renderType == -1 ? BlockRenderIDs.DOUBLE_PLANTS.id() : this.renderType;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && this.hasSpoopyTexture) {
			return spoopyTopIcon;
		}
		return topIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && this.hasSpoopyTexture) {
			return func_149887_c(world.getBlockMetadata(x, y, z)) ? spoopyTopIcon : spoopyBottomIcon;
		}
		return func_149887_c(world.getBlockMetadata(x, y, z)) ? topIcon : bottomIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		topIcon = reg.registerIcon("thebetweenlands:doublePlant" + name + "Top");
		bottomIcon = reg.registerIcon("thebetweenlands:doublePlant" + name + "Bottom");
		if(this.hasSpoopyTexture) {
			spoopyTopIcon = reg.registerIcon("thebetweenlands:doublePlant" + name + "TopSpoopy");
			spoopyBottomIcon = reg.registerIcon("thebetweenlands:doublePlant" + name + "BottomSpoopy");
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess access, int x, int y, int z) {
		return 0xFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item));
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
