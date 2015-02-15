package thebetweenlands.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.world.biomes.BiomeGenBaseBetweenlands;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author The Erebus Team
 *
 */
public class WorldProviderBetweenlands extends WorldProvider {
	@SideOnly(Side.CLIENT)
	private double[] currentFogColor;
	@SideOnly(Side.CLIENT)
	private short[] targetFogColor;

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
		if (biome instanceof BiomeGenBaseBetweenlands) {
			this.targetFogColor = ((BiomeGenBaseBetweenlands) biome).getFogRGB();
		} else {
			this.targetFogColor = new short[] { 255, 255, 255 };
		}

		if (this.currentFogColor == null) {
			this.currentFogColor = new double[3];
			for (int a = 0; a < 3; a++) {
				this.currentFogColor[a] = this.targetFogColor[a];
			}
		}

		for (int a = 0; a < 3; a++)
			if (this.currentFogColor[a] != this.targetFogColor[a]) {
				if (this.currentFogColor[a] < this.targetFogColor[a]) {
					this.currentFogColor[a] += 2D;
					if (this.currentFogColor[a] > this.targetFogColor[a]) {
						this.currentFogColor[a] = this.targetFogColor[a];
					}
				} else if (this.currentFogColor[a] > this.targetFogColor[a]) {
					this.currentFogColor[a] -= 2D;
					if (this.currentFogColor[a] < this.targetFogColor[a]) {
						this.currentFogColor[a] = this.targetFogColor[a];
					}
				}
			}

		return Vec3.createVectorHelper(currentFogColor[0] / 255D, currentFogColor[1] / 255D, currentFogColor[2] / 255D);
	}

	@Override
	protected void generateLightBrightnessTable() {
		float f = 0.1F;

		for (int i = 0; i <= 15; i++) {
			float f1 = 1F - i / 15F;
			lightBrightnessTable[i] = (1F - f1) / (f1 * 3F + 1F) * (1F - f) + f;
		}
	}

	@Override
	public void registerWorldChunkManager() {
		/*worldChunkMgr = new WorldChunkManagerErebus(worldObj);
		hasNoSky = true;
		dimensionId = ConfigHandler.INSTANCE.erebusDimensionID;*/
		this.worldChunkMgr = new WorldChunkManagerBetweenlands(this.worldObj);
		this.hasNoSky = true;
		this.dimensionId = ModInfo.DIMENSION_ID;
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		//return new ChunkProviderErebus(worldObj, worldObj.getSeed());
		return new ChunkProviderBetweenlands(this.worldObj, this.worldObj.getSeed(), Blocks.stone, Blocks.water, 80);
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
	public String getDimensionName() {
		return "Betweenlands";
	}

	@Override
	public ChunkCoordinates getRandomizedSpawnPoint() {
		ChunkCoordinates chunkcoordinates = new ChunkCoordinates(worldObj.getSpawnPoint());

		boolean isAdventure = worldObj.getWorldInfo().getGameType() == GameType.ADVENTURE;
		int spawnFuzz = 100;
		int spawnFuzzHalf = spawnFuzz / 2;

		if (!hasNoSky && !isAdventure) {
			chunkcoordinates.posX += worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
			chunkcoordinates.posZ += worldObj.rand.nextInt(spawnFuzz) - spawnFuzzHalf;
			chunkcoordinates.posY = worldObj.getTopSolidOrLiquidBlock(chunkcoordinates.posX, chunkcoordinates.posZ);
		}

		return chunkcoordinates;
	}
}
