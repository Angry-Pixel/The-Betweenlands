package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemGertsDonut extends ItemBLFood {
    public ItemGertsDonut() {
        super(6, 13.2F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        player.heal(8.0F);
    }
}
