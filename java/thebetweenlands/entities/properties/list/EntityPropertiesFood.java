package thebetweenlands.entities.properties.list;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.entities.properties.EntityProperties;

import java.util.Map;

public class EntityPropertiesFood extends EntityProperties<EntityPlayer> {
	private Map<String, Integer> hatredMap = Maps.newHashMap();

	@Override
	public String getID() {
		return "blPropertyFood";
	}

	@Override
	public Class<EntityPlayer> getEntityClass() {
		return EntityPlayer.class;
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		compound.setInteger("Size", hatredMap.size());
		NBTTagList list = new NBTTagList();
		for (Map.Entry<String, Integer> entry : hatredMap.entrySet()) {
			NBTTagCompound listCompound = new NBTTagCompound();
			listCompound.setString("Food", entry.getKey());
			listCompound.setInteger("Level", entry.getValue());
			list.appendTag(listCompound);
		}
		compound.setTag("HatredMap", list);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		hatredMap = Maps.newHashMap();
		int size = compound.getInteger("Size");
		NBTTagList list = compound.getTagList("HatredMap", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < size; i++) {
			NBTTagCompound listCompound = list.getCompoundTagAt(i);
			String food = listCompound.getString("Food");
			int level = listCompound.getInteger("Level");
			hatredMap.put(food, level);
		}
	}

	@Override
	public boolean saveTrackingSensitiveData(NBTTagCompound compound) {
		compound.setInteger("Size", hatredMap.size());
		NBTTagList list = new NBTTagList();
		for (Map.Entry<String, Integer> entry : hatredMap.entrySet()) {
			NBTTagCompound listCompound = new NBTTagCompound();
			listCompound.setString("Food", entry.getKey());
			listCompound.setInteger("Level", entry.getValue());
			list.appendTag(listCompound);
		}
		compound.setTag("HatredMap", list);
		return true;
	}

	@Override
	public void loadTrackingSensitiveData(NBTTagCompound compound) {
		hatredMap = Maps.newHashMap();
		int size = compound.getInteger("Size");
		NBTTagList list = compound.getTagList("HatredMap", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < size; i++) {
			NBTTagCompound listCompound = list.getCompoundTagAt(i);
			String food = listCompound.getString("Food");
			int level = listCompound.getInteger("Level");
			hatredMap.put(food, level);
		}
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}

	public int getFoodHatred(ItemFood food) {
		if (hatredMap.containsKey(food.getUnlocalizedName())) {
			return hatredMap.get(food.getUnlocalizedName());
		} else {
			return 0;
		}
	}

	public void increaseFoodHatred(ItemFood food) {
		increaseFoodHatred(food, 1);
	}

	public void increaseFoodHatred(ItemFood food, int amount) {
		if (hatredMap.containsKey(food.getUnlocalizedName())) {
			int currentAmount = hatredMap.get(food.getUnlocalizedName());
			hatredMap.put(food.getUnlocalizedName(), currentAmount + amount);
		} else {
			hatredMap.put(food.getUnlocalizedName(), amount);
		}
	}
}
