package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import thebetweenlands.client.render.model.loader.IBakedModelDependant;

public class BakedModelItemWrapper implements IBakedModel, IBakedModelDependant {
	private final Collection<ResourceLocation> dependencies;
	private final IBakedModel transformsModel, bakedQuadModel;
	private boolean shouldInheritOverrides = true;
	private boolean shouldCacheOverrideModels = true;
	private ItemOverrideList wrappedOverrideList;

	/**
	 * Creates a baked model that returns the quads of quadModel and the transforms and item overrides list of transformsModel.
	 * @param transformsModel The baked model with the transforms and item overrides list to be used
	 * @param bakedQuadModel The baked model with the quads to be used
	 * @param quadModel Dependencies of the IModel of bakedQuadModel
	 */
	public BakedModelItemWrapper(IBakedModel transformsModel, IBakedModel bakedQuadModel, Collection<ResourceLocation> dependencies) {
		this.transformsModel = transformsModel;
		this.bakedQuadModel = bakedQuadModel;
		this.dependencies = dependencies;
	}

	/**
	 * Sets whether the overrides of the transforms model should be inherited
	 * @param inherit
	 * @return
	 */
	public BakedModelItemWrapper setInheritOverrides(boolean inherit) {
		this.shouldInheritOverrides = inherit;
		return this;
	}

	/**
	 * Sets whether override models returned by {@link ItemOverrideList#handleItemState(IBakedModel, ItemStack, World, EntityLivingBase)} should be cached
	 * @param cached
	 * @return
	 */
	public BakedModelItemWrapper setCacheOverrideModels(boolean cached) {
		this.shouldCacheOverrideModels = cached;
		return this;
	}

	@Override
	public Collection<ModelResourceLocation> getDependencies(ResourceLocation modelLocation) {
		List<ModelResourceLocation> dependencies = new ArrayList<>();

		for(ResourceLocation location : this.dependencies) {
			dependencies.add(ModelLoader.getInventoryVariant(location.toString()));
		}

		if(this.bakedQuadModel instanceof IBakedModelDependant) {
			dependencies.addAll(((IBakedModelDependant) this.bakedQuadModel).getDependencies(modelLocation));
		}

		return dependencies;
	}

	@Override
	public void setDependencies(ResourceLocation modelLocation, Map<ModelResourceLocation, IBakedModel> dependencies) {
		if(this.bakedQuadModel instanceof IBakedModelDependant) {
			((IBakedModelDependant) this.bakedQuadModel).setDependencies(modelLocation, dependencies);
		}
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return this.bakedQuadModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return this.bakedQuadModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return this.bakedQuadModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return this.bakedQuadModel.getParticleTexture();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return this.transformsModel.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		if(this.shouldInheritOverrides){
			return this.transformsModel.getOverrides();
		} else {
			if(this.wrappedOverrideList == null)
				this.wrappedOverrideList = new WrappedItemOverrideList(this.bakedQuadModel.getOverrides(), this.dependencies);
			return this.wrappedOverrideList;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		Pair<? extends IBakedModel, Matrix4f> result;
		if(this.transformsModel != null) {
			result = ((IBakedModel)this.transformsModel).handlePerspective(cameraTransformType);
		} else 
			result = PerspectiveMapWrapper.handlePerspective(this, this.getItemCameraTransforms().getTransform(cameraTransformType), cameraTransformType);
		return Pair.of(this, result.getValue());
	}

	private static final class WrappedItemOverrideList extends ItemOverrideList {
		private final Collection<ResourceLocation> dependencies;
		private final Map<IBakedModel, IBakedModel> cachedModels = new HashMap<IBakedModel, IBakedModel>();
		private final ItemOverrideList parent;

		protected WrappedItemOverrideList(ItemOverrideList parent, Collection<ResourceLocation> dependencies) {
			super(ImmutableList.of());
			this.parent = parent;
			this.dependencies = dependencies;
		}

		@SuppressWarnings("deprecation")
		@Override
		public ResourceLocation applyOverride(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
			return this.parent.applyOverride(stack, worldIn, entityIn);
		}

		@Override
		public ImmutableList<ItemOverride> getOverrides() {
			return this.parent.getOverrides();
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			//Make sure that the correct baked model is passed in here, so that if the object is cast later on it doesn't cause issues
			BakedModelItemWrapper wrapper = ((BakedModelItemWrapper)originalModel);
			IBakedModel quadModel = wrapper.bakedQuadModel;
			IBakedModel newModel = this.parent.handleItemState(quadModel, stack, world, entity);
			//This makes sure that all baked models are wrapped properly
			if(newModel instanceof BakedModelItemWrapper == false) {
				//Cache the wrapped models if possible
				if(wrapper.shouldCacheOverrideModels) {
					IBakedModel cachedModel = this.cachedModels.get(newModel);
					if(cachedModel == null)
						this.cachedModels.put(newModel, cachedModel = new BakedModelItemWrapper(wrapper.transformsModel, newModel, this.dependencies).setInheritOverrides(wrapper.shouldInheritOverrides));
					return cachedModel;
				}
				return new BakedModelItemWrapper(wrapper.transformsModel, newModel, this.dependencies).setInheritOverrides(wrapper.shouldInheritOverrides);
			}
			return newModel;
		}
	}
}
