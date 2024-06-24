package thebetweenlands.api.entity.spawning;

/**
 * Stores persistent per biome information about entity spawn entries
 */
public interface IBiomeSpawnEntriesData {
	/**
	 * Returns when the specified spawn entry last spawned an entity.
	 * Returns -1 if there is no information stored about the spawn entry.
	 * @param spawnEntry
	 * @return
	 */
	long getLastSpawn(ICustomSpawnEntry spawnEntry);

	/**
	 * Removes the last spawned data for the specified spawn entry
	 * @param spawnEntry
	 * @return
	 */
	long removeLastSpawn(ICustomSpawnEntry spawnEntry);

	/**
	 * Sets when the specified spawn entry last spawned an entity
	 * @param spawnEntry
	 * @param lastSpawn
	 */
	void setLastSpawn(ICustomSpawnEntry spawnEntry, long lastSpawn);
}
