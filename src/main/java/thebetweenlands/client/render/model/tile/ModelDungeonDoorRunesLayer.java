package thebetweenlands.client.render.model.tile;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityDungeonDoorRunes;
import thebetweenlands.util.LightingUtil;

@SideOnly(Side.CLIENT)
public class ModelDungeonDoorRunesLayer extends ModelBase {

	public ModelRenderer top_overlay;
	public ModelRenderer mid_overlay;
	public ModelRenderer bottom_overlay;

	public ModelDungeonDoorRunesLayer() {
		textureWidth = 32;
		textureHeight = 32;

		top_overlay = new ModelRenderer(this, 0, 0);
		top_overlay.setRotationPoint(0.0F, -4.5F, -5.5F);
		top_overlay.addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5, 0.0F);

		mid_overlay = new ModelRenderer(this, 1, 11);
		mid_overlay.setRotationPoint(0.0F, 0.0F, -6.0F);
		mid_overlay.addBox(-7.0F, -2.0F, -2.0F, 14, 4, 4, 0.0F);

		bottom_overlay = new ModelRenderer(this, 0, 20);
		bottom_overlay.setRotationPoint(0.0F, 4.5F, -5.5F);
		bottom_overlay.addBox(-7.0F, -2.5F, -2.5F, 14, 5, 5, 0.0F);
	}

	public void renderTopOverlay(TileEntity tile, ResourceLocation glow, int ticks, float scale, float partialTicks) {
		if (tile instanceof TileEntityDungeonDoorRunes) {
			TileEntityDungeonDoorRunes tileDoor = (TileEntityDungeonDoorRunes) tile;
			top_overlay.rotateAngleX = 0F + (tileDoor.lastTickTopRotate + (tileDoor.top_rotate - tileDoor.lastTickTopRotate) * partialTicks) / (180F / (float) Math.PI);
		} else {
			top_overlay.rotateAngleX = 0;
		}
		
		this.renderRune(top_overlay, glow, ticks, scale, partialTicks);
	}

	public void renderMidOverlay(TileEntity tile, ResourceLocation glow, int ticks, float scale, float partialTicks) {
		if (tile instanceof TileEntityDungeonDoorRunes) {
			TileEntityDungeonDoorRunes tileDoor = (TileEntityDungeonDoorRunes) tile;
			mid_overlay.rotateAngleX = 0F + (tileDoor.lastTickMidRotate + (tileDoor.mid_rotate - tileDoor.lastTickMidRotate) * partialTicks) / (180F / (float) Math.PI);
		} else {
			top_overlay.rotateAngleX = 0;
		}
		
		this.renderRune(mid_overlay, glow, ticks, scale, partialTicks);
	}

	public void renderBottomOverlay(TileEntity tile, ResourceLocation glow, int ticks, float scale, float partialTicks) {
		if (tile instanceof TileEntityDungeonDoorRunes) {
			TileEntityDungeonDoorRunes tileDoor = (TileEntityDungeonDoorRunes) tile;
			bottom_overlay.rotateAngleX = 0F + (tileDoor.lastTickBottomRotate + (tileDoor.bottom_rotate - tileDoor.lastTickBottomRotate) * partialTicks) / (180F / (float) Math.PI);
		} else {
			top_overlay.rotateAngleX = 0;
		}
		
		this.renderRune(bottom_overlay, glow, ticks, scale, partialTicks);
	}
	
	private void renderRune(ModelRenderer box, ResourceLocation glow, int ticks, float scale, float partialTicks) {
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(-0.001F, -3F);
		
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
		
		boolean useStencil = false;
		int stencilBit = MinecraftForgeClient.reserveStencilBit();
		int stencilMask = 1 << stencilBit;

		if(stencilBit >= 0) {
			useStencil = fbo.isStencilEnabled() ? true : fbo.enableStencil();
		}
		
		if(useStencil) {
			GL11.glEnable(GL11.GL_STENCIL_TEST);
			
			//Clear our stencil bit to 0
			GL11.glStencilMask(stencilMask);
			GL11.glClearStencil(0);
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
			GL11.glStencilMask(~0);

			GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			
			GlStateManager.color(1, 1, 1, 1);
			
			GlStateManager.depthMask(false);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

			//Render rune mask
			box.render(scale);

			GlStateManager.depthMask(true);

			GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
			GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			
			//Render glowy stuff
			Minecraft.getMinecraft().getTextureManager().bindTexture(glow);
			this.renderRuneGlow(box, ticks, scale, partialTicks);
			
			GL11.glDisable(GL11.GL_STENCIL_TEST);
			
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		} else {
			//No fancy runes for toasters
			box.render(scale);
		}
		
		if(stencilBit >= 0) {
			MinecraftForgeClient.releaseStencilBit(stencilBit);
		}
		
		GlStateManager.disablePolygonOffset();
	}

	private void renderRuneGlow(ModelRenderer box, int ticks, float scale, float partialTicks) {
		LightingUtil.INSTANCE.setLighting(255);
		
		float renderTicks = ticks + partialTicks;
		
		float texOffset = renderTicks * 0.0015F;
		
		int passes = 3;
		
		for(int i = 0; i < passes; i++) {
			GlStateManager.depthMask(i == passes - 1);
			if(i == passes - 1) {
				GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
			}
			
			GlStateManager.color(1, 1, 1, 0.3f + (float)(Math.sin(renderTicks / 10.0f + i * Math.PI * 2.0f / passes) + 1) / 2.0f * 0.3f);
			
			float dirU = (float) Math.cos(i * Math.PI * 2.0f / passes);
			float dirV = (float) Math.sin(i * Math.PI * 2.0f / passes);
			
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			GlStateManager.translate(dirU * texOffset, dirV * texOffset, 0);
			GlStateManager.scale(1, 2.0, 1); //V needs to be scaled x2 because texture is not square
			GlStateManager.scale(passes - i, passes - i, 1);
			GlStateManager.rotate(renderTicks / 30.0f, 0, 0, 1);
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			
			box.render(scale);
			
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		
		LightingUtil.INSTANCE.revert();
	}
	
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
