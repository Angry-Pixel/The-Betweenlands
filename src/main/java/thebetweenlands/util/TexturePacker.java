package thebetweenlands.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class TexturePacker {
	public static interface ITexturePackable {
		public void onPacked();
	}

	public static class TextureQuadMap {
		private final List<TextureQuad> quads;
		private final ITexturePackable owner;

		public TextureQuadMap(List<TextureQuad> quads, @Nullable ITexturePackable owner) {
			this.quads = quads;
			this.owner = owner;
		}

		public List<TextureQuad> getQuads() {
			return this.quads;
		}

		@Nullable
		public ITexturePackable getOwner() {
			return this.owner;
		}
	}

	public static class TextureQuad {
		private final ResourceLocation texture;
		private final int u, v, w, h;
		private double packedU, packedV, packedMaxU, packedMaxV;
		private ResourceLocation packedLocation;

		public TextureQuad(ResourceLocation texture, int u, int v, int width, int height) {
			this.texture = texture;
			this.u = u;
			this.v = v;
			this.w = width;
			this.h = height;
		}

		public double getPackedU() {
			return this.packedU;
		}

		public double getPackedV() {
			return this.packedV;
		}

		public double getPackedMaxU() {
			return this.packedMaxU;
		}

		public double getPackedMaxV() {
			return this.packedMaxV;
		}

		public ResourceLocation getPackedLocation() {
			return this.packedLocation;
		}
	}

	private static class TextureBin {
		private static class Space {
			private int x, y, w, h;

			private Space(int x, int y, int w, int h) {
				this.x = x;
				this.y = y;
				this.w = w;
				this.h = h;
			}
		}

		private final ResourceLocation location;
		private final BufferedImage image;
		private final int width, height;
		private final List<Space> spaces = new ArrayList<>();

		protected TextureBin(ResourceLocation location, int width, int height) {
			this.location = location;
			this.width = width;
			this.height = height;
			this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			this.spaces.add(new Space(0, 0, width, height));
		}
	}

	private final ResourceLocation textureName;
	private final List<TextureQuadMap> textureMaps;

	private final Map<ResourceLocation, BufferedImage> cachedTextures = new HashMap<>();

	private final List<TextureBin> textureBins = new ArrayList<>();

	private int packedTextureID = 0;

	private final BufferedImage missingTexture;

	private int optimalFootprint;
	private int packedFootprint;

	public TexturePacker(ResourceLocation textureName) {
		this(textureName, new ArrayList<>());
	}

	public TexturePacker(ResourceLocation textureName, List<TextureQuadMap> textureMaps) {
		this.textureName = textureName;
		this.textureMaps = textureMaps;

		this.missingTexture = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		System.arraycopy(TextureUtil.MISSING_TEXTURE.getTextureData(), 0, ((DataBufferInt) this.missingTexture.getRaster().getDataBuffer()).getData(), 0, 16*16);
	}

	public void addTextureMap(TextureQuadMap map) {
		this.textureMaps.add(map);
	}

	public List<TextureQuadMap> getTextureMaps() {
		return this.textureMaps;
	}

	protected ResourceLocation generateNewTextureLocation() {
		ResourceLocation rl = new ResourceLocation(this.textureName.getNamespace(), this.textureName.getPath() + "_" + this.packedTextureID);
		this.packedTextureID++;
		return rl;
	}

	protected BufferedImage getOrLoadTexture(ResourceLocation location, IResourceManager manager) {
		BufferedImage cached = this.cachedTextures.get(location);
		if(cached == null) {
			try(IResource resource = manager.getResource(new ResourceLocation(location.getNamespace(), "textures/" + location.getPath() + ".png"))) {
				this.cachedTextures.put(location, cached = ImageIO.read(resource.getInputStream()));
			} catch (IOException e) {
				TheBetweenlands.logger.error("Failed loading model texture to pack. Location: " + location, e);
				this.cachedTextures.put(location, cached = this.missingTexture);
			}
		}
		return cached;
	}

	public Map<ResourceLocation, BufferedImage> pack(IResourceManager manager) {
		this.optimalFootprint = 0;
		this.packedFootprint = 0;

		List<TextureQuad> quads = new ArrayList<>();
		for(TextureQuadMap map : this.textureMaps) {
			quads.addAll(map.getQuads());
		}

		Collections.sort(quads, (q1, q2) -> q2.h - q1.h);

		for(TextureQuad quad : quads) {
			this.optimalFootprint += quad.w * quad.h;

			boolean packed = false;

			for(TextureBin bin : this.textureBins) {
				if(this.packIntoBin(quad, bin, manager)) {
					packed = true;
					break;
				}
			}

			if(!packed) {
				int dimension = Math.max(16, Math.max(quad.w, quad.h));
				int binSize = 1 << (32 - Integer.numberOfLeadingZeros(dimension - 1)); //Round to next power of 2

				TextureBin bin = new TextureBin(this.generateNewTextureLocation(), binSize, binSize);

				this.textureBins.add(bin);

				this.packedFootprint += binSize * binSize;

				if(!this.packIntoBin(quad, bin, manager)) {
					throw new RuntimeException("Was unable to pack texture quad into new bin. This should not happen!");
				}
			}
		}

		Map<ResourceLocation, BufferedImage> packedTextures = new HashMap<>();

		for(TextureBin bin : this.textureBins) {
			packedTextures.put(bin.location, bin.image);
		}

		this.packedTextureID = 0;
		this.cachedTextures.clear();
		this.textureBins.clear();

		return packedTextures;
	}

	protected static void copySubImage(BufferedImage source, int x, int y, int w, int h, BufferedImage dest, int x2, int y2) {
		WritableRaster srcRaster = source.getRaster();
		WritableRaster destRaster = dest.getRaster();

		for(int py = 0; py < h; py++) {
			for(int px = 0; px < w; px++) {
				for(int i = 0; i < srcRaster.getNumBands(); i++) {
					int sx = x + px;
					if(sx < 0) {
						sx = source.getWidth() + sx % source.getWidth();
					} else {
						sx = sx % source.getWidth();
					}
					int sy = y + py;
					if(sy < 0) {
						sy = source.getHeight() + sy % source.getHeight();
					} else {
						sy = sy % source.getHeight();
					}
					destRaster.setSample(x2 + px, y2 + py, i, srcRaster.getSample(sx, sy, i));
				}
			}
		}
	}

	protected boolean packIntoBin(TextureQuad quad, TextureBin bin, IResourceManager manager) {
		List<TextureBin.Space> spaces = bin.spaces;

		for(int i = spaces.size() - 1; i >= 0; i--) {
			TextureBin.Space space = spaces.get(i);

			if(quad.w > space.w || quad.h > space.h) {
				continue;
			}

			int packedU = space.x;
			int packedV = space.y;

			BufferedImage sourceImage = this.getOrLoadTexture(quad.texture, manager);
			copySubImage(sourceImage, quad.u, quad.v, quad.w, quad.h, bin.image, packedU, packedV);

			quad.packedU = packedU / (double) bin.width;
			quad.packedV = packedV / (double) bin.height;
			quad.packedMaxU = (packedU + quad.w) / (double) bin.width;
			quad.packedMaxV = (packedV + quad.h) / (double) bin.height;
			quad.packedLocation = bin.location;

			if(quad.w == space.w && quad.h == space.h) {
				spaces.remove(i);
			} else if(quad.h == space.h) {
				space.x += quad.w;
				space.w -= quad.w;
			} else if(quad.w == space.w) {
				space.y += quad.h;
				space.h -= quad.h;
			} else {
				spaces.add(new TextureBin.Space(space.x + quad.w, space.y, space.w - quad.w, quad.h));
				space.y += quad.h;
				space.h -= quad.h;
			}

			return true;
		}

		return false;
	}

	public int getOptimalFootprint() {
		return this.optimalFootprint;
	}

	public int getPackedFootprint() {
		return this.packedFootprint;
	}
}
