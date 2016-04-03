package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSword;
import thebetweenlands.common.item.ICustomItemRenderType;

public class ItemBLSword extends ItemSword implements ICustomItemRenderType {
    public ItemBLSword(ToolMaterial material) {
        super(material);
    }

    @Override
    public String getCustomRenderType(int meta) {
        return "item/handheld";
    }
}
