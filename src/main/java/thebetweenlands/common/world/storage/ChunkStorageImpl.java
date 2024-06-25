package thebetweenlands.common.world.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.ITickable;
import thebetweenlands.api.storage.IChunkStorage;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.ILocalStorageHandle;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalStorageReference;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.network.SyncChunkStoragePacket;
import thebetweenlands.common.network.SyncLocalStorageReferencesPacket;

public abstract class ChunkStorageImpl implements IChunkStorage, ITickable {
	protected final IWorldStorage worldStorage;
	protected final Level level;
	protected final ChunkAccess chunk;

	private Set<ServerPlayer> watchers = new HashSet<>();
	private boolean dirty = false;

	private final List<LocalStorageReference> localStorageReferences = new ArrayList<>();

	protected boolean syncStorageLinks = false;

	public ChunkStorageImpl(IWorldStorage worldStorage, ChunkAccess chunk) {
		this.worldStorage = worldStorage;
		this.level = worldStorage.getLevel();
		this.chunk = chunk;
	}

	@Override
	public IWorldStorage getWorldStorage() {
		return this.worldStorage;
	}

	@Override
	public ChunkAccess getChunk() {
		return this.chunk;
	}

	@Override
	public void init() {

	}

	@Override
	public void onUnload() {
		for(LocalStorageReference ref : this.localStorageReferences) {
			ILocalStorage localStorage = this.getWorldStorage().getLocalStorageHandler().getLocalStorage(ref.getID());
			if(localStorage != null) {
				localStorage.unloadReference(ref);
				if(localStorage.getLoadedReferences().isEmpty()) {
					this.getWorldStorage().getLocalStorageHandler().unloadLocalStorage(localStorage);
				}
			}
		}
	}

	@Override
	public void setDefaults() {

	}

	@Override
	public void readFromNBT(CompoundTag nbt, boolean packet) {
		this.readLocalStorageReferences(nbt);
	}

	@Override
	public CompoundTag readLocalStorageReferences(CompoundTag nbt) {
		this.localStorageReferences.clear();
		ListTag localReferenceList = nbt.getList("LocalStorageReferences", Tag.TAG_COMPOUND);
		for(int i = 0; i < localReferenceList.size(); i++) {
			this.localStorageReferences.add(LocalStorageReference.readFromNBT((CompoundTag)localReferenceList.get(i)));
		}

		Iterator<LocalStorageReference> refIT = this.localStorageReferences.iterator();
		while(refIT.hasNext()) {
			LocalStorageReference ref = refIT.next();

			try(ILocalStorageHandle handle = this.worldStorage.getLocalStorageHandler().getOrLoadLocalStorage(ref)) {
				//Load reference if properly linked
				if(handle != null && handle.get().getLinkedChunks().contains(this.chunk.getPos())) {
					handle.get().loadReference(ref);
				} else if(!this.worldStorage.getLevel().isClientSide()) {
					//Local storage doesn't exist or chunk shouldn't be linked to local storage, remove link
					refIT.remove();
				}
			}
		}
		return nbt;
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt, boolean packet) {
		this.writeLocalStorageReferences(nbt);
		return nbt;
	}

	@Override
	public CompoundTag writeLocalStorageReferences(CompoundTag nbt) {
		if(!this.localStorageReferences.isEmpty()) {
			ListTag localReferenceList = new ListTag();
			for(LocalStorageReference ref : this.localStorageReferences) {
				if(ref.getHandle() == null) {
					localReferenceList.add(ref.writeToNBT(new CompoundTag()));
				}
			}
			nbt.put("LocalStorageReferences", localReferenceList);
		}
		return nbt;
	}

	@Override
	public boolean addWatcher(ServerPlayer player) {
		if(this.watchers.add(player)) {
			this.onWatched(player);
			return true;
		}
		return false;
	}

	/**
	 * Called when a new watcher is added
	 * @param player
	 */
	protected void onWatched(ServerPlayer player) {
		for(LocalStorageReference ref : this.localStorageReferences) {
			ILocalStorage localStorage = this.getWorldStorage().getLocalStorageHandler().getLocalStorage(ref.getID());
			if(localStorage != null) {
				localStorage.addWatcher(this, player);
			}
		}

		PacketDistributor.sendToPlayer(player, new SyncChunkStoragePacket(this));
	}

	@Override
	public boolean removeWatcher(ServerPlayer player) {
		if(this.watchers.remove(player)) {
			this.onUnwatched(player);
			return true;
		}
		return false;
	}

	/**
	 * Called when a player stops watching this chunk
	 * @param player
	 */
	protected void onUnwatched(ServerPlayer player) {
		for(LocalStorageReference ref : this.localStorageReferences) {
			ILocalStorage localStorage = this.getWorldStorage().getLocalStorageHandler().getLocalStorage(ref.getID());
			if(localStorage != null) {
				localStorage.removeWatcher(this, player);
			}
		}
	}

	@Override
	public Set<ServerPlayer> getWatchers() {
		return Collections.unmodifiableSet(this.watchers);
	}

	@Override
	public void markDirty() {
		this.setDirty(true);
	}

	@Override
	public void setDirty(boolean dirty) {
		if(dirty) {
			this.chunk.setUnsaved(true);
		}
		this.dirty = dirty;
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public LocalStorageReference getReference(StorageID id) {
		for(LocalStorageReference ref : this.localStorageReferences) {
			if(id.equals(ref.getID()))
				return ref;
		}
		return null;
	}

	@Override
	public boolean unlinkLocalStorage(ILocalStorage storage) {
		StorageID id = storage.getID();
		List<LocalStorageReference> unlinkedReferences = new ArrayList<>();
		Iterator<LocalStorageReference> referenceIT = this.localStorageReferences.iterator();

		while(referenceIT.hasNext()) {
			LocalStorageReference ref = referenceIT.next();
			if(id.equals(ref.getID())) {
				unlinkedReferences.add(ref);
				referenceIT.remove();
			}
		}

		if(!unlinkedReferences.isEmpty()) {
			//Make sure the local storage is also unlinked from this chunk
			for(LocalStorageReference ref : unlinkedReferences) {
				if(storage.getLoadedReferences().contains(ref)) {
					storage.unlinkChunk(this.chunk);
				}
			}

			this.markDirty();

			//Remove watchers
			for(ServerPlayer watcher : this.getWatchers()) {
				storage.removeWatcher(this, watcher);
			}

			this.syncStorageLinks = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean linkLocalStorage(ILocalStorage storage) {
		StorageID id = storage.getID();
		for(LocalStorageReference ref : this.localStorageReferences) {
			if(id.equals(ref.getID()))
				return false;
		}

		LocalStorageReference ref = new LocalStorageReference(this.chunk.getPos(), id, storage.getRegion());

		if(this.localStorageReferences.add(ref)) {
			this.markDirty();

			//Make sure that the storage also knows about the reference
			storage.loadReference(ref);

			//Add watchers
			for(ServerPlayer watcher : this.getWatchers()) {
				storage.addWatcher(this, watcher);
			}

			this.syncStorageLinks = true;
			return true;
		}

		return false;
	}

	@Override
	public Collection<LocalStorageReference> getLocalStorageReferences() {
		return Collections.unmodifiableCollection(this.localStorageReferences);
	}

	@Override
	public void tick() {
		if(this.syncStorageLinks) {
			this.syncStorageLinks = false;

			SyncLocalStorageReferencesPacket message = new SyncLocalStorageReferencesPacket(this);
			for(ServerPlayer watcher : this.watchers) {
				PacketDistributor.sendToPlayer(watcher, message);
			}
		}
	}
}
