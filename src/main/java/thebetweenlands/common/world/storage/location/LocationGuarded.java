package thebetweenlands.common.world.storage.location;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import thebetweenlands.api.storage.TickableStorage;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.BlockGuardDataPacket;
import thebetweenlands.common.network.ChangeBlockGuardSectionPacket;
import thebetweenlands.common.network.ClearBlockGuardPacket;
import thebetweenlands.common.world.storage.location.guard.BlockLocationGuard;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class LocationGuarded extends LocationStorage implements TickableStorage {
	private final BlockLocationGuard guard = new BlockLocationGuard() {
		@Override
		public boolean setGuarded(Level level, BlockPos pos, boolean guarded) {
			if (super.setGuarded(level, pos, guarded)) {
				LocationGuarded.this.setDirty(true);
				if (!LocationGuarded.this.getWatchers().isEmpty()) {
					LocationGuarded.this.queuedChanges.add(new BlockPos(pos.getX() / 16, pos.getY() / 16, pos.getZ() / 16));
				}
				return true;
			}
			return false;
		}

		@Override
		public void clear(Level level) {
			super.clear(level);
			LocationGuarded.this.setDirty(true);
			if (!LocationGuarded.this.getWatchers().isEmpty()) {
				LocationGuarded.this.queuedChanges.clear();
				LocationGuarded.this.queuedClear = true;
			}
		}
	};

	private final Set<BlockPos> queuedChanges = new HashSet<>();
	private boolean queuedClear;

	public LocationGuarded(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		super(worldStorage, id, region);
	}

	public LocationGuarded(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region, String name, EnumLocationType type) {
		super(worldStorage, id, region, name, type);
	}

	@Override
	public BlockLocationGuard getGuard() {
		return this.guard;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		this.readGuardNBT(nbt);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		this.writeGuardNBT(nbt);
		return nbt;
	}

	@Override
	public void onWatched(ServerPlayer player) {
		super.onWatched(player);
		BlockGuardDataPacket message = new BlockGuardDataPacket(this);
		this.sendDataToPlayer(message, player);
	}

	@Override
	public void tick(Level level) {
		if (this.queuedClear) {
			ClearBlockGuardPacket message = new ClearBlockGuardPacket(this);
			for (ServerPlayer watcher : this.getWatchers()) {
				this.sendDataToPlayer(message, watcher);
			}
			this.queuedClear = false;
		} else if (!this.queuedChanges.isEmpty()) {
			if (this.getWatchers().isEmpty()) {
				this.queuedChanges.clear();
			} else {
				Iterator<BlockPos> it = this.queuedChanges.iterator();
				while (it.hasNext()) {
					BlockPos pos = it.next();
					BlockPos worldPos = new BlockPos(pos.getX() * 16, pos.getY() * 16, pos.getZ() * 16);
					BlockLocationGuard.GuardChunkSection section = this.guard.getSection(worldPos);
					ChangeBlockGuardSectionPacket message = new ChangeBlockGuardSectionPacket(this, worldPos, section);
					for (ServerPlayer watcher : this.getWatchers()) {
						this.sendDataToPlayer(message, watcher);
					}
					it.remove();
				}
			}
		}
	}
}
