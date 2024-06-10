package thebetweenlands.client.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.handlers.BLRenderItemInFrameEvent;
import thebetweenlands.common.handlers.SaveDataHandler;
import thebetweenlands.common.items.ItemAmateMap;
import thebetweenlands.common.savedata.AmateMapItemSavedData;

@Mod.EventBusSubscriber(modid = "thebetweenlands", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventHandler {

    @SubscribeEvent
    public static void renderItemInFrameEvent(BLRenderItemInFrameEvent event) {
        ItemStack item = event.getItemStack();
        PoseStack pose = event.getPoseStack();
        ItemFrame ItemFrameEnt = event.getItemFrameEntity();
        if (item.getItem() instanceof ItemAmateMap) {
            pose.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            float f = 0.0078125F;
            pose.scale(0.0078125F, 0.0078125F, 0.0078125F);
            pose.translate(-64.0D, -64.0D, 0.0D);
            Integer integer = ItemAmateMap.getAmateMapId(item);
            pose.translate(0.0D, 0.0D, -1.0D);
            AmateMapItemSavedData data = SaveDataHandler.getAmateMapData(ItemFrameEnt.level, ItemAmateMap.makeKey(integer));
            if (data != null) {
                TheBetweenlands.amateMapRenderer.render(pose, event.getMultiBufferSource(), integer, data, true, ItemFrameEnt.getType() == EntityType.GLOW_ITEM_FRAME ? 15728850 : event.getPackedLight());
            }
            event.setResult(Event.Result.ALLOW);
            event.setCanceled(true);
        }
    }
}
