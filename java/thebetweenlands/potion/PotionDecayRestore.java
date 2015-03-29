package thebetweenlands.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import thebetweenlands.manager.DecayManager;

//TODO: add icon and name!
public class PotionDecayRestore
        extends Potion
{
    public PotionDecayRestore(int id) {
        super(id, false, 0x00B7FF);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        int k = 50 >> amplifier;
        return k <= 0 || duration % k == 0;
    }

    @Override
    public void performEffect(EntityLivingBase livingBase, int level) {
        if( livingBase instanceof EntityPlayer ) {
            EntityPlayer player = (EntityPlayer) livingBase;
            DecayManager.setDecayLevel(DecayManager.getDecayLevel(player) + 1, player);
        }
    }
}
