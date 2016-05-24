package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSword;

public class ItemBLSword extends ItemSword {
    public ItemBLSword(ToolMaterial material) {
        super(material);

        setRegistryName(material.name() + "Sword");
        setUnlocalizedName(getRegistryName().toString());
    }
}
