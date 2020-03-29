package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerAttachedItems;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelChiromaw;
import thebetweenlands.common.entity.mobs.EntityChiromawGreeblingRider;

@SideOnly(Side.CLIENT)
public class RenderChiromawGreeblingRider extends RenderLiving<EntityChiromawGreeblingRider> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw.png");

	public RenderChiromawGreeblingRider(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelChiromaw(), 0.5F);
		this.addLayer(new LayerOverlay<>(this, new ResourceLocation("thebetweenlands:textures/entity/chiromaw_glow.png")).setGlow(true));
		ModelChiromaw model = (ModelChiromaw) this.getMainModel();
		
		this.addLayer(new LayerAttachedItems<EntityChiromawGreeblingRider>(model)
        		.attach(model.body, model.arm_left_lower, chiromawRider -> chiromawRider.getHeldItemOffhand(), EnumHandSide.LEFT, 0.75F, attachment -> {
        			attachment.rotationPointY = 18.5F;
        			attachment.rotationPointZ = -2F;
        		})
        		.attach(model.body, model.arm_right_lower, chiromawRider -> chiromawRider.getHeldItemMainhand(), EnumHandSide.RIGHT, 0.75F, attachment -> {
        			attachment.rotationPointY =  18.5F;
        			attachment.rotationPointZ = -2F;
        		})
        		);
	}

	@Override
	protected void preRenderCallback(EntityChiromawGreeblingRider chiromaw, float partialTickTime) {
		if (!chiromaw.getIsHanging()) {
			float flap = MathHelper.sin((chiromaw.ticksExisted + partialTickTime) * 0.5F) * 0.6F;
			GlStateManager.translate(0.0F, 0F - flap * 0.5F, 0.0F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChiromawGreeblingRider entity) {
		return TEXTURE;
	}
}
