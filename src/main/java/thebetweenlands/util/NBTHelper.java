package thebetweenlands.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thebetweenlands.common.capability.base.AbstractCapability;

public class NBTHelper {
	/**
	 * Returns the ItemStack NBT and creates a new NBTTagCompound if necessary
	 * @param stack
	 * @return
	 */
	public static NBTTagCompound getStackNBTSafe(ItemStack stack) {
		if(stack.getTagCompound() == null)
			stack.setTagCompound(new NBTTagCompound());
		return stack.getTagCompound();
	}

	/**
	 * Returns <tt>true</tt> if the two specifies item stacks' NBT
	 * compound tags are <i>equal</i> to one another.
	 * 
	 * @param a one NBT compound tag to be tested for equality
	 * @param b the other NBT compound tag to be tested for equality
	 * @param exclusions a list of capabilities to be excluded in checking equality
	 * @return <tt>true</tt> if the two NBT compounds tags are equal
	 * @see #areNBTCompoundsEquals(NBTTagCompound, NBTTagCompound, List)
	 */
	public static boolean areItemStackTagsEqualWithoutCapabilities(ItemStack a, ItemStack b, AbstractCapability<?, ?, ?>... exclusions) {
		List<String> strExclusions = new ArrayList<String>();
		for(AbstractCapability<?, ?, ?> exclusion : exclusions) {
			strExclusions.add("ForgeCaps." + exclusion.getID().toString());
		}
		return areItemStackTagsEqual(a, b, strExclusions);
	}
	
	/**
	 * Returns <tt>true</tt> if the two specifies item stacks' NBT
	 * compound tags are <i>equal</i> to one another.
	 * 
	 * @param a one NBT compound tag to be tested for equality
	 * @param b the other NBT compound tag to be tested for equality
	 * @param exclusions a list of tags to be excluded in checking equality
	 * @return <tt>true</tt> if the two NBT compounds tags are equal
	 * @see #areNBTCompoundsEquals(NBTTagCompound, NBTTagCompound, List)
	 */
	public static boolean areItemStackTagsEqual(ItemStack a, ItemStack b, Collection<String> exclusions) {
		if (a == null && b == null) {
			return true;
		}
		if (a != null && b != null) {
			if (a.getTagCompound() == null && b.getTagCompound() == null) {
				return true;
			}
			if (a.getTagCompound() == null ^ b.getTagCompound() == null) {
				return false;
			}
			return areNBTCompoundsEquals(a.getTagCompound(), b.getTagCompound(), exclusions);
		}
		return false;
	}

	/**
	 * Returns <tt>true</tt> if the two specified NBT compound tags
	 * are <i>equal</i> to one another. Two NBT compound tags are
	 * considered equal if both NBT compounds tags contain all of
	 * the same keys with the same values, while ignoring tags
	 * whose keys are in the exclusions list.
	 * 
	 * @param a one NBT compound tag to be tested for equality
	 * @param b the other NBT compound tag to be tested for equality
	 * @param exclusions a list of tags to be excluded in checking equality
	 * @return <tt>true</tt> if the two NBT compounds tags are equal
	 */
	public static boolean areNBTCompoundsEquals(NBTTagCompound a, NBTTagCompound b, Collection<String> exclusions) {
		Stack<String> tagOwners = new Stack<String>();
		Stack<NBTTagCompound> aTagCompounds = new Stack<NBTTagCompound>();
		Stack<NBTTagCompound> bTagCompounds = new Stack<NBTTagCompound>();
		tagOwners.push("");
		aTagCompounds.push(a);
		bTagCompounds.push(b);
		while (!aTagCompounds.isEmpty()) {
			String tagOwner = tagOwners.pop();
			NBTTagCompound aCurrentTagCompound = aTagCompounds.pop();
			NBTTagCompound bCurrentTagCompound = bTagCompounds.pop();
			Set<String> aKeys = aCurrentTagCompound.getKeySet();
			Set<String> bKeys = bCurrentTagCompound.getKeySet();
			for (String key : bKeys) {
				if (exclusions.contains(key)) {
					continue;
				}
				if (!aKeys.contains(key)) {
					return false;
				}
			}
			for (String key : aKeys) {
				String totalKey = tagOwner == "" ? key : tagOwner + '.' + key;
				if (exclusions.contains(totalKey)) {
					continue;
				}
				NBTBase aTag = aCurrentTagCompound.getTag(key);
				NBTBase bTag = bCurrentTagCompound.getTag(key);
				if (aTag instanceof NBTTagCompound && bTag instanceof NBTTagCompound) {
					tagOwners.push(totalKey);
					aTagCompounds.push((NBTTagCompound) aTag);
					bTagCompounds.push((NBTTagCompound) bTag);
				} else {
					if (!aTag.equals(bTag)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static NBTTagCompound saveAllItems(NBTTagCompound tag, IInventory inventory)
    {
        return saveAllItems(tag, inventory, true);
    }

    public static NBTTagCompound saveAllItems(NBTTagCompound tag, IInventory inventory, boolean saveEmpty)
    {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        if (!nbttaglist.isEmpty() || saveEmpty)
        {
            tag.setTag("Items", nbttaglist);
        }

        return tag;
    }

    public static void loadAllItems(NBTTagCompound tag, IInventory inventory)
    {
        NBTTagList nbttaglist = tag.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < inventory.getSizeInventory())
            {
            	inventory.setInventorySlotContents(j,  new ItemStack(nbttagcompound));
            }
        }
    }
}
