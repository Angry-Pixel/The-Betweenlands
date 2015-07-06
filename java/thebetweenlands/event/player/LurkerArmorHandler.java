package thebetweenlands.event.player;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEvent;
import thebetweenlands.items.LurkerSkinArmor;

/**
 * Created by Bart on 6-7-2015.
 */
public class LurkerArmorHandler {

    @SubscribeEvent
    public void onlivingUpdate(LivingEvent.LivingUpdateEvent event){
        if(event.entityLiving instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer)event.entityLiving;
            ItemStack[] armor = player.inventory.armorInventory;
            int armorPeaces = 0;
            for (ItemStack anArmor : armor) {
                if (anArmor != null && anArmor.getItem() instanceof LurkerSkinArmor) {
                    armorPeaces+= 1;
                }
            }
            float speedMod = 1;
            speedMod *= 1.0F + 0.2F * armorPeaces + 1;
            if(armorPeaces > 1 && player.isInWater()){
                player.setAIMoveSpeed(speedMod);

                if(!player.isPotionActive(Potion.waterBreathing) && armorPeaces >= 4)
                    player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 10));
            }

        }

    }
}
