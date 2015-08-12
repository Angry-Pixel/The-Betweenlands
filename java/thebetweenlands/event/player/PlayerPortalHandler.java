package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.render.shader.MainShader;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.world.teleporter.TeleporterHandler;

public class PlayerPortalHandler {
	@SubscribeEvent
	public void teleportCheck(LivingEvent.LivingUpdateEvent event) {
		if(event.entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)event.entity;
			NBTTagCompound nbt = player.getEntityData();
			int timer = nbt.getInteger("PORTALTIMER");
			if(timer == 0) nbt.setInteger("PORTALTIMER", 120);
			if(nbt.getBoolean("INPORTAL")){
				if(player.worldObj.getBlock(floor(player.posX), floor(player.posY), floor(player.posZ)) == BLBlockRegistry.treePortalBlock) {
					if (timer <= 1 || player.capabilities.isCreativeMode) {
						if (player.dimension == 0) {
							player.timeUntilPortal = 10;
							TeleporterHandler.transferToBL(player);
						} else {
							player.timeUntilPortal = 10;
							TeleporterHandler.transferToOverworld(player);
						}
						nbt.setBoolean("INPORTAL", false);
						nbt.setInteger("PORTALTIMER", 120);
					} else {
						nbt.setInteger("PORTALTIMER", timer - 1);
					}
				} else {
					nbt.setInteger("PORTALTIMER", 120);
					nbt.setBoolean("INPORTAL", false);
				}
			} else {
				nbt.setInteger("PORTALTIMER", 120);
			}
		}
		if(event.entity == Minecraft.getMinecraft().thePlayer) {
			boolean inPortal = event.entity.getEntityData().getBoolean("INPORTAL");
			if(ShaderHelper.INSTANCE.canUseShaders()) {
				MainShader shader = ShaderHelper.INSTANCE.getCurrentShader();
				if(shader != null) {
					if(!inPortal) {
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
			if(inPortal) {
				int timer = event.entity.getEntityData().getInteger("PORTALTIMER");
				if(timer == 0) event.entity.getEntityData().setInteger("PORTALTIMER", 120);
				TheBetweenlands.proxy.playPortalSounds(event.entity, timer);
			}
		}
	}


	public static int floor(double x) {
		int xi = (int)x;
		return x<xi ? xi-1 : xi;
	}
}
