package thebetweenlands.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.crash.CrashReport;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.aspect.IAspect;

import java.util.List;
import java.util.Map;

public class AspectManager {
	private static Map<String, IAspect> aspectMap = Maps.newHashMap();

	public static void addAspectToStack(IAspect aspect, ItemStack itemStack) {
		if (!itemStack.hasTagCompound()) itemStack.setTagCompound(new NBTTagCompound());
		if (!itemStack.getTagCompound().hasKey("aspects")) itemStack.getTagCompound().setTag("aspects", new NBTTagList());

		NBTTagList list = itemStack.getTagCompound().getTagList("aspects", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound aspectTag = new NBTTagCompound();
		aspectTag.setString("aspectName", aspect.getName());
		aspect.writeToNBT(aspectTag);
		list.appendTag(aspectTag);
		itemStack.getTagCompound().setTag("aspects", list);
	}

	public static void removeAspectFromStack(IAspect aspect, ItemStack itemStack) {
		if (!itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("aspects")) return;

		NBTTagList list = itemStack.getTagCompound().getTagList("aspects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound aspectTag = list.getCompoundTagAt(i);
			if (aspect.getName().equals(aspectTag.getString("aspectName"))) {
				list.removeTag(i);
			}
		}
	}

	public static List<IAspect> getAspectsFromStack(ItemStack itemStack) {
		List<IAspect> aspects = Lists.newArrayList();
		if (!itemStack.hasTagCompound() || !itemStack.getTagCompound().hasKey("aspects")) return aspects;

		NBTTagList list = itemStack.getTagCompound().getTagList("aspects", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound aspectTag = list.getCompoundTagAt(i);
			IAspect aspect = aspectMap.get(aspectTag.getString("aspectName"));
			aspect.readFromNBT(aspectTag);
			aspects.add(aspect);
		}

		return aspects;
	}

	public static boolean hasAspect(IAspect aspect, ItemStack itemStack) {
		for (IAspect aspectInStack : getAspectsFromStack(itemStack)) {
			if (aspectInStack == aspect) return true;
		}
		return false;
	}

	public static void registerAspect(IAspect aspect) {
		if (aspectMap.containsKey(aspect.getName())) CrashReport.makeCrashReport(null, "Can't register aspect " + aspect.getName());
		aspectMap.put(aspect.getName(), aspect);
	}
}
