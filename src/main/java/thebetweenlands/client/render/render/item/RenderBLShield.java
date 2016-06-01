package thebetweenlands.client.render.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderBLShield extends TileEntitySpecialRenderer<TileEntityShield> {
    private ResourceLocation SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/octine_shield.png");
    private ModelShield MODEL_SHIELD = new ModelShield();

    @Override
    public void renderTileEntityAt(TileEntityShield te, double x, double y, double z, float partialTicks, int destroyStage) {
        /*ItemStack itemStack = Minecraft.getMinecraft().thePlayer.getHeldItem(EnumHand.MAIN_HAND);
        if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemBLShield)
            renderShieldFromItem(itemStack.getItem());
        itemStack = Minecraft.getMinecraft().thePlayer.getHeldItem(EnumHand.OFF_HAND);
        if (itemStack != null && itemStack.getItem() != null && itemStack.getItem() instanceof ItemBLShield)
            renderShieldFromItem(itemStack.getItem());*/
        Minecraft.getMinecraft().getTextureManager().bindTexture(SHIELD_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        this.MODEL_SHIELD.render();
        GlStateManager.popMatrix();
    }


    private void renderShieldFromItem(Item item) {
        if (item == ItemRegistry.OCTINE_SHIELD) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(SHIELD_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            this.MODEL_SHIELD.render();
            GlStateManager.popMatrix();
        } else if (item == ItemRegistry.VALONITE_SHIELD) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(SHIELD_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            this.MODEL_SHIELD.render();
            GlStateManager.popMatrix();
        }

    }
}
