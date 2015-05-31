package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;
import sun.plugin.com.ParameterListCorrelator;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockTreePortal;
import thebetweenlands.world.teleporter.TeleporterHandler;

public class PlayerPortalHandler {
    int timer = 120;
    Minecraft mc;

    @SubscribeEvent
    public void teleportCheck(LivingEvent.LivingUpdateEvent event)
    {
        if(event.entity instanceof EntityPlayerMP){
            EntityPlayerMP player = (EntityPlayerMP)event.entity;
            NBTTagCompound nbt = player.getEntityData();
            if(nbt.getBoolean("INPORTAL")){
                player.worldObj.playSound(player.posX, player.posY, player.posZ, "thebetweenlands:portalTrigger", 0.2F, 0.8F, false);
                if(player.worldObj.getBlock(floor(player.posX), floor(player.posY), floor(player.posZ)) == BLBlockRegistry.treePortalBlock) {
                    if (timer == 0 || player.capabilities.isCreativeMode) {
                        player.worldObj.playSound(player.posX, player.posY, player.posZ, "thebetweenlands:portalTravel", 0.2F, 0.8F, false);
                        if (player.dimension == 0) {
                            player.timeUntilPortal = 10;
                            TeleporterHandler.transferToBL(player);
                        } else {
                            player.timeUntilPortal = 10;
                            TeleporterHandler.transferToOverworld(player);
                        }
                        nbt.setBoolean("INPORTAL", false);
                        timer = 120;
                    } else
                        timer--;
                }else {
                    timer = 120;
                    nbt.setBoolean("INPORTAL", false);
                }
            }
        }
    }

    public static int floor(double x){
        int xi = (int)x;
        return x<xi ? xi-1 : xi;
    }
}
