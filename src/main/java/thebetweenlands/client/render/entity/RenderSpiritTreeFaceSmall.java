package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSpiritTreeFaceSmall1;
import thebetweenlands.common.entity.mobs.EntitySpiritTreeFaceSmall;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderSpiritTreeFaceSmall extends Render<EntitySpiritTreeFaceSmall> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/spirit_tree_face_small.png");

	private final ModelSpiritTreeFaceSmall1 model = new ModelSpiritTreeFaceSmall1();

	public RenderSpiritTreeFaceSmall(RenderManager renderManager) {
		super(renderManager);
		this.shadowSize = 0.0F;
	}

	@Override
	public void doRender(EntitySpiritTreeFaceSmall entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y, (float)z);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);

		GlStateManager.color(1, 1, 1, 1);

		this.bindEntityTexture(entity);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		GlStateManager.translate(0, -entity.getEyeHeight(), 0);

		switch(entity.getFacing()) {
		default:
		case NORTH:
			break;
		case EAST:
			GlStateManager.rotate(90, 0, 1, 0);
			break;
		case SOUTH:
			GlStateManager.rotate(180, 0, 1, 0);
			break;
		case WEST:
			GlStateManager.rotate(270, 0, 1, 0);
			break;
		case UP:
			GlStateManager.rotate(-90, 1, 0, 0);
			break;
		case DOWN:
			GlStateManager.rotate(90, 1, 0, 0);
			break;
		}

		if(entity.getFacing().getAxis().isVertical()) {
			switch(entity.getFacingUp()) {
			default:
			case NORTH:
				break;
			case EAST:
				GlStateManager.rotate(90, 0, 0, 1);
				break;
			case SOUTH:
				GlStateManager.rotate(180, 0, 0, 1);
				break;
			case WEST:
				GlStateManager.rotate(270, 0, 0, 1);
				break;
			}
		}

		GlStateManager.translate(0, 0, -0.25D);

		this.model.render(entity, partialTicks, 0, 0, 0, 0, 0.0625F);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySpiritTreeFaceSmall entity) {
		return TEXTURE;
	}
}
