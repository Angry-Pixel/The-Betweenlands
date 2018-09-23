package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.entity.layer.LayerGlow;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall1;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall2;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmall;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderSpiritTreeFaceSmall extends RenderWallFace<EntitySpiritTreeFaceSmall> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small.png");

	private final ModelSpiritTreeFaceSmall1 model1 = new ModelSpiritTreeFaceSmall1();
	private final ModelSpiritTreeFaceSmall2 model2 = new ModelSpiritTreeFaceSmall2();

	protected final LayerGlow<EntitySpiritTreeFaceSmall> glow;

	public RenderSpiritTreeFaceSmall(RenderManager renderManager) {
		super(renderManager, new ModelSpiritTreeFaceSmall1(), 0);
		this.addLayer(this.glow = new LayerGlow<>(this, new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small_glow.png")));
	}

	@Override
	protected void preRenderCallback(EntitySpiritTreeFaceSmall entity, float partialTicks) {
		super.preRenderCallback(entity, partialTicks);

		this.glow.setAlpha(entity.getGlow(partialTicks));

		float scale = 0.8F + entity.getHalfMovementProgress(partialTicks) * entity.getHalfMovementProgress(partialTicks) * 0.2F;
		GlStateManager.scale(scale, scale, scale);

		int variant = entity.getVariant();
		if(variant == 0) {
			this.mainModel = this.model1;
			GlStateManager.translate(0, 1, -0.25D);
		} else {
			this.mainModel = this.model2;
			GlStateManager.translate(0, 0, -0.74D);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceSmall entity) {
		return TEXTURE;
	}
}
