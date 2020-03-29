package thebetweenlands.client.render.model.baked;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import thebetweenlands.client.render.model.loader.extension.LoaderExtension;
import thebetweenlands.common.item.misc.ItemMob;

public class ModelMobItem implements IModel {
	private final Map<ResourceLocation, ResourceLocation> modelMap;

	public ModelMobItem() {
		this.modelMap = new HashMap<>();
	}

	protected ModelMobItem(Map<ResourceLocation, ResourceLocation> modelMap) {
		this.modelMap = modelMap;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.unmodifiableCollection(this.modelMap.values());
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
		return new ModelBakedMobItem(this.modelMap, bakedTextureGetter.apply(TextureMap.LOCATION_MISSING_TEXTURE));
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		if(customData.containsKey("map")) {
			Map<ResourceLocation, ResourceLocation> newModelMap = new HashMap<>();
			newModelMap.putAll(this.modelMap);

			JsonParser parser = new JsonParser();

			Map<String, String> jsonMap = LoaderExtension.parseJsonElementList(parser, customData.get("map"), "map");

			for(Entry<String, String> entry : jsonMap.entrySet()) {
				newModelMap.put(new ResourceLocation(entry.getKey()), new ResourceLocation(parser.parse(entry.getValue()).getAsString()));
			}

			return new ModelMobItem(newModelMap);
		}

		return this;
	}

	private static class ModelBakedMobItem implements IBakedModel {
		private final Map<ResourceLocation, ResourceLocation> modelMap;
		private final TextureAtlasSprite missingTexture;

		private final ItemOverrideList overrides;

		private ModelBakedMobItem(Map<ResourceLocation, ResourceLocation> modelMap, TextureAtlasSprite missingTexture) {
			this.modelMap = modelMap;
			this.missingTexture = missingTexture;

			this.overrides = new ItemOverrideList(Collections.emptyList()) {
				@Override
				public ResourceLocation applyOverride(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
					if(stack.getItem() instanceof ItemMob) {
						ResourceLocation id = ((ItemMob) stack.getItem()).getCapturedEntityId(stack);
						ResourceLocation modelLocation = ModelBakedMobItem.this.modelMap.get(id);
						return modelLocation;
					}

					return null;
				}
			};
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			return ImmutableList.of();
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
			return this.missingTexture;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return this.overrides;
		}
	}
}
