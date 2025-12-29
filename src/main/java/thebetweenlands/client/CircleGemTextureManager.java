package thebetweenlands.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ArmorMaterial;
import org.apache.commons.lang3.function.TriFunction;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CircleGemTextureManager extends TextureAtlasHolder {

	private static final BiFunction<CircleGemType, Boolean, ResourceLocation> DEFAULT_TEXTURE_BUILDER = (type, bool) -> TheBetweenlands.prefix("textures/circle_gems/model/default_" + type.name + "_" + (bool ? 2 : 1) + ".png");
	private static final Function<CircleGemType, MiddleGemTexture> DEFAULT_GEM_TEXTURE = (type) -> new MiddleGemTexture(DEFAULT_TEXTURE_BUILDER.apply(type, false), DEFAULT_TEXTURE_BUILDER.apply(type, true));
	private static final TriFunction<Holder<ArmorMaterial>, CircleGemType, Boolean, ResourceLocation> TEXTURE_BUILDER = (material, type, bool) -> ResourceLocation.fromNamespaceAndPath(material.getKey().location().getNamespace(), "textures/circle_gems/model/" + material.getKey().location().getPath()+ "_" + type.name + "_" + (bool ? 2 : 1) + ".png");

	private static final Map<Holder<ArmorMaterial>, MiddleGemTextures> ARMOR_MODEL_TEXTURES = new HashMap<>();
	public static final ResourceLocation ATLAS_LOC = TheBetweenlands.prefix("circle_gems");

	public CircleGemTextureManager(TextureManager textureManager) {
		super(textureManager, TheBetweenlands.prefix("textures/atlas/circle_gems.png"), ATLAS_LOC);
	}

	@Override
	public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		return super.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor).thenAcceptAsync(v -> {
			ARMOR_MODEL_TEXTURES.clear();
			this.reloadQuickLookupMap(resourceManager);
		});
	}

	private void reloadQuickLookupMap(ResourceManager manager) {
		for (Holder<ArmorMaterial> material : BuiltInRegistries.ARMOR_MATERIAL.holders().toList()) {
			MiddleGemTexture crimson = DEFAULT_GEM_TEXTURE.apply(CircleGemType.CRIMSON);
			MiddleGemTexture aqua = DEFAULT_GEM_TEXTURE.apply(CircleGemType.AQUA);
			MiddleGemTexture green = DEFAULT_GEM_TEXTURE.apply(CircleGemType.GREEN);
			for (CircleGemType gem : CircleGemType.values()) {
				if (gem == CircleGemType.NONE) continue;
				ResourceLocation overrideOuterTex = TEXTURE_BUILDER.apply(material, gem, false);
				ResourceLocation overrideInnerTex = TEXTURE_BUILDER.apply(material, gem, true);
				if (manager.getResource(overrideOuterTex).isPresent() && manager.getResource(overrideInnerTex).isPresent()) {
					switch (gem) {
						case AQUA -> aqua = new MiddleGemTexture(overrideOuterTex, overrideInnerTex);
						case CRIMSON -> crimson = new MiddleGemTexture(overrideOuterTex, overrideInnerTex);
						case GREEN -> green = new MiddleGemTexture(overrideOuterTex, overrideInnerTex);
					}
				}
			}
			ARMOR_MODEL_TEXTURES.put(material, new MiddleGemTextures(crimson, green, aqua));
		}
	}

	public TextureAtlasSprite getForItem(ResourceLocation texture) {
		return this.getSprite(texture);
	}

	public ResourceLocation getForMaterial(Holder<ArmorMaterial> material, CircleGemType type, boolean innerModel) {
		MiddleGemTextures textures = ARMOR_MODEL_TEXTURES.get(material);
		return switch (type) {
			case AQUA -> innerModel ? textures.aqua().inner() : textures.aqua().outer();
			case GREEN ->  innerModel ? textures.green().inner() : textures.green().outer();
			default ->  innerModel ? textures.crimson().inner() : textures.crimson().outer();
		};
	}

	public record MiddleGemTextures(MiddleGemTexture crimson, MiddleGemTexture green, MiddleGemTexture aqua) {

	}

	public record MiddleGemTexture(ResourceLocation outer, ResourceLocation inner) {

	}
}
