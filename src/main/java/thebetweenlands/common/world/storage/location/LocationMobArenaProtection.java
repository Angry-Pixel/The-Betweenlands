package thebetweenlands.common.world.storage.location;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import thebetweenlands.api.entity.IMobArenaProtectionEntity;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;

public class LocationMobArenaProtection extends LocationGuarded implements ITickable {

	private UUID entityUuid;
	private ResourceLocation entityRegId;

	private Entity cachedEntity;

	private int updateTimer;

	public LocationMobArenaProtection(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region);
	}

	public LocationMobArenaProtection(IWorldStorage worldStorage, StorageID id, @Nullable LocalRegion region, String name, EnumLocationType type) {
		super(worldStorage, id, region, name, type);
	}

	public void setProtectingMob(@Nullable Entity mob) {
		if(mob != null) {
			this.entityUuid = mob.getUniqueID();
			this.entityRegId = EntityList.getKey(mob);
			this.cachedEntity = mob;
		} else {
			this.entityUuid = null;
			this.entityRegId = null;
			this.cachedEntity = null;
		}
		this.markDirty();
	}

	protected boolean isMobProtecting(Entity mob) {
		if(this.entityUuid == null || !mob.isEntityAlive()) {
			return false;
		}

		if(mob == this.cachedEntity || mob.getUniqueID().equals(this.entityUuid)) {
			this.cachedEntity = mob;

			if(mob instanceof IMobArenaProtectionEntity) {
				return ((IMobArenaProtectionEntity) mob).isProtectingLocation(this);
			}

			return true;
		}

		return false;
	}

	protected boolean isMobProtecting(World world) {
		if(this.entityUuid == null || this.entityRegId == null) {
			return false;
		}

		if(this.cachedEntity != null) {
			return this.isInside(this.cachedEntity) && this.isMobProtecting(this.cachedEntity);
		}

		Class<? extends Entity> cls = EntityList.getClass(this.entityRegId);

		if(cls != null) {
			for(AxisAlignedBB aabb : this.getBounds()) {
				if(!world.getEntitiesWithinAABB(cls, aabb, this::isMobProtecting).isEmpty()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void update() {
		super.update();

		World world = this.getWorldStorage().getWorld();

		if(!world.isRemote && this.updateTimer++ >= 20) {
			this.updateTimer = 0;

			if(!this.isMobProtecting(world)) {
				this.getGuard().clear(world);

				this.getWorldStorage().getLocalStorageHandler().removeLocalStorage(this);
			}
		}
	}

}
