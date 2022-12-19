package thebetweenlands.client.render.model.baked;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModelDynBucketBL implements IModel {
	private final IModel model;
	private final ResourceLocation baseLocation, liquidLocation, coverLocation;
	private final Fluid fluid;
	private final boolean flipGas;
	
	public ModelDynBucketBL() {
		this.model = ModelDynBucket.MODEL;
		this.baseLocation = null;
		this.liquidLocation = null;
		this.coverLocation = null;
		this.fluid = null;
		this.flipGas = false;
	}

	public ModelDynBucketBL(ResourceLocation baseLocation, ResourceLocation liquidLocation, ResourceLocation coverLocation, Fluid fluid, boolean flipGas) {
		this.model = new ModelDynBucket(baseLocation, liquidLocation, coverLocation, fluid, flipGas);
		this.fluid = fluid;
		this.flipGas = flipGas;
		this.baseLocation = baseLocation;
		this.liquidLocation = liquidLocation;
		this.coverLocation = coverLocation;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		return this.model.getTextures();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return this.model.bake(state, format, bakedTextureGetter);
	}

	@Override
	public IModelState getDefaultState() {
		return this.model.getDefaultState();
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		JsonParser parser = new JsonParser();

		Fluid fluid = this.fluid;
		if(customData.containsKey("default_fluid")) {
			String fluidName = JsonUtils.getString(parser.parse(customData.get("default_fluid")), "default_fluid");
			fluid = FluidRegistry.getFluid(fluidName);
		}

		boolean flip = this.flipGas;
		if (customData.containsKey("flipGas")) {
			String flipStr = JsonUtils.getString(parser.parse(customData.get("flipGas")), "flipGas");
			switch (flipStr) {
				case "true":
					flip = true;
					break;
				case "false":
					flip = false;
					break;
				default:
					throw new IllegalArgumentException(String.format("DynBucket custom data \"flipGas\" must have value \'true\' or \'false\' (was \'%s\')", flipStr));
			}
		}

		ResourceLocation baseLocation = null;
		ResourceLocation liquidLocation = null;
		ResourceLocation coverLocation = null;

		if(customData.containsKey("base_texture")) {
			baseLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("base_texture")), "base_texture"));
		} else {
			baseLocation = this.baseLocation;
		}

		if(customData.containsKey("liquid_template")) {
			liquidLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("liquid_template")), "liquid_template"));
		} else {
			liquidLocation = this.liquidLocation;
		}

		if(customData.containsKey("cover_texture")) {
			coverLocation = new ResourceLocation(JsonUtils.getString(parser.parse(customData.get("cover_texture")), "cover_texture"));
		} else {
			coverLocation = this.coverLocation;
		}

		// create new model with correct liquid and textures
		return new ModelDynBucketBL(baseLocation, liquidLocation, coverLocation, fluid, flip);
	}
}
