package thebetweenlands.client.render;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;

import thebetweenlands.lib.ModInfo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import cpw.mods.fml.common.ObfuscationReflectionHelper;

public final class PlayerLimbPreMirrorer {
	private static final LoadingCache<AbstractClientPlayer, ResourceLocation> PRE_MIRRORED_SKINS = CacheBuilder.newBuilder()
		.weakKeys()
		.maximumSize(2622)
		.expireAfterAccess(20, TimeUnit.MINUTES)
		.removalListener(new RemovalListener<AbstractClientPlayer, ResourceLocation>() {
			@Override
			public void onRemoval(RemovalNotification<AbstractClientPlayer, ResourceLocation> notification) {
				Minecraft.getMinecraft().getTextureManager().deleteTexture(notification.getValue());
			}
		})
		.build(
			new CacheLoader<AbstractClientPlayer, ResourceLocation>() {
				@Override
				public ResourceLocation load(AbstractClientPlayer key) throws Exception {
					final BufferedImage image = getPlayerSkinImage(key);
					if (image == null) {
						return STEVE_PRE_MIRRORED;
					}
					ResourceLocation resourceLocation = createResourceLocation(key);
					Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, new ITextureObject() {
						private int textureId;

						@Override
						public void loadTexture(IResourceManager resourceManager) throws IOException {
							BufferedImage adjusted = new BufferedImage(TEXTURE_SIZE, TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
							Graphics2D g = adjusted.createGraphics();
							g.drawImage(image, 0, 0, null);
							int[] pixels = ((DataBufferInt) adjusted.getRaster().getDataBuffer()).getData();
							// leg
							mirrorLimb(pixels, 0, 16, 16, 48);
							// arm
							mirrorLimb(pixels, 40, 16, 32, 48);
							textureId = TextureUtil.glGenTextures();
							TextureUtil.uploadTextureImage(textureId, adjusted);
						}

						@Override
						public int getGlTextureId() {
							return textureId;
						}
					});
					return resourceLocation;
				}
			}
		);

	private static final ResourceLocation STEVE_PRE_MIRRORED = new ResourceLocation(ModInfo.ID, "textures/entity/steve_mirror_adjusted.png");

	private static final int TEXTURE_SIZE = 64;

	private PlayerLimbPreMirrorer() {}

	public static ResourceLocation getPlayerSkin(AbstractClientPlayer player) {
		try {
			return PRE_MIRRORED_SKINS.get(player);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return STEVE_PRE_MIRRORED;
	}

	private static BufferedImage getPlayerSkinImage(AbstractClientPlayer player) {
		Minecraft mc = Minecraft.getMinecraft();
		BufferedImage image = null;
		InputStream steveInputStream = null;
		Map<Type, MinecraftProfileTexture> map = mc.func_152342_ad().func_152788_a(player.getGameProfile());
		ITextureObject texture;
		try {
			if (map.containsKey(Type.SKIN)) {
				texture = mc.renderEngine.getTexture(mc.func_152342_ad().func_152792_a(map.get(Type.SKIN), Type.SKIN));
			} else {
				texture = mc.renderEngine.getTexture(player.getLocationSkin());
			}
			if (texture instanceof ThreadDownloadImageData) {
				image = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, (ThreadDownloadImageData) texture, "field_110560_d", "bufferedImage");
			} else if (texture instanceof DynamicTexture) {
				int width = ObfuscationReflectionHelper.getPrivateValue(DynamicTexture.class, (DynamicTexture) texture, "field_94233_j", "width");
				int height = ObfuscationReflectionHelper.getPrivateValue(DynamicTexture.class, (DynamicTexture) texture, "field_94234_k", "height");
				image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				image.setRGB(0, 0, width, height, ((DynamicTexture) texture).getTextureData(), 0, width);
			} else {
				// fallback to pre mirror adjusted steve texture
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(steveInputStream);
		}
		return image;
	}

	private static ResourceLocation createResourceLocation(AbstractClientPlayer player) {
		return new ResourceLocation(ModInfo.ID, "skins_madj/" + player.getLocationSkin().hashCode());
	}

	private static void mirrorLimb(int[] pixels, int sx, int sy, int dx, int dy) {
		// top & bottom
		mirrorX(pixels, sx + 4, sy, dx + 4, dy, 8, 4);
		// front & sides
		mirrorY(pixels, sx, sy + 4, dx, dy + 4, 12, 12);
		// back
		mirrorY(pixels, sx + 12, sy + 4, dx + 12, dy + 4, 4, 12);
	}

	private static void mirrorY(int[] pixels, int sx, int sy, int dx, int dy, int width, int height) {
		for (int y = 0; y < height; y++) {
			int si = sx + (sy + y) * TEXTURE_SIZE;
			int di = dx + (dy + y) * TEXTURE_SIZE;
			for (int x = 0, mx = width - 1; x < width; x++, mx--) {
				pixels[di + mx] = pixels[si + x];
			}
		}
	}

	private static void mirrorX(int[] pixels, int sx, int sy, int dx, int dy, int width, int height) {
		for (int y = 0, my = height - 1; y < height; y++, my--) {
			int si = sx + (sy + y) * TEXTURE_SIZE;
			int di = dx + (dy + my) * TEXTURE_SIZE;
			for (int x = 0; x < width; x++) {
				pixels[di + x] = pixels[si + x];
			}
		}
	}
}
