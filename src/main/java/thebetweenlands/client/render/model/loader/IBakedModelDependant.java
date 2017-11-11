package thebetweenlands.client.render.model.loader;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

public interface IBakedModelDependant {
	/**
	 * Returns a list of model dependencies.
	 * If the model wasn't registered previously it will be loaded and baked with the arguments returned
	 * by {@link IBakedModelDependant#getModelState(IModel)}, {@link IBakedModelDependant#getVertexFormat(IModel)}
	 * and {@link IBakedModelDependant#getTextureGetter(IModel)}.
	 * @param modelLocation
	 * @return
	 */
	Collection<ModelResourceLocation> getDependencies(ResourceLocation modelLocation);

	/**
	 * Sets the baked model dependencies
	 * @param modelLocation
	 * @param dependencies
	 */
	void setDependencies(ResourceLocation modelLocation, Map<ModelResourceLocation, IBakedModel> dependencies);

	/**
	 * Returns the model state for the specified model
	 * @param dependecyModel
	 * @return
	 */
	default IModelState getModelState(IModel dependecyModel) {
		return dependecyModel.getDefaultState();
	}

	/**
	 * Returns the vertex format for the specified model
	 * @param dependencyModel
	 * @return
	 */
	default VertexFormat getVertexFormat(IModel dependencyModel) {
		return DefaultVertexFormats.BLOCK;
	}

	static enum DefaultTextureGetter implements Function<ResourceLocation, TextureAtlasSprite> {
		INSTANCE;

		@Override
		public TextureAtlasSprite apply(ResourceLocation location) {
			return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
		}
	}

	/**
	 * Returns the texture getter for the specified model
	 * @param dependencyModel
	 * @return
	 */
	default Function<ResourceLocation, TextureAtlasSprite> getTextureGetter(IModel dependencyModel) {
		return DefaultTextureGetter.INSTANCE;
	}
}
