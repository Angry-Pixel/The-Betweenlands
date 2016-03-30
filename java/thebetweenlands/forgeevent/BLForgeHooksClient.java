package thebetweenlands.forgeevent;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.client.input.WeedwoodRowboatHandler;
import thebetweenlands.forgeevent.client.ClientAttackEvent;
import thebetweenlands.forgeevent.client.ClientBlockDamageEvent;
import thebetweenlands.forgeevent.client.RenderEntitiesEvent;

public class BLForgeHooksClient {
	private BLForgeHooksClient() {}

	public static boolean getMouseOverHook() {
		if (WeedwoodRowboatHandler.INSTANCE.shouldPreventWorldInteraction()) {
			Minecraft.getMinecraft().objectMouseOver = new MovingObjectPosition(0, -1, 0, -1, Vec3.createVectorHelper(0, 0, 0), false);
			return true;
		}
		return false;
	}

	public static int handlePlayerAttackInput(int leftClickCounter) {
		Minecraft mc = Minecraft.getMinecraft();
		if (leftClickCounter <= 0) {
			if (postPlayerAttackEvent()) {
				return leftClickCounter;
			}
			mc.thePlayer.swingItem();
			if (mc.objectMouseOver == null) {
				if (mc.playerController.isNotCreative()) {
					leftClickCounter = 10;
				}
			} else {
				switch (mc.objectMouseOver.typeOfHit) {
				case ENTITY:
					mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
					break;
				case BLOCK:
					int x = mc.objectMouseOver.blockX;
					int y = mc.objectMouseOver.blockY;
					int z = mc.objectMouseOver.blockZ;
					if (mc.theWorld.getBlock(x, y, z).getMaterial() == Material.air) {
						if (mc.playerController.isNotCreative()) {
							leftClickCounter = 10;
						}
					} else {
						mc.playerController.clickBlock(x, y, z, mc.objectMouseOver.sideHit);
					}
				default:
					break;
				}
			}
		}
		return leftClickCounter;
	}

	public static int handleBlockBreakingInput(boolean breaking, int leftClickCounter) {
		if (!breaking) {
			leftClickCounter = 0;
		}

		Minecraft mc = Minecraft.getMinecraft();

		if (leftClickCounter <= 0) {
			if (breaking && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				if(postPreBlockDamageEvent()) return leftClickCounter;
				int i = mc.objectMouseOver.blockX;
				int j = mc.objectMouseOver.blockY;
				int k = mc.objectMouseOver.blockZ;

				if (mc.theWorld.getBlock(i, j, k).getMaterial() != Material.air) {
					mc.playerController.onPlayerDamageBlock(i, j, k, mc.objectMouseOver.sideHit);

					if (mc.thePlayer.isCurrentToolAdventureModeExempt(i, j, k)) {
						mc.effectRenderer.addBlockHitEffects(i, j, k, mc.objectMouseOver);
						mc.thePlayer.swingItem();
					}
				}
				postPostBlockDamageEvent();
			} else {
				mc.playerController.resetBlockRemoving();
			}
		}

		return leftClickCounter;
	}

	private static boolean postPreBlockDamageEvent() {
		return MinecraftForge.EVENT_BUS.post(new ClientBlockDamageEvent.Pre());
	}

	private static void postPostBlockDamageEvent() {
		MinecraftForge.EVENT_BUS.post(new ClientBlockDamageEvent.Post());
	}

	private static boolean postPlayerAttackEvent() {
		return MinecraftForge.EVENT_BUS.post(new ClientAttackEvent());
	}

	public static void postPreRenderEntitiesEvent() {
		MinecraftForge.EVENT_BUS.post(new RenderEntitiesEvent.Pre());
	}

	public static void postPostRenderEntitiesEvent() {
		MinecraftForge.EVENT_BUS.post(new RenderEntitiesEvent.Post());
	}
}
