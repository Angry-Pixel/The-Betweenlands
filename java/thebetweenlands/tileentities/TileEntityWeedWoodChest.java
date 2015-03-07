package thebetweenlands.tileentities;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.util.AxisAlignedBB;
import thebetweenlands.blocks.BlockWeedWoodChest;
import thebetweenlands.inventory.container.ContainerWeedWoodChest;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityWeedWoodChest extends TileEntityBasicInventory {

	public boolean adjacentChestChecked;
	public TileEntityWeedWoodChest adjacentChestZNeg, adjacentChestXPos, adjacentChestXNeg, adjacentChestZPosition;

	public float lidAngle;
	public float prevLidAngle;

	public int numUsingPlayers;

	private int ticksSinceSync;

	public TileEntityWeedWoodChest() {
		super(27, "container.weedWoodChest");
	}

	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		adjacentChestChecked = false;
	}

	private void func_90009_a(TileEntityWeedWoodChest chest, int side) {
		if (chest.isInvalid())
			adjacentChestChecked = false;
		else if (adjacentChestChecked)
			switch (side) {
				case 0:
					if (adjacentChestZPosition != chest)
						adjacentChestChecked = false;
					break;
				case 1:
					if (adjacentChestXNeg != chest)
						adjacentChestChecked = false;
					break;
				case 2:
					if (adjacentChestZNeg != chest)
						adjacentChestChecked = false;
					break;
				case 3:
					if (adjacentChestXPos != chest)
						adjacentChestChecked = false;
			}
	}

	public void checkForAdjacentChests() {
		if (!adjacentChestChecked) {
			adjacentChestChecked = true;
			adjacentChestZNeg = null;
			adjacentChestXPos = null;
			adjacentChestXNeg = null;
			adjacentChestZPosition = null;

			if (func_94044_a(xCoord - 1, yCoord, zCoord))
				adjacentChestXNeg = (TileEntityWeedWoodChest) worldObj.getTileEntity(xCoord - 1, yCoord, zCoord);
			if (func_94044_a(xCoord + 1, yCoord, zCoord))
				adjacentChestXPos = (TileEntityWeedWoodChest) worldObj.getTileEntity(xCoord + 1, yCoord, zCoord);
			if (func_94044_a(xCoord, yCoord, zCoord - 1))
				adjacentChestZNeg = (TileEntityWeedWoodChest) worldObj.getTileEntity(xCoord, yCoord, zCoord - 1);
			if (func_94044_a(xCoord, yCoord, zCoord + 1))
				adjacentChestZPosition = (TileEntityWeedWoodChest) worldObj.getTileEntity(xCoord, yCoord, zCoord + 1);
			if (adjacentChestZNeg != null)
				adjacentChestZNeg.func_90009_a(this, 0);
			if (adjacentChestZPosition != null)
				adjacentChestZPosition.func_90009_a(this, 2);
			if (adjacentChestXPos != null)
				adjacentChestXPos.func_90009_a(this, 1);
			if (adjacentChestXNeg != null)
				adjacentChestXNeg.func_90009_a(this, 3);
		}
	}
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 1, zCoord + 2);
	}

	private boolean func_94044_a(int x, int y, int z) {
		Block block = worldObj.getBlock(x, y, z);
		return block != null && block instanceof BlockWeedWoodChest;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateEntity() {
		checkForAdjacentChests();
		ticksSinceSync++;
		float f;

		if (!worldObj.isRemote && numUsingPlayers != 0 && (ticksSinceSync + xCoord + yCoord + zCoord) % 200 == 0) {
			numUsingPlayers = 0;
			f = 5.0F;
			List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord - f, yCoord - f, zCoord - f, xCoord + 1 + f, yCoord + 1 + f, zCoord + 1 + f));
			Iterator<EntityPlayer> iterator = list.iterator();

			while (iterator.hasNext()) {
				EntityPlayer entityplayer = iterator.next();

				if (entityplayer.openContainer instanceof ContainerWeedWoodChest) {
					IInventory iinventory = ((ContainerWeedWoodChest) entityplayer.openContainer).getLowerChestInventory();

					if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).isPartOfLargeChest(this))
						++numUsingPlayers;
				}
			}
		}

		prevLidAngle = lidAngle;
		f = 0.1F;
		double d0;

		if (numUsingPlayers > 0 && lidAngle == 0.0F && adjacentChestZNeg == null && adjacentChestXNeg == null) {
			double d1 = xCoord + 0.5D;
			d0 = zCoord + 0.5D;

			if (adjacentChestZPosition != null)
				d0 += 0.5D;

			if (adjacentChestXPos != null)
				d1 += 0.5D;

			worldObj.playSoundEffect(d1, yCoord + 0.5D, d0, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (numUsingPlayers == 0 && lidAngle > 0.0F || numUsingPlayers > 0 && lidAngle < 1.0F) {
			float f1 = lidAngle;

			if (numUsingPlayers > 0)
				lidAngle += f;
			else
				lidAngle -= f;

			if (lidAngle > 1.0F)
				lidAngle = 1.0F;

			float f2 = 0.5F;

			if (lidAngle < f2 && f1 >= f2 && adjacentChestZNeg == null && adjacentChestXNeg == null) {
				d0 = xCoord + 0.5D;
				double d2 = zCoord + 0.5D;

				if (adjacentChestZPosition != null)
					d2 += 0.5D;

				if (adjacentChestXPos != null)
					d0 += 0.5D;

				worldObj.playSoundEffect(d0, yCoord + 0.5D, d2, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (lidAngle < 0.0F)
				lidAngle = 0.0F;
		}
	}

	@Override
	public boolean receiveClientEvent(int eventId, int data) {
		if (eventId == 1) {
			numUsingPlayers = data;
			return true;
		} else
			return super.receiveClientEvent(eventId, data);
	}

	@Override
	public void openInventory() {
		if (numUsingPlayers < 0)
			numUsingPlayers = 0;

		numUsingPlayers++;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 1, numUsingPlayers);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, getBlockType());
	}

	@Override
	public void closeInventory() {
		if (getBlockType() != null && getBlockType() instanceof BlockWeedWoodChest) {
			numUsingPlayers--;
			worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 1, numUsingPlayers);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, getBlockType());
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		updateContainingBlockInfo();
		checkForAdjacentChests();
	}
}