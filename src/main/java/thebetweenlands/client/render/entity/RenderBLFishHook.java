package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelBLFishHook;
import thebetweenlands.common.entity.projectiles.EntityBLFishHook;
import thebetweenlands.common.item.tools.ItemBLFishingRod;

@SideOnly(Side.CLIENT)
public class RenderBLFishHook extends Render<EntityBLFishHook> {
	private static final ResourceLocation HOOK_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/fish_hook.png");
	public final static ModelBLFishHook HOOK_MODEL = new ModelBLFishHook();

	public RenderBLFishHook(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	public void doRender(EntityBLFishHook entity, double x, double y, double z, float entityYaw, float partialTicks) {
		EntityPlayer entityplayer = entity.getAngler();
		if (entityplayer != null && !this.renderOutlines) {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x, (float) y + 1.1F, (float) z);
			GlStateManager.scale(-0.8F, -0.8F, 0.8F);
			bindTexture(HOOK_TEXTURE);
			//causes janky rotationrendering atm
			//GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			//GlStateManager.rotate((float) (this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

			if (this.renderOutlines) {
				GlStateManager.enableColorMaterial();
				GlStateManager.enableOutlineMode(this.getTeamColor(entity));
			}
			
			HOOK_MODEL.render();

			if (this.renderOutlines) {
				GlStateManager.disableOutlineMode();
				GlStateManager.disableColorMaterial();
			}
			GlStateManager.popMatrix();
			
	///////	
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			int k = entityplayer.getPrimaryHand() == EnumHandSide.RIGHT ? 1 : -1;
			ItemStack itemstack = entityplayer.getHeldItemMainhand();

			if (!(itemstack.getItem() instanceof ItemBLFishingRod)) {
				k = -k;
			}

			float f7 = entityplayer.getSwingProgress(partialTicks);
			float f8 = MathHelper.sin(MathHelper.sqrt(f7) * (float) Math.PI);
			float f9 = (entityplayer.prevRenderYawOffset + (entityplayer.renderYawOffset - entityplayer.prevRenderYawOffset) * partialTicks) * 0.017453292F;
			double d0 = (double) MathHelper.sin(f9);
			double d1 = (double) MathHelper.cos(f9);
			double d2 = (double) k * 0.35D;
			double d3 = 0.8D;
			double d4;
			double d5;
			double d6;
			double d7;

			if ((this.renderManager.options == null || this.renderManager.options.thirdPersonView <= 0)
					&& entityplayer == Minecraft.getMinecraft().player) {
				float f10 = this.renderManager.options.fovSetting;
				f10 = f10 / 100.0F;
				Vec3d vec3d = new Vec3d((double) k * -0.36D * (double) f10, -0.045D * (double) f10, 0.4D);
				vec3d = vec3d.rotatePitch(-(entityplayer.prevRotationPitch + (entityplayer.rotationPitch - entityplayer.prevRotationPitch) * partialTicks) * 0.017453292F);
				vec3d = vec3d.rotateYaw(-(entityplayer.prevRotationYaw + (entityplayer.rotationYaw - entityplayer.prevRotationYaw) * partialTicks) * 0.017453292F);
				vec3d = vec3d.rotateYaw(f8 * 0.5F);
				vec3d = vec3d.rotatePitch(-f8 * 0.7F);
				d4 = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * (double) partialTicks + vec3d.x;
				d5 = entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * (double) partialTicks + vec3d.y;
				d6 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * (double) partialTicks + vec3d.z;
				d7 = (double) entityplayer.getEyeHeight();
			} else {
				d4 = entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * (double) partialTicks - d1 * d2 - d0 * 0.95D;
				d5 = entityplayer.prevPosY + (double) entityplayer.getEyeHeight() + (entityplayer.posY - entityplayer.prevPosY) * (double) partialTicks - 0.625D;
				d6 = entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * (double) partialTicks - d0 * d2 + d1 * 0.95D;
				d7 = entityplayer.isSneaking() ? -0.1875D : 0.0D;
			}

			double d13 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
			double d8 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + 0.235D;
			double d9 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
			double d10 = (double) ((float) (d4 - d13));
			double d11 = (double) ((float) (d5 - d8)) + d7;
			double d12 = (double) ((float) (d6 - d9));

			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();

			bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

			for (int count = 0; count <= 48; ++count) {
				float fx = 0.737F;
				float f1x = 0.737F;
				float f2x = 0.737F;

				if (count % 2 == 0) {
					fx = 0.811F;
					f1x = 0.811F;
					f2x = 0.811F;
				}

				float f3x = (float) count / 48.0F;
				bufferbuilder.pos(x + d10 * (double) f3x - 0.0125D, y + d11 * (double) (f3x * f3x + f3x) * 0.5D + 0.2375D, z + d12 * (double) f3x).color(fx, f1x, f2x, 1.0F).endVertex();
				bufferbuilder.pos(x + d10 * (double) f3x + 0.0125D, y + d11 * (double) (f3x * f3x + f3x) * 0.5D + 0.2625D, z + d12 * (double) f3x).color(fx, f1x, f2x, 1.0F).endVertex();
			}

			tessellator.draw();

			bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);

			for (int count2 = 0; count2 <= 48; ++count2) {
				float f4x = 0.737F;
				float f5x = 0.737F;
				float f6x = 0.737F;

				if (count2 % 2 == 0) {
					f4x = 0.811F;
					f5x = 0.811F;
					f6x = 0.811F;
				}

				float f7x = (float) count2 / 48.0F;
				bufferbuilder.pos(x + d10 * (double) f7x - 0.0125D, y + d11 * (double) (f7x * f7x + f7x) * 0.5D + 0.2625D, z + d12 * (double) f7x).color(f4x, f5x, f6x, 1.0F).endVertex();
				bufferbuilder.pos(x + d10 * (double) f7x + 0.0125D, y + d11 * (double) (f7x * f7x + f7x) * 0.5D + 0.2375D, z + d12 * (double) f7x).color(f4x, f5x, f6x, 1.0F).endVertex();
			}

			tessellator.draw();

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.enableCull();
			super.doRender(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	protected ResourceLocation getEntityTexture(EntityBLFishHook entity) {
		return HOOK_TEXTURE;
	}
}