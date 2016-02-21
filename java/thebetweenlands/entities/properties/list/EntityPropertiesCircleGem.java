package thebetweenlands.entities.properties.list;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.gemcircle.CircleGem;

public class EntityPropertiesCircleGem extends EntityProperties<EntityLivingBase> {
	private CircleGem circleGem = CircleGem.NONE;
	private boolean hasAmulet = false;
	private boolean isRemovable = false;

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setString("blCircleGem", this.circleGem.name);
		nbt.setBoolean("blAmulet", this.hasAmulet);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.circleGem = CircleGem.fromName(nbt.getString("blCircleGem"));
		this.hasAmulet = nbt.getBoolean("blAmulet");
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

	public void setGem(CircleGem gem) {
		this.circleGem = gem != null ? gem : CircleGem.NONE;
	}

	public CircleGem getGem() {
		return this.circleGem;
	}

	public void addAmulet(boolean removable) {
		this.hasAmulet = true;
		this.isRemovable = removable;
	}

	public boolean isRemovable() {
		return this.isRemovable;
	}
	
	public void removeAmulet() {
		this.hasAmulet = false;
	}

	public boolean hasAmulet() {
		return this.hasAmulet;
	}
}
