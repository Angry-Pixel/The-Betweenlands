package thebetweenlands.client.render.entity;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.EntityShockwaveBlock;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.IsolatedBlockModelRenderer;

@SideOnly(Side.CLIENT)
public class RenderShockwaveBlock extends Render<EntityShockwaveBlock> {
	private static final IsolatedBlockModelRenderer blockRenderer = new IsolatedBlockModelRenderer();

	public RenderShockwaveBlock(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntityShockwaveBlock entity, double x, double y, double z, float yaw, float tick) {
		renderShockwaveBlock(entity, x, y, z, yaw, tick);
	}

	public void renderShockwaveBlock(EntityShockwaveBlock entity, double x, double y, double z, float yaw, float tick) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x - 0.5F, (float) y, (float) z - 0.5F);
		//Lighting is already handled in the block renderer
		GL11.glDisable(GL11.GL_LIGHTING);
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		@SuppressWarnings("deprecation")
		IBlockState state = entity.blockID.getStateFromMeta(entity.blockMeta);
		IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
		if(model != null) {
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
			blockRenderer.setLighting((IBlockState blockState, @Nullable EnumFacing facing) -> {
				return state.getPackedLightmapCoords(entity.worldObj, facing != null ? entity.origin.up().offset(facing) : entity.origin.up());
			}).setTint((IBlockState blockState, int tintIndex) -> {
				if(blockState.getBlock() == Blocks.GRASS || blockState.getBlock() == BlockRegistry.SWAMP_GRASS)
					return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, entity.worldObj, entity.getPosition(), tintIndex);
				else
					return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, null, null, tintIndex);
			});
			blockRenderer.renderModel(entity.origin, model, state, MathHelper.getPositionRandom(entity.origin), buffer);
			tessellator.draw();
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShockwaveBlock entity) {
		return null;
	}
}