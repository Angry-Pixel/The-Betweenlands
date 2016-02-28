package thebetweenlands.world.storage.world;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.world.WorldEvent;

public abstract class WorldDataBase extends WorldSavedData {
	private World world;

	private NBTTagCompound data = new NBTTagCompound();

	public WorldDataBase(String name) {
		super(name);
	}

	public World getWorld() {
		return this.world;
	}

	@Override
	public final void readFromNBT(NBTTagCompound compound) {
		this.data = compound.getCompoundTag("worldData");
	}

	@Override
	public final void writeToNBT(NBTTagCompound compound) {
		this.save();
		compound.setTag("worldData", this.data);
	}

	/**
	 * Load data here
	 */
	protected abstract void load();

	/**
	 * Save data here
	 */
	protected abstract void save();

	/**
	 * Called before loading data and setting defaults
	 */
	protected abstract void init();

	/**
	 * Called when this data is created for the first time for this world
	 */
	protected abstract void setDefaults();

	public NBTTagCompound getData() {
		return this.data;
	}

	private static class WorldDataTypePair {
		private World world;
		private Class<? extends WorldDataBase> data;
		private WorldDataTypePair(World world, Class<? extends WorldDataBase> data) {
			this.world = world;
			this.data = data;
		}
	}

	private static final Map<WorldDataTypePair, WorldDataBase> CACHE = new HashMap<WorldDataTypePair, WorldDataBase>();

	private static WorldDataBase getMatchingData(World world, Class<? extends WorldDataBase> clazz) {
		for(Entry<WorldDataTypePair, WorldDataBase> cacheEntry : CACHE.entrySet()) {
			WorldDataTypePair pair = cacheEntry.getKey();
			if(pair.world == world && pair.data.equals(clazz)) {
				return cacheEntry.getValue();
			}
		}
		return null;
	}

	public static <T extends WorldDataBase> T forWorld(World world, Class<T> clazz) {
		WorldDataBase cached = getMatchingData(world, clazz);
		if(cached != null) {
			cached.world = world;
			return (T) cached;
		}
		MapStorage storage = world.perWorldStorage;
		T newInstance = null;
		try {
			newInstance = clazz.getConstructor().newInstance();
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		WorldDataBase result = (WorldDataBase)storage.loadData(clazz, newInstance.mapName);
		if (result == null) {
			result = newInstance;
			result.world = world;
			result.init();
			result.setDefaults();
			result.save();
			storage.setData(result.mapName, result);
		} else {
			result.world = world;
			result.init();
			result.load();
		}
		CACHE.put(new WorldDataTypePair(world, clazz), result);
		return (T) result;
	}

	public static final WorldUnloadHandler WORLD_UNLOAD_HANDLER = new WorldUnloadHandler();

	public static class WorldUnloadHandler {
		private WorldUnloadHandler(){}

		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload event) {
			Iterator<Entry<WorldDataTypePair, WorldDataBase>> cacheIT = CACHE.entrySet().iterator();
			while(cacheIT.hasNext()) {
				World world = cacheIT.next().getKey().world;
				if(world.equals(event.world)) {
					cacheIT.remove();
				}
			}
		}
	}
}
