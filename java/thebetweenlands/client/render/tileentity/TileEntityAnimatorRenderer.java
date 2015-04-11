package thebetweenlands.client.render.tileentity;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import thebetweenlands.client.model.block.ModelAnimator;
import thebetweenlands.entities.particles.EntityAnimatorFX;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.tileentities.TileEntityAnimator;
import thebetweenlands.utils.ItemRenderHelper;

public class TileEntityAnimatorRenderer extends TileEntitySpecialRenderer {
	private static final ModelAnimator model = new ModelAnimator();
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/animator.png");

	private float crystalVelocity = 0.0F;

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileEntityAnimator te = (TileEntityAnimator) tileEntity;

		this.crystalVelocity -= 0.1F;
		if(this.crystalVelocity <= 0.0F) {
			this.crystalVelocity = 0.0F;
		}

		this.bindTexture(TEXTURE);
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.render(null, 0, 0, 0, 0, 0, 0.0625F);
		GL11.glPopMatrix();

		//Sulfur rendering
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y + 0.27D, z + 0.5D);
		GL11.glRotated(180, 1, 0, 0);
		int items = 32 - (int) Math.ceil(te.progress / 100.0D * 32.0D);
		Random rand = new Random();
		rand.setSeed((long) (tileEntity.xCoord + tileEntity.yCoord + tileEntity.zCoord));
		for(int i = 0; i < items; i++) {
			GL11.glPushMatrix();
			GL11.glTranslated(rand.nextFloat() / 3.0D - 1.0D / 6.0D, 0.0D, rand.nextFloat() / 3.0D - 1.0D / 6.0D);
			GL11.glRotated(rand.nextFloat() * 30.0D - 15.0D, 1, 0, 0);
			GL11.glRotated(rand.nextFloat() * 30.0D - 15.0D, 0, 0, 1);
			GL11.glScaled(0.125D, 0.125D, 0.125D);
			GL11.glRotated(90, 1, 0, 0);
			GL11.glRotated(rand.nextFloat() * 360.0F, 0, 0, 1);
			ItemRenderHelper.renderItem(ItemMaterialsBL.createStack(EnumMaterialsBL.SULFUR), 0);
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();

		//Life crystal
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y + 0.43D, z + 0.5D);
		GL11.glScaled(0.18D, 0.18D, 0.18D);
		GL11.glRotatef(te.crystalRotation, 0, 1, 0);
		ItemRenderHelper.renderItem(ItemMaterialsBL.createStack(EnumMaterialsBL.LIFE_CRYSTAL), 0);
		GL11.glPopMatrix();

		//'Sphere'
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(0, 0, 1.0F, 0.5F);
		GL11.glRotatef(90.0F, 1, 0, 0);
		Sphere s = new Sphere();
		s.draw(0.4F, 6, 2);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();

		ArrayList<Vector3d> points = new ArrayList<Vector3d>();

		/*for(int i = 0; i < 8; i++){
			points.add(new Vector3d(te.xCoord, te.yCoord+i, te.zCoord));
		}*/

		for(int i = 0; i < 10; i++){
			points.add(new Vector3d(te.xCoord + i*2, te.yCoord+2 + Math.sin(Math.toRadians(i*2/20.0D*360.0D))*4, te.zCoord));
		}
		
		/*rand = te.getWorldObj().rand;
		
		points.add(new Vector3d(te.xCoord+0.5D + rand.nextFloat()*0.8D - 0.4D, te.yCoord+0.1D, te.zCoord+0.5D + rand.nextFloat()*0.8D - 0.4D));
		
		points.add(new Vector3d(te.xCoord+0.5D, te.yCoord+1, te.zCoord+0.5D));
		points.add(new Vector3d(te.xCoord+0.5D, te.yCoord+1, te.zCoord+0.5D));
		
		points.add(new Vector3d(te.xCoord+0.5D + rand.nextFloat()*0.5D - 0.25D, te.yCoord+1.2, te.zCoord+0.5D + rand.nextFloat()*0.5D - 0.25D));
		
		points.add(new Vector3d(te.xCoord+0.5D, te.yCoord+1.5D, te.zCoord+0.5D));*/
		
		//if(te.getWorldObj().rand.nextInt(20) == 0) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX(te.getWorldObj(), te.xCoord + 0.5D, te.yCoord, te.zCoord + 0.5D, 0, 0, 0, points));
		//}
	}
}
