package thebetweenlands.blocks.container;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;

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
		setBlockName("thebetweenlands.itemShelf");
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
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return  world.isSideSolid(x - 1, y, z, EAST) || world.isSideSolid(x + 1, y, z, WEST) || world.isSideSolid(x, y, z - 1, SOUTH) || world.isSideSolid(x, y, z + 1, NORTH);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		int meta = world.getBlockMetadata(x, y, z);
		boolean flag = false;
		if (meta == 2 && world.isSideSolid(x, y, z + 1, NORTH))
			flag = true;
		if (meta == 3 && world.isSideSolid(x, y, z - 1, SOUTH))
			flag = true;
		if (meta == 4 && world.isSideSolid(x + 1, y, z, WEST))
			flag = true;
		if (meta == 5 && world.isSideSolid(x - 1, y, z, EAST))
			flag = true;
		if (!flag) {
			breakBlock(world, x, y, z, this, 0);
			dropBlockAsItem(world, x, y, z, meta, 0);
			world.setBlockToAir(x, y, z);
		}
		super.onNeighborBlockChange(world, x, y, z, neighbour);
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
		if (world.getTileEntity(x, y, z) instanceof TileEntityItemShelf) {
			TileEntityItemShelf tile = (TileEntityItemShelf) world.getTileEntity( x, y, z);
			int slotClicked = -1; // imaginary slot of -1 to prevent derp
			if (metadata == 3) {
				if (hitX >= 0 && hitX <= 0.46875 && hitY >= 0.5625 && hitY <= 1)
					slotClicked = 0;
				if (hitX >= 0.53125 && hitX <= 1 && hitY >= 0.5625 && hitY <= 1)
					slotClicked = 1;
				if (hitX >= 0 && hitX <= 0.46875 && hitY >= 0.0625 && hitY <= 0.5)
					slotClicked = 2;
				if (hitX >= 0.53125 && hitX <= 1 && hitY >= 0.0625 && hitY <= 0.5)
					slotClicked = 3;
			}
			if (metadata == 2) {
				if (hitX >= 0 && hitX <= 0.46875 && hitY >= 0.5625 && hitY <= 1)
					slotClicked = 1;
				if (hitX >= 0.53125 && hitX <= 1 && hitY >= 0.5625 && hitY <= 1)
					slotClicked = 0;
				if (hitX >= 0 && hitX <= 0.46875 && hitY >= 0.0625 && hitY <= 0.5)
					slotClicked = 3;
				if (hitX >= 0.53125 && hitX <= 1 && hitY >= 0.0625 && hitY <= 0.5)
					slotClicked = 2;
			}
			if (metadata == 4) {
				if (hitZ >= 0 && hitZ <= 0.46875 && hitY >= 0.5625 && hitY <= 1)
					slotClicked = 0;
				if (hitZ >= 0.53125 && hitZ <= 1 && hitY >= 0.5625 && hitY <= 1)
					slotClicked = 1;
				if (hitZ >= 0 && hitZ <= 0.46875 && hitY >= 0.0625 && hitY <= 0.5)
					slotClicked = 2;
				if (hitZ >= 0.53125 && hitZ <= 1 && hitY >= 0.0625 && hitY <= 0.5)
					slotClicked = 3;
			}
			if (metadata == 5) {
				if (hitZ >= 0 && hitZ <= 0.46875 && hitY >= 0.5625 && hitY <= 1)
					slotClicked = 1;
				if (hitZ >= 0.53125 && hitZ <= 1 && hitY >= 0.5625 && hitY <= 1)
					slotClicked = 0;
				if (hitZ >= 0 && hitZ <= 0.46875 && hitY >= 0.0625 && hitY <= 0.5)
					slotClicked = 3;
				if (hitZ >= 0.53125 && hitZ <= 1 && hitY >= 0.0625 && hitY <= 0.5)
					slotClicked = 2;
			}
			if (player.getCurrentEquippedItem() != null && slotClicked != -1) {
				ItemStack item = player.getCurrentEquippedItem();
				if (tile.getStackInSlot(slotClicked) == null) {
					tile.setInventorySlotContents(slotClicked, item.copy());
					if (!player.capabilities.isCreativeMode)
						player.getCurrentEquippedItem().stackSize--;
					world.markBlockForUpdate(x, y, z);
					return true;
				}
			}
			if (player.getCurrentEquippedItem() == null && slotClicked != -1) {
				if (tile.getStackInSlot(slotClicked) != null) {
					ItemStack stack = tile.getStackInSlot(slotClicked);
					if (!world.isRemote) {
						EntityItem entityitem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, stack);
						entityitem.delayBeforeCanPickup = 10;
						entityitem.hoverStart = 0.0F;
						entityitem.rotationYaw = -player.rotationYaw;
						world.spawnEntityInWorld(entityitem);
						double moveX = ((player.posX - entityitem.posX) - entityitem.motionX) * 0.000001D;
						double moveY = 0;
						double moveZ = ((player.posZ - entityitem.posZ) - entityitem.motionZ) * 0.000001D;
						entityitem.moveEntity(moveX, moveY, moveZ);
						tile.setInventorySlotContents(slotClicked, null);
					}
					world.markBlockForUpdate(x, y, z);
					return true;
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
