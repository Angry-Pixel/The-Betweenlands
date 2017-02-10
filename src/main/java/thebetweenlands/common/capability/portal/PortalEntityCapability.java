package thebetweenlands.common.capability.portal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class PortalEntityCapability extends EntityCapability<PortalEntityCapability, IPortalCapability, EntityPlayer> implements IPortalCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "portal");
	}

	@Override
	protected Capability<IPortalCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_PORTAL;
	}

	@Override
	protected Class<IPortalCapability> getCapabilityClass() {
		return IPortalCapability.class;
	}

	@Override
	protected PortalEntityCapability getDefaultCapabilityImplementation() {
		return new PortalEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	private boolean inPortal = false;
	private boolean wasTeleported = false;
	private int ticksUntilTeleport = 0;

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("inPortal", this.inPortal);
		nbt.setInteger("ticks", this.ticksUntilTeleport);
		nbt.setBoolean("wasTeleported", this.wasTeleported);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.inPortal = nbt.getBoolean("inPortal");
		this.ticksUntilTeleport = nbt.getInteger("ticks");
		this.wasTeleported = nbt.getBoolean("wasTeleported");
	}

	@Override
	public boolean isInPortal() {
		return this.inPortal;
	}

	@Override
	public void setInPortal(boolean inPortal) {
		this.inPortal = inPortal;
	}

	@Override
	public int getTicksUntilTeleport() {
		return this.ticksUntilTeleport;
	}

	@Override
	public void setTicksUntilTeleport(int ticks) {
		this.ticksUntilTeleport = ticks;
	}

	@Override
	public boolean wasTeleported() {
		return this.wasTeleported;
	}

	@Override
	public void setWasTeleported(boolean wasTeleported) {
		this.wasTeleported = wasTeleported;
	}
}
