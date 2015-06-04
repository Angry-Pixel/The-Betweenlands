package thebetweenlands.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

/**
 * Created by Bart on 4-6-2015.
 */
public class PotionPetrify extends Potion {
    public PotionPetrify(int id) {
        super(id, false, 0x00B7FF);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase livingBase, int level) {
        livingBase.posX = MathHelper.floor_double(livingBase.posX) + 0.5;
        livingBase.posY = MathHelper.floor_double(livingBase.posY);
        livingBase.posZ = MathHelper.floor_double(livingBase.posZ) + 0.5;
        livingBase.rotationYaw = livingBase.prevRotationYaw = 0F;
        livingBase.renderYawOffset = livingBase.prevRenderYawOffset = 0F;

        livingBase.motionX = livingBase.motionY = livingBase.motionZ = 0.0;

        int x = MathHelper.floor_double(livingBase.posX);
        int y = MathHelper.floor_double(livingBase.posY) - 1;
        int z = MathHelper.floor_double(livingBase.posZ);

        livingBase.moveStrafing = 0.0F;
        livingBase.moveForward = 0.0F;
        if (livingBase.worldObj.getBlock(x, y, z) == null)
            livingBase.posY -= 1;
    }
}
