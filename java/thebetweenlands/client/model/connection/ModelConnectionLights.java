package thebetweenlands.client.model.connection;

import thebetweenlands.connection.ConnectionLogicFairyLights;
import thebetweenlands.connection.Light;
import thebetweenlands.tileentities.TileEntityConnectionFastener;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.model.AdvancedModelRenderer;
import thebetweenlands.client.model.lights.ModelLight;
import thebetweenlands.client.model.lights.ModelLightWeedwoodLantern;
import thebetweenlands.utils.MathUtils;
import thebetweenlands.utils.vectormath.Point3f;
import thebetweenlands.utils.vectormath.Vector3f;

public class ModelConnectionLights extends ModelConnection<ConnectionLogicFairyLights> {
	private AdvancedModelRenderer cordModel;

	private ModelLight[] lightModels = new ModelLight[]{
			new ModelLightWeedwoodLantern()
	};

	public ModelConnectionLights() {
		cordModel = new AdvancedModelRenderer(this, 0, 0);
		cordModel.addBox(-1, -1, 0, 2, 2, 1);
	}

	@Override
	public void render(TileEntityConnectionFastener fastener, ConnectionLogicFairyLights logic, World world, int skylight, int moonlight, float delta) {
		super.render(fastener, logic, world, skylight, moonlight, delta);
		Light[] lightPoints = logic.getLightPoints();
		Light[] lightPointsOld = logic.getPrevLightPoints();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_LIGHTING);
		int fastenerRotation = fastener.getBlockMetadata();
		for (int i = 0; i < lightPoints.length; i++) {
			Light light = lightPoints[i];
			Point3f point = light.getPoint();
			Vector3f color = light.getLight();
			Vector3f rotation = light.getRotation();
			float brightness = 1;
			if (lightPointsOld != null && i < lightPointsOld.length) {
				point.interpolate(lightPointsOld[i].getPoint(), 1 - delta);
				rotation.interpolate(lightPointsOld[i].getRotation(), 1 - delta, true);
				brightness = light.getBrightness();
			}
			ModelLight lightModel = lightModels[light.getVariant().ordinal()];
			lightModel.setOffsets(point.x / 16, point.y / 16, point.z / 16);
			float rotationOffset = 0;
			boolean vert = Math.abs(rotation.y) == MathUtils.PI / 2;
			if (vert) {
				switch (fastenerRotation) {
					case 2:
						rotationOffset = -MathUtils.PI;
						break;
					case 3:
						rotationOffset = -MathUtils.PI / 2;
						break;
					case 4:
						rotationOffset = MathUtils.PI / 2;
				}
			}
			lightModel.setAfts(0, -2.2F / 16, 0);
			lightModel.setRotationAngles(lightModel.shouldParallelCord() ? rotation.y : vert ? 0.3F : 0, rotation.x + rotationOffset, rotation.z);
			lightModel.setScale(1);
			lightModel.render(world, 0.0625F, color, moonlight, skylight, brightness, i, delta);
		}
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	protected void renderSegment(ConnectionLogicFairyLights fairylights, int index, float angleX, float angleY, float length, float x, float y, float z, float delta) {
		cordModel.rotateAngleX = angleX;
		cordModel.rotateAngleY = angleY;
		cordModel.scaleZ = length;
		cordModel.setRotationPoint(x, y, z);
		cordModel.render(0.0625F);
	}
}
