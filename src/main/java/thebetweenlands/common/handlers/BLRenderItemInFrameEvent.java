package thebetweenlands.common.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.function.Consumer;

public class BLRenderItemInFrameEvent extends RenderItemInFrameEvent implements IModBusEvent {
    public BLRenderItemInFrameEvent(ItemFrame itemFrame, ItemFrameRenderer renderItemFrame, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        super(itemFrame, renderItemFrame, poseStack, multiBufferSource, packedLight);
    }
}
