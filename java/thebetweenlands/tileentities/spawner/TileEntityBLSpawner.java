package thebetweenlands.tileentities.spawner;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityBLSpawner extends TileEntity {
	public float counter = 0.0F;
	public float lastCounter = 0.0F;

	private final MobSpawnerBaseLogicBL spawnerLogic = new MobSpawnerBaseLogicBL() {
		@Override
		public void sendBlockEvent(int eventID) {
			TileEntityBLSpawner.this.worldObj.addBlockEvent(TileEntityBLSpawner.this.xCoord, TileEntityBLSpawner.this.yCoord, TileEntityBLSpawner.this.zCoord, Blocks.mob_spawner, eventID, 0);
		}
		@Override
		public World getSpawnerWorld() {
			return TileEntityBLSpawner.this.worldObj;
		}
		@Override
		public int getSpawnerX() {
			return TileEntityBLSpawner.this.xCoord;
		}
		@Override
		public int getSpawnerY() {
			return TileEntityBLSpawner.this.yCoord;
		}
		@Override
		public int getSpawnerZ() {
			return TileEntityBLSpawner.this.zCoord;
		}
	};

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.spawnerLogic.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.spawnerLogic.writeToNBT(nbt);
	}

	@Override
	public void updateEntity() {
		this.spawnerLogic.updateSpawner();
		this.lastCounter = this.counter;
		this.counter += 0.0085F;
		super.updateEntity();
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("entityType", this.getSpawnerLogic().getEntityNameToSpawn());
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		if(packet.func_148857_g().hasKey("entityType")) {
			String entityType = packet.func_148857_g().getString("entityType");
			this.getSpawnerLogic().setEntityName(entityType);
		}
	}

	@Override
	public boolean receiveClientEvent(int event, int parameter) {
		return this.spawnerLogic.setDelayToMin(event) ? true : super.receiveClientEvent(event, parameter);
	}

	public MobSpawnerBaseLogicBL getSpawnerLogic() {
		return this.spawnerLogic;
	}
}
