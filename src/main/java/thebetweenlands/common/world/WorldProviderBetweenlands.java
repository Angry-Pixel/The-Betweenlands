package thebetweenlands.common.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.client.handler.FogHandler;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.client.render.sky.BLSnowRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.handler.LocationHandler;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.event.BLEnvironmentEventRegistry;
import thebetweenlands.common.world.event.EventRift;
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

	public WorldProviderBetweenlands() {
		this.allowHostiles = true;
		this.allowAnimals = true;
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
		return 0.8F;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float partialTicks) {
		EventRift rift = BetweenlandsWorldStorage.forWorld(world).getEnvironmentEventRegistry().rift;
		return rift.getVisibility(partialTicks) * this.getOverworldSunBrightness(partialTicks) * 0.6F + rift.getVisibility(partialTicks) * 0.2F;
	}

	@Override
	public float getSunBrightnessFactor(float partialTicks) {
		BetweenlandsWorldStorage storage = BetweenlandsWorldStorage.forWorldNullable(world); //Null if called by World#calculateinitialSkylight from constructor
		if(storage != null) {
			EventRift rift = storage.getEnvironmentEventRegistry().rift;
			return rift.getVisibility(partialTicks) * this.getOverworldSunBrightnessFactor(partialTicks);
		}
		return 0.2F;
	}

	protected float getOverworldSunBrightnessFactor(float partialTicks) {
		float f = this.getOverworldCelestialAngle(partialTicks);
		float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.5F);
		f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
		f1 = 1.0F - f1;
		//f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(partialTicks) * 5.0F) / 16.0D));
		// f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(partialTicks) * 5.0F) / 16.0D));
		return f1;
	}

	@SideOnly(Side.CLIENT)
	protected float getOverworldSunBrightness(float partialTicks) {
		float f = this.getOverworldCelestialAngle(partialTicks);
		float f1 = 1.0F - (MathHelper.cos(f * ((float)Math.PI * 2F)) * 2.0F + 0.2F);
		f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
		f1 = 1.0F - f1;
		//f1 = (float)((double)f1 * (1.0D - (double)(this.getRainStrength(partialTicks) * 5.0F) / 16.0D));
		//f1 = (float)((double)f1 * (1.0D - (double)(this.getThunderStrength(partialTicks) * 5.0F) / 16.0D));
		return f1 * 0.8F + 0.2F;
	}

	public float getOverworldCelestialAngle(float partialTicks) {
		int i = (int)(this.world.getWorldTime() % 24000L);
		float f = ((float)i + partialTicks) / 24000.0F - 0.25F;

		if (f < 0.0F)
		{
			++f;
		}

		if (f > 1.0F)
		{
			--f;
		}

		float f1 = 1.0F - (float)((Math.cos((double)f * Math.PI) + 1.0D) / 2.0D);
		f = f + (f1 - f) / 3.0F;
		return f;
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkGeneratorBetweenlands(this.world, this.world.getSeed(), BlockRegistry.BETWEENSTONE, /*Blocks.WATER*/BlockRegistry.SWAMP_WATER, LAYER_HEIGHT);
	}

	@Override
	protected void generateLightBrightnessTable() {
		if(this.world.isRemote) {
			float configBrightness = BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionBrightness / 100.0F;
			for(int i = 0; i <= 15; i++) {
				float f1 = 1F - (float)Math.pow(i / 15F, 1.1F + 0.35F * (1.0F - configBrightness));
				this.lightBrightnessTable[i] = Math.max((1.0F - f1) / (f1 * f1 * (0.75F + configBrightness * 0.6F) + 1.0F) * (0.4F + configBrightness * 0.5F) - 0.1F, 0.0F);
			}
		} else {
			super.generateLightBrightnessTable();
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
		this.allowHostiles = allowHostiles;
		this.allowAnimals = allowAnimals;
	}

	public boolean getCanSpawnHostiles() {
		return this.allowHostiles;
	}

	public boolean getCanSpawnAnimals() {
		return this.allowAnimals;
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
		return true;
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
	@SideOnly(Side.CLIENT)
	public void updateClientLightTable(EntityPlayer player) {
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

	@Override
	public boolean canMineBlock(EntityPlayer player, BlockPos pos) {
		if (super.canMineBlock(player, pos)) {
			final ItemStack held = player.getHeldItemMainhand();
			// buckets check position clicked instead of where water goes
			return held.getItem() instanceof ItemBucket || held.getItem() instanceof UniversalBucket || !LocationHandler.isProtected(this.world, player, pos);
		}
		return false;
	}
}
