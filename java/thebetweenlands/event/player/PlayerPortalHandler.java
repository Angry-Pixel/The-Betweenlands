package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.render.shader.MainShader;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.entities.property.BLEntityPropertiesRegistry;
import thebetweenlands.entities.property.EntityPropertiesPortal;
import thebetweenlands.world.teleporter.TeleporterHandler;

public class PlayerPortalHandler {
	public static final int MAX_PORTAL_TIME = 120;

	@SubscribeEvent
	public void teleportCheck(LivingEvent.LivingUpdateEvent event) {
		if(event.entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)event.entity;
			EntityPropertiesPortal props = BLEntityPropertiesRegistry.INSTANCE.<EntityPropertiesPortal>getProperties(player, BLEntityPropertiesRegistry.PORTAL);
			if(props.inPortal){
				if(player.worldObj.getBlock(floor(player.posX), floor(player.posY), floor(player.posZ)) == BLBlockRegistry.treePortalBlock) {
					if(!props.wasTeleported) {
						if (props.portalTimer <= 0 || player.capabilities.isCreativeMode) {
							if (player.dimension == 0) {
								player.timeUntilPortal = 10;
								props.wasTeleported = true;
								TeleporterHandler.transferToBL(player);
							} else {
								player.timeUntilPortal = 10;
								props.wasTeleported = true;
								TeleporterHandler.transferToOverworld(player);
							}
							props.inPortal = false;
							props.portalTimer = MAX_PORTAL_TIME;
						} else {
							props.portalTimer--;
						}
					}
				} else {
					props.portalTimer = MAX_PORTAL_TIME;
					props.inPortal = false;
					props.wasTeleported = false;
				}
			} else {
				props.portalTimer = MAX_PORTAL_TIME;
				if(!props.inPortal) {
					props.wasTeleported = false;
				}
			}
		}
		if(event.entity.worldObj.isRemote && event.entity == TheBetweenlands.proxy.getClientPlayer()) {
			EntityPropertiesPortal props = BLEntityPropertiesRegistry.INSTANCE.<EntityPropertiesPortal>getProperties(event.entity, BLEntityPropertiesRegistry.PORTAL);
			boolean renderPortalEffect = props.inPortal && !props.wasTeleported;
			if(renderPortalEffect) {
				TheBetweenlands.proxy.playPortalSounds(event.entity, props.portalTimer);
			}
			if(ShaderHelper.INSTANCE.canUseShaders()) {
				MainShader shader = ShaderHelper.INSTANCE.getCurrentShader();
				if(shader != null) {
					if(!renderPortalEffect) {
						shader.setSwirlAngle(0.0F);
					} else {
						if(event.entity.dimension == 0) {
							if(shader.getSwirlAngle() < 2) {
								shader.setSwirlAngle(shader.getSwirlAngle() + (shader.getSwirlAngle() * 0.055F) + 0.0005F);
							} else {
								shader.setSwirlAngle(shader.getSwirlAngle() + ((shader.getSwirlAngle() * 0.055F) / (shader.getSwirlAngle() - 1)) + 0.0005F);
							}
						} else {
							if(shader.getSwirlAngle() > -2) {
								shader.setSwirlAngle(shader.getSwirlAngle() + (shader.getSwirlAngle() * 0.055F) - 0.0005F);
							} else {
								shader.setSwirlAngle(shader.getSwirlAngle() + ((shader.getSwirlAngle() * 0.055F) / (-shader.getSwirlAngle() - 1)) - 0.0005F);
							}
						}
					}
				}
			}
		}
	}


	public static int floor(double x) {
		int xi = (int)x;
		return x<xi ? xi-1 : xi;
	}
}
