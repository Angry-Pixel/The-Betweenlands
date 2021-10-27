package thebetweenlands.common.capability.fishing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.capability.IRotSmellCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class RotSmellEntityCapability extends EntityCapability<RotSmellEntityCapability, IRotSmellCapability, EntityPlayer> implements IRotSmellCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "rot_smell");
	}

	@Override
	protected Capability<IRotSmellCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_ROT_SMELL;
	}

	@Override
	protected Class<IRotSmellCapability> getCapabilityClass() {
		return IRotSmellCapability.class;
	}

	@Override
	protected RotSmellEntityCapability getDefaultCapabilityImplementation() {
		return new RotSmellEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	private long smellyTime = -1;
	private long immunityTime = -1;

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setLong("smellyTime", this.smellyTime);
		nbt.setLong("immunityTime", this.immunityTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.smellyTime = nbt.getLong("smellyTime");
		this.immunityTime = nbt.getLong("immunityTime");
	}

	@Override
	public boolean isSmellingBad() {
		return this.getRemainingSmellyTicks() > 0 && this.getRemainingImmunityTicks() <= 0;
	}

	@Override
	public int getRemainingSmellyTicks() {
		return this.smellyTime >= 0 ? Math.max(0, (int)(this.smellyTime - this.getEntity().world.getTotalWorldTime())) : 0;
	}

	@Override
	public void setSmellingBad(int duration) {
		if(duration <= 0) {
			this.setNotSmellingBad();
		} else {
			this.smellyTime = this.getEntity().world.getTotalWorldTime() + duration;
			this.markDirty();
		}
	}

	@Override
	public void setNotSmellingBad() {
		if(this.smellyTime != -1) {
			this.smellyTime = -1;
			this.markDirty();
		}
	}

	@Override
	public int getRemainingImmunityTicks() {
		return this.immunityTime >= 0 ? Math.max(0, (int)(this.immunityTime - this.getEntity().world.getTotalWorldTime())) : 0;
	}

	@Override
	public void setImmune(int duration) {
		if(duration <= 0) {
			if(this.immunityTime != -1) {
				this.immunityTime = -1;
				this.markDirty();
			}
		} else {
			this.immunityTime = this.getEntity().world.getTotalWorldTime() + duration;
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
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.END && event.player.world.isRemote) {
			IRotSmellCapability cap = event.player.getCapability(CapabilityRegistry.CAPABILITY_ROT_SMELL, null);
			if (cap != null && event.player.world.rand.nextInt(4) == 0) {
				if (cap.isSmellingBad()) {
					BLParticles.FLY.spawn(event.player.world, event.player.posX, event.player.posY + 1D, event.player.posZ);
				}
			}
		}
	}

}
