package thebetweenlands.client.render.entity;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneEffectModifier.RenderProperties;
import thebetweenlands.api.rune.impl.RuneEffectModifier.Subject;
import thebetweenlands.client.render.model.entity.ModelRunicBeetleFlying;
import thebetweenlands.common.entity.EntityRunicBeetleProjectile;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderRunicBeetleProjectile extends Render<EntityRunicBeetleProjectile> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/runic_beetle.png");

	private static final ModelRunicBeetleFlying MODEL = new ModelRunicBeetleFlying();

	public RenderRunicBeetleProjectile(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityRunicBeetleProjectile entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.rotate(180.0f + entityYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(-1, -1, 1);
		GlStateManager.translate(0, -1.5f, 0);

		GlStateManager.color(1, 1, 1, 1);
		
		this.bindEntityTexture(entity);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		
		GlStateManager.disableCull();
		
		GlStateManager.translate(0, 0.125f, 0);
		
		MODEL.render(entity, 0, 0, 0, 0, 0, 0.0625F);

		Pair<RuneEffectModifier, Subject> visualModifier = entity.getVisualModifier();
		if(visualModifier != null) {
			RuneEffectModifier modifier = visualModifier.getLeft();
			Subject subject = visualModifier.getRight();
			
			GlStateManager.pushMatrix();

			GlStateManager.translate(0, 0.75f, 0);			
			GlStateManager.scale(1, -1, 1);
			
			RenderProperties properties = new RenderProperties();
			properties.sizeX = properties.sizeY = properties.sizeZ = 0.5f;
			properties.alpha = 0.3f;
			
			for(int i = 0; i < Math.min(3, modifier.getRendererCount(subject)); i++) {
				modifier.render(subject, i, properties, entity.getRenderState(), partialTicks);
				
				GlStateManager.translate(0, 0.5f, 0);
			}
			
			GlStateManager.popMatrix();
		}
		
		GlStateManager.enableCull();
		
		GlStateManager.disableBlend();
		
		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRunicBeetleProjectile entity) {
		return TEXTURE;
	}
}