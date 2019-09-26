package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelBarrishee;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityBarrishee;

@SideOnly(Side.CLIENT)
public class RenderBarrishee extends RenderLiving<EntityBarrishee> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/barrishee.png");
	public int fudge = 0;

	public RenderBarrishee(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelBarrishee(), 1F);
        addLayer(new LayerOverlay<EntityBarrishee>(this, new ResourceLocation("thebetweenlands:textures/entity/barrishee_face.png")).setGlow(true));
    }

	@Override
	protected void preRenderCallback(EntityBarrishee entity, float partialTickTime) {
		if(entity.isAmbushSpawn() || entity.isSlamming())
			if(entity.isScreaming()) {
				GlStateManager.translate(0F, -0.5F + entity.standingAngle * 0.5F - getTimerFudge(entity) * 0.00625F - 0.0625F, 0F);
				lightUpStuff(entity, partialTickTime);
			}
			else
				GlStateManager.translate(0F, -0.5F + entity.standingAngle * 0.5F, 0F);
		else {
			if(entity.isScreaming()) {
				GlStateManager.translate(0F, 0F - getTimerFudge(entity) * 0.00625F - 0.0625F , 0F);
				lightUpStuff(entity, partialTickTime);
			}
		}
	}

	public void lightUpStuff(EntityBarrishee entity, float partialTickTime) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY + 1.25D, entity.posZ, (fudge + partialTickTime) / 2F + 1F, 255f / 255.0f * 4.0F, 102f / 255.0f * 4.0F, 0f / 255.0f * 4.0F));
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY + 1.25D, entity.posZ, (getTimerFudge(entity) + partialTickTime) / 2F + 0.6F, 105f / 255.0f * 4.0F, 26f / 255.0f * 4.0F, 0f / 255.0f * 4.0F));
		}
	}

	public int getTimerFudge(EntityBarrishee entity) {
		if(entity.getScreamTimer() >= 20 && entity.getScreamTimer() <= 30)
			fudge = entity.getScreamTimer() - 20;
		if (entity.getScreamTimer() > 30 && entity.getScreamTimer() < 40)
			fudge = 10;
		if (entity.getScreamTimer() >= 40)
			 fudge = -entity.getScreamTimer() + 50;
		return fudge;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBarrishee entity) {
		return TEXTURE;
	}
}