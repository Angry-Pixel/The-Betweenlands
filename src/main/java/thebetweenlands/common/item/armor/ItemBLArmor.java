package thebetweenlands.common.item.armor;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.lib.ModInfo;

public class ItemBLArmor extends ItemArmor {
    private final String armorTexture1, armorTexture2;

    public ItemBLArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn, String armorName) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        this.armorTexture1 = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_1.png";
        this.armorTexture2 = ModInfo.ASSETS_PREFIX + "textures/armor/" + armorName + "_2.png";
    }


    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (renderIndex == 2) {
            return this.armorTexture2;
        } else {
            return this.armorTexture1;
        }
    }
}
