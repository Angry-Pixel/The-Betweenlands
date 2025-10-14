package thebetweenlands.client.handler.gallery;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.FileInputStream;
import java.io.IOException;

public class GalleryTexture extends AbstractTexture {

	public final GalleryEntry entry;

	public GalleryTexture(GalleryEntry entry) {
		this.entry = entry;
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		this.releaseId();

		try (FileInputStream fio = new FileInputStream(this.entry.getPictureFile())) {
			NativeImage image = NativeImage.read(fio);
			if (image.getWidth() > 0 && image.getHeight() > 0) {
				TextureUtil.prepareImage(this.getId(), image.getWidth(), image.getHeight());
				this.entry.setUploaded(image.getWidth(), image.getHeight());
				image.upload(0, 0, 0, true);
			} else {
				throw new IOException("Gallery picture could not be loaded properly");
			}
		}
	}
}
