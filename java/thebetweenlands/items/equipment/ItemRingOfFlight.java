package thebetweenlands.items.equipment;

import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesFlight;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.loot.ItemRing;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.client.PacketFlightState;

public class ItemRingOfFlight extends ItemRing implements IManualEntryItem {
	public ItemRingOfFlight() {
		this.setMaxDamage(1800);
		this.setUnlocalizedName("thebetweenlands.ringOfFlight");
		this.setTextureName("thebetweenlands:ringOfFlight");
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		list.add(StatCollector.translateToLocal("ring.flight.bonus"));
	}

	@Override
	public String manualName(int meta) {
		return "ringOfFlight";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{6};
	}

	@Override
	public int metas() {
		return 0;
	}

	@Override
	public void onEquipmentTick(ItemStack stack, Entity entity) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			EntityPropertiesFlight props = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesFlight.class);
			player.getEntityData().setBoolean("thebetweenlands.hadFlightRing", true);
			if(!player.capabilities.isCreativeMode && this.canFly(player, stack)) {
				double flightHeight = 2.1D;
				if(player.worldObj.isRemote || props.isFlying())
					player.capabilities.allowFlying = true;
				boolean isFlying = props.isFlying();
				if(!entity.onGround) {
					if(isFlying) {
						if(!entity.worldObj.isRemote && entity.ticksExisted % 20 == 0)
							this.removeXp((EntityPlayer)entity, 2);

						if(entity.isSneaking()) {
							entity.motionY = -0.2F;
						} else {
							double actualPosY = entity.posY + (entity == TheBetweenlands.proxy.getClientPlayer() ? -1.65D : 0.0D);
							double height = this.getGroundHeight(player);
							Vec3 dir = Vec3.createVectorHelper(entity.getLookVec().xCoord, 0, entity.getLookVec().zCoord).normalize();
							if(player.moveForward > 0) {
								entity.motionX += dir.xCoord * 0.015F;
								entity.motionZ += dir.zCoord * 0.015F;
							}

							double my = 0.0D;
							boolean moveUp = false;
							if(player.worldObj.isRemote) {
								if(player instanceof EntityPlayerSP) {
									moveUp = ((EntityPlayerSP)player).movementInput.jump;
								}
							}
							if(moveUp) {
								my = 0.1D;
							}
							double mul = ((height - (actualPosY - flightHeight)));
							entity.motionY = my * mul;
							if(actualPosY - flightHeight > height && entity.motionY > 0) {
								entity.motionY = 0;
							}
							if(actualPosY - flightHeight > height) {
								entity.motionY = -Math.min(actualPosY - flightHeight - height, 2.0D) / 8.0F;
							}
						}

						entity.fallDistance = 0.0F;

						if(!entity.onGround && entity.worldObj.isRemote) {
							BLParticle.LEAF_SWIRL.spawn(entity.worldObj, entity.posX, entity.posY, entity.posZ, 0, 0, 0, 1, entity);
						}
					}
				} else {
					props.setFlying(false);
				}
			} else if(props.isFlying() && !player.onGround && player.worldObj.isRemote) {
				BLParticle.LEAF_SWIRL.spawn(entity.worldObj, entity.posX, entity.posY, entity.posZ, 0, 0, 0, 1, entity);
			}
		}
	}

	@SubscribePacket
	public static void onPacketEquipment(PacketFlightState packet) {
		if(packet.getContext().getServerHandler() != null) {
			EntityPlayer sender = packet.getContext().getServerHandler().playerEntity;
			if(sender != null) {
				EntityPropertiesFlight props = BLEntityPropertiesRegistry.HANDLER.getProperties(sender, EntityPropertiesFlight.class);
				props.setFlying(packet.isFlying());
			}
		}
	}

	private double getGroundHeight(EntityPlayer player) {
		double actualPosY = player.posY + (player == TheBetweenlands.proxy.getClientPlayer() ? -1.65D : 0.0D);
		MovingObjectPosition result = player.worldObj.rayTraceBlocks(Vec3.createVectorHelper(player.posX, actualPosY, player.posZ), Vec3.createVectorHelper(player.posX, actualPosY - 64, player.posZ), true);
		if(result != null && result.typeOfHit == MovingObjectType.BLOCK) {
			return result.hitVec.yCoord;
		}
		return 512.0D;
	}

	public boolean canFly(EntityPlayer player, ItemStack stack) {
		return player.capabilities.isCreativeMode || player.experienceTotal > 0;
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if(event.player != null) {
			EntityPlayer player = (EntityPlayer) event.player;
			if(!player.capabilities.isCreativeMode) {
				EntityPropertiesFlight props = BLEntityPropertiesRegistry.HANDLER.getProperties(player, EntityPropertiesFlight.class);
				EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(player);
				boolean canPlayerFly = false;
				if(equipmentInventory != null) {
					List<Equipment> rings = equipmentInventory.getEquipment(EnumEquipmentCategory.RING);
					for(Equipment equipment : rings) {
						if(equipment.item.getItem() == BLItemRegistry.ringOfFlight && ((ItemRingOfFlight)equipment.item.getItem()).canFly(player, equipment.item))
							canPlayerFly = true;
					}
				}
				if(canPlayerFly && player.worldObj.isRemote) {
					if(event.phase == Phase.START) {
						player.capabilities.isFlying = false;
					} else {
						if(player.capabilities.isFlying) {
							props.setFlying(!props.isFlying());
							if(player == TheBetweenlands.proxy.getClientPlayer()) {
								TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketFlightState(props.isFlying())));
							}
						}
					}
				}
				if(player == TheBetweenlands.proxy.getClientPlayer() && player.ticksExisted % 20 == 0) {
					TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketFlightState(props.isFlying())));
				}
				if(event.phase == Phase.END) {
					if(!canPlayerFly || !props.isFlying()) {
						NBTTagCompound playerNBT = player.getEntityData();
						boolean hadFlightRing = playerNBT.getBoolean("thebetweenlands.hadFlightRing");
						if(hadFlightRing) {
							if(!player.capabilities.isCreativeMode) {
								player.capabilities.isFlying = false;
								player.capabilities.allowFlying = false;
								if(player.worldObj.isRemote) {
									player.capabilities.setFlySpeed(0.05F);
								}
							}
							playerNBT.setBoolean("thebetweenlands.hadFlightRing", false);
						}
					}
				}
			}
		}
	}
}
