package thebetweenlands.blocks.container;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.tileentities.TileEntityPurifier;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPurifier extends BlockContainer {

	public BlockPurifier() {
		super(Material.rock);
		setHardness(2.0F);
		setResistance(5.0F);
		setBlockName("thebetweenlands.purifier");
		setCreativeTab(ModCreativeTabs.blocks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return blockIcon;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		if (world.isRemote)
			return true;
		if (world.getTileEntity(x, y, z) instanceof TileEntityPurifier ) {
			TileEntityPurifier  tile = (TileEntityPurifier ) world.getTileEntity(x, y, z);

			if (player.isSneaking())
				return false;

			if (player.getCurrentEquippedItem() != null) {
				ItemStack oldItem = player.getCurrentEquippedItem();
				ItemStack newItem = tile.fillTankWithBucket(player.inventory.getStackInSlot(player.inventory.currentItem));

				if (!player.capabilities.isCreativeMode)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, newItem);
				if (!ItemStack.areItemStacksEqual(oldItem, newItem))
					return true;
		}

			if (tile != null)
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_PURIFIER, world, x, y, z);
		}
		return true;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		IInventory tile = (IInventory) world.getTileEntity(x, y, z);
		if (tile != null)
			for (int i = 0; i < tile.getSizeInventory(); i++) {
				ItemStack stack = tile.getStackInSlot(i);
				if (stack != null) {
				//	TODO inventory dropping code here
				}
			}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPurifier();
	}
}