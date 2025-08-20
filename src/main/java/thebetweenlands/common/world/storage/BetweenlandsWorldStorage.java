package thebetweenlands.common.world.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import thebetweenlands.api.entity.spawning.CustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.CustomSpawnEntry;
import thebetweenlands.api.environment.EnvironmentEvent;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;

public class BetweenlandsWorldStorage extends WorldStorageImpl {

	private final BLEnvironmentEventRegistry environmentEventRegistry = new BLEnvironmentEventRegistry();
	private final AspectManager aspectManager = new AspectManager();

	private final Map<CustomSpawnEntriesProvider, BiomeSpawnEntriesData> biomeSpawnEntriesData = new HashMap<>();

	protected final Set<ChunkPos> previousCheckedAmbientChunks = new HashSet<>();
	protected int ambienceTicks;
	protected int updateLCG = RandomSource.create().nextInt();

	private final List<SpiritTreeKillToken> spiritTreeKillTokens = new ArrayList<>();

	public static BetweenlandsWorldStorage create(IAttachmentHolder holder) {
		if(!(holder instanceof Level level)) {
//			throw new IllegalArgumentException("World storage attachments must be registered to a Level!");
			TheBetweenlands.LOGGER.warn("Tried to attach level-only world storage attachment to non-level {}", holder);
			return null;
		}
		BetweenlandsWorldStorage worldStorage = new BetweenlandsWorldStorage();
		worldStorage.setLevel(level);
//		worldStorage.init();
		return worldStorage;
	}
	
	public static BetweenlandsWorldStorage copy(BetweenlandsWorldStorage attachment, IAttachmentHolder holder, HolderLookup.Provider provider) {
		if(!(holder instanceof Level)) {
			TheBetweenlands.LOGGER.warn("Tried to copy level-only world storage attachment to non-level {}", holder);
			return null;
		}
		return WorldStorageSerializer.deserialize(holder, WorldStorageSerializer.serialize(attachment, provider), provider);
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
		super.init();
		Level level = getLevel();
		if (!level.isClientSide()) {
			for (EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
				event.setDefaults(level);
				event.setLoaded(level);
			}
			this.aspectManager.loadAndPopulateStaticAspects(null, level.registryAccess(), AspectManager.getAspectsSeed(level.getServer().getLevel(Level.OVERWORLD).getSeed()));
		}

		this.ambienceTicks = level.getRandom().nextInt(7000);
	}

	public BLEnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.environmentEventRegistry;
	}

	public AspectManager getAspectManager() {
		return this.aspectManager;
	}

	public Map<CustomSpawnEntriesProvider, BiomeSpawnEntriesData> getBiomeSpawnEntriesData() {
		return this.biomeSpawnEntriesData;
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if (level.isClientSide() && level.dimension() == DimensionRegistries.DIMENSION_KEY) {
			this.updateAmbientCaveSounds(level, BetweenlandsClient.getClientPlayer());
		}
	}

	protected void updateAmbientCaveSounds(Level level, Player player) {
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

						ChunkAccess chunk = level.getChunkSource().getChunkNow(chunkpos.x, chunkpos.z);

						if (chunk != null && this.playAmbientCaveSounds(level, player, bx, bz, chunk)) {
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

	protected boolean playAmbientCaveSounds(Level level, Player player, int x, int z, ChunkAccess chunk) {
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
				level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundRegistry.AMBIENT_CAVE_SPOOK.get(), SoundSource.AMBIENT, 0.85F, 0.8F + level.getRandom().nextFloat() * 0.2F, false);
				this.ambienceTicks = level.getRandom().nextInt(7000) + 3000;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets any existing Betweenlands World Storage for the Level (regardless of whether or not that level is The Betweenlands)
	 * @param level
	 * @return
	 */
	public static Optional<BetweenlandsWorldStorage> getExistingForLevel(Level level) {
		return level.getExistingData(AttachmentRegistry.WORLD_STORAGE);
	}

	public static BetweenlandsWorldStorage getOrCreateForLevel(Level level) {
		return level.getData(AttachmentRegistry.WORLD_STORAGE);
	}

	public static Optional<BetweenlandsWorldStorage> getForLevel(Level level) {
		if(TheBetweenlands.isBetweenlands(level)) {
			return Optional.ofNullable(getOrCreateForLevel(level));
		} else {
			return getExistingForLevel(level);
		}
	}
	
	public static BetweenlandsWorldStorage getForLevelNullable(Level level) {
		return getForLevel(level).orElse(null);
	}
	
	public static Optional<BetweenlandsWorldStorage> getExisting(Level level) {
		Level betweenlandsLevel = TheBetweenlands.getBetweenlands(level);
		if(betweenlandsLevel != null) {
			return getExistingForLevel(betweenlandsLevel);
		}
		return Optional.empty();
	}
	
	public static Optional<BetweenlandsWorldStorage> get(Level level) {
		Level betweenlandsLevel = TheBetweenlands.getBetweenlands(level);
		if(betweenlandsLevel != null) {
			return Optional.ofNullable(getOrCreateForLevel(betweenlandsLevel));
		}
		return Optional.empty();
	}

	@Nullable
	public static BetweenlandsWorldStorage getNullable(Level level) {
		return get(level).orElse(null);
	}
	
	public static BetweenlandsWorldStorage getOrThrow(Level level) {
		return get(level).orElseThrow(() -> new RuntimeException(String.format("World %s does not have BetweenlandsWorldStorage saved data attached", level.dimension().location())));
	}

	public static BetweenlandsWorldStorage getOrThrow(ServerLevelAccessor level) {
		return getOrThrow((Level)level.getLevel());
	}

	public static boolean isEventActive(Level level, Holder<EnvironmentEvent> event) {
		return BetweenlandsWorldStorage.get(level).map(storage -> storage.getEnvironmentEventRegistry().getActiveEvents().contains(event.value())).orElse(false);
	}

	public List<SpiritTreeKillToken> getSpiritTreeKillTokens() {
		return this.spiritTreeKillTokens;
	}

	public static class BiomeSpawnEntriesData implements thebetweenlands.api.entity.spawning.BiomeSpawnEntriesData {
		public final CustomSpawnEntriesProvider biome;

		private final Object2LongMap<ResourceLocation> lastSpawnMap = new Object2LongOpenHashMap<>();

		protected BiomeSpawnEntriesData(CustomSpawnEntriesProvider biome) {
			this.biome = biome;
		}

		@Override
		public long getLastSpawn(CustomSpawnEntry spawnEntry) {
			return this.lastSpawnMap.containsKey(spawnEntry.getID()) ? this.lastSpawnMap.getLong(spawnEntry.getID()) : -1;
		}

		@Override
		public void setLastSpawn(CustomSpawnEntry spawnEntry, long lastSpawn) {
			this.lastSpawnMap.put(spawnEntry.getID(), lastSpawn);
		}

		@Override
		public long removeLastSpawn(CustomSpawnEntry spawnEntry) {
			return this.lastSpawnMap.removeLong(spawnEntry.getID());
		}

		public void readFromNbt(CompoundTag tag) {
			this.lastSpawnMap.clear();
			for (CustomSpawnEntry spawnEntry : this.biome.getCustomSpawnEntries()) {
				if (spawnEntry.isSaved()) {
					if (tag.contains(spawnEntry.getID().toString(), Tag.TAG_LONG)) {
						this.lastSpawnMap.put(spawnEntry.getID(), tag.getLong(spawnEntry.getID().toString()));
					}
				}
			}
		}

		public void writeToNbt(CompoundTag tag) {
			for (CustomSpawnEntry spawnEntry : this.biome.getCustomSpawnEntries()) {
				if (spawnEntry.isSaved()) {
					if (this.lastSpawnMap.containsKey(spawnEntry.getID())) {
						tag.putLong(spawnEntry.getID().toString(), this.lastSpawnMap.getLong(spawnEntry.getID()));
					}
				}
			}
		}
	}
}
