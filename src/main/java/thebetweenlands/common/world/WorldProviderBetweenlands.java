package thebetweenlands.common.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.event.FogHandler;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeProviderBetweenlands;
import thebetweenlands.common.world.storage.chunk.storage.location.LocationAmbience;
import thebetweenlands.common.world.storage.chunk.storage.location.LocationStorage;
import thebetweenlands.common.world.storage.world.BetweenlandsWorldData;
import thebetweenlands.util.config.ConfigHandler;

/**
 *
 * @author A long time ago in a galaxy far, far away: The Erebus Team
 *
 */
public class WorldProviderBetweenlands extends WorldProvider {
	public static final int LAYER_HEIGHT = 120;

	public static final int CAVE_WATER_HEIGHT = 20;

	public static final int PITSTONE_HEIGHT = CAVE_WATER_HEIGHT + 20;

	public static final int CAVE_START = LAYER_HEIGHT - 10;
	
	public float[] originalLightBrightnessTable = new float[16];
	
	private boolean allowHostiles, allowAnimals;
	private BetweenlandsWorldData worldData;

	public WorldProviderBetweenlands() {
		this.hasNoSky = false;
	}

	/**
	 * Returns a WorldProviderBetweenlands instance if world is not null and world#provider is an instance of WorldProviderBetweenlands
	 *
	 * @param world
	 */
	public static final WorldProviderBetweenlands getProvider(World world) {
		if (world != null && world.provider instanceof WorldProviderBetweenlands) {
			return (WorldProviderBetweenlands) world.provider;
		}
		return null;
	}

	@Override
	public boolean canRespawnHere() {
		return true;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTickTime) {
		return 0.35F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float partialTicks) {
		return 0.2F;
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkGeneratorBetweenlands(this.worldObj, this.worldObj.getSeed(), BlockRegistry.BETWEENSTONE, BlockRegistry.SWAMP_WATER, LAYER_HEIGHT);
	}

	@Override
	protected void generateLightBrightnessTable() {
		float minBrightness = (float) (1.0F / 10000000.0F * Math.pow(ConfigHandler.dimensionBrightness, 3.2F) + 0.002F);
		for(int i = 0; i <= 15; i++) {
			float f1 = 1F - (i*i) / (15F*15F);
			this.lightBrightnessTable[i] = ((1F - f1) / (f1 * 6F + 1F) * (1F - minBrightness) + minBrightness);
		}
		System.arraycopy(this.lightBrightnessTable, 0, this.originalLightBrightnessTable, 0, 16);
	}

	@Override
	public void createBiomeProvider() {
		this.setDimension(ConfigHandler.dimensionId);
		this.biomeProvider = new BiomeProviderBetweenlands(this.worldObj.getWorldInfo());
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z) {
		return false;
	}

	@Override
	public void setAllowedSpawnTypes(boolean allowHostiles, boolean allowAnimals) {
		this.allowHostiles = allowHostiles;
		this.allowAnimals = allowAnimals;
		super.setAllowedSpawnTypes(allowHostiles, allowAnimals);
	}

	public boolean getCanSpawnHostiles() {
		return this.allowHostiles;
	}

	public boolean getCanSpawnAnimals() {
		return this.allowAnimals;
	}

	@Override
	public BlockPos getRandomizedSpawnPoint() {
		BlockPos spawnPos = worldObj.getSpawnPoint();

		boolean isAdventure = worldObj.getWorldInfo().getGameType() == GameType.ADVENTURE;
		int spawnFuzz = 100;
		int spawnFuzzHalf = spawnFuzz / 2;

		if(!isAdventure) {
			spawnPos = spawnPos.add(worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf, 0, worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf);
			spawnPos = worldObj.getTopSolidOrLiquidBlock(spawnPos);
		}

		return spawnPos;
	}

	public BetweenlandsWorldData getWorldData() {
		if(this.worldData == null) {
			this.worldData = BetweenlandsWorldData.forWorld(this.worldObj);
		}
		return this.worldData;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}

	@Override
	public void updateWeather() {
		EnvironmentEventRegistry eeRegistry = this.getWorldData().getEnvironmentEventRegistry();
		this.worldObj.getWorldInfo().setRaining(eeRegistry.HEAVY_RAIN.isActive());
		this.worldObj.getWorldInfo().setThundering(false);
		this.worldObj.prevRainingStrength = this.worldObj.rainingStrength;
		if(!this.worldObj.isRemote) {
			float rainingStrength = this.worldObj.rainingStrength;
			if(eeRegistry.HEAVY_RAIN.isActive()) {
				if (rainingStrength < 0.5F) {
					rainingStrength += 0.0125F;
				}
				if (rainingStrength > 0.5F) {
					rainingStrength = 0.5F;
				}
			} else {
				if (rainingStrength > 0) {
					rainingStrength -= 0.0125F;
				}
				if (rainingStrength < 0) {
					rainingStrength = 0;
				}
			}
			this.worldObj.rainingStrength = rainingStrength;
		} else {
			//TODO: Update fog colors
		}
	}

	//TODO: Sky render
	/*@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer() {
		return BLSkyRenderer.INSTANCE;
	}*/

	//Fix for buggy rain (?)
	@Override
	public void calculateInitialWeather() {
		EnvironmentEventRegistry eeRegistry = this.getWorldData().getEnvironmentEventRegistry();
		this.worldObj.getWorldInfo().setRaining(eeRegistry.HEAVY_RAIN.isActive());
		this.worldObj.getWorldInfo().setThundering(false);
		super.calculateInitialWeather();
	}

	public EnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.getWorldData().getEnvironmentEventRegistry();
	}

	@Override
	public DimensionType getDimensionType() {
		return TheBetweenlands.dimensionType;
	}
}
