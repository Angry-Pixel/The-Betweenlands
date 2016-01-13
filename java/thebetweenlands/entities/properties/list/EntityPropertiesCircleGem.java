package thebetweenlands.entities.properties.list;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.gemcircle.CircleGem;

public class EntityPropertiesCircleGem extends EntityProperties {
	private CircleGem circleGem = CircleGem.NONE;

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setString("blCircleGem", this.circleGem.name);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.circleGem = CircleGem.fromName(nbt.getString("blCircleGem"));
	}

	@Override
	public String getID() {
		return "blPropertyCircleGem";
	}

	@Override
	public Class<? extends Entity> getEntityClass() {
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

	public void setGem(CircleGem gem) {
		this.circleGem = gem != null ? gem : CircleGem.NONE;
	}

	public CircleGem getGem() {
		return this.circleGem;
	}
}
