package thebetweenlands.common.world.biome;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.Biome;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler.BLSpawnEntry;
import thebetweenlands.common.world.gen.feature.terrain.BiomeGenerator;
import thebetweenlands.util.IWeightProvider;

public class BiomeBetweenlands extends Biome implements IWeightProvider {
	protected final List<BLSpawnEntry> blSpawnEntries = new ArrayList<BLSpawnEntry>();
	private short biomeWeight;
	private BiomeGenerator biomeGenerator;

	public BiomeBetweenlands(BiomeProperties properties) {
		super(properties);
	}

	/**
	 * Sets the biome generator
	 * @param generator
	 * @return
	 */
	protected final BiomeBetweenlands setBiomeGenerator(BiomeGenerator generator) {
		if(this.biomeGenerator != null)
			throw new RuntimeException("Can only set the biome generator once!");
		this.biomeGenerator = generator;
		return this;
	}

	/**
	 * Returns the biome primer.
	 * If no primer was specified the default biome primer is returned
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
	 * @param weight
	 */
	protected final BiomeBetweenlands setWeight(int weight) {
		if (this.biomeWeight != 0)
			throw new RuntimeException("Cannot set biome weight twice!");
		this.biomeWeight = (short) weight;
		return this;
	}

	/**
	 * Returns Biome specific weighted probability.
	 */
	@Override
	public final short getWeight() {
		return this.biomeWeight;
	}
}
