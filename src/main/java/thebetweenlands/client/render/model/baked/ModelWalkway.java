package thebetweenlands.client.render.model.baked;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.baked.modelbase.ModelWalkwayBlock;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.ModelConverter.Box;
import thebetweenlands.util.ModelConverter.Quad;
import thebetweenlands.util.QuadBuilder;
import thebetweenlands.util.TexturePacker;
import thebetweenlands.util.Vec3UV;

public class ModelWalkway extends ModelFromModelBase {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "blocks/walkway");
	public static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation(ModInfo.ID, "blocks/log_weedwood");

	public static final ModelWalkwayBlock MODEL = new ModelWalkwayBlock();

	public ModelWalkway(TexturePacker packer, boolean hasStands) {
		super(new ModelFromModelBase.Builder(MODEL, TEXTURE, 128, 128).particleTexture(PARTICLE_TEXTURE).packer(packer).processor(new IVertexProcessor() {
			@Override
			public Vec3UV process(Vec3UV vertexIn, Quad quad, Box box, QuadBuilder builder) {
				if(!hasStands && (box.getModelRenderer() == MODEL.standright || box.getModelRenderer() == MODEL.standleft)) {
					return null;
				}
				return vertexIn;
			}
		}));
	}
}
