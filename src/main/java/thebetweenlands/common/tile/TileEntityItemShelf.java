package thebetweenlands.common.tile;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityItemShelf extends TileEntityBasicInventory {
    public TileEntityItemShelf() {
        super(4, "container.bl.item_shelf");
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeInventoryNBT(nbt);
        return new SPacketUpdateTileEntity(this.pos, 0, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        this.readInventoryNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbt = super.getUpdateTag();
        this.writeInventoryNBT(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound nbt) {
        super.handleUpdateTag(nbt);
        this.readInventoryNBT(nbt);
    }


    @Override
    public ItemStack decrStackSize(int index, int count) {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        return super.decrStackSize(index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        return super.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
        super.setInventorySlotContents(index, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
    	//24 block render range for items
    	return 576;
    }
}
