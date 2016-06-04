package thebetweenlands.client.render.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderBLShield extends TileEntitySpecialRenderer<TileEntityShield> {

    //private ResourceLocation OCTINE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/octine_shield.png");
    //private ResourceLocation VALONITE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/valonite_shield.png");
    //private ResourceLocation WEEDWOOD_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/weedwood_shield.png");
    //private ResourceLocation SYMORiTE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/symorite_shield.png");
    private ModelShield MODEL_SHIELD = new ModelShield();
    public Shieldtype type;

    public RenderBLShield(Shieldtype type) {
        this.type = type;
    }

    @Override
    public void renderTileEntityAt(TileEntityShield te, double x, double y, double z, float partialTicks, int destroyStage) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(type.resloc);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        this.MODEL_SHIELD.render();
        GlStateManager.popMatrix();
    }

    public enum Shieldtype{
        OCTINE(new ResourceLocation("thebetweenlands", "textures/items/octine_shield.png")),
        VALONITE(new ResourceLocation("thebetweenlands", "textures/items/valonite_shield.png")),
        WEEDWOOD(new ResourceLocation("thebetweenlands", "textures/items/weedwood_shield.png")),
        SYMORITE(new ResourceLocation("thebetweenlands", "textures/items/symorite_shield.png"));

        public ResourceLocation resloc;
        Shieldtype(ResourceLocation loc){
            this.resloc = loc;
        }

    }
}
