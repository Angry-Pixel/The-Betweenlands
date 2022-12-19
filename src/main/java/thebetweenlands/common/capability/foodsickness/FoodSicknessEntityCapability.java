package thebetweenlands.common.capability.foodsickness;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.api.capability.IFoodSicknessCapability;
import thebetweenlands.api.capability.ISerializableCapability;
import thebetweenlands.common.capability.base.EntityCapability;
import thebetweenlands.common.handler.FoodSicknessHandler;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class FoodSicknessEntityCapability extends EntityCapability<FoodSicknessEntityCapability, IFoodSicknessCapability, EntityPlayer> implements IFoodSicknessCapability, ISerializableCapability {
	@Override
	public ResourceLocation getID() {
		return new ResourceLocation(ModInfo.ID, "food_sickness");
	}

	@Override
	protected Capability<IFoodSicknessCapability> getCapability() {
		return CapabilityRegistry.CAPABILITY_FOOD_SICKNESS;
	}

	@Override
	protected Class<IFoodSicknessCapability> getCapabilityClass() {
		return IFoodSicknessCapability.class;
	}

	@Override
	protected FoodSicknessEntityCapability getDefaultCapabilityImplementation() {
		return new FoodSicknessEntityCapability();
	}

	@Override
	public boolean isApplicable(Entity entity) {
		return entity instanceof EntityPlayer;
	}

	@Override
	public boolean isPersistent(EntityPlayer oldPlayer, EntityPlayer newPlayer, boolean wasDead) {
		return true;
	}



	private Map<Item, Integer> hatredMap = Maps.newHashMap();
	private FoodSickness lastSickness = FoodSickness.FINE;

	@Override
	public FoodSickness getLastSickness() {
		return this.lastSickness;
	}

	@Override
	public void setLastSickness(FoodSickness sickness) {
		this.lastSickness = sickness;
	}

	@Override
	public FoodSickness getSickness(Item food) {
		return FoodSickness.getSicknessForHatred(this.getFoodHatred(food));
	}

	@Override
	public void decreaseHatredForAllExcept(Item food, int decrease) {
		if(decrease > 0) {
			Map<Item, Integer> newHatredMap = Maps.newHashMap();
			for (Item key : this.hatredMap.keySet()) {
				if (key != food) {
					newHatredMap.put(key, Math.max(this.hatredMap.get(key) - decrease, 0));
				}
			}
			if(!newHatredMap.isEmpty()) {
				this.hatredMap.putAll(newHatredMap);
				this.markDirty();
			}
		}
	}

	@Override
	public void increaseFoodHatred(Item food, int amount, int decreaseForOthers) {
		if (!FoodSicknessHandler.isFoodSicknessEnabled(this.getEntity().getEntityWorld()))
			return;
		int finalMaxHatred = FoodSickness.VALUES[Math.max(FoodSickness.VALUES.length - 1, 0)].maxHatred;
		if (this.hatredMap.containsKey(food)) {
			int currentAmount = this.hatredMap.get(food);
			this.hatredMap.put(food, Math.max(Math.min(currentAmount + amount, finalMaxHatred), 0));
		} else {
			this.hatredMap.put(food, Math.max(Math.min(amount, finalMaxHatred), 0));
		}
		this.decreaseHatredForAllExcept(food, decreaseForOthers);
		this.markDirty();
	}

	@Override
	public int getFoodHatred(Item food) {
		if (this.hatredMap.containsKey(food)) {
			return this.hatredMap.get(food);
		}
		return 0;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for (Map.Entry<Item, Integer> entry : this.hatredMap.entrySet()) {
			NBTTagCompound listCompound = new NBTTagCompound();
			listCompound.setString("Food", entry.getKey().getRegistryName().toString());
			listCompound.setInteger("Level", entry.getValue());
			list.appendTag(listCompound);
		}
		nbt.setTag("HatredMap", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.hatredMap = Maps.newHashMap();
		NBTTagList list = nbt.getTagList("HatredMap", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound listCompound = list.getCompoundTagAt(i);
			Item food = Item.getByNameOrId(listCompound.getString("Food"));
			if(food != null) {
				int level = listCompound.getInteger("Level");
				this.hatredMap.put(food, level);
			}
		}
	}

	@Override
	public void writeTrackingDataToNBT(NBTTagCompound nbt) {
		this.writeToNBT(nbt);
	}

	@Override
	public void readTrackingDataFromNBT(NBTTagCompound nbt) {
		this.readFromNBT(nbt);
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}
}
