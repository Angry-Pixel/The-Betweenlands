package thebetweenlands.blocks.container;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.tileentities.TileEntityItemShelf;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockItemShelf extends BlockContainer {

	public BlockItemShelf() {
		super(Material.wood);
		setHardness(1.0F);
		setStepSound(soundTypeWood);
		setCreativeTab(ModCreativeTabs.blocks);
		setBlockName("thebetweenlands.weedwoodItemShelf");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return BLBlockRegistry.weedwoodPlanks.getIcon(side, 1);
	}

	@Override
	public int getRenderType() {
		return -1;
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
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityItemShelf();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack is) {
		byte rotationMeta = 0;
		int rotation = MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if (rotation == 0)
			rotationMeta = 2;
		if (rotation == 1)
			rotationMeta = 5;
		if (rotation == 2)
			rotationMeta = 3;
		if (rotation == 3)
			rotationMeta = 4;
		world.setBlockMetadataWithNotify(x, y, z, rotationMeta, 3);
		System.out.println("Meta: "+ rotationMeta);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess access, int x, int y, int z) {
		int meta = access.getBlockMetadata(x, y, z);
		float widthMin = 0, depthMin = 0;
		float widthMax = 0, depthMax = 0;
		switch (meta) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				depthMin = 0.5F;
				break;
			case 3:
				depthMax = 0.5F;
				break;
			case 4:
				widthMin = 0.5F;
				break;
			case 5:
				widthMax = 0.5F;
				break;
		}
		setBlockBounds(0F + widthMin, 0F, 0F + depthMin, 1F - widthMax, 1F, 1F - depthMax);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float hitX, float hitY, float hitZ) {
		// TODO hit detection for specific quarter of shelf
		if (world.getTileEntity(x, y, z) instanceof TileEntityItemShelf) {
			TileEntityItemShelf tile = (TileEntityItemShelf) world.getTileEntity( x, y, z);
			if (player.getCurrentEquippedItem() != null) {
				ItemStack item = player.getCurrentEquippedItem();
				for (int i = 0; i < 4; i++) {
					if (tile.getStackInSlot(i) == null) {
						tile.setInventorySlotContents(i, new ItemStack(item.getItem(), 1, item.getItemDamage()));
						if (!player.capabilities.isCreativeMode)
							player.getCurrentEquippedItem().stackSize--;
						world.markBlockForUpdate(x, y, z);
						return true;
					}
				}
			}
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
					if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
						float f = 0.7F;
						double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, stack);
						entityitem.delayBeforeCanPickup = 10;
						world.spawnEntityInWorld(entityitem);
					}
				}
			}
		super.breakBlock(world, x, y, z, block, meta);
	}
}
