package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelChiromawEgg;
import thebetweenlands.client.render.model.entity.ModelChiromawHatchling;
import thebetweenlands.common.entity.mobs.EntityChiromawHatchling;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderChiromawHatchling extends RenderLiving<EntityChiromawHatchling> {
	private static final ResourceLocation TEXTURE_HATCHLING = new ResourceLocation(ModInfo.ID, "textures/entity/chiromaw_hatchling.png");
	private static final ResourceLocation TEXTURE_EGG = new ResourceLocation(ModInfo.ID, "textures/entity/chiromaw_egg.png");
	private static final ModelChiromawHatchling MODEL_HATCHLING = new ModelChiromawHatchling();
	private static final ModelChiromawEgg MODEL_EGG = new ModelChiromawEgg();
	public RenderChiromawHatchling(RenderManager renderManager) {
		super(renderManager, MODEL_HATCHLING, 0.2F);
	}

	@Override
    public void doRender(EntityChiromawHatchling entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        if (!this.renderOutlines) {
            this.renderLeash(entity, x, y, z, entityYaw, partialTicks);
        }
        
    	GlStateManager.pushMatrix();
    	GlStateManager.translate(x, y + 1.51F, z);
    	GlStateManager.scale(1F, -1F, -1F);
		if (entity.getHasHatched()) {
			MODEL_HATCHLING.renderEgg(entity, partialTicks, 0.0625F);
			MODEL_HATCHLING.renderBaby(entity, partialTicks, 0.0625F);
		}
		else
			MODEL_EGG.renderEgg(entity, partialTicks, 0.0625F);
        GlStateManager.popMatrix();
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityChiromawHatchling entity) {
		return entity.getHasHatched() ? TEXTURE_HATCHLING : TEXTURE_EGG;
	}
}
