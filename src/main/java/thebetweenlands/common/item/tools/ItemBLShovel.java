package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSpade;

public class ItemBLShovel extends ItemSpade {
    public ItemBLShovel(ToolMaterial material) {
        super(material);
        setRegistryName(material.name() + "Shovel");
        setUnlocalizedName(getRegistryName().toString());
    }
}
