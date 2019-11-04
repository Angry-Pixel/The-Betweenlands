package thebetweenlands.client.render.model.baked;

import java.util.Collection;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import thebetweenlands.client.render.model.baked.modelbase.ModelRubberTap;
import thebetweenlands.util.TexturePacker;

public class ModelRubberTapCombined implements IModel {
	private final ModelFromModelBase tapModel;
	private final IModel combinedTapModel;
	private final int height;
	private final ResourceLocation particleTexture;
	private final ResourceLocation fluidTexture;

	public ModelRubberTapCombined(TexturePacker packer, ResourceLocation texture, ResourceLocation particle) {
		this(packer, texture, particle, null, 0);
	}

	public ModelRubberTapCombined(TexturePacker packer, ResourceLocation texture, ResourceLocation particleTexture, ResourceLocation fluidTexture, int height) {
		this.combinedTapModel = new ModelCombined(this.tapModel = new ModelFromModelBase.Builder(new ModelRubberTap(), texture, 128, 128).particleTexture(particleTexture).packer(packer).doubleFace(false).build(), new ModelRubberTapLiquid(fluidTexture, height));
		this.particleTexture = particleTexture;
		this.height = height;
		this.fluidTexture = fluidTexture;
	}

	protected ModelRubberTapCombined(ModelFromModelBase parent, ResourceLocation particleTexture, ResourceLocation fluidTexture, int height) {
		this.combinedTapModel = new ModelCombined(this.tapModel = new ModelFromModelBase(parent, particleTexture, 128, 128, null, parent.doubleFace, parent.ambientOcclusion), new ModelRubberTapLiquid(fluidTexture, height));
		this.particleTexture = particleTexture;
		this.height = height;
		this.fluidTexture = fluidTexture;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return this.combinedTapModel.getDependencies();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return this.combinedTapModel.getTextures();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return this.combinedTapModel.bake(state, format, bakedTextureGetter);
	}

	@Override
	public IModelState getDefaultState() {
		return this.combinedTapModel.getDefaultState();
	}

	@Override
	public ModelRubberTapCombined process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();

		ResourceLocation fluidTexture = this.fluidTexture;

		if(customData.containsKey("fluid_texture")) {
			fluidTexture = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("fluid_texture")), "fluid_texture"));
		}

		if(fluidTexture == null) {
			fluidTexture = TextureMap.LOCATION_MISSING_TEXTURE;
		}

		int height = this.height;
		if(customData.containsKey("fluid_height")) {
			String fluidHeightJsonStr = customData.get("fluid_height");
			height = JsonUtils.getInt(parser.parse(fluidHeightJsonStr), "fluid_height");
		}

		ResourceLocation particleTexture = this.particleTexture;
		if(customData.containsKey("particle_texture")) {
			String particleTextureJsonStr = customData.get("particle_texture");
			particleTexture = new ResourceLocation(JsonUtils.getString(parser.parse(particleTextureJsonStr), "particle_texture"));
		}

		return new ModelRubberTapCombined(this.tapModel, particleTexture, fluidTexture, height);
	}
}
