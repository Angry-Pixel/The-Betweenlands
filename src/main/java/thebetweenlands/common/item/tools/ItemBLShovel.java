package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemSpade;
import thebetweenlands.common.item.ICustomItemRenderType;

/**
 * Created by Bart on 02/04/2016.
 */
public class ItemBLShovel extends ItemSpade implements ICustomItemRenderType {
    public ItemBLShovel(ToolMaterial material) {
        super(material);
    }

    @Override
    public String getCustomRenderType(int meta) {
        return "item/handheld";
    }
}
