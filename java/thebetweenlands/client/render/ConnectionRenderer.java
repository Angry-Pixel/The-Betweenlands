package thebetweenlands.client.render;

import thebetweenlands.connection.ConnectionType;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.tileentities.TileEntityConnectionFastener;
import thebetweenlands.tileentities.connection.Connection;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thebetweenlands.blocks.lanterns.BlockConnectionFastener;
import thebetweenlands.client.model.connection.ModelConnection;
import thebetweenlands.utils.Catenary;
import thebetweenlands.utils.Segment;
import thebetweenlands.utils.vectormath.Point3f;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConnectionRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/fairy_lights.png");

	public static final int TEXTURE_WIDTH = 128;

	public static final int TEXTURE_HEIGHT = 128;

	private Field currentFrustumField;

	private ModelConnection[] connectionRenderers;

	public ConnectionRenderer() {
		currentFrustumField = ReflectionHelper.findField(EntityRenderer.class, "currentFrustum");
		ConnectionType[] types = ConnectionType.values();
		connectionRenderers = new ModelConnection[types.length];
		for (int i = 0; i < types.length; i++) {
			connectionRenderers[i] = types[i].createRenderer();
		}
	}

	private Frustrum getFrustrum() {
		try {
			return (Frustrum) currentFrustumField.get(Minecraft.getMinecraft().entityRenderer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent e) {
		Minecraft mc = Minecraft.getMinecraft();
		float delta = e.partialTicks;
		World world = mc.theWorld;
		Frustrum frustrum = getFrustrum();
		mc.getTextureManager().bindTexture(TEXTURE);
		mc.entityRenderer.enableLightmap(delta);
		RenderHelper.enableStandardItemLighting();
		GL11.glEnable(GL11.GL_FOG);
		for (TileEntity loadedTileEntity : (List<TileEntity>) world.loadedTileEntityList) {
			if (loadedTileEntity instanceof TileEntityConnectionFastener) {
				TileEntityConnectionFastener fastener = (TileEntityConnectionFastener) loadedTileEntity;
				if (frustrum.isBoundingBoxInFrustum(fastener.getRenderBoundingBox())) {
					int x = fastener.xCoord, y = fastener.yCoord, z = fastener.zCoord;
					int combinedLight = world.getLightBrightnessForSkyBlocks(x, y, z, 0);
					int sunlight = combinedLight % 65536;
					int moonlight = combinedLight / 65536;
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight, moonlight);
					GL11.glColor3f(1, 1, 1);
					GL11.glPushMatrix();
					GL11.glTranslated(x - TileEntityRendererDispatcher.staticPlayerX, y - TileEntityRendererDispatcher.staticPlayerY, z - TileEntityRendererDispatcher.staticPlayerZ);
					renderConnections(fastener, delta);
					GL11.glPopMatrix();
					if (RenderManager.debugBoundingBox) {
						drawBoundingBox(fastener);
					}
				}
			}
		}
		GL11.glDisable(GL11.GL_FOG);
		RenderHelper.disableStandardItemLighting();
		mc.entityRenderer.disableLightmap(delta);
	}

	private void drawBoundingBox(TileEntityConnectionFastener fastener) {
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPushMatrix();
		GL11.glTranslated(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
		RenderGlobal.drawOutlinedBoundingBox(fastener.getRenderBoundingBox(), 0xFFFFFF);
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_CULL_FACE);
		RenderHelper.enableStandardItemLighting();
	}

	public void renderConnections(TileEntityConnectionFastener fastener, float delta) {
		World world = fastener.getWorldObj();
		if (world == null || !(world.getBlock(fastener.xCoord, fastener.yCoord, fastener.zCoord) instanceof BlockConnectionFastener)) {
			return;
		}
		List<Connection> connections = removeUnnecessaryConnections(fastener.getConnections());
		GL11.glPushMatrix();
		Point3f offset = ((BlockConnectionFastener) fastener.getBlockType()).getOffsetForData(fastener.getWorldObj().getBlockMetadata(fastener.xCoord, fastener.yCoord, fastener.zCoord), 0.125F);
		GL11.glTranslatef(offset.x, offset.y, offset.z);
		int blockBrightness = fastener.getWorldObj().getLightBrightnessForSkyBlocks(fastener.xCoord, fastener.yCoord, fastener.zCoord, 0);
		int skylight = blockBrightness % 65536;
		int moonlight = blockBrightness / 65536;
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		for (Connection connection : connections) {
			if (connection.isOrigin()) {
				ModelConnection renderer = connectionRenderers[connection.getType().ordinal()];
				renderer.render(fastener, connection.getLogic(), world, skylight, moonlight, delta);
			}
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor3f(1, 1, 1);
		GL11.glPopMatrix();
	}

	private static List<Connection> removeUnnecessaryConnections(Collection<Connection> connections) {
		List<Connection> visibleConnections = new ArrayList<Connection>();
		for (Connection connection : connections) {
			Catenary catenary = connection.getCatenary();
			if (catenary == null) {
				continue;
			}
			if (connection.getTo() == null) {
				continue;
			}
			Segment[] vertices = catenary.getSegments();
			if (vertices == null) {
				continue;
			}
			visibleConnections.add(connection);
		}
		return visibleConnections;
	}

	public static void render3DTexture(int width, int height, int u, int v) {
		float u1 = u / (float) TEXTURE_WIDTH;
		float u2 = (u + width) / (float) TEXTURE_WIDTH;
		float v1 = v / (float) TEXTURE_HEIGHT;
		float v2 = (v + height) / (float) TEXTURE_HEIGHT;
		GL11.glPushMatrix();
		GL11.glScalef(width / 16F, height / 16F, 1);
		Tessellator tessellator = Tessellator.instance;
		float depth = 0.0625F;
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 0, 1);
		tessellator.addVertexWithUV(0, 0, 0, u1, v2);
		tessellator.addVertexWithUV(1, 0, 0, u2, v2);
		tessellator.addVertexWithUV(1, 1, 0, u2, v1);
		tessellator.addVertexWithUV(0, 1, 0, u1, v1);
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 0, -1);
		tessellator.addVertexWithUV(0, 1, -depth, u1, v1);
		tessellator.addVertexWithUV(1, 1, -depth, u2, v1);
		tessellator.addVertexWithUV(1, 0, -depth, u2, v2);
		tessellator.addVertexWithUV(0, 0, -depth, u1, v2);
		tessellator.draw();
		float widthStretch = 0.5F * (u1 - u2) / width;
		float heightStretch = 0.5F * (v2 - v1) / height;
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1, 0, 0);
		for (int p = 0; p < width; p++) {
			float x = (float) p / width;
			float ui = u1 + (u2 - u1) * x - widthStretch;
			tessellator.addVertexWithUV(x, 0, -depth, ui, v2);
			tessellator.addVertexWithUV(x, 0, 0, ui, v2);
			tessellator.addVertexWithUV(x, 1, 0, ui, v1);
			tessellator.addVertexWithUV(x, 1, -depth, ui, v1);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1, 0, 0);
		for (int p = 0; p < width; p++) {
			float xi = (float) p / width;
			float ui = u1 + (u2 - u1) * xi - widthStretch;
			float x = xi + 1F / width;
			tessellator.addVertexWithUV(x, 1, -depth, ui, v1);
			tessellator.addVertexWithUV(x, 1, 0, ui, v1);
			tessellator.addVertexWithUV(x, 0, 0, ui, v2);
			tessellator.addVertexWithUV(x, 0, -depth, ui, v2);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, 1, 0);
		for (int p = 0; p < height; p++) {
			float yi = (float) p / height;
			float vi = v2 + (v1 - v2) * yi - heightStretch;
			float y = yi + 1.0F / height;
			tessellator.addVertexWithUV(0, y, 0, u1, vi);
			tessellator.addVertexWithUV(1, y, 0, u2, vi);
			tessellator.addVertexWithUV(1, y, -depth, u2, vi);
			tessellator.addVertexWithUV(0, y, -depth, u1, vi);
		}
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0, -1, 0);
		for (int p = 0; p < height; p++) {
			float y = (float) p / height;
			float vi = v2 + (v1 - v2) * y - heightStretch;
			tessellator.addVertexWithUV(1, y, 0, u2, vi);
			tessellator.addVertexWithUV(0, y, 0, u1, vi);
			tessellator.addVertexWithUV(0, y, -depth, u1, vi);
			tessellator.addVertexWithUV(1, y, -depth, u2, vi);
		}
		tessellator.draw();
		GL11.glPopMatrix();
	}
}
