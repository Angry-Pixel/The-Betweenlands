package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.opengl.GL11;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.world.teleporter.TeleporterHandler;

import javax.swing.text.html.parser.Entity;

public class PlayerPortalHandler {
    int timer = 120;


    @SubscribeEvent
    public void teleportCheck(LivingEvent.LivingUpdateEvent event)
    {
        if(event.entity instanceof EntityPlayerMP){
            EntityPlayerMP player = (EntityPlayerMP)event.entity;
            NBTTagCompound nbt = player.getEntityData();
            if(nbt.getBoolean("INPORTAL")){
                if(player.worldObj.getBlock(floor(player.posX), floor(player.posY), floor(player.posZ)) == BLBlockRegistry.treePortalBlock) {
                    if(timer == 119)
                        player.addPotionEffect(new PotionEffect(Potion.confusion.id, 200, 0));
                    if (timer == 0 || player.capabilities.isCreativeMode) {
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
        if(event.entity instanceof EntityPlayerSP){
            EntityPlayerSP player = (EntityPlayerSP)event.entity;
            if(timer < 120) {
                player.closeScreen();
                if (timer == 119)
                    player.playSound("thebetweenlands:portalTrigger", 1.0F, 0.8F);
                if(timer == 2)
                    player.playSound("thebetweenlands:portalTravel", 1.25f, 0.8f);
            }
        }
    }


    public static int floor(double x){
        int xi = (int)x;
        return x<xi ? xi-1 : xi;
    }
}
