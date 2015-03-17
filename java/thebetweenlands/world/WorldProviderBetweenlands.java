package thebetweenlands.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.message.MessageSyncWeather;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.biomes.base.BiomeGenBaseBetweenlands;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 *
 * @author The Erebus Team
 *
 */
public class WorldProviderBetweenlands
extends WorldProvider
{
	public static final int LAYER_HEIGHT = 80;

	@SideOnly(Side.CLIENT)
	private double[] currentFogColor;

	private boolean allowHostiles, allowAnimals;

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
			targetFogColor = ((BiomeGenBaseBetweenlands) biome).getFogRGB();
		} else {
			targetFogColor = new byte[]{(byte) 255, (byte) 255, (byte) 255};
		}

		if(this.currentFogColor == null) {
			this.currentFogColor = new double[3];
			for( int a = 0; a < 3; a++ ) {
				this.currentFogColor[a] = targetFogColor[a];
			}
		}

		for(int a = 0; a < 3; a++) {
			if(this.currentFogColor[a] != targetFogColor[a]) {
				if(this.currentFogColor[a] < targetFogColor[a]) {
					this.currentFogColor[a] += 0.3D;
					if(this.currentFogColor[a] > targetFogColor[a]) {
						this.currentFogColor[a] = targetFogColor[a];
					}
				} else if(this.currentFogColor[a] > targetFogColor[a]) {
					this.currentFogColor[a] -= 0.3D;
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
		float minBrightness = (float) (1.0F / 100000000.0F * Math.pow((float)ConfigHandler.DIMENSION_BRIGHTNESS, 4) + 0.005F);
		for(int i = 0; i <= 15; i++) {
			float f1 = 1F - (i*i*i) / (15F*15F*15F);
			lightBrightnessTable[i] = (1F - f1) / (f1 * 3F + 1F) * (1F - minBrightness) + minBrightness;
		}
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

		if( !hasNoSky && !isAdventure ) {
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
	public void updateWeather() {
		if(!this.worldObj.isRemote) {
			int timeToFog = this.getWorldData().getTimeToFog();
			if(timeToFog <= 0) {
				if(this.getWorldData().getDenseFog()) {
					this.getWorldData().setTimeToFogNBT(this.worldObj.rand.nextInt(12000) + 12000);
				} else {
					this.getWorldData().setTimeToFogNBT(this.worldObj.rand.nextInt(168000) + 12000);
				}
			} else {
				--timeToFog;
				this.getWorldData().setTimeToFogNBT(timeToFog);
				if (timeToFog <= 0) {
					this.getWorldData().setDenseFog(!this.getWorldData().getDenseFog());
				}
			}
			TheBetweenlands.networkWrapper.sendToAll(new MessageSyncWeather(this.getWorldData().getDenseFog()));
		}
	}
}
