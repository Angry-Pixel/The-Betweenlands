package thebetweenlands.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.client.IItemDecorator;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.CircleGemTextureManager;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.registries.DataComponentRegistry;

import java.util.HashMap;
import java.util.Map;

public class CircleGemItemOverlay implements IItemDecorator {

	private static final Map<Item, MiddleGemTextures> GEM_CACHE = new HashMap<>();

	@Override
	public boolean render(GuiGraphics graphics, Font font, ItemStack stack, int xOffset, int yOffset) {
		if (stack.has(DataComponentRegistry.CIRCLE_GEM) && stack.get(DataComponentRegistry.CIRCLE_GEM) != CircleGemType.NONE) {
			MiddleGemTextures textures = GEM_CACHE.computeIfAbsent(stack.getItem(), item -> {
				ResourceLocation crimson = null;
				ResourceLocation green = null;
				ResourceLocation aqua = null;
				ResourceManager manager = Minecraft.getInstance().getResourceManager();
				for (CircleGemType gem : CircleGemType.values()) {
					if (gem == CircleGemType.NONE) continue;
					ResourceLocation checkTex = item.builtInRegistryHolder().key().location().withPrefix("").withSuffix("_" + gem.getSerializedName());
					if (manager.getResource(checkTex.withPrefix("textures/circle_gems/item/").withSuffix(".png")).isPresent()) {
						switch (gem) {
							case AQUA -> aqua = checkTex;
							case CRIMSON -> crimson = checkTex;
							case GREEN -> green = checkTex;
						}
					} else {
						if (stack.getItem() instanceof ArmorItem armor) {
							switch (gem) {
								case AQUA -> aqua = TheBetweenlands.prefix("default_" + armor.getType().getName() + "_" + gem.getSerializedName());
								case CRIMSON -> crimson = TheBetweenlands.prefix("default_" + armor.getType().getName() + "_" + gem.getSerializedName());
								case GREEN -> green = TheBetweenlands.prefix("default_" + armor.getType().getName() + "_" + gem.getSerializedName());
							}
						}
					}
				}
				return new MiddleGemTextures(crimson, green, aqua);
			});
			switch (stack.get(DataComponentRegistry.CIRCLE_GEM)) {
				case AQUA -> {
					if (textures.aqua() != null) {
						TextureAtlasSprite sprite = BetweenlandsClient.getCircleGemManager().getForItem(textures.aqua());
						graphics.blit(xOffset, yOffset, 200, sprite.contents().width(), sprite.contents().height(), sprite);
					}
				}
				case CRIMSON -> {
					if (textures.crimson() != null) {
						TextureAtlasSprite sprite = BetweenlandsClient.getCircleGemManager().getForItem(textures.crimson());
						graphics.blit(xOffset, yOffset, 200, sprite.contents().width(), sprite.contents().height(), sprite);
					}
				}
				case GREEN -> {
					if (textures.green() != null) {
						TextureAtlasSprite sprite = BetweenlandsClient.getCircleGemManager().getForItem(textures.green());
						graphics.blit(xOffset, yOffset, 200, sprite.contents().width(), sprite.contents().height(), sprite);
					}
				}
				//should never be hit but just wanted to shut the IDE up
				case null, default -> {}
			}

		}

		return false;
	}

	public record MiddleGemTextures(@Nullable ResourceLocation crimson, @Nullable ResourceLocation green, @Nullable ResourceLocation aqua) {}
}
