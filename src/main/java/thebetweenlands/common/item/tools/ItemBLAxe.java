package thebetweenlands.common.item.tools;

import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.relauncher.ReflectionHelper;


public class ItemBLAxe extends ItemAxe {

    public ItemBLAxe(ToolMaterial material) {
        super(material);
        this.damageVsEntity = ReflectionHelper.getPrivateValue(ItemTool.class, this, 2);
    }
}
