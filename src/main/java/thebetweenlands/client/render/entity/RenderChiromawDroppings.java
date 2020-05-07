package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelChiromawDroppings;
import thebetweenlands.common.entity.projectiles.EntityChiromawDroppings;

@SideOnly(Side.CLIENT)
public class RenderChiromawDroppings extends Render<EntityChiromawDroppings> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/chiromaw_droppings.png");
	private final ModelChiromawDroppings MODEL_DROPPINGS = new ModelChiromawDroppings();
	
	public RenderChiromawDroppings(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(EntityChiromawDroppings entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if(!entity.getHasExploded()) {
			FMLClientHandler.instance().getClient().getTextureManager().bindTexture(TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y + 0.25D, z);
			GlStateManager.scale(1, -1, -1);
			MODEL_DROPPINGS.render(entity, partialTicks);
			GlStateManager.popMatrix();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChiromawDroppings entity) {
		return TEXTURE;
	}
}