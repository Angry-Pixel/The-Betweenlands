package thebetweenlands.client.render.tileentity;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelAnimator;
import thebetweenlands.entities.particles.EntityAnimatorFX;
import thebetweenlands.entities.particles.EntityAnimatorFX2;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemSpawnEggs;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.tileentities.TileEntityAnimator;
import thebetweenlands.utils.ItemRenderHelper;

public class TileEntityAnimatorRenderer extends TileEntitySpecialRenderer {
	private static final ModelAnimator model = new ModelAnimator();
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/animator.png");
	public static TileEntityAnimatorRenderer instance;
	private RenderManager renderManager;
	private double viewRot;

	public TileEntityAnimatorRenderer() {
		renderManager = RenderManager.instance;
	}

	@Override
	public void func_147497_a(TileEntityRendererDispatcher renderer) {
		super.func_147497_a(renderer);
		instance = this;
	}

	public void renderTileAsItem(double x, double y, double z) {
		bindTexture(TEXTURE);
		GL11.glPushMatrix();
		renderMainModel(x, y, z);
		GL11.glPopMatrix();
	}

	private void renderMainModel(double x, double y, double z) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 2F, z + 0.5F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		GL11.glScalef(1.5F, 1.5F, 1.5F);
		model.renderAll(0.0625F);
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileEntityAnimator te = (TileEntityAnimator) tileEntity;
		Random rand = new Random();
		ArrayList<Vector3d> points = new ArrayList<Vector3d>();
		viewRot = 180D + Math.toDegrees(Math.atan2(renderManager.viewerPosX - te.xCoord - 0.5D, renderManager.viewerPosZ - te.zCoord - 0.5D));
		int meta = te.getBlockMetadata();
		bindTexture(TEXTURE);
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 1.5F, z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		switch (meta) {
			case 2:
				GL11.glRotatef(180F, 0.0F, 1F, 0F);
				break;
			case 3:
				GL11.glRotatef(0F, 0.0F, 1F, 0F);
				break;
			case 4:
				GL11.glRotatef(90F, 0.0F, 1F, 0F);
				break;
			case 5:
				GL11.glRotatef(-90F, 0.0F, 1F, 0F);
				break;
		}
		GL11.glDisable(GL11.GL_CULL_FACE);
		model.render(null, 0, 0, 0, 0, 0, 0.0625F);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();

		// Sulfur rendering
		if (te.getStackInSlot(2) != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 0.27D, z + 0.5D);
			GL11.glRotated(180, 1, 0, 0);
			int items = te.getStackInSlot(2).stackSize;
			rand.setSeed((long) (tileEntity.xCoord + tileEntity.yCoord + tileEntity.zCoord));
			for (int i = 0; i < items; i++) {
				GL11.glPushMatrix();
				GL11.glTranslated(rand.nextFloat() / 3.0D - 1.0D / 6.0D, 0.0D, rand.nextFloat() / 3.0D - 1.0D / 6.0D);
				GL11.glRotated(rand.nextFloat() * 30.0D - 15.0D, 1, 0, 0);
				GL11.glRotated(rand.nextFloat() * 30.0D - 15.0D, 0, 0, 1);
				GL11.glScaled(0.125D, 0.125D, 0.125D);
				GL11.glRotated(90, 1, 0, 0);
				GL11.glRotated(rand.nextFloat() * 360.0F, 0, 0, 1);
				ItemRenderHelper.renderItem(ItemGeneric.createStack(EnumItemGeneric.SULFUR), 0);
				GL11.glPopMatrix();
			}
			GL11.glPopMatrix();
		}

		// Life crystal
		if (te.getStackInSlot(1) != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 0.43D, z + 0.5D);
			GL11.glScaled(0.18D, 0.18D, 0.18D);
			GL11.glRotated(viewRot, 0, 1, 0);
			ItemRenderHelper.renderItem(new ItemStack(BLItemRegistry.lifeCrystal), 0);
			GL11.glPopMatrix();
		}

		// Item
		if (te.getStackInSlot(0) != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.43D, z + 0.5D);
			
			if (te.getStackInSlot(0).getItem() instanceof ItemMonsterPlacer) {
				GL11.glTranslated(0.0D, -0.5D, 0.0D);
				GL11.glRotated(viewRot +180, 0, 1, 0);
			}
			if (!(te.getStackInSlot(0).getItem() instanceof ItemMonsterPlacer) && te.getStackInSlot(0) != null) {
				GL11.glScaled(0.3D, 0.3D, 0.3D);
				GL11.glRotated(viewRot, 0, 1, 0);
				ItemRenderHelper.renderItem(te.getStackInSlot(0), 0);
			} else {
				GL11.glScaled(0.5D, 0.5D, 0.5D);
				Entity entity = null;
				if (te.getStackInSlot(0).getItem() instanceof ItemSpawnEggs)
					entity = ItemSpawnEggs.getEntity(te.getWorldObj(), x, y, z, te.getStackInSlot(0));
				else
					entity = EntityList.createEntityByID(te.getStackInSlot(0).getItemDamage(), tileEntity.getWorldObj());
				if (entity != null) {
					entity.setRotationYawHead(0F);
					entity.rotationPitch = 0F;
					RenderManager.instance.renderEntityWithPosYaw(entity, 0D, 0D, 0D, 0F, 0F);
				}
			}
			GL11.glPopMatrix();
		}

		// Sulfur Particles
		if (te.getStackInSlot(2) != null) {
			rand = te.getWorldObj().rand;
			points.add(new Vector3d(te.xCoord + 0.5D + rand.nextFloat() * 0.6D - 0.3D, te.yCoord + 0.1D, te.zCoord + 0.5D + rand.nextFloat() * 0.6D - 0.3D));
			points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1, te.zCoord + 0.5D));
			points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1, te.zCoord + 0.5D));
			points.add(new Vector3d(te.xCoord + 0.5D + rand.nextFloat() * 0.5D - 0.25D, te.yCoord + 1.2, te.zCoord + 0.5D + rand.nextFloat() * 0.5D - 0.25D));
			points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1.5D, te.zCoord + 0.5D));
			if (te.getWorldObj().rand.nextInt(15) == 0) {
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX(te.getWorldObj(), te.xCoord + 0.5D, te.yCoord, te.zCoord + 0.5D, 0, 0, 0, points, ItemGeneric.createStack(EnumItemGeneric.SULFUR).getIconIndex(), 0.01F));
			}
		}

		// Life Crystal Particles
		if (te.getStackInSlot(1) != null) {
			points = new ArrayList<Vector3d>();
			points.add(new Vector3d(te.xCoord + 0.5D + rand.nextFloat() * 0.3D - 0.15D, te.yCoord + 0.5D, te.zCoord + 0.5D + rand.nextFloat() * 0.3D - 0.15D));
			points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1, te.zCoord + 0.5D));
			points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1, te.zCoord + 0.5D));
			points.add(new Vector3d(te.xCoord + 0.5D + rand.nextFloat() * 0.5D - 0.25D, te.yCoord + 1.2, te.zCoord + 0.5D + rand.nextFloat() * 0.5D - 0.25D));
			points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1.5D, te.zCoord + 0.5D));
			if (te.getWorldObj().rand.nextInt(50) == 0) {
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX(te.getWorldObj(), te.xCoord + 0.5D, te.yCoord, te.zCoord + 0.5D, 0, 0, 0, points, new ItemStack(BLItemRegistry.lifeCrystal).getIconIndex(), 0.0003F));
			}
		}

		// Runes
		points = new ArrayList<Vector3d>();
		points.add(new Vector3d(te.xCoord + rand.nextFloat() * 0.3D - 0.15D, te.yCoord + 0.9, te.zCoord + 0.65 + rand.nextFloat() * 0.3D - 0.15D));
		points.add(new Vector3d(te.xCoord + rand.nextFloat() * 0.3D - 0.15D, te.yCoord + 1.36, te.zCoord + 0.65 + rand.nextFloat() * 0.3D - 0.15D));
		points.add(new Vector3d(te.xCoord + 0.5D, te.yCoord + 1.5D, te.zCoord + 0.5D));
		if (te.getWorldObj().rand.nextInt(150) == 0) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityAnimatorFX2(te.getWorldObj(), te.xCoord, te.yCoord + 0.9, te.zCoord + 0.65, 0, 0, 0, points));
		}

		// Smoke
		if (te.getWorldObj().rand.nextInt(150) == 0) {
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySmokeFX(te.getWorldObj(), te.xCoord + 0.5 + rand.nextFloat() * 0.3D - 0.15D, te.yCoord + 0.3, te.zCoord + 0.5 + rand.nextFloat() * 0.3D - 0.15D, 0, 0, 0, (rand.nextFloat() / 2.0F) + 1F));
		}
	}
}
