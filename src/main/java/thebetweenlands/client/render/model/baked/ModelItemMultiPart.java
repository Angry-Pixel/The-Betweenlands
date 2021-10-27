package thebetweenlands.client.render.model.baked;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import thebetweenlands.client.render.model.loader.extension.LoaderExtension;

public class ModelItemMultiPart implements IModel {
	public static interface PartsMapper {
		public Map<String, String> getPartsMapping(ItemStack stack, World world, EntityLivingBase entity);
	}

	protected final PartsMapper mapper;

	protected final Map<String, Map<String, Pair<ResourceLocation, IModel>>> baseModelsMapping;

	protected final String category, key;

	protected final ResourceLocation particleTexture;

	public ModelItemMultiPart(PartsMapper mapper, ResourceLocation particleTexture) {
		this.mapper = mapper;
		this.particleTexture = particleTexture;
		this.baseModelsMapping = new HashMap<>();
		this.category = this.key = null;
	}

	private ModelItemMultiPart(PartsMapper mapper, Map<String, Map<String, Pair<ResourceLocation, IModel>>> baseModelsMapping, ResourceLocation particleTexture) {
		this.mapper = mapper;
		this.particleTexture = particleTexture;
		this.baseModelsMapping = baseModelsMapping;
		this.category = this.key = null;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		List<ResourceLocation> dependencies = new ArrayList<ResourceLocation>();
		if(this.baseModelsMapping != null) {
			for(Map<String, Pair<ResourceLocation, IModel>> map : this.baseModelsMapping.values()) {
				dependencies.addAll(map.values().stream().map(pair -> pair.getLeft()).collect(Collectors.toList()));
			}
		}
		return dependencies;
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		List<ResourceLocation> textures = new ArrayList<ResourceLocation>();
		if(this.baseModelsMapping != null) {
			for(Map<String, Pair<ResourceLocation, IModel>> map : this.baseModelsMapping.values()) {
				for(Pair<ResourceLocation, IModel> pair : map.values()) {
					textures.addAll(pair.getRight().getTextures());
				}
			}
		}
		return textures;
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		Map<String, Map<String, IBakedModel>> bakedModelsMapping = new HashMap<>();

		for(Entry<String, Map<String, Pair<ResourceLocation, IModel>>> categoryPair : this.baseModelsMapping.entrySet()) {
			Map<String, IBakedModel> map = new HashMap<>();
			bakedModelsMapping.put(categoryPair.getKey(), map);

			for(Entry<String, Pair<ResourceLocation, IModel>> modelPair : categoryPair.getValue().entrySet()) {
				IBakedModel baked = modelPair.getValue().getRight().bake(state, format, bakedTextureGetter);
				map.put(modelPair.getKey(), baked);
			}
		}

		return new BakedItemMultiPartModel(this.mapper, bakedModelsMapping, this.particleTexture, bakedTextureGetter);
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		Map<String, Map<String, Pair<ResourceLocation, IModel>>> baseModelsMapping = new HashMap<>();

		for(Entry<String, Map<String, Pair<ResourceLocation, IModel>>> entry1 : this.baseModelsMapping.entrySet()) {
			Map<String, Pair<ResourceLocation, IModel>> map = new HashMap<>();
			baseModelsMapping.put(entry1.getKey(), map);

			for(Entry<String, Pair<ResourceLocation, IModel>> entry2 : entry1.getValue().entrySet()) {
				map.put(entry2.getKey(), entry2.getValue());
			}
		}

		JsonParser parser = new JsonParser();

		if(customData.containsKey("map")) {
			Map<String, String> categoryMap = LoaderExtension.parseJsonElementList(parser, customData.get("map"), "map");

			for(Entry<String, String> entry : categoryMap.entrySet()) {
				String category = entry.getKey();
				String partsMapJson = entry.getValue();

				Map<String, Pair<ResourceLocation, IModel>> partsMapping = baseModelsMapping.get(category);
				if(partsMapping == null) {
					baseModelsMapping.put(category, partsMapping = new HashMap<String, Pair<ResourceLocation, IModel>>());
				}

				Map<String, String> partsMap = LoaderExtension.parseJsonElementList(parser, partsMapJson, "");

				for(Entry<String, String> partEntry : partsMap.entrySet()) {
					String part = partEntry.getKey();

					JsonObject partJson = parser.parse(partEntry.getValue()).getAsJsonObject();

					ResourceLocation modelName = new ResourceLocation(JsonUtils.getString(partJson, "model"));
					IModel partModel = ModelLoaderRegistry.getModelOrLogError(modelName, "Could not find model '" + part + "' for multipart model");

					if(partJson.has("custom")) {
						ImmutableMap<String, String> partCustomData = LoaderExtension.parseJsonElementList(partJson);
						partModel = partModel.process(partCustomData);
					}

					partsMapping.put(part, Pair.of(modelName, partModel));
				}
			}
		}

		ResourceLocation particle = this.particleTexture;
		if(customData.containsKey("particle")) {
			particle = new ResourceLocation(customData.get("particle"));
		}

		return new ModelItemMultiPart(this.mapper, baseModelsMapping, particle);
	}

	private class BakedItemMultiPartModel implements IBakedModel {
		private final Map<String, Map<String, IBakedModel>> bakedModelsMapping;

		private final ItemOverrideList overrides;
		private final List<IBakedModel> models;

		private final TextureAtlasSprite sprite;

		public BakedItemMultiPartModel(PartsMapper mapper, Map<String, Map<String, IBakedModel>> bakedModelsMapping, ResourceLocation particleTexture, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
			this.bakedModelsMapping = bakedModelsMapping;
			this.models = null;

			if(particleTexture == null) {
				this.sprite = bakedTextureGetter.apply(TextureMap.LOCATION_MISSING_TEXTURE);;
			} else {
				this.sprite = bakedTextureGetter.apply(particleTexture);
			}

			this.overrides = new ItemOverrideList(ImmutableList.of()) {
				@Override
				public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
					List<IBakedModel> models = new ArrayList<>();

					Map<String, String> partsMapping = mapper.getPartsMapping(stack, world, entity);

					if(partsMapping != null) {
						for(Entry<String, String> entry : partsMapping.entrySet()) {
							Map<String, IBakedModel> partMapping = BakedItemMultiPartModel.this.bakedModelsMapping.get(entry.getKey());

							if(partMapping != null) {
								IBakedModel bakedPart = partMapping.get(entry.getValue());

								if(bakedPart != null) {
									models.add(bakedPart);
								}
							}
						}

						return new BakedItemMultiPartModel(models, BakedItemMultiPartModel.this.sprite);
					}

					return BakedItemMultiPartModel.this;
				}
			};
		}

		private BakedItemMultiPartModel(List<IBakedModel> models, TextureAtlasSprite sprite) {
			this.bakedModelsMapping = null;
			this.models = models;
			this.overrides = ItemOverrideList.NONE;
			this.sprite = sprite;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if(this.models != null) {
				List<BakedQuad> quads = new ArrayList<>();
				for(IBakedModel model : this.models) {
					quads.addAll(model.getQuads(state, side, rand));
				}
				return quads;
			} else {
				return ImmutableList.of();
			}
		}

		@Override
		public boolean isAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return false;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return this.sprite;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return this.overrides;
		}
	}
}
