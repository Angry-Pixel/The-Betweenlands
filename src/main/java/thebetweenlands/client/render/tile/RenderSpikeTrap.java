package thebetweenlands.client.render.tile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.loader.IFastTESRBakedModels;
import thebetweenlands.client.render.model.tile.ModelSpikeBlock;
import thebetweenlands.common.block.structure.BlockSpikeTrap;
import thebetweenlands.common.tile.TileEntitySpikeTrap;
import thebetweenlands.util.StatePropertyHelper;

@SideOnly(Side.CLIENT)
public class RenderSpikeTrap extends TileEntitySpecialRenderer<TileEntitySpikeTrap> implements IFastTESRBakedModels {
	private static final ModelSpikeBlock MODEL = new ModelSpikeBlock();

	private final ResourceLocation spikeTexture;

	private final ModelResourceLocation[] overlayModelLocations = new ModelResourceLocation[EnumFacing.VALUES.length];
	private final Map<ModelResourceLocation, EnumFacing> overlayModelFacings = new HashMap<>();

	protected static BlockRendererDispatcher blockRenderer;

	private IBakedModel[] overlayModels = new IBakedModel[EnumFacing.VALUES.length];

	public RenderSpikeTrap() {
		this(new ResourceLocation("thebetweenlands:spike_trap"), new ResourceLocation("thebetweenlands:textures/tiles/spike_block_spikes_1.png"));
	}

	public RenderSpikeTrap(ResourceLocation overlayModel, ResourceLocation spikeTexture) {
		this.spikeTexture = spikeTexture;

		for(EnumFacing facing : EnumFacing.VALUES) {
			ModelResourceLocation location = new ModelResourceLocation(overlayModel, "facing=" + facing.getName() + ",overlay=true");
			overlayModelLocations[facing.ordinal()] = location;
			overlayModelFacings.put(location, facing);
		}
	}

	@Override
	public Collection<ModelResourceLocation> getModelLocations() {
		return ImmutableSet.copyOf(overlayModelLocations);
	}

	@Override
	public void onModelBaked(ModelResourceLocation location, IBakedModel model) {
		this.overlayModels[overlayModelFacings.get(location).ordinal()] = model;
	}

	@Override
	public void render(TileEntitySpikeTrap tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		this.renderFast(tile, x, y, z, partialTicks, destroyStage, alpha);
	}

	private void renderFast(TileEntitySpikeTrap tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();
		GlStateManager.disableCull();

		if (Minecraft.isAmbientOcclusionEnabled())
		{
			GlStateManager.shadeModel(GL11.GL_SMOOTH);
		}
		else
		{
			GlStateManager.shadeModel(GL11.GL_FLAT);
		}

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		renderTileEntityFast(tile, x, y, z, partialTicks, destroyStage, alpha, buffer);
		buffer.setTranslation(0, 0, 0);

		tessellator.draw();

		RenderHelper.enableStandardItemLighting();
	}

	private void renderSpikes(TileEntitySpikeTrap tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if(tile.animationTicks > 0) {
			EnumFacing facing = StatePropertyHelper.getStatePropertySafely(tile, BlockSpikeTrap.class, BlockSpikeTrap.FACING, EnumFacing.UP);

			if(tile.getWorld() != null) {
				RenderHelper.enableStandardItemLighting();
				int i = tile.getWorld().getCombinedLight(tile.getPos().offset(facing), 0);
				int j = i % 65536;
				int k = i / 65536;
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}

			this.bindTexture(spikeTexture);

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x + 0.5F, (float) y + 0.5F, (float) z + 0.5F);
			GlStateManager.scale(-1, -1, 1);

			switch (facing) {
			case UP:
				GlStateManager.rotate(0F, 0.0F, 1F, 0F);
				break;
			case DOWN:
				GlStateManager.rotate(180F, 1F, 0F, 0F);
				break;
			case NORTH:
				GlStateManager.rotate(90F, 1F, 0F, 0F);
				break;
			case SOUTH:
				GlStateManager.rotate(-90F, 1.0F, 0F, 0F);
				GlStateManager.rotate(180F, 0.0F, 1F, 0F);
				break;
			case WEST:
				GlStateManager.rotate(90F, 0.0F, 0F, 1F);
				GlStateManager.rotate(-90F, 0.0F, 1F, 0F);
				break;
			case EAST:
				GlStateManager.rotate(-90F, 0.0F, 0F, 1F);
				GlStateManager.rotate(90F, 0.0F, 1F, 0F);
				break;
			}

			GlStateManager.translate(0F, -1F, 0F);
			GlStateManager.disableCull();
			MODEL.renderSpikes(tile, partialTicks);
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
		}
	}

	@Override
	public void renderTileEntityFast(TileEntitySpikeTrap tile, double x, double y, double z, float partialTicks,
			int destroyStage, float partial, BufferBuilder buffer) {
		BlockPos pos = tile.getPos();

		IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(tile.getWorld(), pos);

		IBlockState state = world.getBlockState(pos);

		if(state.getBlock() instanceof BlockSpikeTrap) {
			if(tile.type != 0) {
				if(blockRenderer == null) {
					blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
				}

				if(state.getBlock() instanceof BlockSpikeTrap) {
					EnumFacing facing = state.getValue(BlockSpikeTrap.FACING);

					buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

					blockRenderer.getBlockModelRenderer().renderModel(world, this.overlayModels[facing.ordinal()], state, pos, buffer, false);
				}
			}

			this.renderSpikes(tile, x, y, z, partialTicks, destroyStage, 1);
		}
	}
}