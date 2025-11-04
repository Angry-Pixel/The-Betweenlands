package thebetweenlands.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.ArrayList;
import java.util.List;

public abstract class RandomAnimatedParticle extends TextureSheetParticle {

	private List<TextureAtlasSprite> frames = new ArrayList<>();
	private int frameTicks = 0;
	private int frameCounter = 0;
	private final int duration;

	protected RandomAnimatedParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, int frameTime) {
		super(level, x, y, z);
		if (spriteSet instanceof ParticleEngine.MutableSpriteSet mutable) {
			this.frames = mutable.sprites;
		}
		this.duration = frameTime;
		this.pickSprite(spriteSet);
	}

	protected RandomAnimatedParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet, int frameTime) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		if (spriteSet instanceof ParticleEngine.MutableSpriteSet mutable) {
			this.frames = mutable.sprites;
		}
		this.duration = frameTime;
		this.pickSprite(spriteSet);
	}

	@Override
	public void pickSprite(SpriteSet sprite) {
		int index = this.random.nextInt(this.frames.size());
		this.frameCounter = index;
		this.setSprite(this.frames.get(index));
	}

	@Override
	public void tick() {
		super.tick();
		this.tickParticleAnimation();
	}

	protected void tickParticleAnimation() {
		if (!this.frames.isEmpty()) {
			this.frameTicks++;

			if (this.frameTicks >= this.duration) {
				this.frameCounter = (this.frameCounter + 1) % this.frames.size();
				this.frameTicks = 0;
				this.setSprite(this.frames.get(this.frameCounter));
			}
		}
	}
}
