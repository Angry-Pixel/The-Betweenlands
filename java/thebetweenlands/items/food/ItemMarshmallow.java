package thebetweenlands.items.food;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemMarshmallow
        extends ItemFood
{
    public ItemMarshmallow() {
        super(4, 10F, false);
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);

        if( player != null ) {
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(), 400, 1));
        }
    }
}
