package thebetweenlands.common.world.storage;

import java.util.HashMap;
import java.util.Map;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.entity.spawning.IBiomeSpawnEntriesData;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;

public class BetweenlandsWorldStorage extends WorldStorageImpl {
	private BLEnvironmentEventRegistry environmentEventRegistry;
	private AspectManager aspectManager = new AspectManager();

	private Map<BiomeBetweenlands, BiomeSpawnEntriesData> biomeSpawnEntriesData = new HashMap<>();

	private int environmentEventSyncTicks;

	public BLEnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.environmentEventRegistry;
	}

	public AspectManager getAspectManager() {
		return this.aspectManager;
	}

	@Override
	public BiomeSpawnEntriesData getBiomeSpawnEntriesData(Biome biome) {
		if(biome instanceof BiomeBetweenlands) {
			BiomeBetweenlands biomeBL = (BiomeBetweenlands) biome;
			BiomeSpawnEntriesData data = this.biomeSpawnEntriesData.get(biomeBL);
			if(data == null) {
				this.biomeSpawnEntriesData.put(biomeBL, data = new BiomeSpawnEntriesData(biomeBL));
			}
			return data;
		}
		return null;
	}

	@Override
	protected void init() {
		this.environmentEventRegistry = new BLEnvironmentEventRegistry(this.getWorld());
		this.environmentEventRegistry.init();

		if(!this.getWorld().isRemote) {
			for(IEnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.setDefaults();
				event.setLoaded();
			}
			this.aspectManager.loadAndPopulateStaticAspects(null, AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if(!this.getWorld().isRemote) {
			for(IEnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.readFromNBT(nbt);
			}
			this.environmentEventRegistry.setDisabled(nbt.getBoolean("eventsDisabled"));
			this.aspectManager.loadAndPopulateStaticAspects(nbt.getCompoundTag("itemAspects"), AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));

			this.biomeSpawnEntriesData.clear();
			if(nbt.hasKey("biomeData", Constants.NBT.TAG_COMPOUND)) {
				NBTTagCompound biomesNbt = nbt.getCompoundTag("biomeData");
				for(BiomeBetweenlands biome : BiomeRegistry.REGISTERED_BIOMES) {
					if(biomesNbt.hasKey(biome.getRegistryName().toString(), Constants.NBT.TAG_COMPOUND)) {
						NBTTagCompound biomeSpawnEntriesNbt = biomesNbt.getCompoundTag(biome.getRegistryName().toString());
						this.getBiomeSpawnEntriesData(biome).readFromNbt(biomeSpawnEntriesNbt);
					}
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if(!this.getWorld().isRemote) {
			for(IEnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.writeToNBT(nbt);
			}
			nbt.setBoolean("eventsDisabled", this.environmentEventRegistry.isDisabled());
			NBTTagCompound aspectData = new NBTTagCompound();
			this.aspectManager.saveStaticAspects(aspectData);
			nbt.setTag("itemAspects", aspectData);

			NBTTagCompound biomesNbt = new NBTTagCompound();
			for(BiomeBetweenlands biome : BiomeRegistry.REGISTERED_BIOMES) {
				NBTTagCompound biomeSpawnEntriesNbt = new NBTTagCompound();
				this.getBiomeSpawnEntriesData(biome).writeToNbt(biomeSpawnEntriesNbt);
				biomesNbt.setTag(biome.getRegistryName().toString(), biomeSpawnEntriesNbt);
			}
			nbt.setTag("biomeData", biomesNbt);
		}
	}

	public static BetweenlandsWorldStorage forWorld(World world) {
		if(world.hasCapability(CAPABILITY_INSTANCE, null)) {
			IWorldStorage storage = world.getCapability(CAPABILITY_INSTANCE, null);
			if(storage instanceof BetweenlandsWorldStorage) {
				return (BetweenlandsWorldStorage) storage;
			}
		}
		return null;
	}

	public static class BiomeSpawnEntriesData implements IBiomeSpawnEntriesData {
		public final BiomeBetweenlands biome;

		private final TObjectLongMap<ResourceLocation> lastSpawnMap = new TObjectLongHashMap<>();
		
		protected BiomeSpawnEntriesData(BiomeBetweenlands biome) {
			this.biome = biome;
		}

		@Override
		public long getLastSpawn(ICustomSpawnEntry spawnEntry) {
			return this.lastSpawnMap.containsKey(spawnEntry.getID()) ? this.lastSpawnMap.get(spawnEntry.getID()) : -1;
		}

		@Override
		public void setLastSpawn(ICustomSpawnEntry spawnEntry, long lastSpawn) {
			this.lastSpawnMap.put(spawnEntry.getID(), lastSpawn);
		}

		@Override
		public long removeLastSpawn(ICustomSpawnEntry spawnEntry) {
			return this.lastSpawnMap.remove(spawnEntry.getID());
		}

		public void readFromNbt(NBTTagCompound nbt) {
			this.lastSpawnMap.clear();
			for(ICustomSpawnEntry spawnEntry : this.biome.getSpawnEntries()) {
				if(spawnEntry.isSaved()) {
					if(nbt.hasKey(spawnEntry.getID().toString(), Constants.NBT.TAG_LONG)) {
						this.lastSpawnMap.put(spawnEntry.getID(), nbt.getLong(spawnEntry.getID().toString()));
					}
				}
			}
		}

		public void writeToNbt(NBTTagCompound nbt) {
			for(ICustomSpawnEntry spawnEntry : this.biome.getSpawnEntries()) {
				if(spawnEntry.isSaved()) {
					if(this.lastSpawnMap.containsKey(spawnEntry.getID())) {
						nbt.setLong(spawnEntry.getID().toString(), this.lastSpawnMap.get(spawnEntry.getID()));
					}
				}
			}
		}
	}

	@Override
	public void setEnvironmentEventSyncTicks(int ticks) {
		this.environmentEventSyncTicks = ticks;
	}

	@Override
	public int getEnvironmentEventSyncTicks() {
		return this.environmentEventSyncTicks;
	}
}
