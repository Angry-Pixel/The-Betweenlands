package thebetweenlands.api.event;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import thebetweenlands.api.entity.spawning.ICustomSpawnEntry;
import thebetweenlands.common.world.biome.BiomeBetweenlands;

public class InitializeBetweenlandsBiomeEvent extends Event {
	private final BiomeBetweenlands biome;
	private final List<ICustomSpawnEntry> spawnEntries;

	public InitializeBetweenlandsBiomeEvent(BiomeBetweenlands biome, List<ICustomSpawnEntry> spawnEntries) {
		this.biome = biome;
		this.spawnEntries = spawnEntries;
	}

	/**
	 * Returns the biome
	 * @return
	 */
	public BiomeBetweenlands getBiome() {
		return this.biome;
	}

	/**
	 * Returns a modifiable list of the biome's entity spawn entries
	 * @return
	 */
	public List<ICustomSpawnEntry> getModifiableSpawnEntries() {
		return this.spawnEntries;
	}
}
