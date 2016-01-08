package thebetweenlands.entities.properties.list;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.event.player.PlayerPortalHandler;

public class EntityPropertiesPortal extends EntityProperties {
	public boolean inPortal = false;
	public boolean wasTeleported = false;
	public int portalTimer = PlayerPortalHandler.MAX_PORTAL_TIME;

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		compound.setBoolean("inPortal", this.inPortal);
		compound.setBoolean("wasTeleported", this.wasTeleported);
		compound.setInteger("portalTimer", this.portalTimer);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		this.inPortal = compound.getBoolean("inPortal");
		this.wasTeleported = compound.getBoolean("wasTeleported");
		this.portalTimer = compound.getInteger("portalTimer");
	}

	@Override
	public void init(Entity entity, World world) {

	}

	@Override
	public String getID() {
		return "blPropertyPortal";
	}

	@Override
	public Class<? extends Entity> getEntityClass() {
		return EntityPlayer.class;
	}

	@Override
	public int getTrackingTime() {
		return -1;
	}
}
