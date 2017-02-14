package thebetweenlands.common.world.storage.world.shared.location;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.network.clientbound.MessageBlockGuardData;
import thebetweenlands.common.network.clientbound.MessageBlockGuardSectionChange;
import thebetweenlands.common.network.clientbound.MessageClearBlockGuard;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.SharedRegion;
import thebetweenlands.common.world.storage.world.shared.location.guard.BlockLocationGuard;
import thebetweenlands.common.world.storage.world.shared.location.guard.BlockLocationGuard.GuardChunkSection;

public class LocationGuarded extends LocationStorage {
	private BlockLocationGuard guard = new BlockLocationGuard() {
		@Override
		public void setGuarded(World world, BlockPos pos, boolean guarded) {
			super.setGuarded(world, pos, guarded);
			if(!LocationGuarded.this.getWatchers().isEmpty()) {
				LocationGuarded.this.queuedChanges.add(new BlockPos(pos.getX() / 16, pos.getY() / 16, pos.getZ() / 16));
			}
		}

		@Override
		public void clear(World world) {
			super.clear(world);
			if(!LocationGuarded.this.getWatchers().isEmpty()) {
				LocationGuarded.this.queuedChanges.clear();
				LocationGuarded.this.queuedClear = true;
			}
		}
	};

	private Set<BlockPos> queuedChanges = new HashSet<>();
	private boolean queuedClear;

	public LocationGuarded(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region) {
		super(worldStorage, id, region);
	}

	public LocationGuarded(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region, String name, EnumLocationType type) {
		super(worldStorage, id, region, name, type);
	}

	@Override
	public BlockLocationGuard getGuard() {
		return this.guard;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readGuardNBT(nbt);
	}

	@Override
	public void readFromPacketNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.writeGuardNBT(nbt);
		return nbt;
	}

	@Override
	public NBTTagCompound writeToPacketNBT(NBTTagCompound nbt) {
		return super.writeToNBT(nbt);
	}

	@Override
	public void onWatched(ChunkDataBase chunkStorage, EntityPlayerMP player) {
		super.onWatched(chunkStorage, player);
		MessageBlockGuardData message = new MessageBlockGuardData(this);
		this.sendDataToPlayer(message, player);
	}

	@Override
	protected void updateTracker() {
		super.updateTracker();

		if(this.queuedClear) {
			MessageClearBlockGuard message = new MessageClearBlockGuard(this); 
			for(EntityPlayerMP watcher : this.getWatchers()) {
				this.sendDataToPlayer(message, watcher);
			}
			this.queuedClear = false;
		} else if(!this.queuedChanges.isEmpty()) {
			if(this.getWatchers().isEmpty()) {
				this.queuedChanges.clear();
			} else {
				Iterator<BlockPos> it = this.queuedChanges.iterator();
				while(it.hasNext()) {
					BlockPos pos = it.next();
					BlockPos worldPos = new BlockPos(pos.getX() * 16, pos.getY() * 16, pos.getZ() * 16);
					GuardChunkSection section = this.guard.getSection(worldPos);
					if(section != null) {
						MessageBlockGuardSectionChange message = new MessageBlockGuardSectionChange(this, worldPos, section); 
						for(EntityPlayerMP watcher : this.getWatchers()) {
							this.sendDataToPlayer(message, watcher);
						}
					}
					it.remove();
				}
			}
		}
	}
}