package thebetweenlands.common.tile.spawner;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityMobSpawnerBetweenlands extends TileEntity implements ITickable {
	public float counter = 0.0F;
	public float lastCounter = 0.0F;

	private final MobSpawnerLogicBetweenlands spawnerLogic = new MobSpawnerLogicBetweenlands() {
		@Override
		public void sendBlockEvent(int eventID) {
			TileEntityMobSpawnerBetweenlands.this.worldObj.addBlockEvent(TileEntityMobSpawnerBetweenlands.this.getPos(), BlockRegistry.MOB_SPAWNER, eventID, 0);
		}

		@Override
		public World getSpawnerWorld() {
			return TileEntityMobSpawnerBetweenlands.this.worldObj;
		}

		@Override
		public int getSpawnerX() {
			return TileEntityMobSpawnerBetweenlands.this.getPos().getX();
		}

		@Override
		public int getSpawnerY() {
			return TileEntityMobSpawnerBetweenlands.this.getPos().getY();
		}

		@Override
		public int getSpawnerZ() {
			return TileEntityMobSpawnerBetweenlands.this.getPos().getZ();
		}
	};


	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.spawnerLogic.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.spawnerLogic.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void update() {
		this.spawnerLogic.updateSpawner();
		this.lastCounter = this.counter;
		this.counter += 0.0085F;
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("entityType", this.getSpawnerLogic().getEntityNameToSpawn());
		return new SPacketUpdateTileEntity(pos, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		if (packet.getNbtCompound().hasKey("entityType")) {
			String entityType = packet.getNbtCompound().getString("entityType");
			this.getSpawnerLogic().setEntityName(entityType);
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.getSpawnerLogic().writeToNBT(nbt);
		return nbt;
	}

	@Override
	public boolean receiveClientEvent(int event, int parameter) {
		return this.spawnerLogic.setDelayToMin(event) || super.receiveClientEvent(event, parameter);
	}

	public MobSpawnerLogicBetweenlands getSpawnerLogic() {
		return this.spawnerLogic;
	}
}
