package thebetweenlands.common.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeProviderBetweenlands;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.util.config.ConfigHandler;

/**
 *
 * @author A long time ago in a galaxy far, far away: The Erebus Team
 *
 */
public class WorldProviderBetweenlands extends WorldProvider {
	public static final int LAYER_HEIGHT = 120;

	public static final int CAVE_WATER_HEIGHT = 15;

	public static final int PITSTONE_HEIGHT = CAVE_WATER_HEIGHT + 30;

	public static final int CAVE_START = LAYER_HEIGHT - 10;

	protected float[] originalLightBrightnessTable = new float[16];

	private boolean allowHostiles, allowAnimals;
	private BetweenlandsWorldData worldData;

	public WorldProviderBetweenlands() {

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
		return new ChunkGeneratorBetweenlands(this.world, this.world.getSeed(), BlockRegistry.BETWEENSTONE, /*Blocks.WATER*/BlockRegistry.SWAMP_WATER, LAYER_HEIGHT);
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
	public void init() {
		this.setDimension(ConfigHandler.dimensionId);
		this.biomeProvider = new BiomeProviderBetweenlands(this.world.getWorldInfo());
		this.hasSkyLight = true;
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
		super.setAllowedSpawnTypes(allowHostiles, allowAnimals);
		//TODO: This only seems to work on the client side...
		this.allowHostiles = allowHostiles;
		this.allowAnimals = allowAnimals;
	}

	public boolean getCanSpawnHostiles() {
		//TODO: See setAllowedSpawnTypes
		return /*this.allowHostiles*/this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
	}

	public boolean getCanSpawnAnimals() {
		//TODO: See setAllowedSpawnTypes
		return /*this.allowAnimals*/true;
	}

	@Override
	public BlockPos getRandomizedSpawnPoint() {
		BlockPos spawnPos = world.getSpawnPoint();

		boolean isAdventure = world.getWorldInfo().getGameType() == GameType.ADVENTURE;
		int spawnFuzz = 100;
		int spawnFuzzHalf = spawnFuzz / 2;

		if(!isAdventure) {
			spawnPos = spawnPos.add(world.rand.nextInt(spawnFuzz) - spawnFuzzHalf, 0, world.rand.nextInt(spawnFuzz) - spawnFuzzHalf);
			spawnPos = world.getTopSolidOrLiquidBlock(spawnPos);
		}

		return spawnPos;
	}

	public BetweenlandsWorldData getWorldData() {
		if(this.worldData == null) {
			this.worldData = BetweenlandsWorldData.forWorld(this.world);
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
		this.world.getWorldInfo().setRaining(eeRegistry.HEAVY_RAIN.isActive());
		this.world.getWorldInfo().setThundering(false);
		this.world.prevRainingStrength = this.world.rainingStrength;
		if(!this.world.isRemote) {
			float rainingStrength = this.world.rainingStrength;
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
			this.world.rainingStrength = rainingStrength;
		}
	}

	/**
	 * Updates the brightness table relative to the specified player
	 * @param player
	 */
	public void updateLightTable(EntityPlayer player) {
		double diff = Math.max(WorldProviderBetweenlands.CAVE_START - player.posY, 0.0D);
		float multiplier = (float) diff / WorldProviderBetweenlands.CAVE_START;
		multiplier = 1.0F - multiplier;
		multiplier *= Math.pow(multiplier, 6);
		multiplier = multiplier * 0.9F + 0.1F;
		for(int i = 0; i < 16; i++) {
			this.lightBrightnessTable[i] = this.originalLightBrightnessTable[i] * (multiplier + (float)Math.pow(i, (1.0F - multiplier) * 2.2F) / 32.0F + multiplier * 0.5F);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTickTime) {
		Fog fog = FogHandler.getFogState().getFog(partialTickTime);
		return new Vec3d(fog.getRed(), fog.getGreen(), fog.getBlue());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer() {
		return BLSkyRenderer.INSTANCE;
	}

	//Fix for buggy rain (?)
	@Override
	public void calculateInitialWeather() {
		EnvironmentEventRegistry eeRegistry = this.getWorldData().getEnvironmentEventRegistry();
		this.world.getWorldInfo().setRaining(eeRegistry.HEAVY_RAIN.isActive());
		this.world.getWorldInfo().setThundering(false);
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
