package thebetweenlands.client.render.model.baked;

import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;

public class BakedModelItemWrapper implements IPerspectiveAwareModel {
	private final IBakedModel transformsModel, quadModel;

	/**
	 * Creates a baked model that returns the quads of quadModel and the transforms of transformsModel.
	 * @param transformsModel The model with the transforms to be used
	 * @param quadModel The model with the quads to be used
	 */
	public BakedModelItemWrapper(IBakedModel transformsModel, IBakedModel quadModel) {
		this.transformsModel = transformsModel;
		this.quadModel = quadModel;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return this.quadModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return this.quadModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return this.quadModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return true;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return this.quadModel.getParticleTexture();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return this.transformsModel.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return this.quadModel.getOverrides();
	}

	@SuppressWarnings("deprecation")
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		Pair<? extends IBakedModel, Matrix4f> result;
		if(this.transformsModel instanceof IPerspectiveAwareModel) {
			result = ((IPerspectiveAwareModel)this.transformsModel).handlePerspective(cameraTransformType);
		} else 
			result = IPerspectiveAwareModel.MapWrapper.handlePerspective(this, this.getItemCameraTransforms().getTransform(cameraTransformType), cameraTransformType);
		return Pair.of(this.quadModel, result.getValue());
	}
}
