package thebetweenlands.common.tile.spawner;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityMobSpawnerBetweenlands extends TileEntity implements ITickable {
	public float counter = 0.0F;
	public float lastCounter = 0.0F;

	private final MobSpawnerLogicBetweenlands spawnerLogic = new MobSpawnerLogicBetweenlands() {
		@Override
		public void broadcastEvent(int eventID) {
			TileEntityMobSpawnerBetweenlands.this.world.addBlockEvent(TileEntityMobSpawnerBetweenlands.this.getPos(), BlockRegistry.MOB_SPAWNER, eventID, 0);
		}

		@Override
		public World getSpawnerWorld() {
			return TileEntityMobSpawnerBetweenlands.this.world;
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

		@Override
		protected void spawnParticles() {
			if(this.getSpawnerWorld().rand.nextInt(2) == 0) {
				double rx = (double) (this.getSpawnerWorld().rand.nextFloat());
				double ry = (double) (this.getSpawnerWorld().rand.nextFloat());
				double rz = (double) (this.getSpawnerWorld().rand.nextFloat());

				double len = Math.sqrt(rx * rx + ry * ry + rz * rz);

				float counter = -TileEntityMobSpawnerBetweenlands.this.counter;

				BLParticles.SPAWNER.spawn(this.getSpawnerWorld(),
						(float) this.getSpawnerX() + rx, (float) this.getSpawnerY() + ry, (float) this.getSpawnerZ() + rz,
						ParticleFactory.ParticleArgs.get()
						.withMotion((rx - 0.5D) / len * 0.05D, (ry - 0.5D) / len * 0.05D, (rz - 0.5D) / len * 0.05D)
						.withColor(1.0F, MathHelper.clamp(4 + (float) Math.sin(counter) * 3, 0, 1), MathHelper.clamp((float) Math.sin(counter) * 2, 0, 1), 0.65F));
			}
		}
		
		@Override
		public MobSpawnerLogicBetweenlands setNextEntityName(String name) {
			super.setNextEntityName(name);
			TileEntityMobSpawnerBetweenlands te = TileEntityMobSpawnerBetweenlands.this;
			if(te != null && te.world != null) {
				IBlockState blockState = te.world.getBlockState(te.pos);
				te.world.notifyBlockUpdate(te.pos, blockState, blockState, 3);
			}
			return this;
		}
		
		@Override
		public MobSpawnerLogicBetweenlands setNextEntity(String name) {
			super.setNextEntity(name);
			TileEntityMobSpawnerBetweenlands te = TileEntityMobSpawnerBetweenlands.this;
			if(te != null && te.world != null) {
				IBlockState blockState = te.world.getBlockState(te.pos);
				te.world.notifyBlockUpdate(te.pos, blockState, blockState, 3);
			}
			return this;
		}
		
		@Override
		public MobSpawnerLogicBetweenlands setNextEntity(WeightedSpawnerEntity entity) {
			super.setNextEntity(entity);
			TileEntityMobSpawnerBetweenlands te = TileEntityMobSpawnerBetweenlands.this;
			if(te != null && te.world != null) {
				IBlockState blockState = te.world.getBlockState(te.pos);
				te.world.notifyBlockUpdate(te.pos, blockState, blockState, 3);
			}
			return this;
		}
		
		@Override
		public MobSpawnerLogicBetweenlands setEntitySpawnList(List<WeightedSpawnerEntity> entitySpawnList) {
			super.setEntitySpawnList(entitySpawnList);
			TileEntityMobSpawnerBetweenlands te = TileEntityMobSpawnerBetweenlands.this;
			if(te != null && te.world != null) {
				IBlockState blockState = te.world.getBlockState(te.pos);
				te.world.notifyBlockUpdate(te.pos, blockState, blockState, 3);
			}
			return this;
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
		nbt.setString("entityType", this.getSpawnerLogic().getEntityId().toString());
		return new SPacketUpdateTileEntity(pos, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		if (packet.getNbtCompound().hasKey("entityType")) {
			String entityType = packet.getNbtCompound().getString("entityType");
			this.getSpawnerLogic().setNextEntityName(entityType);
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
