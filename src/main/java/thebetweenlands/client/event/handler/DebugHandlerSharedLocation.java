package thebetweenlands.client.event.handler;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.SharedStorage;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;
import thebetweenlands.util.config.ConfigHandler;

public class DebugHandlerSharedLocation {
	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
//		if(ConfigHandler.debug) {
//			for(int cx = -6; cx < 6; cx++) {
//				for(int cz = -6; cz < 6; cz++) {
//					BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(Minecraft.getMinecraft().theWorld);
//
//					for(SharedStorage sharedStorage : worldStorage.getSharedStorage()) {
//						if(sharedStorage instanceof LocationStorage) {
//							LocationStorage location = (LocationStorage) sharedStorage;
//							for(AxisAlignedBB bb : location.getBounds()) {
//								GL11.glPushMatrix();
//								//GL11.glDisable(GL11.GL_DEPTH_TEST);
//								GL11.glDepthMask(false);
//								GL11.glDisable(GL11.GL_TEXTURE_2D);
//								GL11.glEnable(GL11.GL_BLEND);
//								GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//								GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
//								GL11.glColor4f(1, 1, 1, 1);
//								GL11.glLineWidth(1.2f);
//
//								Random rnd = new Random(location.getSeed());
//								
//								float red = rnd.nextFloat() / 2.0F + location.getLayer() / 5.0F;
//								float green = rnd.nextFloat() / 2.0F;
//								float blue = rnd.nextFloat() - location.getLayer() / 5.0F;
//								float alpha = 0.08F;
//
//								GL11.glColor4f(red/1.5f, green/1.5f, blue/1.5f, alpha/1.5f);
//								drawBoundingBox(bb.offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ));
//								GL11.glColor4f(red, green, blue, alpha);
//								drawBoundingBoxOutline(bb.offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX, -Minecraft.getMinecraft().getRenderManager().viewerPosY, -Minecraft.getMinecraft().getRenderManager().viewerPosZ));
//								GL11.glEnable(GL11.GL_TEXTURE_2D);
//
//								GL11.glDepthMask(true);
//								//GL11.glEnable(GL11.GL_DEPTH_TEST);
//								GL11.glPopMatrix();
//							}
//						}
//					}
//				}
//			}
//		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawBoundingBox(AxisAlignedBB axisalignedbb) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glEnd();
	}

	@SideOnly(Side.CLIENT)
	public static void drawBoundingBoxOutline(AxisAlignedBB par1AxisAlignedBB) {
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glEnd();
	}
}
