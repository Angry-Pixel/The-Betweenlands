package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemPickaxe;
import thebetweenlands.common.item.ICustomItemRenderType;

public class ItemBLPickaxe extends ItemPickaxe implements ICustomItemRenderType {
    public ItemBLPickaxe(ToolMaterial material) {
        super(material);
    }

    @Override
    public String getCustomRenderType(int meta) {
        return "item/handheld";
    }
}
