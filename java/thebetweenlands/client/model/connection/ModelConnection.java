package thebetweenlands.client.model.connection;

import thebetweenlands.connection.ConnectionLogic;
import thebetweenlands.tileentities.TileEntityConnectionFastener;
import thebetweenlands.tileentities.connection.Connection;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import thebetweenlands.client.render.ConnectionRenderer;
import thebetweenlands.utils.Catenary;
import thebetweenlands.utils.Segment;
import thebetweenlands.utils.vectormath.Point3f;
import thebetweenlands.utils.vectormath.Vector3f;

import java.util.Random;

public abstract class ModelConnection<T extends ConnectionLogic> extends ModelBase {
	public ModelConnection() {
		textureWidth = ConnectionRenderer.TEXTURE_WIDTH;
		textureHeight = ConnectionRenderer.TEXTURE_HEIGHT;
	}

	public void render(TileEntityConnectionFastener fastener, T logic, World world, int skylight, int moonlight, float delta) {
		renderCord(logic, world, skylight, moonlight, delta);
	}

	public void renderCord(T connectionLogic, World world, int sunlight, int moonlight, float delta) {
		Connection connection = connectionLogic.getConnection();
		Point3f to = connection.getTo();
		int toBlockBrightness = world.getLightBrightnessForSkyBlocks(MathHelper.floor_float(to.x), MathHelper.floor_float(to.y), MathHelper.floor_float(to.z), 0);
		int toSunlight = toBlockBrightness % 65536;
		int toMoonlight = toBlockBrightness / 65536;
		Segment[] segments = connection.getCatenary().getSegments();
		Catenary prevCatenary = connection.getPrevCatenary();
		Segment[] segmentsOld = null;
		if (prevCatenary != null) {
			segmentsOld = prevCatenary.getSegments();
		}
		GL11.glColor3f(1, 1, 1);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		for (int i = 0; i < segments.length; i++) {
			float v = i / (float) segments.length;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, sunlight * (1 - v) + toSunlight * v, moonlight * (1 - v) + toMoonlight * v);
			Segment segment = segments[i];
			Vector3f rotation = segment.getRotation();
			if (segmentsOld != null && i < segmentsOld.length) {
				rotation.interpolate(segmentsOld[i].getRotation(), 1 - delta, true);
			}
			float length = segment.getLength();
			if (segmentsOld != null && i < segmentsOld.length) {
				length = length * delta + segmentsOld[i].getLength() * (1 - delta);
			}
			Point3f vertex = segment.getVertex();
			if (segmentsOld != null && i < segmentsOld.length) {
				vertex.interpolate(segmentsOld[i].getVertex(), 1 - delta);
			}
			renderSegment(connectionLogic, i, rotation.y, rotation.x, length, vertex.x, vertex.y, vertex.z, delta);
		}
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

	protected abstract void renderSegment(T connectionLogic, int index, float angleX, float angleY, float length, float x, float y, float z, float delta);

	@Override
	public final ModelRenderer getRandomModelBox(Random rand) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final TextureOffset getTextureOffset(String partName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void render(Entity entity, float speed, float swing, float entityAge, float yaw, float pitch, float scale) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void setLivingAnimations(EntityLivingBase entity, float yaw, float pitch, float delta) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void setRotationAngles(float speed, float swing, float entityAge, float yaw, float pitch, float scale, Entity entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected final void setTextureOffset(String partName, int x, int y) {
		throw new UnsupportedOperationException();
	}
}
