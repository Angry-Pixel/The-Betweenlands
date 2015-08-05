package thebetweenlands.manager;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class TextureManager {
	private static Minecraft mc = Minecraft.getMinecraft();

	public static BufferedImage getPlayerSkin(AbstractClientPlayer player) {
		BufferedImage bufferedImage = null;
		InputStream inputStream = null;
		Map map = mc.func_152342_ad().func_152788_a(player.getGameProfile());
		ITextureObject texture;

		try {
			if (map.containsKey(Type.SKIN))
				texture = mc.renderEngine.getTexture(mc.func_152342_ad().func_152792_a((MinecraftProfileTexture) map.get(Type.SKIN), Type.SKIN));
			else texture = mc.renderEngine.getTexture(player.getLocationSkin());

			if (texture instanceof ThreadDownloadImageData) {
				bufferedImage = ObfuscationReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, (ThreadDownloadImageData) texture, "field_110560_d", "bufferedImage");
			} else if (texture instanceof DynamicTexture) {
				int width = ObfuscationReflectionHelper.getPrivateValue(DynamicTexture.class, (DynamicTexture) texture, "field_94233_j", "width");
				int height = ObfuscationReflectionHelper.getPrivateValue(DynamicTexture.class, (DynamicTexture) texture, "field_94234_k", "height");
				bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				bufferedImage.setRGB(0, 0, width, height, ((DynamicTexture) texture).getTextureData(), 0, width);
			} else {
				inputStream = mc.getResourceManager().getResource(AbstractClientPlayer.locationStevePng).getInputStream();
				bufferedImage = ImageIO.read(inputStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return bufferedImage;
	}

	public static void uploadTexture(ITextureObject textureObject, BufferedImage bufferedImage) {
		TextureUtil.uploadTextureImage(textureObject.getGlTextureId(), bufferedImage);
	}
}
