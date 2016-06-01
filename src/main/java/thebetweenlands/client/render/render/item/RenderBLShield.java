package thebetweenlands.client.render.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderBLShield extends TileEntitySpecialRenderer<TileEntityShield> {
    public String type;
    private ResourceLocation OCTINE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/octine_shield.png");
    private ResourceLocation VALONITE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/valonite_shield.png");
    private ResourceLocation WEEDWOOD_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/weedwood_shield.png");
    private ResourceLocation SYMORiTE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/symorite_shield.png");
    private ModelShield MODEL_SHIELD = new ModelShield();

    public RenderBLShield(String type) {
        this.type = type;
    }

    @Override
    public void renderTileEntityAt(TileEntityShield te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderShieldFromItem(type);
    }


    private void renderShieldFromItem(String type) {
        switch (type) {
            case "octine":
                Minecraft.getMinecraft().getTextureManager().bindTexture(OCTINE_SHIELD_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.0F, -1.0F, -1.0F);
                this.MODEL_SHIELD.render();
                GlStateManager.popMatrix();
                break;
            case "valonite":
                Minecraft.getMinecraft().getTextureManager().bindTexture(VALONITE_SHIELD_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.0F, -1.0F, -1.0F);
                this.MODEL_SHIELD.render();
                GlStateManager.popMatrix();
                break;
            case "weedwood":
                Minecraft.getMinecraft().getTextureManager().bindTexture(WEEDWOOD_SHIELD_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.0F, -1.0F, -1.0F);
                this.MODEL_SHIELD.render();
                GlStateManager.popMatrix();
                break;
            case "symorite":
                Minecraft.getMinecraft().getTextureManager().bindTexture(SYMORiTE_SHIELD_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.0F, -1.0F, -1.0F);
                this.MODEL_SHIELD.render();
                GlStateManager.popMatrix();
                break;
        }

    }
}
