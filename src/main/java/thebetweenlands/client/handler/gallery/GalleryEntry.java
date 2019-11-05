package thebetweenlands.client.handler.gallery;

import java.io.File;
import java.io.FileInputStream;

import javax.annotation.Nullable;

import org.apache.commons.codec.digest.DigestUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.sprite.TextureGalleryEntry;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.lib.ModInfo;

public class GalleryEntry {
	private String sha256, url, title, author;

	@Nullable
	private String description;

	@Nullable
	private String localSha256;

	@Nullable
	private String sourceUrl;

	private File pictureFile;

	private volatile int width = 1, height = 1;
	private volatile boolean isUploaded;

	public GalleryEntry(String sha256, String url, String title, String author, @Nullable String description, @Nullable String sourceUrl, File pictureFile) {
		this.sha256 = sha256.toLowerCase();
		this.url = url;
		this.title = title;
		this.author = author;
		this.description = description;
		this.sourceUrl = sourceUrl;
		this.pictureFile = pictureFile;
	}

	public void setUploaded(int width, int height) {
		this.isUploaded = true;
		this.width = width;
		this.height = height;
	}

	/**
	 * Returns whether the pictures has been uploaded to the GPU
	 * @return
	 */
	public boolean isUploaded() {
		return this.isUploaded;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public ResourceLocation getLocation() {
		return new ResourceLocation(ModInfo.ID, "online_gallery_" + this.sha256);
	}

	public boolean loadTexture() {
		TextureManager manager = Minecraft.getMinecraft().getTextureManager();

		ITextureObject texture = manager.getTexture(this.getLocation());

		if(texture == null) {
			TheBetweenlands.logger.info("Loading gallery picture '" + this.getSha256() + "'/'" + this.getUrl() + "'/'" + this.getLocation() + "'");
			return manager.loadTexture(this.getLocation(), new TextureGalleryEntry(this));
		}

		return texture instanceof TextureGalleryEntry;
	}

	public ResourceLocation loadTextureAndGetLocation(ResourceLocation fallback) {
		if(this.loadTexture()) {
			return this.getLocation();
		}
		return fallback;
	}

	public File getPictureFile() {
		return this.pictureFile;
	}

	private void computeLocalPictureSha256() {
		this.localSha256 = null;

		if(this.pictureFile.exists()) {
			try(FileInputStream fio = new FileInputStream(this.pictureFile)) {
				this.localSha256 = DigestUtils.sha256Hex(fio).toLowerCase();
			} catch(Exception ex) {
				if(BetweenlandsConfig.DEBUG.debug) TheBetweenlands.logger.info(String.format("Failed computing SHA256 hash of gallery picture: %s", this.pictureFile.toString()), ex);
			}
		}
	}

	/**
	 * Locally computed sha256 hash of the picture for file integrity check or checking for changes.
	 * Null if the file could not be read or does not exist.
	 * @return
	 */
	@Nullable
	public String getLocalSha256() {
		if(this.localSha256 == null) {
			this.computeLocalPictureSha256();
		}
		return this.localSha256;
	}

	/**
	 * Original sha256 hash of the picture when it was downloaded for file integrity check or checking for changes
	 * @return
	 */
	public String getSha256() {
		return this.sha256;
	}

	/**
	 * URL of the picture to be downloaded.
	 * Also used as unique identifier of the entry.
	 * @return
	 */
	public String getUrl() {
		return this.url;
	}

	public String getTitle()  {
		return this.title;
	}

	public String getAuthor() {
		return this.author;
	}

	/**
	 * Description for the picture.
	 * Optional, may be null.
	 * @return
	 */
	@Nullable
	public String getDescription() {
		return this.description;
	}

	/**
	 * URL to the author or original source.
	 * Optional, may be null.
	 * @return
	 */
	@Nullable
	public String getSourceUrl() {
		return this.sourceUrl;
	}
}
