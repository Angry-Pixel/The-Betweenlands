package thebetweenlands.common.capability.falldamagereduce;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IFallDamageReductionCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class FallDamageReductionCapability extends EntityCapability<FallDamageReductionCapability, IFallDamageReductionCapability, EntityPlayer> implements IFallDamageReductionCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "fall_damage_reduction");
	}

	@Override
	protected Capability<IFallDamageReductionCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_FALL_DAMAGE_REDUCTION;
	}

	@Override
	protected Class<IFallDamageReductionCapability> getCapabilityClass() {
		return IFallDamageReductionCapability.class;
	}

	@Override
	protected FallDamageReductionCapability getDefaultCapabilityImplementation() {
		return new FallDamageReductionCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	private long reductionTime = -1;

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setLong("reductionTime", this.reductionTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.reductionTime = nbt.getLong("reductionTime");
	}

	@Override
	public boolean isActive() {
		return this.getRemainingActiveTicks() > 0;
	}

	@Override
	public int getRemainingActiveTicks() {
		return this.reductionTime >= 0 ? Math.max(0, (int)(this.reductionTime - this.getEntity().world.getTotalWorldTime())) : 0;
	}

	@Override
	public void setActive(int duration) {
		if(duration <= 0) {
			this.setNotActive();
		} else {
			this.reductionTime = this.getEntity().world.getTotalWorldTime() + duration;
			this.markDirty();
		}
	}

	@Override
	public void setNotActive() {
		if(this.reductionTime != -1) {
			this.reductionTime = -1;
			this.markDirty();
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
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onPlayerFallDamage(LivingDamageEvent event) {
		if (event.getEntityLiving() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			float amount = event.getAmount();
			if (event.getSource() == DamageSource.FALL) {
				IFallDamageReductionCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_FALL_DAMAGE_REDUCTION, null);
				if (cap != null && cap.isActive()) {
					event.setAmount(amount * 0.25F); // dunno if quarter is enough
				}
			}
		}
	}
}
