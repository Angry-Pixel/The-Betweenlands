package thebetweenlands.compat.tmg;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.m4thg33k.tombmanygraves2api.api.inventory.AbstractSpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.inventory.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.inventory.TransitionInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.registries.CapabilityRegistry;

/**
 * Copied from provided example
 * Original code by M4thG33k
 */
public class TMGEquipmentInventory extends AbstractSpecialInventory {

    @Override
    public String getUniqueIdentifier() {
        return "TheBetweenlandsEquipment";
    }

    @Override
    public NBTBase getNbtData(EntityPlayer player) {
        IEquipmentCapability equipmentCapability = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
        if (equipmentCapability != null) {
            NBTTagCompound compound = new NBTTagCompound();
            boolean setTag = false;

            for (EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
                IInventory inv = equipmentCapability.getInventory(type);

                NBTTagList tagList = SpecialInventoryHelper.getTagListFromIInventory(inv);
                if (tagList != null) {
                    compound.setTag(type.ordinal() + "", tagList);
                    setTag = true;
                }
            }

            if (setTag) {
                return compound;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
        if (compound instanceof NBTTagCompound) {
            IEquipmentCapability equipmentCapability = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

            if (equipmentCapability != null) {

                for (EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
                    if (((NBTTagCompound) compound).hasKey(type.ordinal() + "")) {
                        NBTTagList tagList = (NBTTagList) ((NBTTagCompound) compound).getTag(type.ordinal() + "");

                        TransitionInventory graveItems = new TransitionInventory(tagList);
                        IInventory currentInventory = equipmentCapability.getInventory(type);

                        for (int i = 0; i < graveItems.getSizeInventory(); i++) {
                            ItemStack graveItem = graveItems.getStackInSlot(i);

                            if (type == EnumEquipmentInventory.AMULET && i >= equipmentCapability.getAmuletSlots()){
                                SpecialInventoryHelper.dropItem(player, graveItem);
                                continue;
                            }

                            if (! graveItem.isEmpty()) {
                                ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                                if (playerItem.isEmpty()) {
                                    currentInventory.setInventorySlotContents(i, graveItem);
                                } else if (shouldForce) {
                                    currentInventory.setInventorySlotContents(i, graveItem);
                                    SpecialInventoryHelper.dropItem(player, playerItem);
                                } else {
                                    SpecialInventoryHelper.dropItem(player, graveItem);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(NBTBase compound) {
        List<ItemStack> ret = new ArrayList<>();

        if (compound instanceof NBTTagCompound) {
            for (EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
                if (((NBTTagCompound) compound).hasKey(type.ordinal() + "")) {
                    NBTTagList tagList = (NBTTagList) ((NBTTagCompound) compound).getTag(type.ordinal() + "");

                    ret.addAll((new TransitionInventory(tagList)).getListOfNonEmptyItemStacks());
                }
            }
        }

        return ret;
    }

    @Override
    public String getInventoryDisplayNameForGui() {
        return "Betweenlands Equipment";
    }

    @Override
    public int getInventoryDisplayNameColorForGui() {
        return 0x46AE46;
    }

}
