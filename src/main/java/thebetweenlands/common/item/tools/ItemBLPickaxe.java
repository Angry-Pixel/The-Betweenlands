package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemPickaxe;

public class ItemBLPickaxe extends ItemPickaxe {
    public ItemBLPickaxe(ToolMaterial material) {
        super(material);
        setRegistryName(material.name() + "Pickaxe");
        setUnlocalizedName(getRegistryName().toString());
    }
}
