package thebetweenlands.common.world.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.api.storage.*;
import thebetweenlands.common.network.AddLocalStoragePacket;
import thebetweenlands.common.network.RemoveLocalStoragePacket;
import thebetweenlands.common.world.storage.operation.DeferredLinkOperation;

import java.util.*;

public abstract class LocalStorageImpl implements ILocalStorage {
	private final IWorldStorage worldStorage;
	private final LocalRegion region;
	private final StorageID id;

	private boolean dirty;

	private final List<ChunkPos> linkedChunks = new ArrayList<>();
	private final List<LocalStorageReference> loadedReferences = new ArrayList<>();

	private final List<ServerPlayer> watchers = new ArrayList<>();
	private final List<ServerPlayer> duplicateWatchers = new ArrayList<>();

	private boolean loaded = false;

	public LocalStorageImpl(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region) {
		this.worldStorage = worldStorage;
		this.id = id;
		this.region = region;
	}

	@Override
	public IWorldStorage getWorldStorage() {
		return this.worldStorage;
	}

	@Override
	public StorageID getID() {
		return this.id;
	}

	@Override
	public LocalRegion getRegion() {
		return this.region;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		this.readReferenceChunks(nbt);
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		this.writeReferenceChunks(nbt);
		return nbt;
	}

	@Override
	public void readInitialPacket(CompoundTag nbt) {
		this.readReferenceChunks(nbt);
	}

	@Override
	public CompoundTag writeInitialPacket(CompoundTag nbt) {
		this.writeReferenceChunks(nbt);
		return nbt;
	}

	protected final void writeReferenceChunks(CompoundTag nbt) {
		ListTag referenceChunkList = new ListTag();
		for (ChunkPos referenceChunk : this.linkedChunks) {
			CompoundTag referenceChunkNbt = new CompoundTag();
			referenceChunkNbt.putInt("x", referenceChunk.x);
			referenceChunkNbt.putInt("z", referenceChunk.z);
			referenceChunkList.add(referenceChunkNbt);
		}
		nbt.put("ReferenceChunks", referenceChunkList);
	}

	protected final void readReferenceChunks(CompoundTag nbt) {
		this.linkedChunks.clear();
		ListTag referenceChunkList = nbt.getList("ReferenceChunks", Tag.TAG_COMPOUND);
		for (int i = 0; i < referenceChunkList.size(); i++) {
			CompoundTag referenceChunkNbt = referenceChunkList.getCompound(i);
			this.linkedChunks.add(new ChunkPos(referenceChunkNbt.getInt("x"), referenceChunkNbt.getInt("z")));
		}
	}

	@Override
	public void markDirty() {
		this.setDirty(true);
	}

	/**
	 * Sets whether the data is dirty and needs to be saved to the file
	 *
	 * @param dirty
	 * @return
	 */
	@Override
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public List<ChunkPos> getLinkedChunks() {
		return Collections.unmodifiableList(this.linkedChunks);
	}

	@Override
	public void setLinkedChunks(List<ChunkPos> linkedChunks) {
		this.linkedChunks.clear();
		this.linkedChunks.addAll(linkedChunks);
	}

	@Override
	public void onLoaded() {
		this.loaded = true;
	}

	@Override
	public void onUnloaded() {
		this.loaded = false;
	}

	@Override
	public void onRemoved() {

	}

	@Override
	public void onRemoving() {
		//Notify clients when shared storage is removed.
		//This is done before onRemoved so that the list of watchers is not yet empty.
		if (!this.getWorldStorage().getLevel().isClientSide()) {
			if (!this.getWatchers().isEmpty()) {
				this.sendMessageToAllWatchers(new RemoveLocalStoragePacket(this.getID()));
			}
		}
	}

	@Override
	public Collection<LocalStorageReference> getLoadedReferences() {
		return Collections.unmodifiableCollection(this.loadedReferences);
	}

	@Override
	public boolean loadReference(LocalStorageReference reference) {
		if (!this.loadedReferences.contains(reference)) {
			return this.loadedReferences.add(reference);
		}
		return false;
	}

	@Override
	public boolean unloadReference(LocalStorageReference reference) {
		return this.loadedReferences.remove(reference);
	}

	@Override
	public boolean addWatcher(IChunkStorage chunkStorage, ServerPlayer player) {
		boolean contained = this.duplicateWatchers.contains(player);
		this.duplicateWatchers.add(player);
		if (!contained) {
			this.watchers.add(player);
			this.onWatched(player);
		}
		return !contained;
	}

	/**
	 * Called when a new watcher is added
	 *
	 * @param player
	 */
	protected void onWatched(ServerPlayer player) {
		this.sendDataToPlayer(new AddLocalStoragePacket(this), player);
	}

	@Override
	public boolean removeWatcher(IChunkStorage chunkStorage, ServerPlayer player) {
		boolean contained = this.duplicateWatchers.remove(player);
		if (contained && !this.duplicateWatchers.contains(player)) {
			this.watchers.remove(player);
			this.onUnwatched(player);
		}
		return contained;
	}

	/**
	 * Called when a player stops watching this local storage
	 *
	 * @param player
	 */
	protected void onUnwatched(ServerPlayer player) {

	}

	@Override
	public Collection<ServerPlayer> getWatchers() {
		return Collections.unmodifiableCollection(this.watchers);
	}

	@Override
	public boolean unlinkAllChunks() {
		boolean changed = false;
		boolean allUnlinked = true;
		List<ChunkPos> chunks = new ArrayList<>(this.linkedChunks.size());
		chunks.addAll(this.linkedChunks);
		Iterator<ChunkPos> it = chunks.iterator();
		ChunkPos pos = null;
		while (it.hasNext()) {
			pos = it.next();
			ChunkAccess chunk = this.worldStorage.getLevel().getChunk(pos.x, pos.z);
			IChunkStorage chunkData = this.worldStorage.getChunkStorage(chunk);
			if (chunkData == null || !chunkData.unlinkLocalStorage(this)) {
				allUnlinked = false;
			} else if (chunkData != null) {
				changed = true;
			}
		}
		if (changed) {
			this.setDirty(true);
		}
		this.linkedChunks.clear();
		return allUnlinked;
	}

	@Override
	public boolean linkChunk(ChunkAccess chunk) {
		ChunkPos chunkPos = chunk.getPos();
		if (!this.linkedChunks.contains(chunkPos)) {
			IChunkStorage chunkData = this.worldStorage.getChunkStorage(chunk);
			if (chunkData != null && chunkData.linkLocalStorage(this)) {
				if (this.linkedChunks.add(chunkPos)) {
					//TODO Send packet
					this.setDirty(true);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void linkChunkDeferred(ChunkPos chunk) {
		if (!this.linkedChunks.contains(chunk) && this.linkedChunks.add(chunk)) {
			this.setDirty(true);
			this.worldStorage.getLocalStorageHandler().queueDeferredOperation(chunk, new DeferredLinkOperation(new LocalStorageReference(chunk, this.getID(), this.getRegion())));
		}
	}

	@Override
	public boolean unlinkChunk(ChunkAccess chunk) {
		ChunkPos chunkPos = chunk.getPos();
		if (this.linkedChunks.contains(chunkPos)) {
			IChunkStorage chunkData = this.worldStorage.getChunkStorage(chunk);
			if (chunkData != null) {
				chunkData.unlinkLocalStorage(this);
				if (this.linkedChunks.remove(chunkPos)) {
					//TODO Send packet
					this.setDirty(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sends the message to all watching players
	 */
	protected void sendMessageToAllWatchers(CustomPacketPayload packet) {
		for (ServerPlayer watcher : this.getWatchers()) {
			this.sendDataToPlayer(packet, watcher);
		}
	}

	/**
	 * Sends the message to a player
	 *
	 * @param player
	 */
	protected void sendDataToPlayer(CustomPacketPayload packet, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, packet);
	}

	@Override
	public boolean isLoaded() {
		return this.loaded;
	}
}
