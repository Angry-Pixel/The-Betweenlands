package thebetweenlands.common.world.biome;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.common.world.gen.biome.generator.BiomeGenerator;
import thebetweenlands.util.IWeightProvider;

public class BiomeBetweenlands extends Biome implements IWeightProvider {
	protected final List<BLSpawnEntry> blSpawnEntries = new ArrayList<BLSpawnEntry>();
	private int grassColor, foliageColor;
	private short biomeWeight;
	private BiomeGenerator biomeGenerator;
	private int[] fogColorRGB = new int[]{(int) 255, (int) 255, (int) 255};

	public BiomeBetweenlands(BiomeProperties properties) {
		super(properties);
		this.spawnableCreatureList.clear();
		this.spawnableMonsterList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableCaveCreatureList.clear();
		this.biomeWeight = 100;
		this.topBlock = BlockRegistry.SWAMP_GRASS.getDefaultState();
		this.fillerBlock = BlockRegistry.SWAMP_DIRT.getDefaultState();
	}

	/**
	 * Sets the biome generator
	 * @param generator
	 * @return
	 */
	protected final BiomeBetweenlands setBiomeGenerator(BiomeGenerator generator) {
		if(this.biomeGenerator != null)
			throw new RuntimeException("Can only set the biome generator once!");
		if(this.biomeGenerator.getBiome() != this)
			throw new RuntimeException("Generator was assigned to a different biome!");
		this.biomeGenerator = generator;
		return this;
	}

	/**
	 * Returns the biome generator.
	 * If no generator was specified the default biome generator is returned
	 * @return
	 */
	public final BiomeGenerator getBiomeGenerator() {
		return this.biomeGenerator != null ? this.biomeGenerator : (this.biomeGenerator = new BiomeGenerator(this));
	}

	/**
	 * Returns the BL spawn entries
	 * @return
	 */
	public final List<BLSpawnEntry> getSpawnEntries() {
		return this.blSpawnEntries;
	}

	/**
	 * Sets Biome specific weighted probability.
	 * The default weight is 100.
	 * @param weight
	 */
	protected final BiomeBetweenlands setWeight(int weight) {
		if (this.biomeWeight != 0)
			throw new RuntimeException("Cannot set biome weight twice!");
		this.biomeWeight = (short) weight;
		return this;
	}

	/**
	 * Sets the grass and foliage colors
	 * @param grassColor
	 * @param foliageColor
	 * @return
	 */
	public final BiomeBetweenlands setFoliageColors(int grassColor, int foliageColor) {
		this.grassColor = grassColor;
		this.foliageColor = foliageColor;
		return this;
	}

	/**
	 * Sets the biome fog color
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public final BiomeBetweenlands setFogColor(int red, int green, int blue) {
		this.fogColorRGB[0] = red;
		this.fogColorRGB[1] = green;
		this.fogColorRGB[2] = blue;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getGrassColorAtPos(BlockPos pos) {
		if(this.grassColor == 0)
			return super.getGrassColorAtPos(pos);
		return this.grassColor;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getFoliageColorAtPos(BlockPos pos) {
		if(this.foliageColor == 0)
			return super.getFoliageColorAtPos(pos);
		return this.foliageColor;
	}

	/**
	 * Returns Biome specific weighted probability.
	 */
	@Override
	public final short getWeight() {
		return this.biomeWeight;
	}

	/**
	 * Returns the distance where the fog starts to build up.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	@SideOnly(Side.CLIENT)
	public float getFogStart(float farPlaneDistance, int mode) {
		return mode == -1 ? 0.0F : farPlaneDistance * 0.75F;
	}

	/**
	 * Returns the distance where the fog is fully opaque.
	 * @param farPlaneDistance Maximum render distance
	 * @return float
	 */
	@SideOnly(Side.CLIENT)
	public float getFogEnd(float farPlaneDistance, int mode) {
		return farPlaneDistance;
	}

	/**
	 * Returns the fog RGB color.
	 * @return int[3]
	 */
	@SideOnly(Side.CLIENT)
	public int[] getFogRGB() {
		return this.fogColorRGB;
	}

	/**
	 * Called to update the fog range and color
	 */
	public void updateFog() {

	}
}
