package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerGlow;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceLarge;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceLarge;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderSpiritTreeFaceLarge extends RenderWallFace<EntitySpiritTreeFaceLarge> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_large.png");

	private final ModelSpiritTreeFaceLarge model = new ModelSpiritTreeFaceLarge();

	protected final LayerGlow<EntitySpiritTreeFaceLarge> glow;

	public RenderSpiritTreeFaceLarge(RenderManager renderManager) {
		super(renderManager, new ModelSpiritTreeFaceLarge(), 0);
		this.addLayer(this.glow = new LayerGlow<>(this, new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_large_glow.png")));
	}

	@Override
	protected void preRenderCallback(EntitySpiritTreeFaceLarge entity, float partialTicks) {
		super.preRenderCallback(entity, partialTicks);

		this.glow.setAlpha(entity.getGlow(partialTicks));
		
		float scale = 0.8F + entity.getHalfMovementProgress(partialTicks) * entity.getHalfMovementProgress(partialTicks) * 0.2F;
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.translate(0, 0.15D, -0.7D);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceLarge entity) {
		return TEXTURE;
	}
}
