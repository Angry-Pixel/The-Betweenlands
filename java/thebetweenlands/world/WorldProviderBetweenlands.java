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
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.render.sky.BLSkyRenderer;
import thebetweenlands.event.render.FogHandler;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import thebetweenlands.world.storage.BetweenlandsWorldData;

/**
 *
 * @author The Erebus Team
 *
 */
public class WorldProviderBetweenlands
extends WorldProvider
{
	public static final int LAYER_HEIGHT = 80;

	public static final int WATER_HEIGHT = 25;

	@SideOnly(Side.CLIENT)
	private double[] currentFogColor;

	private boolean allowHostiles, allowAnimals;

	public float[] originalLightBrightnessTable = new float[16];

	@Override
	public boolean canRespawnHere() {
		return false;
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
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		BiomeGenBase biome = worldObj.getBiomeGenForCoords((int) player.posX, (int) player.posZ);
		byte[] targetFogColor;

		if( biome instanceof BiomeGenBaseBetweenlands ) {
			targetFogColor = ((BiomeGenBaseBetweenlands) biome).getFogRGB().clone();
		} else {
			targetFogColor = new byte[]{(byte) 255, (byte) 255, (byte) 255};
		}

		if(this.currentFogColor == null) {
			this.currentFogColor = new double[3];
			for( int a = 0; a < 3; a++ ) {
				this.currentFogColor[a] = targetFogColor[a];
			}
		}

		float m = FogHandler.INSTANCE.hasDenseFog() ? 80.0f : 0.0f;

		for(int i = 0; i < 3; i++) {
			int diff = 255 - targetFogColor[i];
			targetFogColor[i] = (byte) (targetFogColor[i] + (diff / 255.0D * m));
		}

		for(int a = 0; a < 3; a++) {
			if(this.currentFogColor[a] != targetFogColor[a]) {
				if(this.currentFogColor[a] < targetFogColor[a]) {
					this.currentFogColor[a] += 0.025D;
					if(this.currentFogColor[a] > targetFogColor[a]) {
						this.currentFogColor[a] = targetFogColor[a];
					}
				} else if(this.currentFogColor[a] > targetFogColor[a]) {
					this.currentFogColor[a] -= 0.025D;
					if(this.currentFogColor[a] < targetFogColor[a]) {
						this.currentFogColor[a] = targetFogColor[a];
					}
				}
			}
		}

		return Vec3.createVectorHelper(currentFogColor[0] / 255D, currentFogColor[1] / 255D, currentFogColor[2] / 255D);
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
