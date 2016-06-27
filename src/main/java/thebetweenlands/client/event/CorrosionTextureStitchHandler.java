package thebetweenlands.client.event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.ModelLoaderRegistry.LoaderException;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.client.render.sprite.TextureCorrosion;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.ICorrodible;
import thebetweenlands.util.CorrodibleItemHelper;

public class CorrosionTextureStitchHandler {
	//TODO: Mappings!
	private static final Field f_mapRegisteredSprites = ReflectionHelper.findField(TextureMap.class, "mapRegisteredSprites");

	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre e) {
		for (Item item : TheBetweenlands.REGISTRIES.itemRegistry.ITEMS) {
			if (item instanceof ICorrodible) {
				ResourceLocation[] variants = ((ICorrodible)item).getCorrodibleVariants();
				for(ResourceLocation variant : variants) {
					try {
						ResourceLocation modelLocation = new ResourceLocation(variant.getResourceDomain(), "item/" + variant.getResourcePath());
						IModel model = ModelLoaderRegistry.getModel(modelLocation);
						List<ResourceLocation> textures = Lists.newArrayList();
						textures.addAll(model.getTextures());
						List<IModel> dependencies = this.gatherDependencies(model, new ArrayList<IModel>());
						for(IModel dependencyModel : dependencies) {
							Collection<ResourceLocation> dependencyTextures = dependencyModel.getTextures();
							for(ResourceLocation dependencyTexture : dependencyTextures) {
								if(!textures.contains(dependencyTexture))
									textures.add(dependencyTexture);
							}
						}
						for(ResourceLocation texture : textures) {
							String path = texture.getResourcePath();
							if(path.contains("/")) {
								String corrodibleSuffix = "_corrodible";
								String fileName = texture.getResourcePath().substring(texture.getResourcePath().lastIndexOf("/") + 1);
								if(fileName.endsWith(corrodibleSuffix)) {
									ResourceLocation completeBaseTextureLocation = new ResourceLocation(texture.getResourceDomain(), String.format("textures/%s.png", texture.getResourcePath()));
									for (int n = 0; n < CorrodibleItemHelper.CORROSION_STAGE_COUNT; n++) {
										String corrosionSpriteName = "thebetweenlands:items/" + fileName.substring(0, fileName.length() - corrodibleSuffix.length()) + "_corrosion_" + n;
										TextureCorrosion corrosionTexture = new TextureCorrosion(corrosionSpriteName, completeBaseTextureLocation, n, item.getUnlocalizedName().hashCode());
										//Forcibly sets the texture entry because TextureMap#setTextureEntry doesn't allow 
										//overwriting a previously added sprite (usually set in ModelLoader#setupModelRegistry).
										//Maybe find a better way to do this, if at all possible anyways
										Map<String, TextureAtlasSprite> mapRegisteredSprites = (Map<String, TextureAtlasSprite>) f_mapRegisteredSprites.get(e.getMap());
										mapRegisteredSprites.put(corrosionSpriteName, corrosionTexture);
									}
								}
							}
						}
					} catch(Exception ex) {
						throw new RuntimeException("Failed to load corrosion texture", ex);
					}
				}
			}
		}
	}

	/**
	 * Recursively gathers all dependencies of the specified model
	 * @param model
	 * @param foundDependencies
	 * @return
	 */
	private List<IModel> gatherDependencies(IModel model, List<IModel> foundDependencies) {
		Collection<ResourceLocation> dependencies = model.getDependencies();
		for(ResourceLocation dependency : dependencies) {
			try {
				IModel dependencyModel = ModelLoaderRegistry.getModel(dependency);
				if(!foundDependencies.contains(dependencyModel)) {
					foundDependencies.add(dependencyModel);
					this.gatherDependencies(dependencyModel, foundDependencies);
				}
			} catch(LoaderException ex) {
				//Failed to load dependency, ignore
			} catch(Exception ex) {
				//Something else went wrong
				throw new RuntimeException(ex);
			}
		}
		return foundDependencies;
	}
}
