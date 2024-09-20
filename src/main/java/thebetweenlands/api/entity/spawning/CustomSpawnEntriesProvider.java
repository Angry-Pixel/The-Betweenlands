package thebetweenlands.api.entity.spawning;

import java.util.List;

/**
 * Can be implemented by biomes in the Betweenlands to provide {@link CustomSpawnEntry}'s
 */
public interface CustomSpawnEntriesProvider {
	/**
	 * Returns an unmodifiable list of all custom entity spawn entries
	 * @return
	 */
	List<CustomSpawnEntry> getCustomSpawnEntries();
}
