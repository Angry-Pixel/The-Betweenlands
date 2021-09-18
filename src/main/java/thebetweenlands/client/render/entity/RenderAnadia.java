package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelAnadia;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.entity.mobs.EntityAnadia.EnumAnadiaColor;

@SideOnly(Side.CLIENT)
public class RenderAnadia extends RenderLiving<EntityAnadia> {
	private static final ResourceLocation[] TEXTURE_1 = new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_smoked.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_rotten.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_base.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_silver.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_purple.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_1_green.png")
	};

	private static final ResourceLocation[] TEXTURE_2 = new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_smoked.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_rotten.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_base.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_silver.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_purple.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_2_green.png")
	};

	private static final ResourceLocation[] TEXTURE_3 = new ResourceLocation[] {
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_smoked.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_rotten.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_base.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_silver.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_purple.png"),
			new ResourceLocation("thebetweenlands:textures/entity/anadia_3_green.png")
	};

	public final static ModelAnadia ANADIA_MODEL = new ModelAnadia();

	public RenderAnadia(RenderManager manager) {
		super(manager, ANADIA_MODEL, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAnadia entity) {
		return TEXTURE_1[2];
	}

	@Override
	protected void applyRotations(EntityAnadia entity, float ageInTicks, float rotationYaw, float partialTicks) {
		float smoothedPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
		float smoothedYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;

		GlStateManager.rotate(180 - smoothedYaw, 0F, 1F, 0F);
		
		float scale = entity.getFishSize();
		GlStateManager.translate(0, 0.4f * scale, 0);
		GlStateManager.rotate(-smoothedPitch, 1F, 0F, 0F);
		GlStateManager.translate(0, -0.4f * scale, 0);
		
		if(entity.deathTime > 0) {
            float f = ((float)entity.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = MathHelper.sqrt(f);

            if(f > 1.0F) {
                f = 1.0F;
            }

            GlStateManager.rotate(f * this.getDeathMaxRotation(entity), 0.0F, 0.0F, 1.0F);
        }
	}
	
	@Override
	protected void preRenderCallback(EntityAnadia entity, float partialTickTime) {
		float scale = entity.getFishSize();
		GlStateManager.scale(scale, scale, scale);
	}

	@Override
	protected void renderModel(EntityAnadia entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		boolean isVisible = this.isVisible(entity);
		boolean isTransparent = !isVisible && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);

		if(isVisible || isTransparent) {
			if(!this.bindEntityTexture(entity)) {
				return;
			}

			if(isTransparent) {
				GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}

			GlStateManager.enableCull();
			
			switch (entity.getHeadType()) {
			default:
			case HEAD_1:
				bindTexture(getTextureForColor(TEXTURE_1, entity.getFishColour()));
				ANADIA_MODEL.renderHead(entity.getHeadType(), 0.0625F);
				break;
			case HEAD_2:
				bindTexture(getTextureForColor(TEXTURE_2, entity.getFishColour()));
				ANADIA_MODEL.renderHead(entity.getHeadType(), 0.0625F);
				break;
			case HEAD_3:
				bindTexture(getTextureForColor(TEXTURE_3, entity.getFishColour()));
				ANADIA_MODEL.renderHead(entity.getHeadType(), 0.0625F);
				break;
			}

			switch (entity.getBodyType()) {
			default:
			case BODY_1:
				bindTexture(getTextureForColor(TEXTURE_1, entity.getFishColour()));
				ANADIA_MODEL.renderBody(entity.getBodyType(), 0.0625F);
				break;
			case BODY_2:
				bindTexture(getTextureForColor(TEXTURE_2, entity.getFishColour()));
				ANADIA_MODEL.renderBody(entity.getBodyType(), 0.0625F);
				break;
			case BODY_3:
				bindTexture(getTextureForColor(TEXTURE_3, entity.getFishColour()));
				ANADIA_MODEL.renderBody(entity.getBodyType(), 0.0625F);
				break;
			}

			switch (entity.getTailType()) {
			default:
			case TAIL_1:
				bindTexture(getTextureForColor(TEXTURE_1, entity.getFishColour()));
				ANADIA_MODEL.renderTail(entity.getTailType(), 0.0625F);
				break;
			case TAIL_2:
				bindTexture(getTextureForColor(TEXTURE_2, entity.getFishColour()));
				ANADIA_MODEL.renderTail(entity.getTailType(), 0.0625F);
				break;
			case TAIL_3:
				bindTexture(getTextureForColor(TEXTURE_3, entity.getFishColour()));
				ANADIA_MODEL.renderTail(entity.getTailType(), 0.0625F);
				break;
			} 
			
			GlStateManager.disableCull();

			if(isTransparent) {
				GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}
		}
	}
	
	private static ResourceLocation getTextureForColor(ResourceLocation[] textures, EnumAnadiaColor color) {
		switch(color) {
		default:
			return textures[2];
		case SMOKED:
			return textures[0];
		case ROTTEN:
			return textures[1];
		case BASE:
			return textures[2];
		case SILVER:
			return textures[3];
		case PURPLE:
			return textures[4];
		case GREEN:
			return textures[5];
		}
	}
}
