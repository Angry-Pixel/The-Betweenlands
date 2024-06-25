package thebetweenlands.common.world.storage;

import java.util.*;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.api.entity.spawning.IBiomeSpawnEntriesData;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;

public class BetweenlandsWorldStorage extends WorldStorageImpl {
	private BLEnvironmentEventRegistry environmentEventRegistry;
	private AspectManager aspectManager = new AspectManager();

	private Map<ICustomSpawnEntriesProvider, BiomeSpawnEntriesData> biomeSpawnEntriesData = new HashMap<>();

	protected final Set<ChunkPos> previousCheckedAmbientChunks = new HashSet<>();
	protected int ambienceTicks;
	protected int updateLCG = (new Random()).nextInt();

	protected List<SpiritTreeKillToken> spiritTreeKillTokens = new ArrayList<>();

	public BLEnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.environmentEventRegistry;
	}

	public AspectManager getAspectManager() {
		return this.aspectManager;
	}

//	@Override
//	public BiomeSpawnEntriesData getBiomeSpawnEntriesData(Biome biome) {
//		if (biome instanceof ICustomSpawnEntriesProvider) {
//			ICustomSpawnEntriesProvider provider = (ICustomSpawnEntriesProvider) biome;
//			BiomeSpawnEntriesData data = this.biomeSpawnEntriesData.get(provider);
//			if (data == null) {
//				this.biomeSpawnEntriesData.put(provider, data = new BiomeSpawnEntriesData(provider));
//			}
//			return data;
//		}
//		return null;
//	}

	@Override
	protected void init() {
		this.environmentEventRegistry = new BLEnvironmentEventRegistry(this.getLevel());
		this.environmentEventRegistry.init();

		if (!this.getLevel().isClientSide()) {
			for (IEnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.setDefaults();
				event.setLoaded();
			}
			this.aspectManager.loadAndPopulateStaticAspects(null, this.getLevel().registryAccess(), AspectManager.getAspectsSeed(this.getLevel().getServer().getLevel(Level.OVERWORLD).getSeed()));
		}

		this.ambienceTicks = this.getLevel().getRandom().nextInt(7000);
	}

	@Override
	public void readFromNBT(CompoundTag tag) {
		if (!this.getLevel().isClientSide()) {
			for (IEnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.readFromNBT(tag);
			}
			this.environmentEventRegistry.setDisabled(tag.getBoolean("eventsDisabled"));
			this.aspectManager.loadAndPopulateStaticAspects(tag.getCompound("itemAspects"), this.getLevel().registryAccess(), AspectManager.getAspectsSeed(this.getLevel().getServer().getLevel(Level.OVERWORLD).getSeed()));

			this.biomeSpawnEntriesData.clear();
//			if (tag.contains("biomeData", Tag.TAG_COMPOUND)) {
//				CompoundTag biomesNbt = tag.getCompound("biomeData");
//				for (String key : biomesNbt.getAllKeys()) {
//					Biome biome = Biome.REGISTRY.getObject(ResourceLocation.tryParse(key));
//					if (biome instanceof ICustomSpawnEntriesProvider) {
//						this.getBiomeSpawnEntriesData(biome).readFromNbt(biomesNbt.getCompound(key));
//					}
//				}
//			}

			this.spiritTreeKillTokens.clear();
			ListTag spiritTreeKillTokensNbt = tag.getList("spiritTreeKillTokens", Tag.TAG_COMPOUND);
			for (int i = 0; i < spiritTreeKillTokensNbt.size(); i++) {
				this.spiritTreeKillTokens.add(SpiritTreeKillToken.readFromNBT(spiritTreeKillTokensNbt.getCompound(i)));
			}
		}
	}

	@Override
	public void writeToNBT(CompoundTag tag) {
		if (!this.getLevel().isClientSide()) {
			for (IEnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.writeToNBT(tag);
			}
			tag.putBoolean("eventsDisabled", this.environmentEventRegistry.isDisabled());
			CompoundTag aspectData = new CompoundTag();
			this.aspectManager.saveStaticAspects(aspectData, this.getLevel().registryAccess());
			tag.put("itemAspects", aspectData);

//			CompoundTag biomesNbt = new CompoundTag();
//			for (BiomeBetweenlands biome : BiomeRegistry.REGISTERED_BIOMES) {
//				CompoundTag biomeSpawnEntriesNbt = new CompoundTag();
//				this.getBiomeSpawnEntriesData(biome).writeToNbt(biomeSpawnEntriesNbt);
//				biomesNbt.put(biome.getRegistryName().toString(), biomeSpawnEntriesNbt);
//			}
//			tag.put("biomeData", biomesNbt);

			ListTag spiritTreeKillTokensNbt = new ListTag();
			for (SpiritTreeKillToken token : this.spiritTreeKillTokens) {
				spiritTreeKillTokensNbt.add(token.writeToNBT());
			}
			tag.put("spiritTreeKillTokens", spiritTreeKillTokensNbt);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (this.getLevel().isClientSide() && this.getLevel().dimension() == DimensionRegistries.DIMENSION_KEY) {
			this.updateAmbientCaveSounds();
		}
	}

	protected void updateAmbientCaveSounds() {
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;

		if (player != null) {
			Set<ChunkPos> closeChunks = new HashSet<>();

			int cx = Mth.floor(player.getX() / 16.0D);
			int cz = Mth.floor(player.getZ() / 16.0D);

			int chunkRadius = 3;

			for (int ox = -chunkRadius; ox <= chunkRadius; ++ox) {
				for (int oz = -chunkRadius; oz <= chunkRadius; ++oz) {
					closeChunks.add(new ChunkPos(ox + cx, oz + cz));
				}
			}

			if (this.ambienceTicks > 0) {
				--this.ambienceTicks;
			} else {
				this.previousCheckedAmbientChunks.retainAll(closeChunks);

				if (this.previousCheckedAmbientChunks.size() >= closeChunks.size()) {
					this.previousCheckedAmbientChunks.clear();
				}

				int checkedChunks = 0;

				for (ChunkPos chunkpos : closeChunks) {
					if (!this.previousCheckedAmbientChunks.contains(chunkpos)) {
						int bx = chunkpos.x * 16;
						int bz = chunkpos.z * 16;

						ChunkAccess chunk = this.getLevel().getChunk(chunkpos.x, chunkpos.z);

						if (this.playAmbientCaveSounds(player, bx, bz, chunk)) {
							break;
						}

						this.previousCheckedAmbientChunks.add(chunkpos);
						++checkedChunks;

						if (checkedChunks >= 6) {
							break;
						}
					}
				}
			}
		}
	}

	protected boolean playAmbientCaveSounds(Player player, int x, int z, ChunkAccess chunk) {
		Level level = this.getLevel();

		this.updateLCG = this.updateLCG * 3 + 1013904223;
		int rnd = this.updateLCG >> 2;
		int xo = rnd & 15;
		int zo = rnd >> 8 & 15;
		int y = rnd >> 16 & 255;
		BlockPos pos = new BlockPos(xo + x, y, zo + z);
		BlockState state = chunk.getBlockState(pos);

		if (state.isAir() && level.getBrightness(LightLayer.BLOCK, pos) <= level.getRandom().nextInt(8) && level.getBrightness(LightLayer.SKY, pos) <= 0) {
			double dst = player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D);

			if (dst > 4.0D && dst < 256.0D) {
				level.playSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundRegistry.AMBIENT_CAVE_SPOOK, SoundSource.AMBIENT, 0.85F, 0.8F + level.getRandom().nextFloat() * 0.2F, false);
				this.ambienceTicks = level.getRandom().nextInt(7000) + 3000;
				return true;
			}
		}
		return false;
	}

	public static BetweenlandsWorldStorage forWorld(Level level) {
		BetweenlandsWorldStorage storage = forWorldNullable(level);
		if (storage == null) {
			throw new RuntimeException(String.format("World %s does not have BetweenlandsWorldStorage capability", level.dimension().location()));
		}
		return storage;
	}

	//TODO make data attachment
	@Nullable
	public static BetweenlandsWorldStorage forWorldNullable(Level level) {
//		IWorldStorage storage = level.getCapability(CAPABILITY_INSTANCE, null);
//		if (storage instanceof BetweenlandsWorldStorage) {
//			return (BetweenlandsWorldStorage) storage;
//		}
		return null;
	}

	public List<SpiritTreeKillToken> getSpiritTreeKillTokens() {
		return this.spiritTreeKillTokens;
	}

	public static class BiomeSpawnEntriesData implements IBiomeSpawnEntriesData {
		public final ICustomSpawnEntriesProvider biome;

		private final Object2LongMap<ResourceLocation> lastSpawnMap = new Object2LongOpenHashMap<>();

		protected BiomeSpawnEntriesData(ICustomSpawnEntriesProvider biome) {
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

		public void readFromNbt(CompoundTag tag) {
			this.lastSpawnMap.clear();
			for (ICustomSpawnEntry spawnEntry : this.biome.getCustomSpawnEntries()) {
				if (spawnEntry.isSaved()) {
					if (tag.contains(spawnEntry.getID().toString(), Tag.TAG_LONG)) {
						this.lastSpawnMap.put(spawnEntry.getID(), tag.getLong(spawnEntry.getID().toString()));
					}
				}
			}
		}

		public void writeToNbt(CompoundTag tag) {
			for (ICustomSpawnEntry spawnEntry : this.biome.getCustomSpawnEntries()) {
				if (spawnEntry.isSaved()) {
					if (this.lastSpawnMap.containsKey(spawnEntry.getID())) {
						tag.putLong(spawnEntry.getID().toString(), this.lastSpawnMap.get(spawnEntry.getID()));
					}
				}
			}
		}
	}
}
