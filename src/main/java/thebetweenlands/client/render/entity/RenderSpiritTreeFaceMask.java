package thebetweenlands.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.armor.ModelSpiritTreeFaceMaskLarge;
import thebetweenlands.client.render.model.armor.ModelSpiritTreeFaceMaskSmall;
import thebetweenlands.common.entity.EntitySpiritTreeFaceMask;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderSpiritTreeFaceMask extends Render<EntitySpiritTreeFaceMask> {
	private static final ResourceLocation TEXTURE_LARGE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_large.png");
	private static final ResourceLocation TEXTURE_SMALL = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small.png");

	private static final ModelSpiritTreeFaceMaskLarge MODEL_LARGE = new ModelSpiritTreeFaceMaskLarge(false);
	private static final ModelSpiritTreeFaceMaskSmall MODEL_SMALL = new ModelSpiritTreeFaceMaskSmall(false);

	public RenderSpiritTreeFaceMask(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntitySpiritTreeFaceMask entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180.0F - entityYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1, -1, 1);

		this.bindEntityTexture(entity);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		ModelBase model = entity.getType() == EntitySpiritTreeFaceMask.Type.LARGE ? MODEL_LARGE : MODEL_SMALL;

		if(entity.getType() == EntitySpiritTreeFaceMask.Type.LARGE) {
			GlStateManager.translate(0, -0.4D, 0);
		} else {
			GlStateManager.translate(0, -1.1D, -0.5D);
		}

		model.render(entity, 0, 0, 0, 0, 0, 0.0625F);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceMask entity) {
		return entity.getType() == EntitySpiritTreeFaceMask.Type.LARGE ? TEXTURE_LARGE : TEXTURE_SMALL;
	}
}