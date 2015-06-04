package thebetweenlands.tileentities;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packets.PacketAnimatorProgress;
import thebetweenlands.utils.WeightedRandomItem;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class TileEntityAnimator extends TileEntityBasicInventory {

	public static final WeightedRandomItem[] items = new WeightedRandomItem[] { new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.LIFE_CRYSTAL), 10), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.VALONITE_SHARD), 20), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.OCTINE_INGOT), 30), new WeightedRandomItem(ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR), 40) };

	public TileEntityAnimator() {
		super(3, "animator");
	}

	// Progress (0-100). Used for rendering
	public int progress, life = 4800, lifeDecrement = 40;
	public boolean isAnimating = false, isDirty = false;
	public int itemsConsumed = 0, stackSize = 16;

	public float crystalVelocity = 0.0F;
	public float crystalRotation = 0.0F;

	@Override
	public void updateEntity() {
		if (isDirty) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			isDirty = false;
		}
		if (this.worldObj.isRemote) {
			this.crystalVelocity -= Math.signum(this.crystalVelocity) * 0.05F;
			this.crystalRotation += this.crystalVelocity;
			if (this.crystalRotation >= 360.0F) {
				this.crystalRotation -= 360.0F;
			} else if (this.crystalRotation <= 360.0F) {
				this.crystalRotation += 360.0F;
			}
			if (Math.abs(this.crystalVelocity) <= 1.0F && this.getWorldObj().rand.nextInt(15) == 0) {
				this.crystalVelocity = this.worldObj.rand.nextFloat() * 18.0F - 9.0F;
			}
			if (this.isAnimating) {
				this.progress++;
				if (this.progress >= 44) {
					this.progress = 0;
					this.itemsConsumed++;
				}
			}
		} else {
			if (getStackInSlot(0) != null && getStackInSlot(1) != null && getStackInSlot(2) != null && itemsConsumed < stackSize) {
				sendProgressPacket();
				++progress;
				lifeDecrement--;
				if (lifeDecrement <= 0) {
					life -= 10;
					if (life <= 0) {
						this.decrStackSize(1, 1);
						life = 4800;
					}
					lifeDecrement = 40;
				}
				if (this.progress >= 44) {
					this.progress = 0;
					this.decrStackSize(2, 1);
					itemsConsumed++;
					if (getStackInSlot(0) == null || getStackInSlot(1) == null || getStackInSlot(2) == null || itemsConsumed >= stackSize)
						this.stopCraftingProcess();
				}
			} else
				this.stopCraftingProcess();
			if (itemsConsumed >= stackSize && getStackInSlot(0) != null && getStackInSlot(0).getItem() == BLItemRegistry.scroll)
				this.setInventorySlotContents(0, ((WeightedRandomItem) WeightedRandom.getRandomItem(worldObj.rand, items)).getItem(worldObj.rand));
		}
		this.updateContainingBlockInfo();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		inventory[slot] = is;
		if (is != null && is.stackSize > getInventoryStackLimit()) {
			is.stackSize = getInventoryStackLimit();
		}
		if (is != null) {
			if (inventory[0] != null && inventory[1] != null && inventory[2] != null) {
				if (!worldObj.isRemote) {
					if (!isAnimating || (isAnimating && itemsConsumed < stackSize))
						this.startCraftingProcess();
				}
			}
		}
	}

	private void startCraftingProcess() {
		World world = this.getWorldObj();
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer) world).provider.dimensionId;
		}
		isAnimating = true;
		progress = 0;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 2);
		// TODO: Currently not used
		// Packet to start sound
		// TheBetweenlands.networkWrapper.sendToAllAround(new
		// MessageAltarCraftingProgress(xCoord, yCoord, zCoord, -1), new
		// TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
		// Sets client crafting progress to 1
		// TheBetweenlands.networkWrapper.sendToAllAround(new
		// MessageAltarCraftingProgress(xCoord, yCoord, zCoord, 1), new
		// TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
		// Does the metadata stuff for the circle animated textures
	}

	private void stopCraftingProcess() {
		World world = this.getWorldObj();
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer) world).provider.dimensionId;
		}
		isAnimating = false;
		progress = 0;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);
		// TODO: Currently not used
		// Packet to cancel sound
		// TheBetweenlands.networkWrapper.sendToAllAround(new
		// MessageAltarCraftingProgress(xCoord, yCoord, zCoord, -2), new
		// TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
		// Sets client crafting progress to 0
		// TheBetweenlands.networkWrapper.sendToAllAround(new
		// MessageAltarCraftingProgress(xCoord, yCoord, zCoord, 0), new
		// TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
		// Does the metadata stuff for the circle animated textures
	}

	public void sendProgressPacket() {
		World world = this.getWorldObj();
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer) world).provider.dimensionId;
		}
		TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketAnimatorProgress(this)), new TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
	}

	@SubscribePacket
	public static void onProgressPacket(PacketAnimatorProgress pkt) {
		TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(pkt.x, pkt.y, pkt.z);
		if (te instanceof TileEntityAnimator) {
			TileEntityAnimator tile = (TileEntityAnimator) te;
			tile.progress = pkt.progress;
			tile.life = pkt.life;
			tile.setInventorySlotContents(0, new ItemStack(Item.getItemById(pkt.slot0ItemID), pkt.slot0Size, pkt.slot0ItemMeta));
			tile.getStackInSlot(1).stackSize = pkt.slot1Size;
			tile.getStackInSlot(2).stackSize = pkt.slot2Size;
			tile.itemsConsumed = pkt.itemsConsumed;
		}
	}

	/**
	 * @param nbt
	 */
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("life", life);
		nbt.setInteger("itemsConsumed", itemsConsumed);
	}

	/**
	 * @param nbt
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		life = nbt.getInteger("life");
		itemsConsumed = nbt.getInteger("itemsConsumed");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		if (packet.func_148853_f() == 0)
			readFromNBT(packet.func_148857_g());
//		worldObj.func_147479_m(xCoord, yCoord, zCoord);
	}
}
