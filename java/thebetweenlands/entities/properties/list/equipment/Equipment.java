package thebetweenlands.entities.properties.list.equipment;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.items.IEquippable;

public class Equipment {
	public final ItemStack item;
	public final IEquippable equippable;
	public final EnumEquipmentCategory category;

	public Equipment(ItemStack item, EnumEquipmentCategory category) {
		if(item.getItem() instanceof IEquippable == false) 
			throw new RuntimeException("Item is not equippable!");
		this.item = item;
		this.equippable = (IEquippable) item.getItem();
		this.category = category != null ? category : EnumEquipmentCategory.NONE;
	}

	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound itemNBT = new NBTTagCompound();
		this.item.writeToNBT(itemNBT);
		nbt.setTag("item", itemNBT);
		nbt.setString("category", this.category.name);
	}

	public static Equipment readFromNBT(NBTTagCompound nbt) {
		NBTTagCompound itemNBT = nbt.getCompoundTag("item");
		ItemStack item = ItemStack.loadItemStackFromNBT(itemNBT);
		EnumEquipmentCategory category = EnumEquipmentCategory.fromName(nbt.getString("category"));
		return new Equipment(item, category);
	}
}