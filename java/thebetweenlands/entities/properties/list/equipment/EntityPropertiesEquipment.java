package thebetweenlands.entities.properties.list.equipment;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.entities.properties.EntityProperties;
import thebetweenlands.forgeevent.entity.EquipmentChangeEvent;

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

	@Override
	public void onSync() {
		MinecraftForge.EVENT_BUS.post(new EquipmentChangeEvent(this.getEntity()));
	}

	public EquipmentInventory getEquipmentInventory() {
		return this.equipment;
	}

	@Override
	protected boolean isPersistent() {
		return this.getWorld().getGameRules().getGameRuleBooleanValue("keepInventory");
	}
}
