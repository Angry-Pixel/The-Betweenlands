package thebetweenlands.common.capability.blessing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import thebetweenlands.api.capability.IBlessingCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class BlessingEntityCapability extends EntityCapability<BlessingEntityCapability, IBlessingCapability, EntityPlayer> implements IBlessingCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "blessing");
	}

	@Override
	protected Capability<IBlessingCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_BLESSING;
	}

	@Override
	protected Class<IBlessingCapability> getCapabilityClass() {
		return IBlessingCapability.class;
	}

	@Override
	protected BlessingEntityCapability getDefaultCapabilityImplementation() {
		return new BlessingEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}






	private BlockPos location;
	private int dimension;

	@Override
	public boolean isBlessed() {
		return this.location != null;
	}

	@Override
	public BlockPos getBlessingLocation() {
		return this.location;
	}

	@Override
	public int getBlessingDimension() {
		return this.dimension;
	}

	@Override
	public void setBlessed(int dimension, BlockPos location) {
		this.location = location;
		this.dimension = dimension;
		this.markDirty();
	}

	@Override
	public void clearBlessed() {
		this.location = null;
		this.markDirty();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		if(this.location != null) {
			nbt.setInteger("x", this.location.getX());
			nbt.setInteger("y", this.location.getY());
			nbt.setInteger("z", this.location.getZ());
		}
		nbt.setInteger("dimension", this.dimension);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		if(nbt.hasKey("x", Constants.NBT.TAG_INT) && nbt.hasKey("y", Constants.NBT.TAG_INT) && nbt.hasKey("z", Constants.NBT.TAG_INT)) {
			this.location = new BlockPos(nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
		} else {
			this.location = null;
		}
		this.dimension = nbt.getInteger("dimension");
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
		return 10;
	}

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		if(event.phase == TickEvent.Phase.START && !event.player.world.isRemote) {
			IBlessingCapability cap = event.player.getCapability(CapabilityRegistry.CAPABILITY_BLESSING, null);

			if(cap != null && cap.isBlessed() && cap.getBlessingLocation() != null && event.player.dimension == cap.getBlessingDimension()) {
				event.player.addPotionEffect(ElixirEffectRegistry.EFFECT_BLESSED.createEffect(205, 0));
			}
		}
	}
}
