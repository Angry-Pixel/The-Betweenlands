package thebetweenlands.event.entity;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.mobs.EntityBerserkerGuardian;
import thebetweenlands.entities.mobs.EntityDreadfulMummy;
import thebetweenlands.entities.mobs.EntityMeleeGuardian;
import thebetweenlands.entities.mobs.EntityTempleGuardian;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.entities.mobs.boss.fortress.EntityFortressBoss;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.entities.properties.list.recruitment.EntityPropertiesRecruit;
import thebetweenlands.entities.properties.list.recruitment.EntityPropertiesRecruiter;
import thebetweenlands.items.equipment.ItemRingOfRecruitment;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.client.PacketRecruitmentState;

public class RecruitmentRingHandler {
	public static final RecruitmentRingHandler INSTANCE = new RecruitmentRingHandler();

	private static final List<Class<? extends EntityLivingBase>> DISALOWWED_ENTITIES = new ArrayList<Class<? extends EntityLivingBase>>();

	static {
		DISALOWWED_ENTITIES.add(EntityWight.class);
		DISALOWWED_ENTITIES.add(EntityTempleGuardian.class);
		DISALOWWED_ENTITIES.add(EntityMeleeGuardian.class);
		DISALOWWED_ENTITIES.add(EntityBerserkerGuardian.class);
		DISALOWWED_ENTITIES.add(EntityDreadfulMummy.class);
		DISALOWWED_ENTITIES.add(EntityFortressBoss.class);
	}

	private boolean ignoreSetAttackTarget = false;
	@SubscribeEvent
	public void onSetAttackTarget(LivingSetAttackTargetEvent event) {
		if(!this.ignoreSetAttackTarget) {
			if(event.entityLiving instanceof EntityLiving && ((EntityLiving)event.entityLiving).getAttackTarget() != null) {
				EntityPropertiesRecruit props = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityLiving, EntityPropertiesRecruit.class);
				if(props != null && props.isRecruited()) {
					if(event.target == props.getRecruiter()) {
						this.ignoreSetAttackTarget = true;
						EntityLivingBase newTarget = props.getMobToAttack();
						((EntityLiving)event.entityLiving).setAttackTarget(newTarget);
						event.entityLiving.setRevengeTarget(newTarget);
						if(event.entityLiving instanceof EntityCreature) {
							EntityCreature creature = (EntityCreature) event.entityLiving;
							creature.setTarget(newTarget);
						}
					}
				}
			}
		} else {
			this.ignoreSetAttackTarget = false;
		}
	}

	@SubscribeEvent
	public void onEntityUpdate(LivingUpdateEvent event) {
		EntityPropertiesRecruit props = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityLiving, EntityPropertiesRecruit.class);
		if(props != null && props.isRecruited()) {
			if(event.entityLiving.worldObj.isRemote) {
				BLParticle.PORTAL.spawn(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY + event.entityLiving.getEyeHeight() / 4.0F * 3.0F, event.entityLiving.posZ, 
						(event.entityLiving.worldObj.rand.nextFloat() - 0.5F) / 8.0F,
						(event.entityLiving.worldObj.rand.nextFloat() - 0.5F) / 8.0F,
						(event.entityLiving.worldObj.rand.nextFloat() - 0.5F) / 8.0F, 
						1.0F);
			} else {
				props.update();
			}
		}
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		if(event.entityPlayer != null && event.target instanceof EntityLiving && !event.entityPlayer.worldObj.isRemote && 
				!DISALOWWED_ENTITIES.contains(event.target.getClass())) {
			EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(event.entityPlayer);
			List<Equipment> equipmentList = equipmentInventory.getEquipment(EnumEquipmentCategory.RING);
			for(Equipment equipment : equipmentList) {
				if(equipment.item.getItem() instanceof ItemRingOfRecruitment && ((ItemRingOfRecruitment)equipment.item.getItem()).isActive(equipment.item)) {
					EntityPropertiesRecruiter prop = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityPlayer, EntityPropertiesRecruiter.class);
					if(prop != null && !prop.isRecruiting() && prop.canRecruit())
						prop.startRecruiting((EntityLiving)event.target);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerUpdate(PlayerTickEvent event) {
		if(event.phase == Phase.START && event.player != null) {
			EntityPropertiesRecruiter prop = BLEntityPropertiesRegistry.HANDLER.getProperties(event.player, EntityPropertiesRecruiter.class);
			if(prop != null && prop.isRecruiting()) {
				if(event.player.worldObj.isRemote) {
					Vec3 vec = Vec3.createVectorHelper(prop.getRecruit().posX - event.player.posX, (prop.getRecruit().posY + prop.getRecruit().getEyeHeight()) - event.player.posY, prop.getRecruit().posZ - event.player.posZ);
					vec = vec.normalize();
					vec = vec.addVector((event.player.worldObj.rand.nextFloat() - 0.5F) / 3.0F, 
							(event.player.worldObj.rand.nextFloat() - 0.5F) / 3.0F, 
							(event.player.worldObj.rand.nextFloat() - 0.5F) / 3.0F);
					vec = vec.normalize();
					double dist = event.player.getDistanceToEntity(prop.getRecruit());
					vec = Vec3.createVectorHelper(vec.xCoord * dist / 15.0F, vec.yCoord * dist / 15.0F, vec.zCoord * dist / 15.0F);
					BLParticle.PORTAL.spawn(event.player.worldObj, event.player.posX, event.player.posY + event.player.getEyeHeight(), event.player.posZ, vec.xCoord, vec.yCoord, vec.zCoord, 1.0F);
				} else {
					prop.update();
				}
				event.player.motionX *= 0.05D;
				event.player.motionZ *= 0.05D;
			}
		}
	}

	private boolean wasPressed = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onInput(InputEvent event) {
		this.updateClientRecruitmentState();
	}

	@SideOnly(Side.CLIENT)
	private void updateClientRecruitmentState() {
		if(!this.wasPressed && Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed()) {
			this.wasPressed = true;
			TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketRecruitmentState(true)));
		} else if(this.wasPressed && !Minecraft.getMinecraft().gameSettings.keyBindUseItem.getIsKeyPressed()) {
			this.wasPressed = false;
			TheBetweenlands.networkWrapper.sendToServer(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketRecruitmentState(false)));
		}
	}

	@SubscribePacket
	public static void onPacketEquipment(PacketRecruitmentState packet) {
		if(!packet.isPressed() && packet.getContext().getServerHandler() != null) {
			EntityPlayer sender = packet.getContext().getServerHandler().playerEntity;
			if(sender != null) {
				EntityPropertiesRecruiter prop = BLEntityPropertiesRegistry.HANDLER.getProperties(sender, EntityPropertiesRecruiter.class);
				if(prop != null)
					prop.stopRecruiting();
			}
		}
	}
}
