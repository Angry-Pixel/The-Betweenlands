package thebetweenlands.client.render.entity;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.block.IsolatedBlockModelRenderer;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.EntityShockwaveBlock;

@SideOnly(Side.CLIENT)
public class RenderShockwaveBlock extends Render<EntityShockwaveBlock> {
	private static final IsolatedBlockModelRenderer blockRenderer = new IsolatedBlockModelRenderer();

	public RenderShockwaveBlock(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntityShockwaveBlock entity, double x, double y, double z, float yaw, float tick) {
		if(entity.posY != entity.origin.getY())
			renderShockwaveBlock(entity, x, y, z, yaw, tick);
	}

	public void renderShockwaveBlock(EntityShockwaveBlock entity, double x, double y, double z, float yaw, float tick) {
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY + 0.5D, entity.posZ, 
					(entity.ticksExisted + tick) / 12.0F + 1F,
					10.0f / 255.0f * 4.0F, 
					40.0f / 255.0f * 4.0F, 
					160.0f / 255.0f * 4.0F));
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY + 0.5D, entity.posZ, 
					(entity.ticksExisted + tick) / 35.0F + 0.6F,
					-3.4F, 
					-3.4F, 
					-3.4F));
		}

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x - 0.5F, (float) y, (float) z - 0.5F);
		//Lighting is already handled in the block renderer
		GlStateManager.disableLighting();
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		@SuppressWarnings("deprecation")
		IBlockState state = entity.block.getStateFromMeta(entity.blockMeta);
		IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
		if(model != null) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

			blockRenderer.setLighting((IBlockState blockState, @Nullable EnumFacing facing) -> {
				return state.getPackedLightmapCoords(entity.world, facing != null ? entity.origin.up().offset(facing) : entity.origin.up());
			}).setTint((IBlockState blockState, int tintIndex) -> {
				if(blockState.getBlock() == entity.block)
					return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, entity.world, entity.origin, tintIndex);
				else
					return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, null, null, tintIndex);
			});

			blockRenderer.renderModel(entity.world, entity.origin, model, state, MathHelper.getPositionRandom(entity.origin), buffer);

			tessellator.draw();
		}
		GlStateManager.enableLighting();
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShockwaveBlock entity) {
		return null;
	}
}