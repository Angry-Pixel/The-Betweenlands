package thebetweenlands.manual;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import thebetweenlands.items.BLItemRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bart on 21/11/2015.
 */
public class ManualManager {
    public static List<String> findablePagesGuideBook = new ArrayList<>();
    public static List<String> findablePagesHL = new ArrayList<>();
    public static List<String> findablePagesAll = new ArrayList<>();

    public static boolean findPage(EntityPlayer player, String pageName, EnumManual manualType) {
        if (pageName != null && player != null) {
            for (int i = -1; i < 36; i++) {
                ItemStack stack;
                if (i >= 0)
                    stack = player.inventory.getStackInSlot(i);
                else
                    stack = player.getCurrentEquippedItem();
                if (manualType.equals(EnumManual.GUIDEBOOK) && stack != null && stack.getItem() == BLItemRegistry.manualGuideBook) {
                    NBTTagCompound nbt = stack.getTagCompound();
                    if (nbt == null)
                        nbt = new NBTTagCompound();
                    ArrayList<String> foundPages = getFoundPages(player, manualType);
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
                    player.inventory.getStackInSlot(i).setTagCompound(nbt);
                    return true;
                } else if (manualType.equals(EnumManual.HL) && stack != null && stack.getItem() == BLItemRegistry.manualHL) {
                    NBTTagCompound nbt = stack.getTagCompound();
                    if (nbt == null)
                        nbt = new NBTTagCompound();
                    ArrayList<String> foundPages = getFoundPages(player, manualType);
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
                    player.inventory.getStackInSlot(i).setTagCompound(nbt);
                    return true;
                }
            }
        }
        return false;
    }

    public static ArrayList<String> getFoundPages(EntityPlayer player, EnumManual manualType) {

        if (player != null) {
            ArrayList<String> foundPages = new ArrayList<>();
            for (int i = -1; i < 36; i++) {
                ItemStack stack;
                if (i >= 0)
                    stack = player.inventory.getStackInSlot(i);
                else
                    stack = player.getCurrentEquippedItem();
                if (manualType.equals(EnumManual.GUIDEBOOK) && stack != null && stack.getItem() == BLItemRegistry.manualGuideBook) {
                    NBTTagCompound nbt = stack.getTagCompound();
                    if (nbt != null) {
                        NBTTagList tag = nbt.getTagList("pages", 10);
                        if (tag != null) {
                            for (int j = 0; j < tag.tagCount(); j++) {
                                NBTTagCompound data = tag.getCompoundTagAt(j);
                                foundPages.add(data.getString("page"));
                            }
                        }
                        return foundPages;
                    }
                } else if (manualType.equals(EnumManual.HL) && stack != null && stack.getItem() == BLItemRegistry.manualHL) {
                    NBTTagCompound nbt = stack.getTagCompound();
                    if (nbt != null) {
                        NBTTagList tag = nbt.getTagList("pages", 10);
                        if (tag != null) {
                            for (int j = 0; j < tag.tagCount(); j++) {
                                NBTTagCompound data = tag.getCompoundTagAt(j);
                                foundPages.add(data.getString("page"));
                            }
                        }
                        return foundPages;
                    }
                }
            }
        }
        return null;

    }

    public static boolean hasFoundPage(EntityPlayer player, String page, EnumManual manualType) {
        return player != null && page != null && getFoundPages(player, manualType) != null && getFoundPages(player, manualType).contains(page.toLowerCase());
    }


    public static void playerDiscoverPage(EntityLiving entity, String name, EnumManual manualType) {
        EntityPlayer player = entity.worldObj.getClosestPlayerToEntity(entity, 20);
        if (manualType.equals(EnumManual.GUIDEBOOK) && !ManualManager.hasFoundPage(player, name, manualType) && player != null && player.inventory.hasItem(BLItemRegistry.manualGuideBook) && !player.worldObj.isRemote) {
            Vec3 vec3 = player.getLook(1.0F).normalize();
            Vec3 vec31 = Vec3.createVectorHelper(entity.posX - player.posX, entity.boundingBox.minY + (double) (entity.height / 2.0F) - (player.posY + (double) player.getEyeHeight()), entity.posZ - player.posZ);
            double d0 = vec31.lengthVector();
            vec31 = vec31.normalize();
            double d1 = vec3.dotProduct(vec31);
            if (d1 > 1.0D - 0.025D / d0 && player.canEntityBeSeen(entity)) {
                if (ManualManager.findPage(player, name, manualType))
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("manual.discover.page") + ": " + StatCollector.translateToLocal("manual." + name + ".title")));
            }
        } else if (manualType.equals(EnumManual.HL) && !ManualManager.hasFoundPage(player, name, manualType) && player != null && player.inventory.hasItem(BLItemRegistry.manualHL) && !player.worldObj.isRemote) {
            Vec3 vec3 = player.getLook(1.0F).normalize();
            Vec3 vec31 = Vec3.createVectorHelper(entity.posX - player.posX, entity.boundingBox.minY + (double) (entity.height / 2.0F) - (player.posY + (double) player.getEyeHeight()), entity.posZ - player.posZ);
            double d0 = vec31.lengthVector();
            vec31 = vec31.normalize();
            double d1 = vec3.dotProduct(vec31);
            if (d1 > 1.0D - 0.025D / d0 && player.canEntityBeSeen(entity)) {
                if (ManualManager.findPage(player, name, manualType))
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("manual.discover.page") + ": " + StatCollector.translateToLocal("manual." + name + ".title")));
            }
        }
    }


    public static void setCurrentPage(String category, int pageNumber, EnumManual manualType, EntityPlayer player) {
        player = player.worldObj.getClosestPlayerToEntity(player, 20);
        if (manualType.equals(EnumManual.GUIDEBOOK) && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualGuideBook && category != null) {
            NBTTagCompound nbt = player.getHeldItem().getTagCompound();
            if (nbt == null)
                nbt = new NBTTagCompound();
            nbt.setInteger("pageNumber", pageNumber);
            nbt.setString("category", category);
            player.getHeldItem().setTagCompound(nbt);
        } else if (manualType.equals(EnumManual.HL) && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualHL && category != null) {
            NBTTagCompound nbt = player.getHeldItem().getTagCompound();
            if (nbt == null)
                nbt = new NBTTagCompound();
            nbt.setInteger("pageNumber", pageNumber);
            nbt.setString("category", category);
            player.getHeldItem().setTagCompound(nbt);
        }
    }

    public static int getCurrentPageNumber(EnumManual manualType, EntityPlayer player) {
        player = player.worldObj.getClosestPlayerToEntity(player, 20);
        if (manualType.equals(EnumManual.GUIDEBOOK) && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualGuideBook) {
            NBTTagCompound nbt = player.getHeldItem().getTagCompound();
            if (nbt != null && nbt.hasKey("pageNumber"))
                return nbt.getInteger("pageNumber");
        } else if (manualType.equals(EnumManual.HL) && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualHL) {
            NBTTagCompound nbt = player.getHeldItem().getTagCompound();
            if (nbt != null && nbt.hasKey("pageNumber"))
                return nbt.getInteger("pageNumber");
        }
        return 1;
    }

    public static ManualCategory getCurrentCategory(EnumManual manualType, EntityPlayer player) {
        player = player.worldObj.getClosestPlayerToEntity(player, 20);
        if (manualType.equals(EnumManual.GUIDEBOOK) && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualGuideBook) {
            NBTTagCompound nbt = player.getHeldItem().getTagCompound();
            if (nbt != null && nbt.hasKey("category") && getCategoryFromString(nbt.getString("category"), manualType) != null)
                return getCategoryFromString(nbt.getString("category"), manualType);
            else
                return GuideBookEntryRegistry.itemsCategory;
        } else if (manualType.equals(EnumManual.HL) && player != null && player.getHeldItem() != null && player.getHeldItem().getItem() == BLItemRegistry.manualHL) {
            NBTTagCompound nbt = player.getHeldItem().getTagCompound();
            if (nbt != null && nbt.hasKey("category") && getCategoryFromString(nbt.getString("category"), manualType) != null)
                return getCategoryFromString(nbt.getString("category"), manualType);
            else
                return HLEntryRegistry.aspectCategory;
        }
        return null;
    }

    public static enum EnumManual {
        GUIDEBOOK, HL;
    }

    public static ManualCategory getCategoryFromString(String categoryName, EnumManual manualType){
        if (manualType.equals(EnumManual.GUIDEBOOK) && categoryName != null)
            for (ManualCategory category:GuideBookEntryRegistry.CATEGORIES)
                if (category.name.equals(categoryName))
                    return category;
        if (manualType.equals(EnumManual.HL) && categoryName != null)
            for (ManualCategory category:HLEntryRegistry.CATEGORIES)
                if (category.name.equals(categoryName))
                    return category;
        return null;
    }
}
