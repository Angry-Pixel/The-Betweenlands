package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.common.item.ICustomItemRenderType;

/**
 * Created by Bart on 02/04/2016.
 */
public class ItemBLAxe extends ItemAxe implements ICustomItemRenderType {
    private float damageVsEntity;

    public ItemBLAxe(ToolMaterial material) {
        super(material);
        damageVsEntity = ReflectionHelper.getPrivateValue(ItemTool.class, this, 2);
    }

    @Override
    public String getCustomRenderType(int meta) {
        return "item/handheld";
    }
}
