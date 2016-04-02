package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Created by Bart on 02/04/2016.
 */
public class ItemBLAxe extends ItemAxe {
    private float damageVsEntity;

    public ItemBLAxe(ToolMaterial material) {
        super(material);
        damageVsEntity = ReflectionHelper.getPrivateValue(ItemTool.class, this, 2);
    }
}
