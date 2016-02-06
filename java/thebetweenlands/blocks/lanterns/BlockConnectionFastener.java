package thebetweenlands.blocks.lanterns;

import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.list.EntityPropertiesLantern;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.tileentities.TileEntityConnectionFastener;
import thebetweenlands.tileentities.connection.Connection;
import thebetweenlands.tileentities.connection.ConnectionPlayer;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.items.lanterns.ItemConnection;
import thebetweenlands.utils.mc.EnumFacing;
import thebetweenlands.utils.vectormath.Point3f;
import thebetweenlands.utils.vectormath.Point3i;

import java.util.Iterator;
import java.util.Random;

public class BlockConnectionFastener extends BlockContainer {
	public static final int[] SIDE_TO_DATA = {2, 0, 12, 4, 1, 3};

	private static final ForgeDirection[] DATA_TO_DIRECTION = {ForgeDirection.UP, ForgeDirection.WEST, ForgeDirection.DOWN, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.NORTH, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN, ForgeDirection.UNKNOWN};

	public static final EnumFacing[] DATA_TO_FACING = {EnumFacing.UP, EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.UP, EnumFacing.UP, EnumFacing.UP};

	public BlockConnectionFastener() {
		super(Material.circuits);
		setBlockBounds(0.375F, 0, 0.375F, 0.625F, 0.25F, 0.625F);
		setBlockTextureName(ModInfo.ID + ":fastener");
		setBlockName("thebetweenlands.connectionFastener");
	}

	public BlockConnectionFastener(Material material) {
		super(material);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityConnectionFastener fastener = (TileEntityConnectionFastener) world.getTileEntity(x, y, z);
		if (fastener == null) {
			return;
		}
		Iterator<Connection> connectionIterator = fastener.getConnections().iterator();
		float offsetX = world.rand.nextFloat() * 0.8F + 0.1F;
		float offsetY = world.rand.nextFloat() * 0.8F + 0.1F;
		float offsetZ = world.rand.nextFloat() * 0.8F + 0.1F;
		while (connectionIterator.hasNext()) {
			Connection connection = connectionIterator.next();
			if (!(connection instanceof ConnectionPlayer)) {
				ItemStack itemStack = new ItemStack(connection.getType().getItem(), 1);
				NBTTagCompound tagCompound = new NBTTagCompound();
				connection.writeDetailsToNBT(tagCompound);
				if (!tagCompound.hasNoTags()) {
					itemStack.setTagCompound(tagCompound);
				}
				EntityItem entityItem = new EntityItem(world, x + offsetX, y + offsetY, z + offsetZ, itemStack);
				float v = 0.05F;
				entityItem.motionX = world.rand.nextGaussian() * v;
				entityItem.motionY = world.rand.nextGaussian() * v + 0.2F;
				entityItem.motionZ = world.rand.nextGaussian() * v;
				world.spawnEntityInWorld(entityItem);
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int data) {
		return new TileEntityConnectionFastener();
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public Item getItemDropped(int data, Random random, int fortune) {
		return null;
	}

	public Point3f getOffsetForData(int data, float offset) {
		Point3f offsetPoint = new Point3f(offset, offset, offset);
		switch (data) {
			case 2:
				offsetPoint.y += 0.75F;
			case 0:
				offsetPoint.x += 0.375F;
				offsetPoint.z += 0.375F;
				break;
			case 1:
				offsetPoint.x += 0.75F;
			case 3:
				offsetPoint.z += 0.375F;
				offsetPoint.y += 0.375F;
				break;
			case 12:
				offsetPoint.z += 0.75F;
			case 4:
				offsetPoint.x += 0.375F;
				offsetPoint.y += 0.375F;
		}
		return offsetPoint;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
		return TheBetweenlands.proxy.getFairyLightsFastenerPickBlock(target, world, x, y, z, this);
	}

	@Override
	public int getRenderType() {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fromBlockX, float fromBlockY, float fromBlockZ) {
		ItemStack heldItemStack = player.getHeldItem();
		if (heldItemStack != null && heldItemStack.getItem() instanceof ItemConnection) {
			ItemConnection item = (ItemConnection) heldItemStack.getItem();
			EntityPropertiesLantern data = EntityPropertiesLantern.getPlayerData(player);
			if (data.hasLastClicked()) {
				Point3i lastClicked = data.getLastClicked();
				TileEntity tileEntity = world.getTileEntity(lastClicked.x, lastClicked.y, lastClicked.z);
				TileEntity tileEntityTo = world.getTileEntity(x, y, z);
				if (tileEntity != tileEntityTo && tileEntity instanceof TileEntityConnectionFastener && tileEntityTo instanceof TileEntityConnectionFastener) {
					if (!world.isRemote) {
						TileEntityConnectionFastener to = (TileEntityConnectionFastener) tileEntity;
						TileEntityConnectionFastener from = (TileEntityConnectionFastener) tileEntityTo;
						from.connectWith(to, item.getConnectionType(), heldItemStack.getTagCompound());
						to.removeConnection(player);
						data.setUnknownLastClicked();
						if (!player.capabilities.isCreativeMode) {
							heldItemStack.stackSize--;
						}
					}
					return true;
				}
			} else {
				if (!world.isRemote) {
					data.setLastClicked(x, y, z);
					TileEntityConnectionFastener tileEntity = (TileEntityConnectionFastener) world.getTileEntity(x, y, z);
					tileEntity.connectWith(player, item.getConnectionType(), heldItemStack.getTagCompound());
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		int data = world.getBlockMetadata(x, y, z);
		ForgeDirection direction = DATA_TO_DIRECTION[data];
		Block blockOn = world.getBlock(x - direction.offsetX, y - direction.offsetY, z - direction.offsetZ);
		if (!world.isSideSolid(x - direction.offsetX, y - direction.offsetY, z - direction.offsetZ, direction) && !(blockOn instanceof BlockSlab && direction.offsetY == 0) && !(blockOn instanceof BlockLeaves) && !(blockOn instanceof BlockStairs)) {
			dropBlockAsItem(world, x, y, z, data, 0);
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int data = world.getBlockMetadata(x, y, z);
		Point3f offset = getOffsetForData(data, 0);
		setBlockBounds(offset.x, offset.y, offset.z, offset.x + 0.25F, offset.y + 0.25F, offset.z + 0.25F);
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int meta) {
		return true;
	}
}
