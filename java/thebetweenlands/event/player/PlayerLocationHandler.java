package thebetweenlands.event.player;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.utils.ColorUtils;
import thebetweenlands.world.storage.chunk.BetweenlandsChunkData;
import thebetweenlands.world.storage.chunk.storage.ChunkStorage;
import thebetweenlands.world.storage.chunk.storage.LocationStorage;
import thebetweenlands.world.storage.chunk.storage.LocationStorage.EnumLocationType;

public class PlayerLocationHandler {
	public static final PlayerLocationHandler INSTANCE = new PlayerLocationHandler();

	private static final ResourceLocation TITLE_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/locationTitle.png");

	private List<LocationStorage> getLocations(Entity entity) {
		List<LocationStorage> locations = new ArrayList<LocationStorage>();
		Chunk chunk = entity.worldObj.getChunkFromBlockCoords(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posZ));
		if(chunk != null) {
			BetweenlandsChunkData chunkData = BetweenlandsChunkData.forChunk(entity.worldObj, chunk);
			for(ChunkStorage storage : chunkData.getStorage()) {
				if(storage instanceof LocationStorage && ((LocationStorage)storage).isInside(entity))
					locations.add((LocationStorage)storage);
			}
		}
		return locations;
	}

	private boolean isInLocationType(Entity entity, EnumLocationType type) {
		List<LocationStorage> locations = this.getLocations(entity);
		for(LocationStorage location : locations) {
			if(location.getType() == type) 
				return true;
		}
		return false;
	}

	private String currentLocation = "";
	private int titleTicks = 0;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase == Phase.START) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if(player != null) {
				if(this.titleTicks > 0) 
					this.titleTicks--;
				List<LocationStorage> locations = this.getLocations(player);
				String prevLocation = this.currentLocation;
				if(locations.isEmpty()) {
					if(this.currentLocation.length() > 0)
						this.currentLocation = StatCollector.translateToLocal("location.wilderness.name");
				} else {
					this.currentLocation = locations.get(0).getLocalizedName();
				}
				if(this.currentLocation.length() > 0 && !prevLocation.equals(this.currentLocation)) {
					this.titleTicks = 80;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderScreen(RenderGameOverlayEvent.Post event) {
		if(event.type == ElementType.TEXT) {
			if(this.titleTicks > 0) {
				int width = event.resolution.getScaledWidth();
				int height = event.resolution.getScaledHeight();
				float scale = 3.0F;
				double strWidth = TheBetweenlands.proxy.getCustomFontRenderer().getStringWidth(this.currentLocation) * scale;
				double strX = width / 2.0D - strWidth / 2.0F;
				double strY = height / 4.0D;
				GL11.glPushMatrix();
				GL11.glTranslated(strX, strY, 0);
				GL11.glScaled(scale, scale, 1.0F);
				float fade = Math.min(1.0F, 1.0F - (this.titleTicks - 20) / 60.0F + 0.02F) - Math.max(0, (-this.titleTicks + 5) / 5.0F);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
				TheBetweenlands.proxy.getCustomFontRenderer().drawString(this.currentLocation, 0, 0, ColorUtils.toHex(1, 1, 1, fade));
				GL11.glPopMatrix();
				Minecraft.getMinecraft().renderEngine.bindTexture(TITLE_TEXTURE);
				GL11.glColor4f(1, 1, 1, fade);
				GL11.glDisable(GL11.GL_CULL_FACE);
				this.renderTexturedRect(strX - 12, strY + 17, strX + 2, strY + 40, 0, 9 / 128.0D, 0, 1);
				this.renderTexturedRect(strX + 2, strY + 17, strX + strWidth / 2.0D - 10, strY + 40, 9 / 128.0D, 58 / 128.0D, 0, 1);
				this.renderTexturedRect(strX + strWidth / 2.0D - 10, strY + 17, strX + strWidth / 2.0D + 10, strY + 40, 58 / 128.0D, 70 / 128.0D, 0, 1);
				this.renderTexturedRect(strX + strWidth / 2.0D + 10, strY + 17, strX + strWidth - 2, strY + 40, 70 / 128.0D, 119 / 128.0D, 0, 1);
				this.renderTexturedRect(strX + strWidth - 2, strY + 17, strX + strWidth + 12, strY + 40, 119 / 128.0D, 1, 0, 1);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}
	}

	private void renderTexturedRect(double x, double y, double x2, double y2, double umin, double umax, double vmin, double vmax) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(umin, vmin);
		GL11.glVertex2d(x, y);
		GL11.glTexCoord2d(umin, vmax);
		GL11.glVertex2d(x, y2);
		GL11.glTexCoord2d(umax, vmax);
		GL11.glVertex2d(x2, y2);
		GL11.glTexCoord2d(umax, vmin);
		GL11.glVertex2d(x2, y);
		GL11.glEnd();
	}

	@SubscribeEvent
	public void onBlockPlace(PlaceEvent event) {
		Chunk chunk = event.world.getChunkFromChunkCoords(event.x / 16, event.z / 16);
		if(chunk != null && !event.world.isRemote) {
			if(event.player != null && !event.player.capabilities.isCreativeMode && this.isInLocationType(event.player, EnumLocationType.WIGHT_TOWER)) {
				this.spawnGuards(event.player);
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		Chunk chunk = event.world.getChunkFromChunkCoords(event.x / 16, event.z / 16);
		if(chunk != null && !event.world.isRemote) {
			EntityPlayer player = event.getPlayer();
			if(player != null && !player.capabilities.isCreativeMode && this.isInLocationType(player, EnumLocationType.WIGHT_TOWER)) {
				this.spawnGuards(player);
			}
		}
	}

	private void spawnGuards(EntityPlayer player) {
		if(player.worldObj.difficultySetting != EnumDifficulty.PEACEFUL) {
			AxisAlignedBB checkAABB = player.boundingBox.expand(12.0F, 12.0F, 12.0F);
			List<EntityWight> wights = player.worldObj.getEntitiesWithinAABB(EntityWight.class, checkAABB);
			int guards = 0;
			for(EntityWight wight : wights) 
				if(wight.isLocationGuard())
					guards++;
			if(guards < 3) {
				for(int i = 0; i < 3 - guards; i++) {
					EntityWight wight = new EntityWight(player.worldObj);
					wight.setLocationGuard(true);
					wight.setVolatile(true);
					double rx = player.worldObj.rand.nextDouble() - 0.5F;
					double ry = player.worldObj.rand.nextDouble() - 0.5F;
					double rz = player.worldObj.rand.nextDouble() - 0.5F;
					Vec3 dir = Vec3.createVectorHelper(rx, ry, rz);
					dir = dir.normalize();
					rx = dir.xCoord * 6.0F;
					ry = dir.yCoord * 6.0F;
					rz = dir.zCoord * 6.0F;
					wight.setLocationAndAngles(player.posX + rx, player.posY + ry, player.posZ + rz, 0, 0);
					wight.faceEntity(player, 360.0F, 360.0F);
					wight.setAttackTarget(player);
					player.worldObj.spawnEntityInWorld(wight);
				}
			}
		}
	}
}
