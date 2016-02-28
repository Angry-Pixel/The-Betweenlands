package thebetweenlands.entities.properties.list.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.entities.properties.EntityProperties;

public class EntityPropertiesEquipment extends EntityProperties<Entity> {
	private EquipmentInventory equipment;

	@Override
	protected void initProperties() {
		this.equipment = new EquipmentInventory(this.getEntity());
	}

	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		this.equipment.saveNBTData(nbt);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		this.equipment.loadNBTData(nbt);
	}

	@Override
	public String getID() {
		return "blPropertyEquipment";
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

	public EquipmentInventory getEquipmentInventory() {
		return this.equipment;
	}
}
