package thebetweenlands.client.model.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import org.lwjgl.opengl.GL11;

import thebetweenlands.tileentities.TileEntitySpikeTrap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSpikeBlock extends ModelBase {
	ModelRenderer spikeBackMid;
	ModelRenderer mainSpike;
	ModelRenderer spikeBackRight;
	ModelRenderer spikeFrontLeft;
	ModelRenderer spikeFrontRight;
	ModelRenderer spikeBackLeft;
	ModelRenderer spikeMidLeft;
	ModelRenderer spikeMidRight;
	ModelRenderer spikeMidFront;

	public ModelSpikeBlock() {
		textureWidth = 64;
		textureHeight = 32;

		spikeBackMid = new ModelRenderer(this, 0, 0);
		spikeBackMid.addBox(-0.5F, -2F, 5.5F, 1, 10, 1);
		spikeBackMid.setRotationPoint(0F, 16F, 0F);
		setRotation(spikeBackMid, 0F, 0F, 0.0698132F);
		mainSpike = new ModelRenderer(this, 5, 0);
		mainSpike.addBox(-1F, -7F, -1F, 2, 15, 2);
		mainSpike.setRotationPoint(0F, 16F, 0F);
		setRotation(mainSpike, 0F, 0F, 0F);
		spikeBackRight = new ModelRenderer(this, 0, 0);
		spikeBackRight.addBox(-5F, -5.6F, 5F, 1, 12, 1);
		spikeBackRight.setRotationPoint(0F, 16F, 0F);
		setRotation(spikeBackRight, -0.1745329F, 0F, -0.1745329F);
		spikeFrontLeft = new ModelRenderer(this, 0, 0);
		spikeFrontLeft.addBox(5F, -3.6F, -5F, 1, 10, 1);
		spikeFrontLeft.setRotationPoint(0F, 16F, 0F);
		setRotation(spikeFrontLeft, 0.1745329F, 0F, 0.1745329F);
		spikeFrontRight = new ModelRenderer(this, 0, 0);
		spikeFrontRight.addBox(-7F, -4F, -5F, 1, 10, 1);
		spikeFrontRight.setRotationPoint(0F, 16F, 0F);
		setRotation(spikeFrontRight, 0.1745329F, 0F, -0.1745329F);
		spikeBackLeft = new ModelRenderer(this, 0, 0);
		spikeBackLeft.addBox(6F, -6F, 4F, 1, 12, 1);
		spikeBackLeft.setRotationPoint(0F, 16F, 0F);
		setRotation(spikeBackLeft, -0.1745329F, 0F, 0.1745329F);
		spikeMidLeft = new ModelRenderer(this, 0, 0);
		spikeMidLeft.addBox(4F, -4.5F, -0.5F, 1, 12, 1);
		spikeMidLeft.setRotationPoint(0F, 16F, 0F);
		setRotation(spikeMidLeft, 0F, 0F, 0.0872665F);
		spikeMidRight = new ModelRenderer(this, 0, 0);
		spikeMidRight.addBox(-5F, -4.5F, -0.5F, 1, 12, 1);
		spikeMidRight.setRotationPoint(0F, 16F, 0F);
		setRotation(spikeMidRight, 0F, 0F, -0.0872665F);
		spikeMidFront = new ModelRenderer(this, 0, 0);
		spikeMidFront.addBox(-0.5F, -4.3F, -6.5F, 1, 12, 1);
		spikeMidFront.setRotationPoint(0F, 16F, 0F);
		setRotation(spikeMidFront, 0.0698132F, 0F, -0.0872665F);
	}

	public void render(TileEntitySpikeTrap tile) {
		if (tile.active || !tile.active && tile.animationTicks > 0) {
			if (tile.animationTicks <= 5)
				GL11.glTranslatef(0F, 0F - 1F / 5 * tile.animationTicks, 0F);
			if (tile.animationTicks > 5)
				GL11.glTranslatef(0F, - 1F, 0F);
			spikeBackMid.render(0.0625F);
			mainSpike.render(0.0625F);
			spikeBackRight.render(0.0625F);
			spikeFrontLeft.render(0.0625F);
			spikeFrontRight.render(0.0625F);
			spikeBackLeft.render(0.0625F);
			spikeMidLeft.render(0.0625F);
			spikeMidRight.render(0.0625F);
			spikeMidFront.render(0.0625F);
		}
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

}
