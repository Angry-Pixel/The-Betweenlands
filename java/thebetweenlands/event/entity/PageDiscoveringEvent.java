package thebetweenlands.event.entity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import thebetweenlands.entities.mobs.EntityDarkDruid;
import thebetweenlands.entities.mobs.IEntityBL;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.ManualManager;

/**
 * Created on 07/12/2015.
 */
public class PageDiscoveringEvent {
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event){
        if (event.entityLiving != null && (event.entityLiving instanceof IEntityBL || event.entityLiving instanceof EntityDarkDruid)){
            EntityLiving entity = (EntityLiving) event.entityLiving;
            EntityPlayer player = event.entityLiving.worldObj.getClosestPlayerToEntity(event.entityLiving, 20);
            Vec3 vec3 = player.getLook(1.0F).normalize();
            Vec3 vec31 = Vec3.createVectorHelper(entity.posX - player.posX, entity.boundingBox.minY + (double) (entity.height / 2.0F) - (player.posY + (double) player.getEyeHeight()), entity.posZ - player.posZ);
            double d0 = vec31.lengthVector();
            vec31 = vec31.normalize();
            double d1 = vec3.dotProduct(vec31);
            if (d1 > 1.0D - 0.025D / d0 && player.canEntityBeSeen(entity)) {
                if (entity instanceof IEntityBL )
                    ManualManager.playerDiscoverPage(player, ((IEntityBL) event.entityLiving).pageName(), BLItemRegistry.manualGuideBook);
                if (entity instanceof EntityDarkDruid)
                    ManualManager.playerDiscoverPage(player, "darkDruid", BLItemRegistry.manualGuideBook);
            }
        }
    }
}
