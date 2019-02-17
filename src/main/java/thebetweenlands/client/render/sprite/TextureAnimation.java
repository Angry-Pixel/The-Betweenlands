package thebetweenlands.client.render.sprite;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import thebetweenlands.client.handler.TextureStitchHandler.Frame;

public class TextureAnimation {
    private Frame[] frames;
    private int frameTicks = 0;
    private int frameCounter = 0;
    private int totalDuration = 0;
    private Random rand;

    /**
     * Returns the total duration of the whole animation
     *
     * @return
     */
    public int getTotalDuration() {
        return this.totalDuration;
    }

    /**
     * If not null the animation will start at a random frame
     *
     * @param random
     * @return
     */
    public TextureAnimation setRandomStart(@Nullable Random random) {
        this.rand = random;
        return this;
    }

    /**
     * Updates the animation
     */
    public void update() {
        this.frameTicks++;

        if (this.getCurrentFrame() != null && this.frameTicks >= this.getCurrentFrame().getDuration()) {
            this.frameCounter = (this.frameCounter + 1) % this.frames.length;
            this.frameTicks = 0;
        }
    }

    /**
     * Returns the current frame counter
     *
     * @return
     */
    public int getFrameCounter() {
        return this.frameCounter;
    }

    /**
     * Sets the current frame counter
     *
     * @param counter
     * @return
     */
    public void setFrameCounter(int counter) {
        this.frameCounter = counter % this.frames.length;
    }

    /**
     * Returns the current frame
     *
     * @return
     */
    public Frame getCurrentFrame() {
        if (frames != null)
            return this.frames[this.frameCounter];
        return null;
    }

    /**
     * Returns the current frame's sprite
     *
     * @return
     */
    public TextureAtlasSprite getCurrentSprite() {
        if (this.getCurrentFrame() != null)
            return this.getCurrentFrame().getSprite();
        return null;
    }

    /**
     * Sets the frames
     *
     * @param frames
     * @return
     */
    public TextureAnimation setFrames(Frame[] frames) {
        boolean setRandom = this.rand != null && this.frames == null;

        this.frames = frames;

        if (setRandom) {
            this.frameCounter = this.rand.nextInt(this.frames.length);
        } else if (this.frameCounter >= this.frames.length) {
            this.frameCounter = 0;
        }

        this.totalDuration = 0;
        for (Frame frame : frames) {
            this.totalDuration += frame.getDuration();
        }

        return this;
    }
}
