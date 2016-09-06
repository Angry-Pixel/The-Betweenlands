package thebetweenlands.common.world.storage.chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ChunkDataContainer {
	private NBTTagCompound nbt;

	private List<ChunkDataBase> chunkDataHandlers;
	private Map<Class<? extends ChunkDataBase>, ChunkDataBase> classToHandler;

	ChunkDataContainer(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	/**
	 * Returns the container NBT
	 * @return
	 */
	final NBTTagCompound getNBT() {
		return this.nbt;
	}

	/**
	 * Sets the container NBT
	 * @param nbt
	 */
	@SideOnly(Side.CLIENT)
	public final void setNBT(NBTTagCompound nbt) {
		this.nbt = nbt;
	}

	/**
	 * Saves all handlers to the NBT
	 */
	public void saveHandlers() {
		if(this.chunkDataHandlers != null)
			for(ChunkDataBase handler : this.chunkDataHandlers) {
				NBTTagCompound nbt = handler.writeToNBT(new NBTTagCompound());
				this.nbt.setTag(handler.getName(), nbt);
			}
	}

	/**
	 * Adds a handler to this container
	 * @param handler
	 */
	public void addHandler(ChunkDataBase handler) {
		if(this.chunkDataHandlers == null)
			this.chunkDataHandlers = new ArrayList<>();
		if(this.classToHandler == null)
			this.classToHandler = new HashMap<>();
		this.chunkDataHandlers.add(handler);
		this.classToHandler.put(handler.getClass(), handler);
	}

	/**
	 * Returns a cached instance of a handler
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public <T extends ChunkDataBase> T getCachedHandler(Class<T> cls) {
		if(this.classToHandler == null)
			return null;
		return (T) this.classToHandler.get(cls);
	}

	/**
	 * Returns all loaded handlers
	 * @return
	 */
	public List<ChunkDataBase> getHandlers() {
		if(this.chunkDataHandlers != null)
			return Collections.unmodifiableList(this.chunkDataHandlers);
		return ImmutableList.of();
	}
}