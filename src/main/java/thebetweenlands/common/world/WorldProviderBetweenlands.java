package thebetweenlands.common.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
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
import thebetweenlands.client.render.sky.BLSnowRenderer;
import thebetweenlands.common.BetweenlandsConfig;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.biome.BiomeProviderBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;

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

	private boolean allowHostiles, allowAnimals;
	private BetweenlandsWorldStorage worldData;

	@SideOnly(Side.CLIENT)
	private static BLSkyRenderer skyRenderer;

	private boolean showClouds = false;
	
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
		return 0.8F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float partialTicks) {
		return super.getSunBrightness(partialTicks);
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkGeneratorBetweenlands(this.world, this.world.getSeed(), BlockRegistry.BETWEENSTONE, /*Blocks.WATER*/BlockRegistry.SWAMP_WATER, LAYER_HEIGHT);
	}

	@Override
	protected void generateLightBrightnessTable() {
		float configBrightness = BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionBrightness / 100.0F;
		for(int i = 0; i <= 15; i++) {
			float f1 = 1F - (float)Math.pow(i / 15F, 1.1F + 0.35F * (1.0F - configBrightness));
			this.lightBrightnessTable[i] = Math.max((1.0F - f1) / (f1 * f1 * (0.75F + configBrightness * 0.6F) + 1.0F) * (0.4F + configBrightness * 0.5F) - 0.1F, 0.0F);
		}
	}

	@Override
	public void init() {
		this.setDimension(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
		this.biomeProvider = new BiomeProviderBetweenlands(this, this.world.getWorldInfo());
		this.hasSkyLight = true;
	}

	@Override
	public boolean isSurfaceWorld() {
		return this.showClouds;
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

	protected BetweenlandsWorldStorage getWorldData() {
		return BetweenlandsWorldStorage.forWorld(this.world);
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk) {
		return false;
	}
	
	@Override
	public void updateWeather() {
		BLEnvironmentEventRegistry eeRegistry = this.getWorldData().getEnvironmentEventRegistry();
		this.world.getWorldInfo().setRainTime(2000); //stop random raining
		this.world.getWorldInfo().setThunderTime(2000);
		this.world.getWorldInfo().setRaining(eeRegistry.heavyRain.isActive());
		this.world.getWorldInfo().setThundering(eeRegistry.thunderstorm.isActive());
		this.world.thunderingStrength = this.world.prevThunderingStrength = eeRegistry.thunderstorm.isActive() ? 1 : 0;
		this.world.prevRainingStrength = this.world.rainingStrength;
		if(!this.world.isRemote) {
			float rainingStrength = this.world.rainingStrength;
			if(eeRegistry.heavyRain.isActive()) {
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
		float configBrightness = BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionBrightness / 100.0F;

		float[] surfaceTable = new float[16];
		float[] undergroundTable = new float[16];

		for(int i = 0; i <= 15; i++) {
			float f1 = 1F - (float)Math.pow(i / 15F, 1.1F + 0.35F * (1.0F - configBrightness));
			surfaceTable[i] = Math.max((1.0F - f1) / (f1 * f1 * (0.75F + configBrightness * 0.6F) + 1.0F) * (0.4F + configBrightness * 0.5F) - 0.1F, 0.0F);
		}

		for(int i = 0; i <= 15; i++) {
			float f1 = 1F - (float)Math.pow(i / 15F, 0.4D);
			undergroundTable[i] = Math.max((1.0F - f1) / (f1 * 2.5F * (1.0F - configBrightness) + 1.0F) * 0.425F - 0.05F, -0.035F + 0.035F * configBrightness * configBrightness);
		}

		double caveHeightDiff = Math.max(WorldProviderBetweenlands.CAVE_START - player.posY, 0.0D);
		float caveMultiplier = (float) caveHeightDiff / WorldProviderBetweenlands.CAVE_START;
		caveMultiplier = 1.0F - caveMultiplier;
		caveMultiplier *= Math.pow(caveMultiplier, 6);
		for(int i = 0; i < 16; i++) {
			this.lightBrightnessTable[i] = surfaceTable[i] + (undergroundTable[i] - surfaceTable[i]) * (1.0F - caveMultiplier);
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
		return getBLSkyRenderer();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
		return new Vec3d(0.1F, 0.8F, 0.55F);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean isSkyColored() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public static BLSkyRenderer getBLSkyRenderer() {
		if(skyRenderer == null) {
			skyRenderer = new BLSkyRenderer();
		}
		return skyRenderer;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getWeatherRenderer() {
		if(this.getEnvironmentEventRegistry().snowfall.isSnowing()) {
			return BLSnowRenderer.INSTANCE;
		}
		return null;
	}

	public BLEnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.getWorldData().getEnvironmentEventRegistry();
	}

	@Override
	public DimensionType getDimensionType() {
		return TheBetweenlands.dimensionType;
	}
	
	@SideOnly(Side.CLIENT)
	public void setShowClouds(boolean show) {
		this.showClouds = show;
	}
}
