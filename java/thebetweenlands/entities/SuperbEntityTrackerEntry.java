package thebetweenlands.entities;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.utils.MathUtils;

public class SuperbEntityTrackerEntry extends EntityTrackerEntry {
	private double posX;

	private double posY;

	private double posZ;

	private boolean isDataInitialized;

	private boolean sendVelocityUpdates;

	private int ticksSinceLastForcedTeleport;

	private Entity ridingEntity;

	private boolean isRidingEntity;

	private float prevRotationYaw;

	private float prevRotationPitch;

	public SuperbEntityTrackerEntry(Entity myEntity, int blockDistanceThreshold, int updateFrequency, boolean sendVelocityUpdates) {
		super(myEntity, blockDistanceThreshold, updateFrequency, sendVelocityUpdates);
		this.sendVelocityUpdates = sendVelocityUpdates;
	}

	@Override
	public void sendLocationToAllClients(List players) {
		playerEntitiesUpdated = false;
		if (!isDataInitialized || myEntity.getDistanceSq(posX, posY, posZ) > 16) {
			posX = myEntity.posX;
			posY = myEntity.posY;
			posZ = myEntity.posZ;
			isDataInitialized = true;
			playerEntitiesUpdated = true;
			sendEventsToPlayers(players);
		}
		if (ridingEntity != myEntity.ridingEntity || myEntity.ridingEntity != null && ticks % 60 == 0) {
			ridingEntity = myEntity.ridingEntity;
			func_151259_a(new S1BPacketEntityAttach(0, myEntity, myEntity.ridingEntity));
		}
		if (myEntity instanceof EntityItemFrame && ticks % 10 == 0) {
			EntityItemFrame itemFrame = (EntityItemFrame) myEntity;
			ItemStack itemStack = itemFrame.getDisplayedItem();
			if (itemStack != null && itemStack.getItem() instanceof ItemMap) {
				MapData mapData = Items.filled_map.getMapData(itemStack, myEntity.worldObj);
				Iterator<EntityPlayer> iterator = players.iterator();
				while (iterator.hasNext()) {
					EntityPlayerMP player = (EntityPlayerMP) iterator.next();
					mapData.updateVisiblePlayers(player, itemStack);
					Packet packet = Items.filled_map.func_150911_c(itemStack, myEntity.worldObj, player);
					if (packet != null) {
						player.playerNetServerHandler.sendPacket(packet);
					}
				}
			}
			sendMetadataToAllAssociatedPlayers();
		} else if (ticks % updateFrequency == 0 || myEntity.isAirBorne || myEntity.getDataWatcher().hasChanges()) {
			// TODO: more extensible system of custom thresholds, possibly user configuration as well
			boolean silk = myEntity instanceof EntityWeedwoodRowboat;
			int moveThreshold = silk ? 1 : 4;
			if (myEntity.ridingEntity == null) {
				ticksSinceLastForcedTeleport++;
				int x = myEntity.myEntitySize.multiplyBy32AndRound(myEntity.posX);
				int y = MathHelper.floor_double(myEntity.posY * 32);
				int z = myEntity.myEntitySize.multiplyBy32AndRound(myEntity.posZ);
				int yaw = MathUtils.degToByte(myEntity.rotationYaw);
				int pitch = MathUtils.degToByte(myEntity.rotationPitch);
				int deltaX = x - lastScaledXPosition;
				int deltaY = y - lastScaledYPosition;
				int deltaZ = z - lastScaledZPosition;
				Packet packet = null;
				boolean movePastThreshold = Math.abs(deltaX) >= moveThreshold || Math.abs(deltaY) >= moveThreshold || Math.abs(deltaZ) >= moveThreshold || ticks % 60 == 0;
				boolean rotationPastThreshold = (silk && (Math.abs(myEntity.rotationYaw - prevRotationYaw) > 0.5F || Math.abs(myEntity.rotationPitch - prevRotationPitch) > 1)) || Math.abs(yaw - lastYaw) >= 4 || Math.abs(pitch - lastPitch) >= 4;
				if (ticks > 0 || myEntity instanceof EntityArrow) {
					if (deltaX >= -128 && deltaX < 128 && deltaY >= -128 && deltaY < 128 && deltaZ >= -128 && deltaZ < 128 && ticksSinceLastForcedTeleport <= 400 && !isRidingEntity) {
						if (movePastThreshold && rotationPastThreshold) {
							packet = new S14PacketEntity.S17PacketEntityLookMove(myEntity.getEntityId(), (byte) deltaX, (byte) deltaY, (byte) deltaZ, (byte) yaw, (byte) pitch);
						} else if (movePastThreshold) {
							packet = new S14PacketEntity.S15PacketEntityRelMove(myEntity.getEntityId(), (byte) deltaX, (byte) deltaY, (byte) deltaZ);
						} else if (rotationPastThreshold) {
							packet = new S14PacketEntity.S16PacketEntityLook(myEntity.getEntityId(), (byte) yaw, (byte) pitch);
						}
					} else {
						ticksSinceLastForcedTeleport = 0;
						packet = new S18PacketEntityTeleport(myEntity.getEntityId(), x, y, z, (byte) yaw, (byte) pitch);
					}
				}
				if (sendVelocityUpdates) {
					double motionDeltaX = myEntity.motionX - motionX;
					double motionDeltaY = myEntity.motionY - motionY;
					double motionDeltaZ = myEntity.motionZ - motionZ;
					double min = 0.02;
					double velocityDelta = motionDeltaX * motionDeltaX + motionDeltaY * motionDeltaY + motionDeltaZ * motionDeltaZ;
					if (velocityDelta > min * min || velocityDelta > 0 && myEntity.motionX == 0 && myEntity.motionY == 0 && myEntity.motionZ == 0) {
						motionX = myEntity.motionX;
						motionY = myEntity.motionY;
						motionZ = myEntity.motionZ;
						func_151259_a(new S12PacketEntityVelocity(myEntity.getEntityId(), motionX, motionY, motionZ));
					}
				}
				if (packet != null) {
					func_151259_a(packet);
				}
				sendMetadataToAllAssociatedPlayers();
				if (movePastThreshold) {
					lastScaledXPosition = x;
					lastScaledYPosition = y;
					lastScaledZPosition = z;
				}
				if (rotationPastThreshold) {
					lastYaw = yaw;
					lastPitch = pitch;
					if (silk) {
						prevRotationYaw = myEntity.rotationYaw;
						prevRotationPitch = myEntity.rotationPitch;
					}
				}
				isRidingEntity = false;
			} else {
				int yaw = MathUtils.degToByte(myEntity.rotationYaw);
				int pitch = MathUtils.degToByte(myEntity.rotationPitch);
				boolean rotationPastThreshold = Math.abs(yaw - lastYaw) >= 4 || Math.abs(pitch - lastPitch) >= 4;
				if (rotationPastThreshold) {
					func_151259_a(new S14PacketEntity.S16PacketEntityLook(myEntity.getEntityId(), (byte) yaw, (byte) pitch));
					lastYaw = yaw;
					lastPitch = pitch;
				}
				lastScaledXPosition = myEntity.myEntitySize.multiplyBy32AndRound(myEntity.posX);
				lastScaledYPosition = MathHelper.floor_double(myEntity.posY * 32);
				lastScaledZPosition = myEntity.myEntitySize.multiplyBy32AndRound(myEntity.posZ);
				sendMetadataToAllAssociatedPlayers();
				isRidingEntity = true;
			}
			int yaw = MathUtils.degToByte(myEntity.getRotationYawHead());
			if (Math.abs(yaw - lastHeadMotion) >= 4) {
				func_151259_a(new S19PacketEntityHeadLook(myEntity, (byte) yaw));
				lastHeadMotion = yaw;
			}
			myEntity.isAirBorne = false;
		}
		ticks++;
		if (myEntity.velocityChanged) {
			func_151261_b(new S12PacketEntityVelocity(myEntity));
			myEntity.velocityChanged = false;
		}
	}

	private void sendMetadataToAllAssociatedPlayers() {
		DataWatcher dataWatcher = myEntity.getDataWatcher();
		if (dataWatcher.hasChanges()) {
			func_151261_b(new S1CPacketEntityMetadata(myEntity.getEntityId(), dataWatcher, false));
		}
		if (myEntity instanceof EntityLivingBase) {
			ServersideAttributeMap attributeMap = (ServersideAttributeMap) ((EntityLivingBase) myEntity).getAttributeMap();
			Set attributes = attributeMap.getAttributeInstanceSet();
			if (!attributes.isEmpty()) {
				func_151261_b(new S20PacketEntityProperties(myEntity.getEntityId(), attributes));
			}
			attributes.clear();
		}
	}
}
