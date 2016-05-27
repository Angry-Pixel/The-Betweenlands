package thebetweenlands.entities.properties.list;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.event.player.FoodSicknessEventHandler;
import thebetweenlands.event.player.FoodSicknessEventHandler.Sickness;

public class EntityPropertiesFood extends EntityProperties<EntityPlayer> {
	private Map<String, Integer> hatredMap = Maps.newHashMap();
	private int lastHatred = 0;

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
		compound.setInteger("Size", this.hatredMap.size());
		NBTTagList list = new NBTTagList();
		for (Map.Entry<String, Integer> entry : this.hatredMap.entrySet()) {
			NBTTagCompound listCompound = new NBTTagCompound();
			listCompound.setString("Food", entry.getKey());
			listCompound.setInteger("Level", entry.getValue());
			list.appendTag(listCompound);
		}
		compound.setTag("HatredMap", list);
		compound.setInteger("LastHatred", this.lastHatred);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		this.hatredMap = Maps.newHashMap();
		int size = compound.getInteger("Size");
		NBTTagList list = compound.getTagList("HatredMap", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < size; i++) {
			NBTTagCompound listCompound = list.getCompoundTagAt(i);
			String food = listCompound.getString("Food");
			int level = listCompound.getInteger("Level");
			this.hatredMap.put(food, level);
		}
		this.lastHatred = compound.getInteger("LastHatred");
	}

	@Override
	public boolean saveTrackingSensitiveData(NBTTagCompound compound) {
		this.saveNBTData(compound);
		return false;
	}

	@Override
	public void loadTrackingSensitiveData(NBTTagCompound compound) {
		this.loadNBTData(compound);
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}

	public int getFoodHatred(ItemFood food) {
		if (this.hatredMap.containsKey(food.getUnlocalizedName())) {
			return this.hatredMap.get(food.getUnlocalizedName());
		} else {
			return 0;
		}
	}

	public void increaseFoodHatred(ItemFood food, int amount, int decreaseForOthers) {
		int finalMaxHatred = Sickness.VALUES[Math.max(Sickness.VALUES.length - 1, 0)].maxHatred;
		if (this.hatredMap.containsKey(food.getUnlocalizedName())) {
			int currentAmount = this.hatredMap.get(food.getUnlocalizedName());
			this.hatredMap.put(food.getUnlocalizedName(), Math.max(Math.min(currentAmount + amount, finalMaxHatred), 0));
		} else {
			this.hatredMap.put(food.getUnlocalizedName(), Math.max(Math.min(amount, finalMaxHatred), 0));
		}
		this.lastHatred = this.hatredMap.get(food.getUnlocalizedName());
		decreaseHatredForAllExcept(food, decreaseForOthers);
	}

	public void decreaseHatredForAllExcept(ItemFood food, int decrease) {
		if(decrease > 0) {
			Map<String, Integer> newHatredMap = Maps.newHashMap();
			for (String key : this.hatredMap.keySet()) {
				if (!key.equals(food.getUnlocalizedName())) {
					newHatredMap.put(key, Math.max(this.hatredMap.get(key) - decrease, 0));
				}
			}
			this.hatredMap.putAll(newHatredMap);
		}
	}

	public FoodSicknessEventHandler.Sickness getSickness(ItemFood food) {
		return FoodSicknessEventHandler.Sickness.getSicknessForHatred(getFoodHatred(food));
	}

	public FoodSicknessEventHandler.Sickness getLastSickness() {
		return FoodSicknessEventHandler.Sickness.getSicknessForHatred(this.lastHatred);
	}
}
