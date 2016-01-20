package thebetweenlands.world;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.event.render.FogHandler;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import thebetweenlands.world.storage.BetweenlandsWorldData;

/**
 *
 * @author A long time ago in a galaxy far, far away: The Erebus Team
 *
 */
public class WorldProviderBetweenlands
extends WorldProvider
{
	public static final int LAYER_HEIGHT = 80;

	public static final int CAVE_WATER_HEIGHT = 20;

	public static final int PITSTONE_HEIGHT = CAVE_WATER_HEIGHT + 14;

	public static final int CAVE_START = LAYER_HEIGHT - 10;

	@SideOnly(Side.CLIENT)
	private double[] currentFogColor;
	@SideOnly(Side.CLIENT)
	private double[] lastFogColor;

	private boolean allowHostiles, allowAnimals;

	public float[] originalLightBrightnessTable = new float[16];

	@Override
	public boolean canRespawnHere() {
		return true;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z) {
		return true;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTickTime) {
		return 0.5F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getFogColor(float celestialAngle, float partialTickTime) {
		if(this.currentFogColor == null || this.lastFogColor == null) {
			this.initFogColors(Minecraft.getMinecraft().thePlayer);
		}
		double r = this.currentFogColor[0] + (this.currentFogColor[0] - this.lastFogColor[0]) * partialTickTime;
		double g = this.currentFogColor[1] + (this.currentFogColor[1] - this.lastFogColor[1]) * partialTickTime;
		double b = this.currentFogColor[2] + (this.currentFogColor[2] - this.lastFogColor[2]) * partialTickTime;
		return Vec3.createVectorHelper(r / 255D, g / 255D, b / 255D);
	}

	@Override
	protected void generateLightBrightnessTable() {
		float minBrightness = (float) (1.0F / 100000000.0F * Math.pow(ConfigHandler.DIMENSION_BRIGHTNESS, 4) + 0.005F);
		for(int i = 0; i <= 15; i++) {
			float f1 = 1F - (i*i*i) / (15F*15F*15F);
			this.lightBrightnessTable[i] = (1F - f1) / (f1 * 3F + 1F) * (1F - minBrightness) + minBrightness;
		}
		System.arraycopy(this.lightBrightnessTable, 0, this.originalLightBrightnessTable, 0, 16);
	}

	@Override
	public void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerBetweenlands(this.worldObj);
		this.hasNoSky = true;
		this.dimensionId = ModInfo.DIMENSION_ID;
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return new ChunkProviderBetweenlands(this.worldObj, this.worldObj.getSeed(), BLBlockRegistry.betweenstone, BLBlockRegistry.swampWater, LAYER_HEIGHT);
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z) {
		return true;
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
	public String getDimensionName() {
		return "Betweenlands";
	}

	@Override
	public ChunkCoordinates getRandomizedSpawnPoint() {
		ChunkCoordinates chunkcoordinates = new ChunkCoordinates(worldObj.getSpawnPoint());

		boolean isAdventure = worldObj.getWorldInfo().getGameType() == GameType.ADVENTURE;
		int spawnFuzz = 100;
		int spawnFuzzHalf = spawnFuzz / 2;

		if( !isAdventure ) {
			chunkcoordinates.posX += worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
			chunkcoordinates.posZ += worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
			chunkcoordinates.posY = worldObj.getTopSolidOrLiquidBlock(chunkcoordinates.posX, chunkcoordinates.posZ);
		}

		return chunkcoordinates;
	}

	private BetweenlandsWorldData worldData;

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
			this.updateFogColors();
		}
	}

	private int[] getTargetFogColor(EntityPlayer player) {
		BiomeGenBase biome = this.worldObj.getBiomeGenForCoords((int) player.posX, (int) player.posZ);
		int[] targetFogColor;

		if( biome instanceof BiomeGenBaseBetweenlands ) {
			targetFogColor = ((BiomeGenBaseBetweenlands) biome).getFogRGB().clone();
		} else {
			targetFogColor = new int[]{(int) 255, (int) 255, (int) 255};
		}

		if(!ShaderHelper.INSTANCE.canUseShaders()) {
			if(WorldProviderBetweenlands.getProvider(this.worldObj).getEnvironmentEventRegistry().BLOODSKY.isActive()) {
				targetFogColor = new int[] {(int) (0.74F * 255), (int) (0.18F * 255), (int) (0.08F * 255)};
			} else if(WorldProviderBetweenlands.getProvider(this.worldObj).getEnvironmentEventRegistry().SPOOPY.isActive()) {
				targetFogColor = new int[] {(int) (0.4F * 255), (int) (0.22F * 255), (int) (0.08F * 255)};
			}
		}

		return targetFogColor;
	}

	private void initFogColors(EntityPlayer player) {
		int[] targetFogColor = this.getTargetFogColor(player);

		if(this.currentFogColor == null) {
			this.currentFogColor = new double[3];
			for(int i = 0; i < 3; i++) {
				this.currentFogColor[i] = targetFogColor[i];
			}
		}

		if(this.lastFogColor == null) {
			this.lastFogColor = new double[3];
		} else {
			for(int i = 0; i < 3; i++) {
				this.lastFogColor[i] = this.currentFogColor[i];
			}
		}
	}

	private void updateFogColors() {
		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();

		if(player == null) return;

		this.initFogColors(player);

		int[] targetFogColor = this.getTargetFogColor(player);

		final int transitionStart = WorldProviderBetweenlands.CAVE_START;
		final int transitionEnd = WorldProviderBetweenlands.CAVE_START - 15;
		float m = 0;
		if (FogHandler.INSTANCE.hasDenseFog()) {
			float y = (float) player.posY;
			if (y < transitionStart) {
				if (transitionEnd < y) {
					m = (y - transitionEnd) / (transitionStart - transitionEnd) * 80;
				}
			} else {
				m = 80;
			}
		}
		if(!ShaderHelper.INSTANCE.canUseShaders()) {
			if(WorldProviderBetweenlands.getProvider(this.worldObj).getEnvironmentEventRegistry().BLOODSKY.isActive()
					|| WorldProviderBetweenlands.getProvider(this.worldObj).getEnvironmentEventRegistry().SPOOPY.isActive()) {
				m = 0;
			}
		}

		for(int i = 0; i < 3; i++) {
			int diff = 255 - targetFogColor[i];
			targetFogColor[i] = (int) (targetFogColor[i] + (diff / 255.0D * m));
		}

		for(int a = 0; a < 3; a++) {
			if(this.currentFogColor[a] != targetFogColor[a]) {
				if(this.currentFogColor[a] < targetFogColor[a]) {
					this.currentFogColor[a] += 0.2D;
					if(this.currentFogColor[a] > targetFogColor[a]) {
						this.currentFogColor[a] = targetFogColor[a];
					}
				} else if(this.currentFogColor[a] > targetFogColor[a]) {
					this.currentFogColor[a] -= 0.2D;
					if(this.currentFogColor[a] < targetFogColor[a]) {
						this.currentFogColor[a] = targetFogColor[a];
					}
				}
			}
		}
	}

	//Fix for buggy rain (?)
	@Override
	public void calculateInitialWeather() { 
		EnvironmentEventRegistry eeRegistry = this.getWorldData().getEnvironmentEventRegistry();
		this.worldObj.getWorldInfo().setRaining(eeRegistry.HEAVY_RAIN.isActive());
		this.worldObj.getWorldInfo().setThundering(false);
		super.calculateInitialWeather();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer()
	{
		return BLSkyRenderer.INSTANCE;
	}

	public EnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.getWorldData().getEnvironmentEventRegistry();
	}

	/**
	 * Returns a WorldProviderBetweenlands instance if world is not null and world#provider is an instance of WorldProviderBetweenlands
	 * @param world
	 */
	public static final WorldProviderBetweenlands getProvider(World world) {
		if(world != null && world.provider instanceof WorldProviderBetweenlands) {
			return (WorldProviderBetweenlands) world.provider;
		}
		return null;
	}
}
