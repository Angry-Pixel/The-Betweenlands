package thebetweenlands.common.item.tools.bow;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.common.entity.projectiles.EntityBLArrow;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.TranslationHelper;

import java.util.List;

public class ItemBLArrow extends Item {
    private EnumArrowType type;

    public ItemBLArrow(EnumArrowType type) {
        super();
        this.type = type;
    }

    public EntityBLArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter) {
        EntityBLArrow entityArrow = new EntityBLArrow(worldIn, shooter);
        entityArrow.setType(type.toString());
        return entityArrow;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        ItemBLArrow item = (ItemBLArrow) stack.getItem();
        if (item == ItemRegistry.OCTINE_ARROW)
            list.add(TranslationHelper.translateToLocal("arrow.caution"));
        if (item == ItemRegistry.BASILISK_ARROW)
            list.add(TranslationHelper.translateToLocal("arrow.stunning"));
    }

    public EnumArrowType getType() {
        return this.type;
    }

}
