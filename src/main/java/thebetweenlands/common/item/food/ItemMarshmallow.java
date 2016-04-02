package thebetweenlands.common.item.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Created by Bart on 03/04/2016.
 */
public class ItemMarshmallow extends ItemBLFood {
    public ItemMarshmallow() {
        super(4, 10F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        if (player != null) {
            player.addPotionEffect(new PotionEffect(Potion.getPotionFromResourceLocation("speed"), 400, 1));
        }
    }

}
