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

		float wispStrengthModifier = entity.getWispStrengthModifier();
		if(wispStrengthModifier < 1.0F) {
			float colors = Math.max((wispStrengthModifier - 0.5F) / 0.5F, 0.0F) * 0.4F + 0.6F;
			this.glow.setColor((float)Math.pow(colors, 1.2F), colors, (float)Math.pow(colors, 1.2F));
		} else if(wispStrengthModifier > 1.0F) {
			float redness = Math.min((wispStrengthModifier - 1.0F) / 2.0F, 1.0F) * 0.8F + 0.2F;
			this.glow.setColor(1, 1 - redness, 1 - redness);
		} else {
			this.glow.setColor(1, 1, 1);
		}

		float scale = 0.8F + entity.getHalfMovementProgress(partialTicks) * entity.getHalfMovementProgress(partialTicks) * 0.2F;
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.translate(0, 0.15D, -0.7D);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceLarge entity) {
		return TEXTURE;
	}
}
