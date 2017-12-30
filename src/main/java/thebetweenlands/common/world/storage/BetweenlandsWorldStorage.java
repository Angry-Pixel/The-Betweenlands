package thebetweenlands.common.world.storage;

import java.util.HashMap;
import java.util.Map;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.environment.EnvironmentEvent;
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

	public BiomeSpawnEntriesData getBiomeSpawnEntriesData(BiomeBetweenlands biome) {
		BiomeSpawnEntriesData data = this.biomeSpawnEntriesData.get(biome);
		if(data == null) {
			this.biomeSpawnEntriesData.put(biome, data = new BiomeSpawnEntriesData(biome));
		}
		return data;
	}

	@Override
	protected void init() {
		this.environmentEventRegistry = new BLEnvironmentEventRegistry(this.getWorld());
		this.environmentEventRegistry.init();

		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.setDefaults();
				event.setLoaded();
			}
			this.aspectManager.loadAndPopulateStaticAspects(null, AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
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
						for(BLSpawnEntry spawnEntry : biome.getSpawnEntries()) {
							if(spawnEntry.isSaved()) {
								if(biomeSpawnEntriesNbt.hasKey(String.valueOf(spawnEntry.id), Constants.NBT.TAG_COMPOUND)) {
									NBTTagCompound spawnEntryNbt = biomeSpawnEntriesNbt.getCompoundTag(String.valueOf(spawnEntry.id));
									this.getBiomeSpawnEntriesData(biome).readFromNbt(spawnEntryNbt);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if(!this.getWorld().isRemote) {
			for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.writeToNBT(nbt);
			}
			nbt.setBoolean("eventsDisabled", this.environmentEventRegistry.isDisabled());
			NBTTagCompound aspectData = new NBTTagCompound();
			this.aspectManager.saveStaticAspects(aspectData);
			nbt.setTag("itemAspects", aspectData);

			NBTTagCompound biomesNbt = new NBTTagCompound();
			for(BiomeBetweenlands biome : BiomeRegistry.REGISTERED_BIOMES) {
				NBTTagCompound biomeSpawnEntriesNbt = new NBTTagCompound();
				for(BLSpawnEntry spawnEntry : biome.getSpawnEntries()) {
					if(spawnEntry.isSaved()) {
						NBTTagCompound spawnEntryNbt = new NBTTagCompound();
						this.getBiomeSpawnEntriesData(biome).writeToNbt(spawnEntryNbt);
						biomeSpawnEntriesNbt.setTag(String.valueOf(spawnEntry.id), spawnEntryNbt);
					}
				}
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

	public static class BiomeSpawnEntriesData {
		public final BiomeBetweenlands biome;

		private final TIntLongMap lastSpawnMap = new TIntLongHashMap();

		protected BiomeSpawnEntriesData(BiomeBetweenlands biome) {
			this.biome = biome;
		}

		public long getLastSpawn(int id) {
			return this.lastSpawnMap.containsKey(id) ? this.lastSpawnMap.get(id) : -1;
		}

		public void setLastSpawn(int id, long lastSpawn) {
			this.lastSpawnMap.put(id, lastSpawn);
		}

		public void readFromNbt(NBTTagCompound nbt) {
			this.lastSpawnMap.clear();
			for(BLSpawnEntry spawnEntry : this.biome.getSpawnEntries()) {
				if(nbt.hasKey(String.valueOf(spawnEntry.id), Constants.NBT.TAG_LONG)) {
					this.lastSpawnMap.put(spawnEntry.id, nbt.getLong(String.valueOf(spawnEntry.id)));
				}
			}
		}

		public void writeToNbt(NBTTagCompound nbt) {
			for(BLSpawnEntry spawnEntry : this.biome.getSpawnEntries()) {
				if(this.lastSpawnMap.containsKey(spawnEntry.id)) {
					nbt.setLong(String.valueOf(spawnEntry.id), this.lastSpawnMap.get(spawnEntry.id));
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
