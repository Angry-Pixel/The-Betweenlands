package thebetweenlands.client.rendering.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import thebetweenlands.common.entities.BetweenlandsEntity;

// This ported nicely*!
public abstract class MowzieModelBase<T extends BetweenlandsEntity> extends HierarchicalModel<T> {

	/**
	 * Rotates a box to face where the entity is looking.
	 * <p/>
	 * Note: Just keep f3 and f4 from the setRotationAngles() method.
	 *
	 * @param f  is the number of boxes being used. (i.e. if you are
	 *           using this on a head and neck, set it to 2. Just a head, 1);
	 * @param f3 is the rotationYaw of the EntityLivingBase;
	 * @param f4 is the rotationPitch of the EntityLivingBase.
	 */
	public void faceTarget(ModelPart box, float f, float f3, float f4) {
		box.yRot = (f3 / (180f / (float) Math.PI)) / f;
		box.xRot = (f4 / (180f / (float) Math.PI)) / f;
	}

	/**
	 * Returns a float that can be used to rotate boxes.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param speed  is how fast the animation runs;
	 * @param degree is how far the box will rotate;
	 * @param invert will invert the rotation;
	 * @param offset will offset the timing of the animation;
	 * @param weight will make the animation favor one direction
	 *               more based on how fast the mob is moving;
	 * @param f      is the walked distance;
	 * @param f1     is the walk speed.
	 */
	public float rotateBox(float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
		if (invert) return (float) (-Math.cos(f * speed + offset) * degree * f1 + weight * f1);
		else return (float) (Math.cos(f * speed + offset) * degree * f1 + weight * f1);
	}

	/**
	 * Returns a float that can be used to move boxes.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param speed  is how fast the animation runs;
	 * @param degree is how far the box will move;
	 * @param bounce will make the box bounce;
	 * @param f      is the walked distance;
	 * @param f1     is the walk speed.
	 */
	public float moveBox(float speed, float degree, boolean bounce, float f, float f1) {
		if (bounce) return (float) -Math.abs((Math.sin(f * speed) * f1 * degree));
		else return (float) (Math.sin(f * speed) * f1 * degree - f1 * degree);
	}

	/**
	 * Rotates a box back and forth (rotateAngleX). Useful for arms and legs.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param box    is the ModelRenderer to be animated;
	 * @param speed  is how fast the animation runs;
	 * @param degree is how far the box will rotate;
	 * @param invert will invert the rotation;
	 * @param offset will offset the timing of the animation;
	 * @param weight will make the animation favor one direction
	 *               more based on how fast the mob is moving;
	 * @param f      is the walked distance;
	 * @param f1     is the walk speed.
	 */
	public void walk(ModelPart box, float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
		int inverted = 1;
		if (invert) inverted = -1;
		box.xRot = (float) Math.cos(f * speed + offset) * degree * inverted * f1 + weight * f1;
	}

	/**
	 * Rotates a box side to side (rotateAngleX). Useful for waists and torsos.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param box    is the ModelRenderer to be animated;
	 * @param speed  is how fast the animation runs;
	 * @param degree is how far the box will rotate;
	 * @param invert will invert the rotation;
	 * @param offset will offset the timing of the animation;
	 * @param weight will make the animation favor one direction
	 *               more based on how fast the mob is moving;
	 * @param f      is the walked distance;
	 * @param f1     is the walk speed.
	 */
	public void swing(ModelPart box, float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
		int inverted = 1;
		if (invert) inverted = -1;
		box.yRot = (float) Math.cos(f * speed + offset) * degree * inverted * f1 + weight * f1;
	}

	/**
	 * Rotates a box up and down (rotateAngleZ). Useful for wings and ears.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param box    is the ModelRenderer to be animated;
	 * @param speed  is how fast the animation runs;
	 * @param degree is how far the box will rotate;
	 * @param invert will invert the rotation;
	 * @param offset will offset the timing of the animation;
	 * @param weight will make the animation favor one direction
	 *               more based on how fast the mob is moving;
	 * @param f      is the walked distance;
	 * @param f1     is the walk speed.
	 */
	public void flap(ModelPart box, float speed, float degree, boolean invert, float offset, float weight, float f, float f1) {
		int inverted = 1;
		if (invert) inverted = -1;
		box.zRot = (float) Math.cos(f * speed + offset) * degree * inverted * f1 + weight * f1;
	}

	/**
	 * Moves a box up and down (rotationPointY). Useful for bodies.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param box    is the ModelRenderer to be animated;
	 * @param speed  is how fast the animation runs;
	 * @param degree is how far the box will move;
	 * @param bounce will make the box bounce;
	 * @param f      is the walked distance;
	 * @param f1     is the walk speed.
	 */
	// UNTESTED!!! may cause floating parts or some other wieardness
	public void bob(ModelPart box, float speed, float degree, boolean bounce, float f, float f1) {
		float bob = (float) (Math.sin(f * speed) * f1 * degree - f1 * degree);
		if (bounce) bob = (float) -Math.abs((Math.sin(f * speed) * f1 * degree));
		box.loadPose(PartPose.offsetAndRotation(box.x, bob, box.z, box.xRot, box.yRot, box.zRot));
	}

	/**
	 * Swings a chain of parented boxes back and forth (rotateAngleY). Useful for tails.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param boxes      are the ModelRenderers to be animated;
	 * @param speed      is how fast the animation runs;
	 * @param degree     is how far the box will move;
	 * @param rootOffset changes the delay between boxes.
	 *                   Try values from 0.0D to 5.0D or so until you like the effect;
	 * @param f          is the walked distance;
	 * @param f1         is the walk speed.
	 */
	public void chainSwing(ModelPart[] boxes, float speed, float degree, double rootOffset, float f, float f1) {
		int numberOfSegments = boxes.length;
		float offset = (float) ((rootOffset * Math.PI) / (2 * numberOfSegments));
		for (int i = 0; i < numberOfSegments; i++)
			boxes[i].yRot = (float) Math.cos(f * speed + offset * i) * f1 * degree;
	}

	/**
	 * Swings a chain of parented boxes up and down (rotateAngleX). Useful for tails.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param boxes      are the ModelRenderers to be animated;
	 * @param speed      is how fast the animation runs;
	 * @param degree     is how far the box will move;
	 * @param rootOffset changes the delay between boxes.
	 *                   Try values from 0.0D to 5.0D or so until you like the effect;
	 * @param f          is the walked distance;
	 * @param f1         is the walk speed.
	 */
	public void chainWave(ModelPart[] boxes, float speed, float degree, double rootOffset, float f, float f1) {
		int numberOfSegments = boxes.length;
		float offset = (float) ((rootOffset * Math.PI) / (2 * numberOfSegments));
		for (int i = 0; i < numberOfSegments; i++)
			boxes[i].xRot = (float) Math.cos(f * speed + offset * i) * f1 * degree;
	}

	/**
	 * Flaps a chain of parented boxes up and down (rotateAngleZ). Useful for tails.
	 * <p/>
	 * Note: Just keep f and f1 from the setRotationAngles() method.
	 *
	 * @param boxes      are the ModelRenderers to be animated;
	 * @param speed      is how fast the animation runs;
	 * @param degree     is how far the box will move;
	 * @param rootOffset changes the delay between boxes.
	 *                   Try values from 0.0D to 5.0D or so until you like the effect;
	 * @param f          is the walked distance;
	 * @param f1         is the walk speed.
	 */
	public void chainFlap(ModelPart[] boxes, float speed, float degree, double rootOffset, float f, float f1) {
		int numberOfSegments = boxes.length;
		float offset = (float) ((rootOffset * Math.PI) / (2 * numberOfSegments));
		for (int i = 0; i < numberOfSegments; i++)
			boxes[i].zRot = (float) Math.cos(f * speed + offset * i) * f1 * degree;
	}
}
