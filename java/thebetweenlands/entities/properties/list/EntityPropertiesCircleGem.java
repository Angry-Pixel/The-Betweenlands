package thebetweenlands.entities.properties.list;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.EntityGem;

public class EntityPropertiesCircleGem extends EntityProperties<Entity> {
	private List<EntityGem> circleGems = new ArrayList<EntityGem>();
	private int amuletSlots = 1;
	public static final int MAX_AMULET_SLOTS = 3;

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagList gemList = new NBTTagList();
		for(EntityGem gem : this.circleGems) {
			NBTTagCompound gemCompound = new NBTTagCompound();
			gem.writeToNBT(gemCompound);
			gemList.appendTag(gemCompound);
		}
		nbt.setTag("gems", gemList);

		nbt.setInteger("amuletSlots", this.amuletSlots);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.circleGems.clear();
		NBTTagList gemList = nbt.getTagList("gems", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < gemList.tagCount(); i++) {
			NBTTagCompound gemCompound = gemList.getCompoundTagAt(i);
			EntityGem gem = EntityGem.readFromNBT(gemCompound);
			if(gem != null) {
				this.circleGems.add(gem);
			}
		}

		if(nbt.hasKey("amuletSlots"))
			this.amuletSlots = nbt.getInteger("amuletSlots");
	}

	@Override
	public String getID() {
		return "blPropertyCircleGem";
	}

	@Override
	public Class<Entity> getEntityClass() {
		return Entity.class;
	}

	@Override
	public int getTrackingTime() {
		return 0;
	}

	@Override
	public boolean saveTrackingSensitiveData(NBTTagCompound nbt) {
		this.saveNBTData(nbt);
		return false;
	}

	@Override
	public void loadTrackingSensitiveData(NBTTagCompound nbt) {
		this.loadNBTData(nbt);
	}

	public void addGem(CircleGem gem, EntityGem.Type type) {
		this.circleGems.add(new EntityGem(gem, type));
	}

	public void removeGem(EntityGem gem) {
		this.circleGems.remove(gem);
	}

	public List<EntityGem> getGems() {
		return this.circleGems;
	}

	public int getAmuletSlots() {
		return this.amuletSlots;
	}

	public void addAmuletSlot() {
		this.amuletSlots++;
	}
}
