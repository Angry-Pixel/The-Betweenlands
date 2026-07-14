package thebetweenlands.common.world.spawning;

public interface ICustomSpawnEntry {
    BaseSpawnProperties base();
    CustomSpawnType getType();
}