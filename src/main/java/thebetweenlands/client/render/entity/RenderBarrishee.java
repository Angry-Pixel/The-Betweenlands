package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelBarrishee;
import thebetweenlands.common.entity.mobs.EntityBarrishee;

@SideOnly(Side.CLIENT)
public class RenderBarrishee extends RenderLiving<EntityBarrishee> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/barrishee.png");

	public RenderBarrishee(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBarrishee(), 2.5F);
        addLayer(new LayerOverlay<EntityBarrishee>(this, new ResourceLocation("thebetweenlands:textures/entity/barrishee_face.png")).setGlow(true));
    }

	@Override
	protected void preRenderCallback(EntityBarrishee entity, float partialTickTime) {
		GlStateManager.translate(0F, -0.5F + entity.standingAngle * 0.5F, 0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBarrishee entity) {
		return TEXTURE;
	}
}