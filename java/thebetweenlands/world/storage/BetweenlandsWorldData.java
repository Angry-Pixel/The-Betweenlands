package thebetweenlands.world.storage;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import thebetweenlands.lib.ModInfo;

public class BetweenlandsWorldData extends WorldSavedData {
	private NBTTagCompound data = new NBTTagCompound();

	private int timeToFog;
	private boolean hasDenseFog;

	public BetweenlandsWorldData() {
		super(ModInfo.ID);
	}
	
	public BetweenlandsWorldData(String name) {
		super(name);
	}

	public void setTimeToFogNBT(int time) {
		this.timeToFog = time;
		this.markDirty();
	}

	public void setDenseFog(boolean fog) {
		this.hasDenseFog = fog;
		this.markDirty();
	}

	public int getTimeToFog() {
		return this.timeToFog;
	}

	public boolean getDenseFog() {
		return this.hasDenseFog;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		this.data = compound.getCompoundTag(ModInfo.ID);
		this.hasDenseFog = this.data.getBoolean("hasDenseFog");
		this.timeToFog = this.data.getInteger("timeToFog");
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		this.data.setBoolean("hasDenseFog", this.hasDenseFog);
		this.data.setInteger("timeToFog", this.timeToFog);
		compound.setTag(ModInfo.ID, this.data);
	}

	public NBTTagCompound getData() {
		return this.data;
	}

	private static final Map<World, BetweenlandsWorldData> CACHE = new HashMap<>();
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
