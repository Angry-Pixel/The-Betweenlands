package thebetweenlands.common.capability.playermounts;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class PlayerMountsEntityCapability extends EntityCapability<PlayerMountsEntityCapability, IPlayerMountsEntityCapability, EntityPlayer> implements IPlayerMountsEntityCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "player_mounts");
	}

	@Override
	protected Capability<IPlayerMountsEntityCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_PLAYER_MOUNTS;
	}

	@Override
	protected Class<IPlayerMountsEntityCapability> getCapabilityClass() {
		return IPlayerMountsEntityCapability.class;
	}

	@Override
	protected PlayerMountsEntityCapability getDefaultCapabilityImplementation() {
		return new PlayerMountsEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}



	private List<NBTTagCompound> queuedPassengers = new ArrayList<>();

	@Override
	public List<NBTTagCompound> getQueuedPassengers() {
		return this.queuedPassengers;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		EntityPlayer player = this.getEntity();

		if(player.isBeingRidden()) {
			NBTTagList passengers = new NBTTagList();

			for(Entity entity : player.getPassengers()) {
				if(entity instanceof IEntityBL) {
					NBTTagCompound passengerNbt = new NBTTagCompound();

					if(entity.writeToNBTAtomically(passengerNbt)) {
						passengers.appendTag(passengerNbt);
					}
				}
			}

			if(!passengers.isEmpty()) {
				nbt.setTag("PlayerPassengers", passengers);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.queuedPassengers.clear();

		if(nbt.hasKey("PlayerPassengers", Constants.NBT.TAG_LIST)) {
			NBTTagList passengers = nbt.getTagList("PlayerPassengers", Constants.NBT.TAG_COMPOUND);

			for(int i = 0; i < passengers.tagCount(); ++i) {
				this.queuedPassengers.add(passengers.getCompoundTagAt(i));
			}
		}
	}

	/**
	 * Prevents player mounts from being removed before player is saved to NBT
	 */
	@SubscribeEvent
	public static void onMountEvent(EntityMountEvent event) {
		if(event.isDismounting()) {
			Entity mount = event.getEntityBeingMounted();
			Entity rider = event.getEntityMounting();

			if(mount instanceof EntityPlayerMP && rider instanceof IEntityBL) {
				EntityPlayerMP player = (EntityPlayerMP) mount;

				if(player.hasDisconnected()) {
					event.setCanceled(true);
				}
			}
		}
	}

	/**
	 * Spawns the player passengers from the NBT tags
	 */
	@SubscribeEvent
	public static void onPlayerJoin(PlayerLoggedInEvent event) {
		if(!event.player.world.isRemote) {
			IPlayerMountsEntityCapability cap = event.player.getCapability(CapabilityRegistry.CAPABILITY_PLAYER_MOUNTS, null);

			if(cap != null) {
				for(NBTTagCompound nbt : cap.getQueuedPassengers()) {
					Entity entity = AnvilChunkLoader.readWorldEntity(nbt, event.player.world, true);
					if(entity != null) {
						entity.startRiding(event.player, true);
					}
				}
			}
		}
	}
}