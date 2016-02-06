package thebetweenlands.items.lanterns;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.lanterns.BlockConnectionFastener;
import thebetweenlands.connection.ConnectionType;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.entities.properties.list.EntityPropertiesLantern;
import thebetweenlands.tileentities.TileEntityConnectionFastener;
import thebetweenlands.utils.vectormath.Point3i;

public abstract class ItemConnection extends Item {
	public ItemConnection() {
		setCreativeTab(BLCreativeTabs.items);
		setMaxStackSize(16);
	}

	public abstract ConnectionType getConnectionType();

	@Override
	public boolean onItemUse(ItemStack heldItemStack, EntityPlayer user, World world, int x, int y, int z, int side, float fromBlockX, float fromBlockY, float fromBlockZ) {
		int data = BlockConnectionFastener.SIDE_TO_DATA[side];
		ForgeDirection direction = ForgeDirection.getOrientation(side);
		Block blockPlacingOn = world.getBlock(x, y, z);
		if (world.isSideSolid(x, y, z, direction) || blockPlacingOn instanceof BlockSlab && direction.offsetY == 0 || blockPlacingOn instanceof BlockLeaves || blockPlacingOn instanceof BlockStairs) {
			x += direction.offsetX;
			y += direction.offsetY;
			z += direction.offsetZ;
			if (BLBlockRegistry.connectionFastener.canPlaceBlockAt(world, x, y, z)) {
				if (!world.isRemote) {
					world.setBlock(x, y, z, BLBlockRegistry.connectionFastener, data, 3);
					world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, BLBlockRegistry.connectionFastener.stepSound.func_150496_b(), (BLBlockRegistry.connectionFastener.stepSound.getVolume() + 1) / 2, BLBlockRegistry.connectionFastener.stepSound.getPitch() * 0.8f);
					EntityPropertiesLantern playerData = EntityPropertiesLantern.getPlayerData(user);
					if (playerData.hasLastClicked()) {
						Point3i lastClicked = playerData.getLastClicked();
						TileEntity tileEntity = world.getTileEntity(lastClicked.x, lastClicked.y, lastClicked.z);
						TileEntity tileEntityTo = world.getTileEntity(x, y, z);
						if (tileEntity instanceof TileEntityConnectionFastener && tileEntityTo instanceof TileEntityConnectionFastener) {
							TileEntityConnectionFastener to = (TileEntityConnectionFastener) tileEntity;
							TileEntityConnectionFastener from = (TileEntityConnectionFastener) tileEntityTo;
							to.removeConnection(user);
							from.connectWith(to, getConnectionType(), heldItemStack.getTagCompound());
							playerData.setUnknownLastClicked();
							heldItemStack.stackSize--;
							return true;
						}
					} else {
						playerData.setLastClicked(x, y, z);
						TileEntityConnectionFastener tileEntity = (TileEntityConnectionFastener) world.getTileEntity(x, y, z);
						tileEntity.connectWith(user, getConnectionType(), heldItemStack.getTagCompound());
					}
				}
				return true;
			}
		} else if (world.getBlock(x, y, z) == Blocks.fence) {
			if (!world.isRemote) {
				world.setBlock(x, y, z, BLBlockRegistry.connectionFastenerFence);
				world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, BLBlockRegistry.connectionFastener.stepSound.func_150496_b(), (BLBlockRegistry.connectionFastener.stepSound.getVolume() + 1) / 2, BLBlockRegistry.connectionFastener.stepSound.getPitch() * 0.8f);
				EntityPropertiesLantern playerData = EntityPropertiesLantern.getPlayerData(user);
				if (playerData.hasLastClicked()) {
					Point3i lastClicked = playerData.getLastClicked();
					TileEntity tileEntity = world.getTileEntity(lastClicked.x, lastClicked.y, lastClicked.z);
					TileEntity tileEntityTo = world.getTileEntity(x, y, z);
					if (tileEntity instanceof TileEntityConnectionFastener && tileEntityTo instanceof TileEntityConnectionFastener) {
						TileEntityConnectionFastener to = (TileEntityConnectionFastener) tileEntity;
						TileEntityConnectionFastener from = (TileEntityConnectionFastener) tileEntityTo;
						to.removeConnection(user);
						from.connectWith(to, getConnectionType(), heldItemStack.getTagCompound());
						playerData.setUnknownLastClicked();
						heldItemStack.stackSize--;
						return true;
					}
				} else {
					playerData.setLastClicked(x, y, z);
					TileEntityConnectionFastener tileEntity = (TileEntityConnectionFastener) world.getTileEntity(x, y, z);
					tileEntity.connectWith(user, getConnectionType(), heldItemStack.getTagCompound());
				}
			}
			return true;
		}
		return false;
	}
}
