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
	public int fudge = 0;

	public RenderBarrishee(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBarrishee(), 2.5F);
        addLayer(new LayerOverlay<EntityBarrishee>(this, new ResourceLocation("thebetweenlands:textures/entity/barrishee_face.png")).setGlow(true));
    }

	@Override
	protected void preRenderCallback(EntityBarrishee entity, float partialTickTime) {
		if(entity.isAmbushSpawn())
			if(entity.isScreaming())
				GlStateManager.translate(0F, -0.5F + entity.standingAngle * 0.5F - 0.0625F, 0F);
			else
				GlStateManager.translate(0F, -0.5F + entity.standingAngle * 0.5F, 0F);
		else {
			if(entity.isScreaming()) {
				if(entity.getScreamTimer() >= 20 && entity.getScreamTimer() <= 30)
					fudge = entity.getScreamTimer() - 20;
				if (entity.getScreamTimer() > 30 && entity.getScreamTimer() < 40)
					fudge = 10;
				if (entity.getScreamTimer() >= 40)
					 fudge = -entity.getScreamTimer() + 50;
				GlStateManager.translate(0F, 0F - fudge * 0.00625F, 0F);
			}
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBarrishee entity) {
		return TEXTURE;
	}
}