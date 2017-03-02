package thebetweenlands.client.render.model.baked;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.baked.modelbase.ModelWalkwayBlock;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.ModelConverter.Box;
import thebetweenlands.util.ModelConverter.Quad;
import thebetweenlands.util.Vec3UV;

public class ModelWalkway extends ModelFromModelBase {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "blocks/walkway");
	public static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation(ModInfo.ID, "blocks/log_weedwood");

	public static final ModelWalkwayBlock MODEL = new ModelWalkwayBlock();

	public ModelWalkway(boolean hasStands) {
		super(MODEL, TEXTURE, PARTICLE_TEXTURE, 128, 128, new IVertexProcessor() {
			@Override
			public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box) {
				if(!hasStands && (box.getModelRenderer() == MODEL.standright || box.getModelRenderer() == MODEL.standleft)) {
					return null;
				}
				return vertexIn;
			}
		});
	}
}
