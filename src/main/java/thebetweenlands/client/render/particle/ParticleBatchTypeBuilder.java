package thebetweenlands.client.render.particle;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBatchTypeBuilder {
	public static class Pass {
		private final ParticleBatchTypeBuilder builder;

		private int maxParticles = 8192;
		private int batchSize = 8192;
		private boolean setFog = true;
		private boolean fog = true;
		private boolean setCull = true;
		private boolean cull = true;
		private boolean setDepthTest = true;
		private boolean depthTest = true;
		private boolean setDepthMask = true;
		private boolean depthMask = true;
		private boolean setColorMask = true;
		private boolean colorMaskR = true;
		private boolean colorMaskG = true;
		private boolean colorMaskB = true;
		private boolean colormaskA = true;
		private boolean depthMaskPass = false;
		private boolean setLit = true;
		private boolean lit = false;
		private VertexFormat format = DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP;
		private int glPrimitive = GL11.GL_QUADS;
		private boolean setBlend = true;
		private boolean blend = true;
		private GlStateManager.SourceFactor glBlendSrc = GlStateManager.SourceFactor.SRC_ALPHA;
		private GlStateManager.DestFactor glBlendDst = GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA;
		private boolean setTexture = true;
		private Supplier<ResourceLocation> texture = () -> TextureMap.LOCATION_BLOCKS_TEXTURE;
		private boolean setBlur = true;
		private boolean blur = false;
		private boolean setMipmap = true;
		private boolean mipmap = false;
		private Runnable preRenderPassCallback = null;
		private Runnable postRenderPassCallback = null;

		private Pass(ParticleBatchTypeBuilder builder) {
			this.builder = builder;
		}

		public Pass preRenderPassCallback(@Nullable Runnable callback) {
			this.preRenderPassCallback = callback;
			return this;
		}

		public Pass postRenderPassCallback(@Nullable Runnable callback) {
			this.postRenderPassCallback = callback;
			return this;
		}

		public Pass maxParticles(int maxParticles) {
			this.maxParticles = maxParticles;
			return this;
		}

		public Pass batchSize(int batchSize) {
			this.batchSize = batchSize;
			return this;
		}

		public Pass setCull(boolean set) {
			this.setCull = set;
			return this;
		}

		public Pass cull(boolean cull) {
			this.cull = cull;
			return this;
		}

		public Pass setDepthTest(boolean set) {
			this.setDepthTest = set;
			return this;
		}

		public Pass depthTest(boolean depth) {
			this.depthTest = depth;
			return this;
		}

		public Pass setDepthMask(boolean set) {
			this.setDepthMask = set;
			return this;
		}

		public Pass depthMask(boolean mask) {
			this.depthMask = mask;
			return this;
		}

		public Pass setColorMask(boolean set) {
			this.setColorMask = set;
			return this;
		}

		public Pass colorMask(boolean r, boolean g, boolean b, boolean a) {
			this.colorMaskR = r;
			this.colorMaskG = b;
			this.colorMaskB = b;
			this.colormaskA = a;
			return this;
		}

		public Pass depthMaskPass(boolean depthMaskPass) {
			this.depthMaskPass = depthMaskPass;
			return this;
		}

		public Pass setLit(boolean set) {
			this.setLit = set;
			return this;
		}

		public Pass lit(boolean lit) {
			this.lit = lit;
			return this;
		}

		public Pass setFog(boolean set) {
			this.setFog = set;
			return this;
		}

		public Pass fog(boolean fog) {
			this.fog = fog;
			return this;
		}

		public Pass format(VertexFormat format) {
			this.format = format;
			return this;
		}

		public Pass primitive(int glPrimitive) {
			this.glPrimitive = glPrimitive;
			return this;
		}

		public Pass setBlend(boolean set) {
			this.setBlend = set;
			return this;
		}

		public Pass blend(boolean blend) {
			this.blend = blend;
			return this;
		}

		public Pass blend(GlStateManager.SourceFactor src, GlStateManager.DestFactor dst) {
			this.glBlendSrc = src;
			this.glBlendDst = dst;
			return this;
		}

		public Pass setTexture(boolean set) {
			this.setTexture = set;
			return this;
		}

		public Pass texture(@Nullable ResourceLocation texture) {
			this.texture = texture != null ? (() -> texture) : null;
			return this;
		}

		public Pass texture(@Nullable Supplier<ResourceLocation> texture) {
			this.texture = texture;
			return this;
		}

		public Pass setBlur(boolean set) {
			this.setBlur = set;
			return this;
		}

		public Pass blur(boolean blur) {
			this.blur = blur;
			return this;
		}

		public Pass setMipmap(boolean set) {
			this.setMipmap = set;
			return this;
		}

		public Pass mipmap(boolean mipmap) {
			this.mipmap = mipmap;
			return this;
		}

		public ParticleBatchTypeBuilder end() {
			return this.builder;
		}
	}

	private List<Pass> passes = new ArrayList<>();
	private Pass mainPass;
	private Predicate<Particle> filter;

	public Pass pass() {
		return this.pass(true);
	}

	public Pass pass(boolean main) {
		Pass pass = new Pass(this);
		if(main) {
			this.mainPass = pass;
		}
		this.passes.add(pass);
		return pass;
	}

	public ParticleBatchTypeBuilder filter(@Nullable Predicate<Particle> filter) {
		this.filter = filter;
		return this;
	}

	public BatchedParticleRenderer.ParticleBatchType build() {
		Preconditions.checkNotNull(this.mainPass, "Particle batch type requires at least one pass");

		final BatchedParticleRenderer.ParticleBatchType type = new BatchedParticleRenderer.ParticleBatchType() {
			private ResourceLocation boundTexture = null;

			@Override
			public boolean filter(Particle particle) {
				if(filter != null) {
					return filter.test(particle);
				}
				return true;
			}

			@Nullable
			protected ResourceLocation preSetup(Pass pass) {
				if(pass.preRenderPassCallback != null) {
					pass.preRenderPassCallback.run();
				}

				if(pass.setCull) { 
					if(pass.cull) {
						GlStateManager.enableCull();
					} else {
						GlStateManager.disableCull();
					}
				}

				if(pass.setDepthTest) {
					if(pass.depthTest) {
						GlStateManager.enableDepth();
					} else {
						GlStateManager.disableDepth();
					}
				}

				if(pass.setDepthMask) {
					if(pass.depthMask) {
						GlStateManager.depthMask(true);
					} else {
						GlStateManager.depthMask(false);
					}
				}

				if(pass.setColorMask) {
					GlStateManager.colorMask(pass.colorMaskR, pass.colorMaskG, pass.colorMaskB, pass.colormaskA);
				}

				if(pass.setLit) { 
					if(pass.lit) {
						Minecraft.getMinecraft().entityRenderer.enableLightmap();
					} else {
						Minecraft.getMinecraft().entityRenderer.disableLightmap();
					}
				}

				if(pass.setFog) { 
					if(pass.fog) {
						GlStateManager.enableFog();
					} else {
						GlStateManager.disableFog();
					}
				}

				if(pass.setBlend) {
					if(pass.blend) {
						GlStateManager.enableBlend();
					} else {
						GlStateManager.disableBlend();
					}

					GlStateManager.blendFunc(pass.glBlendSrc, pass.glBlendDst);
				}

				if(pass.texture != null && pass.setTexture) {
					ResourceLocation texLoc = pass.texture.get();

					if(texLoc != null) {
						Minecraft.getMinecraft().getTextureManager().bindTexture(texLoc);

						if(pass.setBlur) {
							ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(texLoc);
							if(tex != null) {
								tex.setBlurMipmap(pass.blur, pass.mipmap);
							}
						}
					}

					return texLoc;
				}

				return null;
			}

			protected void postSetup(Pass pass, @Nullable ResourceLocation texLoc) {
				if(pass.postRenderPassCallback != null) {
					pass.postRenderPassCallback.run();
				}

				if(texLoc != null && pass.setTexture) {
					Minecraft.getMinecraft().getTextureManager().bindTexture(texLoc);

					if(pass.setBlur) {
						ITextureObject tex = Minecraft.getMinecraft().getTextureManager().getTexture(texLoc);
						if(tex != null) {
							tex.restoreLastBlurMipmap();
						}
					}
				}
			}

			@Override
			protected void preRender(Tessellator tessellator, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
				this.boundTexture = this.preSetup(mainPass);

				tessellator.getBuffer().begin(mainPass.glPrimitive, mainPass.format);
			}

			@Override
			protected void postRender(Tessellator tessellator, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
				this.postSetup(mainPass, this.boundTexture);

				boolean firstPass = true;

				BufferBuilder buffer = tessellator.getBuffer();

				final VertexFormat format = buffer.getVertexFormat();
				final ByteBuffer byteBuffer = buffer.getByteBuffer();
				final int drawMode = buffer.getDrawMode();
				final int vertexCount = buffer.getVertexCount();

				for(Pass pass : passes) {
					ResourceLocation localBoundTex = this.preSetup(pass);

					if(pass.depthMask && pass.depthMaskPass) {
						//Render in two passes by rerendering the byte buffer directly

						GlStateManager.depthMask(false);
						if(firstPass) {
							tessellator.draw();
						} else {
							this.redraw(format, vertexCount, drawMode, byteBuffer);
						}

						GlStateManager.depthMask(true);
						GlStateManager.colorMask(false, false, false, false);
						this.redraw(format, vertexCount, drawMode, byteBuffer);
						GlStateManager.colorMask(true, true, true, true);
					} else {
						if(firstPass) {
							tessellator.draw();
						} else {
							this.redraw(format, vertexCount, drawMode, byteBuffer);
						}
					}

					this.postSetup(pass, localBoundTex);

					firstPass = false;
				}
			}

			private void redraw(VertexFormat vertexformat, int vertexCount, int drawMode, ByteBuffer byteBuffer) {
				//Modified from WorldVertexBufferUploader#draw(BufferBuilder)

				byteBuffer.position(0);

				if(vertexCount > 0) {
					int i = vertexformat.getSize();
					List<VertexFormatElement> list = vertexformat.getElements();

					for (int j = 0; j < list.size(); ++j) {
						VertexFormatElement vertexformatelement = list.get(j);
						byteBuffer.position(vertexformat.getOffset(j));

						// moved to VertexFormatElement.preDraw
						vertexformatelement.getUsage().preDraw(vertexformat, j, i, byteBuffer);
					}

					GlStateManager.glDrawArrays(drawMode, 0, vertexCount);
					int i1 = 0;

					for (int j1 = list.size(); i1 < j1; ++i1) {
						VertexFormatElement vertexformatelement1 = list.get(i1);

						// moved to VertexFormatElement.postDraw
						vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, byteBuffer);
					}
				}
			}

			@Override
			public int maxParticles() {
				return mainPass.maxParticles;
			}

			@Override
			public int batchSize() {
				return mainPass.batchSize;
			}
		};

		return type;
	}
}
