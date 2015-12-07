package thebetweenlands.manual;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import thebetweenlands.entities.property.BLEntityPropertiesRegistry;
import thebetweenlands.items.BLItemRegistry;

import java.util.ArrayList;

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

    public static boolean findPage(EntityPlayer player, String pageName) {
        /*if (pageName != null && player != null) {
            EntityPlayer newPlayer = Minecraft.getMinecraft().theWorld.func_152378_a(player.getUniqueID());
            EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(newPlayer, BLEntityPropertiesRegistry.MANUAL);
            if (property != null && !property.foundPages.contains(pageName))
                property.foundPages.add(pageName.toLowerCase());
        }*/

        if (pageName != null && player != null) {
            if (player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualGuideBook) {
                NBTTagCompound nbt = player.getHeldItem().getTagCompound();
                if (nbt == null)
                    nbt = new NBTTagCompound();
                ArrayList<String> foundPages = getFoundPages(player);
                if (foundPages != null && !foundPages.contains(pageName)) {
                    NBTTagList pages = new NBTTagList();
                    for (String string : foundPages) {
                        NBTTagCompound data = new NBTTagCompound();
                        data.setString("page", string);
                        pages.appendTag(data);
                    }
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("page", pageName);
                    pages.appendTag(data);
                    nbt.setTag("pages", pages);
                } else {
                    NBTTagList pages = new NBTTagList();
                    NBTTagCompound data = new NBTTagCompound();
                    data.setString("page", pageName);
                    pages.appendTag(data);
                    nbt.setTag("pages", pages);
                }
                player.getHeldItem().setTagCompound(nbt);
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> getFoundPages(EntityPlayer player) {
        /*if (player != null) {
            EntityPlayer newPlayer = Minecraft.getMinecraft().theWorld.func_152378_a(player.getUniqueID());
            EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(newPlayer, BLEntityPropertiesRegistry.MANUAL);
            if (property != null)
                return property.foundPages;
        }
        return null;*/

        if (player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualGuideBook) {
            ArrayList<String> foundPages = new ArrayList<>();
            NBTTagCompound nbt = player.getHeldItem().getTagCompound();
            if (nbt != null) {
                NBTTagList tag = nbt.getTagList("pages", 10);
                if (tag != null) {
                    for (int i = 0; i < tag.tagCount(); i++) {
                        NBTTagCompound data = tag.getCompoundTagAt(i);
                        foundPages.add(data.getString("page"));
                    }
                }
                return foundPages;
            }
        }
        return null;

    }

    public static boolean hasFoundPage(EntityPlayer player, String page) {
        /*if (player != null) {
            EntityPlayer newPlayer = Minecraft.getMinecraft().theWorld.func_152378_a(player.getUniqueID());
            EntityPropertiesManual property = BLEntityPropertiesRegistry.INSTANCE.getProperties(newPlayer, BLEntityPropertiesRegistry.MANUAL);
            if (property != null)
                if (property.foundPages.contains(page.toLowerCase())) {
                    return true;
                }
        }
        return false;*/
        return player != null && page != null && getFoundPages(player) != null && getFoundPages(player).contains(page.toLowerCase());
    }


    public static void PlayerDiscoverPage(EntityLiving entity, String name) {
        EntityPlayer player = entity.worldObj.getClosestPlayerToEntity(entity, 20);
        if (!ManualManager.hasFoundPage(player, name) && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualGuideBook && !player.worldObj.isRemote) {
            Vec3 vec3 = player.getLook(1.0F).normalize();
            Vec3 vec31 = Vec3.createVectorHelper(entity.posX - player.posX, entity.boundingBox.minY + (double) (entity.height / 2.0F) - (player.posY + (double) player.getEyeHeight()), entity.posZ - player.posZ);
            double d0 = vec31.lengthVector();
            vec31 = vec31.normalize();
            double d1 = vec3.dotProduct(vec31);
            if (d1 > 1.0D - 0.025D / d0 && player.canEntityBeSeen(entity)) {
                if (ManualManager.findPage(player, name))
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("manual.discover.page") + ": " + StatCollector.translateToLocal("manual." + name + ".title")));
            }
        }
    }
}
