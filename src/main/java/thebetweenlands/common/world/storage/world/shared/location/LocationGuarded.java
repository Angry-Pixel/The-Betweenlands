package thebetweenlands.common.world.storage.world.shared.location;

import javax.annotation.Nullable;

import thebetweenlands.common.world.storage.world.global.WorldDataBase;
import thebetweenlands.common.world.storage.world.shared.SharedRegion;
import thebetweenlands.common.world.storage.world.shared.location.guard.BlockLocationGuard;
import thebetweenlands.common.world.storage.world.shared.location.guard.ILocationGuard;

public class LocationGuarded extends LocationStorage {
	private BlockLocationGuard guard = new BlockLocationGuard();

	public LocationGuarded(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region) {
		super(worldStorage, id, region);
	}

	public LocationGuarded(WorldDataBase<?> worldStorage, String id, @Nullable SharedRegion region, String name, EnumLocationType type) {
		super(worldStorage, id, region, name, type);
	}

	@Override
	public ILocationGuard getGuard() {
		return this.guard;
	}
}