package thebetweenlands.client.render.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.shields.*;

public class RenderBLShield extends TileEntitySpecialRenderer<TileEntityShield> {

    //private ResourceLocation OCTINE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/octine_shield.png");
    //private ResourceLocation VALONITE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/valonite_shield.png");
    //private ResourceLocation WEEDWOOD_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/weedwood_shield.png");
    //private ResourceLocation SYMORiTE_SHIELD_TEXTURE = new ResourceLocation("thebetweenlands", "textures/items/symorite_shield.png");
    public Shieldtype type;

    public RenderBLShield(Shieldtype type) {
        this.type = type;
    }

    @Override
    public void renderTileEntityAt(TileEntityShield te, double x, double y, double z, float partialTicks, int destroyStage) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(type.resloc);
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        if (!(type.shieldModel instanceof ModelShield))
            type.shieldModel.render(null, 0, 0, 0, 0, 0, 0.0625F);
        else
            ((ModelShield) type.shieldModel).render();
        GlStateManager.popMatrix();
    }

    public enum Shieldtype {
        OCTINE(new ResourceLocation("thebetweenlands", "textures/items/shields/octine_shield.png"), new ModelOctineShield()),
        VALONITE(new ResourceLocation("thebetweenlands", "textures/items/shields/valonite_shield.png"), new ModelValoniteShield()),
        WEEDWOOD(new ResourceLocation("thebetweenlands", "textures/items/shields/weedwood_shield.png"), new ModelWeedwoodShield()),
        BONE(new ResourceLocation("thebetweenlands", "textures/items/shields/bone_shield.png"), new ModelBoneShield()),
        SYMORITE(new ResourceLocation("thebetweenlands", "textures/items/shields/symorite_shield.png"), new ModelSymoriteShield());

        public ResourceLocation resloc;
        public ModelBase shieldModel;

        Shieldtype(ResourceLocation loc, ModelBase shieldModel) {
            this.resloc = loc;
            this.shieldModel = shieldModel;
        }

    }
}
