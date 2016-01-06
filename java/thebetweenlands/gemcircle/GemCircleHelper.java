package thebetweenlands.gemcircle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;

public class GemCircleHelper {
	/**
	 * Returns true if gems are applicable to the object
	 * @param obj
	 * @return
	 */
	public static boolean isApplicable(Object obj) {
		if(obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			Item item = stack.getItem();
			return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemBow;
		} else if(obj instanceof EntityLivingBase) {
			return true;
		}
		return false;
	}

	public static void setGem(NBTTagCompound root, CircleGem gem) {
		if(root != null) {
			root.setString("blCircleGem", gem == null ? "none" : gem.name);
		}
	}

	public static CircleGem getGem(NBTTagCompound root) {
		if(root != null) {
			return CircleGem.fromName(root.getString("blCircleGem"));
		}
		return CircleGem.NONE;
	}

	public static int getRelation(NBTTagCompound root1, NBTTagCompound root2) {
		return getGem(root1).getRelation(getGem(root2));
	}

	public static int getRelation(CircleGem gem1, CircleGem gem2) {
		return gem1.getRelation(gem2);
	}

	public static void setGem(ItemStack stack, CircleGem gem) {
		setGem(stack.stackTagCompound, gem);
	}

	public static CircleGem getGem(ItemStack stack) {
		return getGem(stack.stackTagCompound);
	}

	public static void setGem(Entity entity, CircleGem gem) {
		setGem(entity.getEntityData(), gem);
	}

	public static CircleGem getGem(Entity entity) {
		return getGem(entity.getEntityData());
	}
}
