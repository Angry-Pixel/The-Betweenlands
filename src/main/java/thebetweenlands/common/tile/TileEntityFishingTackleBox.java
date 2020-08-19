package thebetweenlands.common.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.entity.EntityFishingTackleBoxSeat;

public class TileEntityFishingTackleBox extends TileEntity implements ITickable {

    public final float MAX_OPEN = 120f;
    public final float MIN_OPEN = 0f;
    public final float OPEN_SPEED = 10f;
    public final float CLOSE_SPEED = 10f;

    private boolean open = false;
    private float lid_angle = 0.0f;

    public TileEntityFishingTackleBox() {
    	super();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public void update() {
        lid_angle = open ? Math.min(lid_angle + OPEN_SPEED, MAX_OPEN) : Math.max(lid_angle - CLOSE_SPEED, MIN_OPEN);

        if (!world.isRemote) {
        	//this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 2);
        //	System.out.println("Lid Angle: " + lid_angle);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        open = nbt.getBoolean("open");
        lid_angle = nbt.getFloat("lid_angle");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        nbt.setBoolean("open", open);
        nbt.setFloat("lid_angle", lid_angle);
        return nbt;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    public float getLidAngle(float partialTicks) {
        return open ? Math.min(lid_angle + OPEN_SPEED * partialTicks, MAX_OPEN) : Math.max(lid_angle - CLOSE_SPEED * partialTicks, MIN_OPEN);
    }

	public void seatPlayer(EntityPlayer player, BlockPos pos) {
		EntityFishingTackleBoxSeat entitySeat = new EntityFishingTackleBoxSeat(world);
		entitySeat.setPosition(pos.getX() + 0.5D, pos.getY() + 0.2D, pos.getZ()  + 0.5D);
		world.spawnEntity(entitySeat);
		player.startRiding(entitySeat, true);
	}

}
