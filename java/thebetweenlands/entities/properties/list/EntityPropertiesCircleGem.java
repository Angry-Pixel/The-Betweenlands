package thebetweenlands.entities.properties.list;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.gemcircle.EntityAmulet;
import thebetweenlands.gemcircle.CircleGem;
import thebetweenlands.gemcircle.EntityGem;
import thebetweenlands.gemcircle.EntityGem.Type;

public class EntityPropertiesCircleGem extends EntityProperties<EntityLivingBase> {
	private List<EntityGem> circleGems = new ArrayList<EntityGem>();
	private List<EntityAmulet> amulets = new ArrayList<EntityAmulet>();

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagList gemList = new NBTTagList();
		for(EntityGem gem : this.circleGems) {
			NBTTagCompound gemCompound = new NBTTagCompound();
			gem.writeToNBT(gemCompound);
			gemList.appendTag(gemCompound);
		}
		nbt.setTag("gems", gemList);

		NBTTagList amuletList = new NBTTagList();
		for(EntityAmulet amulet : this.amulets) {
			NBTTagCompound amuletCompound = new NBTTagCompound();
			amulet.writeToNBT(amuletCompound);
			amuletList.appendTag(amuletCompound);
		}
		nbt.setTag("amulets", amuletList);
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

		this.amulets.clear();
		NBTTagList amuletList = nbt.getTagList("amulets", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < amuletList.tagCount(); i++) {
			NBTTagCompound amuletCompound = amuletList.getCompoundTagAt(i);
			EntityAmulet amulet = EntityAmulet.readFromNBT(amuletCompound);
			if(amulet != null) {
				this.amulets.add(amulet);
			}
		}
	}

	@Override
	public String getID() {
		return "blPropertyCircleGem";
	}

	@Override
	public Class<EntityLivingBase> getEntityClass() {
		return EntityLivingBase.class;
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

	public void removeGem(CircleGem gem) {
		this.circleGems.remove(gem);
	}

	public List<EntityGem> getGems() {
		return this.circleGems;
	}

	public boolean hasGem(CircleGem gem) {
		return this.circleGems.contains(gem);
	}

	public List<EntityAmulet> getAmulets() {
		return this.amulets;
	}

	public void removeAmulet(EntityAmulet amulet) {
		if(this.amulets.remove(amulet)) {
			EntityGem lastOccurrence = null;
			for(int i = this.circleGems.size() - 1; i >= 0; i--) {
				EntityGem currentGem = this.circleGems.get(i);
				if(currentGem.getGem() == amulet.getAmuletGem() && currentGem.matches(Type.BOTH)) {
					lastOccurrence = currentGem;
					break;
				}
			}
			if(lastOccurrence != null) {
				this.circleGems.remove(lastOccurrence);
			}
		}
	}

	public boolean addAmulet(ItemStack item, boolean removable) {
		CircleGem gem = CircleGem.getGem(item);
		if(gem != CircleGem.NONE) {
			this.amulets.add(new EntityAmulet(gem, removable));
			this.addGem(gem, EntityGem.Type.BOTH);
			return true;
		}
		return false;
	}
}
