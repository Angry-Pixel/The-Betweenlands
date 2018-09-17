package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall1;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmall;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderSpiritTreeFaceSmall extends RenderWallFace<EntitySpiritTreeFaceSmall> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small.png");

	private final ModelSpiritTreeFaceSmall1 model = new ModelSpiritTreeFaceSmall1();

	public RenderSpiritTreeFaceSmall(RenderManager renderManager) {
		super(renderManager, new ModelSpiritTreeFaceSmall1(), 0);
	}

	@Override
	protected void preRenderCallback(EntitySpiritTreeFaceSmall entity, float partialTicks) {
		super.preRenderCallback(entity, partialTicks);

		float scale = 0.8F + entity.getHalfMovementProgress(partialTicks) * entity.getHalfMovementProgress(partialTicks) * 0.2F;
		GlStateManager.scale(scale, scale, scale);

		GlStateManager.translate(0, 1, -0.25D);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceSmall entity) {
		return TEXTURE;
	}
}
