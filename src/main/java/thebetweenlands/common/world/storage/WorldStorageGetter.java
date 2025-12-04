package thebetweenlands.common.world.storage;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.AttachmentRegistry;

import javax.annotation.Nullable;
import java.util.Optional;

public class WorldStorageGetter {

	private static Optional<BetweenlandsWorldStorage> getExistingStorage(Level level) {
		return level.getExistingData(AttachmentRegistry.WORLD_STORAGE);
	}

	private static BetweenlandsWorldStorage getOrCreateStorage(Level level) {
		return level.getData(AttachmentRegistry.WORLD_STORAGE);
	}

	public static Optional<BetweenlandsWorldStorage> getExisting(@Nullable LevelAccessor level) {
		Level betweenlandsLevel = TheBetweenlands.getBetweenlands(level);
		if (betweenlandsLevel != null) {
			return getExistingStorage(betweenlandsLevel);
		}
		return Optional.empty();
	}

	public static Optional<BetweenlandsWorldStorage> get(@Nullable LevelAccessor level) {
		Level betweenlandsLevel = TheBetweenlands.getBetweenlands(level);
		if (betweenlandsLevel != null) {
			return Optional.of(getOrCreateStorage(betweenlandsLevel));
		}
		return Optional.empty();
	}

	@Nullable
	public static BetweenlandsWorldStorage getNullable(@Nullable LevelAccessor level) {
		return get(level).orElse(null);
	}

//	public static BetweenlandsWorldStorage getOrThrow(LevelAccessor level) {
//		return get(level).orElseThrow(() -> new RuntimeException(String.format("World %s does not have BetweenlandsWorldStorage saved data attached", level.dimension().location())));
//	}
}
