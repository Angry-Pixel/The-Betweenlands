package thebetweenlands.tileentities;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemSwampTalisman.EnumTalisman;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.server.PacketDruidAltarProgress;
import thebetweenlands.recipes.DruidAltarRecipe;

public class TileEntityDruidAltar extends TileEntityBasicInventory  {

	@SideOnly(Side.CLIENT)
	public float rotation;
	@SideOnly(Side.CLIENT)
	public float prevRotation;
	@SideOnly(Side.CLIENT)
	public float renderYOffset;
	@SideOnly(Side.CLIENT)
	public float prevRenderYOffset;
	@SideOnly(Side.CLIENT)
	private static final float ROTATION_SPEED = 2.0F;
	@SideOnly(Side.CLIENT)
	public final static double FINAL_HEIGHT = 2.0D;
	
	public int craftingProgress = 0;
	private boolean circleShouldRevert = true;
	private int[] damageValues = {0, 0, 0, 0};

	//11 seconds crafting time
	public static final int CRAFTING_TIME = 20*17;

	public TileEntityDruidAltar() {
		super(5, "druidAltar");
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && circleShouldRevert) {
			checkDruidCircleMeta(worldObj);
			circleShouldRevert = false;
		}
		
		if (worldObj.isRemote) {
			prevRotation = rotation;
			rotation += ROTATION_SPEED;
			if (rotation >= 360.0F) {
				rotation -= 360.0F;
				prevRotation -= 360.0F;
			}
			if (craftingProgress != 0) {
				++craftingProgress;
			}
			prevRenderYOffset = renderYOffset;
			renderYOffset = (float) ((double) this.craftingProgress / (double) TileEntityDruidAltar.CRAFTING_TIME * FINAL_HEIGHT + 1.0D);
		} else {
			if (craftingProgress != 0) {
				DruidAltarRecipe recipe = DruidAltarRecipe.getOutput(inventory[1], inventory[2], inventory[3], inventory[4]);
				//Sync clients every second
				if (this.craftingProgress % 20 == 0 || this.craftingProgress == 1) {
					sendCraftingProgressPacket();
				}
				++craftingProgress;

				//updateDamageValues();
				if (recipe == null || inventory[0] != null)  {
					if (this.craftingProgress != 0) {
						this.stopCraftingProcess();
					}
				}

				if (craftingProgress >= CRAFTING_TIME && recipe != null) {
					ItemStack stack = recipe.output;
					setInventorySlotContents(1, null);
					setInventorySlotContents(2, null);
					setInventorySlotContents(3, null);
					setInventorySlotContents(4, null);
					setInventorySlotContents(0, stack);
					this.stopCraftingProcess();
					this.removeSpawner();
				}
			}
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	private void removeSpawner() {
		if (worldObj.getBlock(xCoord, yCoord - 1, zCoord) == BLBlockRegistry.druidSpawner)
			worldObj.setBlock(xCoord, yCoord - 1, zCoord, Blocks.grass);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		inventory[slot] = is;
		if (is != null && is.stackSize > getInventoryStackLimit()) {
			is.stackSize = getInventoryStackLimit();
		}
		//updateDamageValues();
		DruidAltarRecipe recipe = DruidAltarRecipe.getOutput(inventory[1], inventory[2], inventory[3], inventory[4]);
		if (recipe != null && is != null) {
			if (inventory[0] == null) {
				if (!worldObj.isRemote) {
					if (craftingProgress == 0) {
						this.startCraftingProcess();
					}
				}
			}
		}
	}

//	private void updateDamageValues() {
//		for (int i = 0; i < 4; i++) {
//			damageValues[i] = inventory[i + 1] == null ? 0 : inventory[i + 1].getItemDamage();
//		}
//	}
	
//	private boolean containsAllParts() {
//
//		return
//			damageValues[0] > 0 &&
//			damageValues[1] > 0 &&
//			damageValues[2] > 0 &&
//			damageValues[3] > 0 &&
//			damageValues[0] != damageValues[1] &&
//			damageValues[0] != damageValues[2] &&
//			damageValues[0] != damageValues[3] &&
//			damageValues[1] != damageValues[2] &&
//			damageValues[1] != damageValues[3] &&
//			damageValues[2] != damageValues[3];
//	}

	private void startCraftingProcess() {
		World world = this.getWorldObj();
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer)world).provider.dimensionId;
		}
		craftingProgress = 1;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 2);
		//Packet to start sound
		TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidAltarProgress(this, -1)), new TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
		//Sets client crafting progress to 1
		TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidAltarProgress(this, 1)), new TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
		//Does the metadata stuff for the circle animated textures
		checkDruidCircleMeta(world);
	}

	private void stopCraftingProcess() {
		World world = this.getWorldObj();
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer)world).provider.dimensionId;
		}
		craftingProgress = 0;
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);
		//Packet to cancel sound
		TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidAltarProgress(this, -2)), new TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
		//Sets client crafting progress to 0
		TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidAltarProgress(this, 0)), new TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
		//Does the metadata stuff for the circle animated textures
		checkDruidCircleMeta(world);
	}

	public void sendCraftingProgressPacket() {
		World world = this.getWorldObj();
		int dim = 0;
		if (world instanceof WorldServer) {
			dim = ((WorldServer)world).provider.dimensionId;
		}
		TheBetweenlands.networkWrapper.sendToAllAround(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketDruidAltarProgress(this)), new TargetPoint(dim, xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, 64D));
	}
	
	@SubscribePacket
	public static void onProgressPacket(PacketDruidAltarProgress pkt) {
		TileEntity te = FMLClientHandler.instance().getWorldClient().getTileEntity(pkt.x, pkt.y, pkt.z);
		if (te instanceof TileEntityDruidAltar) {
			TileEntityDruidAltar tile = (TileEntityDruidAltar) te;
			if (pkt.progress >= 0) {
				tile.craftingProgress = pkt.progress;
			}
		}
	}
	
	private void checkDruidCircleMeta(World world) {
		int baseRadius = 6;
		for (int y = 0; y < 6; y++) {
			for (int x = baseRadius * -1; x <= baseRadius; ++x) {
				for (int z = baseRadius * -1; z <= baseRadius; ++z) {
					double dSq = x * x + z * z;
					if (Math.round(Math.sqrt(dSq)) == baseRadius) {
						Block block = world.getBlock(xCoord + x, yCoord + y, zCoord + z);
						if (block == BLBlockRegistry.druidStone1
								|| block == BLBlockRegistry.druidStone2
								|| block == BLBlockRegistry.druidStone3
								|| block == BLBlockRegistry.druidStone4
								|| block == BLBlockRegistry.druidStone5) {
							int meta = world.getBlockMetadata(xCoord + x, yCoord + y, zCoord + z);
							if (craftingProgress == 0 && meta >= 4 || circleShouldRevert && meta >= 4) {
								world.setBlockMetadataWithNotify(this.xCoord + x, yCoord + y, zCoord + z, meta - 4, 3);
							} else if (craftingProgress == 1 && meta < 4) {
								world.setBlockMetadataWithNotify(this.xCoord + x, yCoord + y, zCoord + z, meta + 4, 3);
							}
						}
					}
				}
			}
		}
	}


	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 3, zCoord + 2);
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
	}

}
