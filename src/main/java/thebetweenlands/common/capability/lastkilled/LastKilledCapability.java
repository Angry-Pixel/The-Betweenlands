package thebetweenlands.common.capability.lastkilled;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.capability.ILastKilledCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class LastKilledCapability extends EntityCapability<LastKilledCapability, ILastKilledCapability, EntityPlayer> implements ILastKilledCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "last_killed");
	}

	@Override
	protected Capability<ILastKilledCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_LAST_KILLED;
	}

	@Override
	protected Class<ILastKilledCapability> getCapabilityClass() {
		return ILastKilledCapability.class;
	}

	@Override
	protected LastKilledCapability getDefaultCapabilityImplementation() {
		return new LastKilledCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}



	private ResourceLocation lastKilled;

	@Override
	public ResourceLocation getLastKilled() {
		return this.lastKilled;
	}

	@Override
	public void setLastKilled(ResourceLocation key) {
		this.lastKilled = key;
		this.markDirty();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if(this.lastKilled != null) {
			nbt.setString("lastKilled", this.lastKilled.toString());
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("lastKilled", Constants.NBT.TAG_STRING)) {
			this.lastKilled = new ResourceLocation(nbt.getString("lastKilled"));
		} else {
			this.lastKilled = null;
		}
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 20;
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		DamageSource source = event.getSource();

		if(source instanceof EntityDamageSource) {
			Entity attacker = ((EntityDamageSource) source).getTrueSource();

			if(attacker != null) {
				ILastKilledCapability cap = attacker.getCapability(CapabilityRegistry.CAPABILITY_LAST_KILLED, null);

				if(cap != null) {
					cap.setLastKilled(EntityList.getKey(event.getEntityLiving()));
				}
			}
		}
	}
}