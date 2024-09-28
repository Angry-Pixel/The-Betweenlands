package thebetweenlands.client.gui.overlay.swarm;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import thebetweenlands.common.TheBetweenlands;

public class Crawler {
	public float updateCounter = 0;
	private float prevPosX;
	private float prevPosY;
	private float posX;
	private float posY;
	private float motionY;
	private float motionX;
	private final RandomSource random = RandomSource.create();
	private float rotateBias;
	private float rotation;
	private float prevHurtRotation;
	private float hurtRotation;
	private float hurtTint;
	private float alpha = 1;
	private final float scale;
	private final int hurtDir;
	private final TextureAtlasSprite sprite;

	public boolean dead;
	public boolean dropping;

	public Crawler(float x, float y, float mX, float mY) {
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.motionX = mX;
		this.motionY = mY;
		this.hurtDir = this.random.nextBoolean() ? 1 : -1;
		this.sprite = Minecraft.getInstance().particleEngine.spriteSets.get(TheBetweenlands.prefix("swarm")).get(this.random);
		this.scale = this.sprite.contents().name().equals(TheBetweenlands.prefix("swarm_4")) ? 2.0F : 1.0F;
	}

	public void update(int hurtTicks) {
		this.prevHurtRotation = this.hurtRotation;
		if(hurtTicks > 0) {
			this.hurtRotation = (float)Math.pow((hurtTicks / 10.0f) - 0.5f, 5) * 16 * Mth.PI;
			this.hurtTint = hurtTicks / 10.0f;
		} else {
			this.hurtRotation = 0;
			this.hurtTint = 0;
		}

		this.updateCounter++;

		if(this.updateCounter > 80) {
			this.dead = true;
		} else if(this.updateCounter > 70) {
			this.alpha = (80 - this.updateCounter) / 10.0f;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;

		this.posY += this.motionY;
		this.posX += this.motionX;

		double speed = Mth.sqrt(this.motionX * this.motionX + this.motionY * this.motionY);

		Vec3 normal = new Vec3(0, 0, 1);
		Vec3 motion = new Vec3(this.motionX, this.motionY, 0);
		Vec3 side = motion.normalize().cross(normal);

		if(this.random.nextInt(10) == 0) {
			this.rotateBias = (this.random.nextFloat() - 0.5f) * 0.1f;
		}

		Vec3 newMotion = motion.add(side.scale(speed * ((this.random.nextFloat() - 0.5f) * 0.5f + this.rotateBias))).normalize().scale(speed);
		this.motionX = (float) newMotion.x;
		this.motionY = (float) newMotion.y;

		this.rotation = (float) Mth.atan2(this.motionY, this.motionX) + Mth.PI * 0.5f;

		if(this.dropping) {
			this.motionY += 8;
			this.motionX *= 0.5f;
		}
	}

	public float getPosX() {
		return this.posX;
	}

	public float getPosY() {
		return this.posY;
	}

	public void drawCrawler(GuiGraphics graphics, float partialTicks) {
		float interpX = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
		float interpY = this.prevPosY + (this.posY - this.prevPosY) * partialTicks;
		this.drawCrawler(graphics, interpX, interpY, 16 * this.scale, 16 * this.scale, this.rotation + this.hurtDir * (this.prevHurtRotation + (this.hurtRotation - this.prevHurtRotation) * partialTicks), this.alpha);
	}

	public void drawCrawler(GuiGraphics graphics, float x, float y, float width, float height, float rot, float alpha) {
		float cos = (float) Math.cos(rot);
		float sin = (float) Math.sin(rot);

		float cx = x + width * 0.5f;
		float cy = y + height * 0.5f;

		float nnx = (x - cx) * cos - (y - cy) * sin + cx;
		float nny = (x - cx) * sin + (y - cy) * cos + cy;
		float ppx = (x + width - cx) * cos - (y + height - cy) * sin + cx;
		float ppy = (x + width - cx) * sin + (y + height - cy) * cos + cy;
		float mpx = (x - cx) * cos - (y + height - cy) * sin + cx;
		float mpy = (x - cx) * sin + (y + height - cy) * cos + cy;
		float pmx = (x + width - cx) * cos - (y - cy) * sin + cx;
		float pmy = (x + width - cx) * sin + (y - cy) * cos + cy;

		float gb = 1 - this.hurtTint;
		RenderSystem.enableBlend();
		RenderSystem.setShaderTexture(0, this.sprite.atlasLocation());
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		Matrix4f matrix4f = graphics.pose().last().pose();
		BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		builder.addVertex(matrix4f, mpx, mpy, 0.0F).setUv(this.sprite.getU0(), this.sprite.getV1()).setColor(1.0f, gb, gb, alpha);
		builder.addVertex(matrix4f, ppx, ppy, 0.0F).setUv(this.sprite.getU1(), this.sprite.getV1()).setColor(1.0f, gb, gb, alpha);
		builder.addVertex(matrix4f, pmx, pmy, 0.0F).setUv(this.sprite.getU1(), this.sprite.getV0()).setColor(1.0f, gb, gb, alpha);
		builder.addVertex(matrix4f, nnx, nny, 0.0F).setUv(this.sprite.getU0(), this.sprite.getV0()).setColor(1.0f, gb, gb, alpha);
		BufferUploader.drawWithShader(builder.buildOrThrow());
		RenderSystem.disableBlend();
	}
}
