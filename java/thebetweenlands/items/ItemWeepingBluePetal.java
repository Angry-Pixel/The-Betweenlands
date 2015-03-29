package thebetweenlands.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import thebetweenlands.utils.PotionHelper;

public class ItemWeepingBluePetal
        extends ItemFood
{
    public ItemWeepingBluePetal() {
        super(4, 1.2F, false);
        this.setAlwaysEdible();
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        player.addPotionEffect(new PotionEffect(PotionHelper.decayRestore.getId(), 600, 0));
    }
}
