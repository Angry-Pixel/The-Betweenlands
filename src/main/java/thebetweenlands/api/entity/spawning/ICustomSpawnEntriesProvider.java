package thebetweenlands.api.entity.spawning;

import java.util.List;

/**
 * Can be implemented by biomes in the Betweenlands to provide {@link ICustomSpawnEntry}'s
 */
public interface ICustomSpawnEntriesProvider {
	/**
	 * Returns an unmodifiable list of all custom entity spawn entries
	 * @return
	 */
	List<ICustomSpawnEntry> getCustomSpawnEntries();
}
