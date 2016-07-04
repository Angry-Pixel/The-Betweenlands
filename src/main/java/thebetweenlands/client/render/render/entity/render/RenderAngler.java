package thebetweenlands.client.render.render.entity.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelAngler;
import thebetweenlands.client.render.render.entity.layer.LayerGlow;
import thebetweenlands.common.entity.mobs.EntityAngler;

@SideOnly(Side.CLIENT)
public class RenderAngler extends RenderLiving<EntityAngler> {
	public final static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/angler.png");

	public RenderAngler(RenderManager manager) {
		super(manager, new ModelAngler(), 0.5F);
		this.addLayer(new LayerGlow(this, new ResourceLocation("thebetweenlands:textures/entity/angler_glow.png")));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAngler entity) {
		return TEXTURE;
	}

	@Override
	protected void preRenderCallback(EntityAngler entity, float f) {
		GL11.glTranslatef(0F, 0.5F, 0F);
		if (entity.isGrounded() && !entity.isLeaping()) {
			GL11.glRotatef(90F, 0F, 0F, 1F);
			GL11.glTranslatef(-0.7F, 0.7F, 0F);
		}
	}

	@Override
	public void doRender(EntityAngler entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		/*TODO add shader helper
        if (ShaderHelper.INSTANCE.canUseShaders()) {
            double xOff = Math.sin(Math.toRadians(-entity.renderYawOffset)) * 0.3f;
            double zOff = Math.cos(Math.toRadians(-entity.renderYawOffset)) * 0.3f;
            ShaderHelper.INSTANCE.addDynLight(new LightSource(entity.posX + xOff, entity.posY + 0.95f, entity.posZ + zOff,
                    1.6f,
                    10.0f / 255.0f * 13.0F,
                    30.0f / 255.0f * 13.0F,
                    20.0f / 255.0f * 13.0F));
        }*/
	}
}
