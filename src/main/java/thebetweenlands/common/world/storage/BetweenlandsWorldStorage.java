package thebetweenlands.common.world.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.spawning.IBiomeSpawnEntriesData;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntriesProvider;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.api.environment.IEnvironmentEvent;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;
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

	@Override
	public BiomeSpawnEntriesData getBiomeSpawnEntriesData(Biome biome) {
		if(biome instanceof ICustomSpawnEntriesProvider) {
			ICustomSpawnEntriesProvider provider = (ICustomSpawnEntriesProvider) biome;
			BiomeSpawnEntriesData data = this.biomeSpawnEntriesData.get(provider);
			if(data == null) {
				this.biomeSpawnEntriesData.put(provider, data = new BiomeSpawnEntriesData(provider));
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

		this.ambienceTicks = this.getWorld().rand.nextInt(7000);
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
				for(String key : biomesNbt.getKeySet()) {
					Biome biome = Biome.REGISTRY.getObject(new ResourceLocation(key));
					if(biome instanceof ICustomSpawnEntriesProvider) {
						this.getBiomeSpawnEntriesData(biome).readFromNbt(biomesNbt.getCompoundTag(key));
					}
				}
			}

			this.spiritTreeKillTokens.clear();
			NBTTagList spiritTreeKillTokensNbt = nbt.getTagList("spiritTreeKillTokens", Constants.NBT.TAG_COMPOUND);
			for(int i = 0; i < spiritTreeKillTokensNbt.tagCount(); i++) {
				this.spiritTreeKillTokens.add(SpiritTreeKillToken.readFromNBT(spiritTreeKillTokensNbt.getCompoundTagAt(i)));
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

			NBTTagList spiritTreeKillTokensNbt = new NBTTagList();
			for(SpiritTreeKillToken token : this.spiritTreeKillTokens) {
				spiritTreeKillTokensNbt.appendTag(token.writeToNBT());
			}
			nbt.setTag("spiritTreeKillTokens", spiritTreeKillTokensNbt);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if(this.getWorld().isRemote && this.getWorld().provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
			this.updateAmbientCaveSounds();
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateAmbientCaveSounds() {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;

		if(player != null) {
			Set<ChunkPos> closeChunks = new HashSet<>();

			int cx = MathHelper.floor(player.posX / 16.0D);
			int cz = MathHelper.floor(player.posZ / 16.0D);

			int chunkRadius = 3;

			for(int ox = -chunkRadius; ox <= chunkRadius; ++ox) {
				for(int oz = -chunkRadius; oz <= chunkRadius; ++oz) {
					closeChunks.add(new ChunkPos(ox + cx, oz + cz));
				}
			}

			if(this.ambienceTicks > 0) {
				--this.ambienceTicks;
			} else {
				this.previousCheckedAmbientChunks.retainAll(closeChunks);

				if(this.previousCheckedAmbientChunks.size() >= closeChunks.size()) {
					this.previousCheckedAmbientChunks.clear();
				}

				int checkedChunks = 0;

				for(ChunkPos chunkpos : closeChunks) {
					if(!this.previousCheckedAmbientChunks.contains(chunkpos)) {
						int bx = chunkpos.x * 16;
						int bz = chunkpos.z * 16;

						Chunk chunk = this.getWorld().getChunk(chunkpos.x, chunkpos.z);

						if(this.playAmbientCaveSounds(player, bx, bz, chunk)) {
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

	@SideOnly(Side.CLIENT)
	protected boolean playAmbientCaveSounds(EntityPlayer player, int x, int z, Chunk chunk) {
		World world = this.getWorld();

		this.updateLCG = this.updateLCG * 3 + 1013904223;
		int rnd = this.updateLCG >> 2;
		int xo = rnd & 15;
		int zo = rnd >> 8 & 15;
		int y = rnd >> 16 & 255;
		BlockPos pos = new BlockPos(xo + x, y, zo + z);
		IBlockState state = chunk.getBlockState(pos);

		if(state.getMaterial() == Material.AIR && world.getLight(pos) <= world.rand.nextInt(8) && world.getLightFor(EnumSkyBlock.SKY, pos) <= 0) {
			double dst = player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);

			if(dst > 4.0D && dst < 256.0D) {
				world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundRegistry.AMBIENT_CAVE_SPOOK, SoundCategory.AMBIENT, 0.85F, 0.8F + world.rand.nextFloat() * 0.2F, false);
				this.ambienceTicks = world.rand.nextInt(7000) + 3000;
				return true;
			}
		}
		return false;
	}

	public static BetweenlandsWorldStorage forWorld(World world) {
		BetweenlandsWorldStorage storage = forWorldNullable(world);
		if(storage == null) {
			throw new RuntimeException(String.format("World %s (%s) does not have BetweenlandsWorldStorage capability", world.getWorldInfo().getWorldName(), world.provider.getClass().getName()));
		}
		return storage;
	}

	@Nullable
	public static BetweenlandsWorldStorage forWorldNullable(World world) {
		IWorldStorage storage = world.getCapability(CAPABILITY_INSTANCE, null);
		if(storage instanceof BetweenlandsWorldStorage) {
			return (BetweenlandsWorldStorage) storage;
		}
		return null;
	}

	public List<SpiritTreeKillToken> getSpiritTreeKillTokens() {
		return this.spiritTreeKillTokens;
	}

	public static class BiomeSpawnEntriesData implements IBiomeSpawnEntriesData {
		public final ICustomSpawnEntriesProvider biome;

		private final TObjectLongMap<ResourceLocation> lastSpawnMap = new TObjectLongHashMap<>();

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

		public void readFromNbt(NBTTagCompound nbt) {
			this.lastSpawnMap.clear();
			for(ICustomSpawnEntry spawnEntry : this.biome.getCustomSpawnEntries()) {
				if(spawnEntry.isSaved()) {
					if(nbt.hasKey(spawnEntry.getID().toString(), Constants.NBT.TAG_LONG)) {
						this.lastSpawnMap.put(spawnEntry.getID(), nbt.getLong(spawnEntry.getID().toString()));
					}
				}
			}
		}

		public void writeToNbt(NBTTagCompound nbt) {
			for(ICustomSpawnEntry spawnEntry : this.biome.getCustomSpawnEntries()) {
				if(spawnEntry.isSaved()) {
					if(this.lastSpawnMap.containsKey(spawnEntry.getID())) {
						nbt.setLong(spawnEntry.getID().toString(), this.lastSpawnMap.get(spawnEntry.getID()));
					}
				}
			}
		}
	}
}
