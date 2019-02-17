package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.common.herblore.Amounts;

public class TileEntityAspectVial extends TileEntity {
    public static final float MAX_AMOUNT = Amounts.VIAL;

    private Aspect aspect = null;

    /**
     * Tries to add an amount and returns the added amount
     * @param amount
     * @return
     */
    public int addAmount(int amount) {
        int canAdd = (int) (MAX_AMOUNT - this.aspect.amount);
        int added = 0;
        if(canAdd > 0) {
            added = Math.min(canAdd, amount);
            this.aspect = new Aspect(this.aspect.type, this.aspect.amount + added);
        }
        markDirty();
        return added;
    }

    /**
     * Tries to remove an amount and returns the removed amount
     * @param amount
     * @return
     */
    public int removeAmount(int amount) {
        int removed = Math.min(this.aspect.amount, amount);
        if(removed < this.aspect.amount) {
            this.aspect = new Aspect(this.aspect.type, this.aspect.amount - removed);
        } else {
            this.aspect = null;
        }
        markDirty();
        return removed;
    }

    @Override
    public void markDirty() {
        final IBlockState state = getWorld().getBlockState(getPos());
        getWorld().notifyBlockUpdate(getPos(), state, state, 2);
        super.markDirty();
    }

    public Aspect getAspect() {
        return this.aspect;
    }

    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
        markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if(this.aspect != null)
            this.aspect.writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if(nbt.hasKey("aspect")) {
            this.aspect = Aspect.readFromNBT(nbt);
        } else {
            this.aspect = null;
        }
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        readFromNBT(tag);
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
    	return oldState.getBlock() != newSate.getBlock();
    }
}