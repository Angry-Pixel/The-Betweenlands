package thebetweenlands.manual;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import thebetweenlands.entities.property.BLEntityPropertiesRegistry;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Bart on 21/11/2015.
 */
public class ManualManager {
    public static String getCurrentManualPage(EntityPlayer player) {
        EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(player, BLEntityPropertiesRegistry.MANUAL);
        if (property != null && player != null) {
            return property.currentPage;
        }
        return null;
    }

    public static void setCurrentManualPage(EntityPlayer player, String pageName) {
        EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(player, BLEntityPropertiesRegistry.MANUAL);
        if (pageName != null && player != null && property != null) {
            property.currentPage = pageName;
        }
    }

    public static void findPage(EntityPlayer player, String pageName) {
        if (pageName != null && player != null) {
            EntityPlayer newPlayer = Minecraft.getMinecraft().theWorld.func_152378_a(player.getUniqueID());
            EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(newPlayer, BLEntityPropertiesRegistry.MANUAL);
            if (property != null && !property.foundPages.contains(pageName))
                property.foundPages.add(pageName.toLowerCase());
        }
    }

    public static ArrayList<String> getFoundPages(EntityPlayer player) {
        if (player != null) {
            EntityPlayer newPlayer = Minecraft.getMinecraft().theWorld.func_152378_a(player.getUniqueID());
            EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(newPlayer, BLEntityPropertiesRegistry.MANUAL);
            if (property != null)
                return property.foundPages;
        }
        return null;
    }

    public static boolean hasFoundPage(EntityPlayer player, String page) {
        if (player != null) {
            EntityPlayer newPlayer = Minecraft.getMinecraft().theWorld.func_152378_a(player.getUniqueID());
            EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(newPlayer, BLEntityPropertiesRegistry.MANUAL);
            if (property != null)
                for (String s : property.foundPages) {
                    if (s.toLowerCase().equals(page.toLowerCase())) {
                        return true;
                    }
                }
        }
        return false;
    }


    public static void PlayerDiscoverPage(EntityLiving entity, String name) {
        EntityPlayer player = entity.worldObj.getClosestPlayerToEntity(entity, 20);
        if (!ManualManager.hasFoundPage(player, name) && player != null) {
            Vec3 vec3 = player.getLook(1.0F).normalize();
            Vec3 vec31 = Vec3.createVectorHelper(entity.posX - player.posX, entity.boundingBox.minY + (double) (entity.height / 2.0F) - (player.posY + (double) player.getEyeHeight()), entity.posZ - player.posZ);
            double d0 = vec31.lengthVector();
            vec31 = vec31.normalize();
            double d1 = vec3.dotProduct(vec31);
            if (d1 > 1.0D - 0.025D / d0 && player.canEntityBeSeen(entity)) {
                ManualManager.findPage(player, name);
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("manual.discover.page") + ": " + StatCollector.translateToLocal("manual." + name + ".title")));
            }
        }
    }
}
