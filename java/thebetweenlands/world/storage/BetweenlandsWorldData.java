package thebetweenlands.world.storage;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;

public class BetweenlandsWorldData extends WorldSavedData {
	private NBTTagCompound data = new NBTTagCompound();
	private EnvironmentEventRegistry environmentEventRegistry = new EnvironmentEventRegistry();

	public BetweenlandsWorldData() {
		super(ModInfo.ID);
		this.environmentEventRegistry.init();
	}
	
	public BetweenlandsWorldData(String name) {
		super(name);
		this.environmentEventRegistry.init();
	}

	public EnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.environmentEventRegistry;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.data = compound.getCompoundTag(ModInfo.ID + ":worldData");
		for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
			event.readFromNBT(compound);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
			event.writeToNBT(compound);
		}
		compound.setTag(ModInfo.ID + ":worldData", this.data);
	}

	public NBTTagCompound getData() {
		return this.data;
	}

	private static final Map<World, BetweenlandsWorldData> CACHE = new WeakHashMap<World, BetweenlandsWorldData>();
	public static BetweenlandsWorldData forWorld(World world) {
		BetweenlandsWorldData cached = CACHE.get(world);
		if(cached != null) {
			return cached;
		}
		MapStorage storage = world.perWorldStorage;
		BetweenlandsWorldData result = (BetweenlandsWorldData)storage.loadData(BetweenlandsWorldData.class, ModInfo.ID);
		if (result == null) {
			result = new BetweenlandsWorldData();
			storage.setData(ModInfo.ID, result);
		}
		CACHE.put(world, result);
		return result;
	}
}
