package thebetweenlands.client.mixin;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.common.handlers.SaveDataHandler;
import thebetweenlands.common.items.ItemAmateMap;
import thebetweenlands.common.savedata.AmateMapItemSavedData;

@Mixin(ServerEntity.class)
public abstract class MixinServerEntity {

    @Final
    @Shadow private Entity entity;

    @Final
    @Shadow private ServerLevel level;
    @Shadow private int tickCount;

    @Shadow protected abstract void sendDirtyEntityData();

    @Inject(method = "sendChanges", at = @At("HEAD"), cancellable = true)
    void sendChanges(CallbackInfo ci) {
        if (!(this.entity instanceof ItemFrame)) {
            return;
        }

        ItemFrame itemframe = (ItemFrame)this.entity;
        ItemStack itemstack = itemframe.getItem();

        if (!(itemstack.getItem() instanceof ItemAmateMap && this.tickCount % 10 == 0)) {
            return;
        }

        Integer integer = ItemAmateMap.getAmateMapId(itemstack);
        AmateMapItemSavedData mapitemsaveddata = SaveDataHandler.getAmateMapData(this.level, ItemAmateMap.makeKey(integer));
        if (mapitemsaveddata != null) {
            for(ServerPlayer serverplayer : this.level.players()) {
                mapitemsaveddata.tickCarriedBy(serverplayer, itemstack);
                Packet<?> packet = mapitemsaveddata.getUpdatePacket(integer, serverplayer);
                if (packet != null) {
                    serverplayer.connection.send(packet);
                }
            }
        }

        this.sendDirtyEntityData();
        ci.cancel();
    }
}
