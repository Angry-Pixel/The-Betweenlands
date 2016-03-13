package thebetweenlands.event.player;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.biome.BiomeGenBase;
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
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage;
import thebetweenlands.world.storage.chunk.storage.location.LocationStorage.EnumLocationType;

public class PlayerLocationHandler {
	public static final PlayerLocationHandler INSTANCE = new PlayerLocationHandler();

	private static final ResourceLocation TITLE_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/locationTitle.png");

	public static List<LocationStorage> getLocations(Entity entity) {
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

	public static boolean isInLocationType(Entity entity, EnumLocationType type) {
		List<LocationStorage> locations = getLocations(entity);
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
					BiomeGenBase biome = player.worldObj.getBiomeGenForCoords(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ));
					String biomeName = StatCollector.translateToLocal("biome." + biome.biomeName + ".name");
					if(biomeName.equals("biome." + biome.biomeName + ".name"))
						biomeName = biome.biomeName; //Not localized
					this.currentLocation = String.format(StatCollector.translateToLocal("location.wilderness.name"), biomeName);
				} else {
					LocationStorage highestLocation = null;
					for(LocationStorage storage : locations) {
						if(highestLocation == null || storage.getLayer() > highestLocation.getLayer())
							highestLocation = storage;
					}
					this.currentLocation = highestLocation.getLocalizedName();
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
				float scale = 2F;
				double strWidth = TheBetweenlands.proxy.getCustomFontRenderer().getStringWidth(this.currentLocation) * scale;
				double strHeight = (TheBetweenlands.proxy.getCustomFontRenderer().FONT_HEIGHT) * scale;
				double strX = width / 2.0D - strWidth / 2.0F;
				double strY = height / 5.0D;
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
				double sidePadding = 6;
				this.renderTexturedRect(strX - sidePadding*scale, strY + strHeight - 5*scale, strX - sidePadding*scale + 9*scale, strY + strHeight - 5*scale + 16*scale, 0, 9 / 128.0D, 0, 1);
				this.renderTexturedRect(strX - sidePadding*scale + 9*scale, strY + strHeight - 5*scale, strX + strWidth / 2.0D - 6*scale, strY + strHeight - 5*scale + 16*scale, 9 / 128.0D, 58 / 128.0D, 0, 1);
				this.renderTexturedRect(strX + strWidth / 2.0D - 6*scale, strY + strHeight - 5*scale, strX + strWidth / 2.0D + 6*scale, strY + strHeight - 5*scale + 16*scale, 58 / 128.0D, 70 / 128.0D, 0, 1);
				this.renderTexturedRect(strX + strWidth / 2.0D + 6*scale, strY + strHeight - 5*scale, strX + strWidth + sidePadding*scale - 9*scale, strY + strHeight - 5*scale + 16*scale, 70 / 128.0D, 119 / 128.0D, 0, 1);
				this.renderTexturedRect(strX + strWidth + sidePadding*scale - 9*scale, strY + strHeight - 5*scale, strX + strWidth + sidePadding*scale, strY + strHeight - 5*scale + 16*scale, 119 / 128.0D, 1, 0, 1);
				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}
	}

	@SideOnly(Side.CLIENT)
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
	public void onPlayerTick(PlayerTickEvent event) {
		if(!isInLocationType(event.player, EnumLocationType.WIGHT_TOWER)) {
			event.player.getEntityData().setInteger("thebetweenlands.blWightTowerWarnings", 0);
		}
	}

	@SubscribeEvent
	public void onBlockPlace(PlaceEvent event) {
		Chunk chunk = event.world.getChunkFromChunkCoords(event.x / 16, event.z / 16);
		if(chunk != null && !event.world.isRemote) {
			if(event.player != null && !event.player.capabilities.isCreativeMode && this.isInLocationType(event.player, EnumLocationType.WIGHT_TOWER)) {
				int warnings = event.player.getEntityData().getInteger("thebetweenlands.blWightTowerWarnings");
				if(warnings < 3) {
					if(warnings == 0) {
						event.player.addChatMessage(new ChatComponentTranslation("chat.wightTower.warning1"));
					}
					event.player.getEntityData().setInteger("thebetweenlands.blWightTowerWarnings", warnings + 1);
				} else {
					event.player.getEntityData().setInteger("thebetweenlands.blWightTowerWarnings", 0);
					event.player.addChatMessage(new ChatComponentTranslation("chat.wightTower.warning2"));
					this.spawnGuards(event.player);
				}
				EntityWight wight = new EntityWight(event.player.worldObj);
				wight.setRepairGuard(event.x, event.y, event.z);
				wight.setVolatile(true);
				double rx = event.player.worldObj.rand.nextDouble() - 0.5F;
				double ry = event.player.worldObj.rand.nextDouble() - 0.5F;
				double rz = event.player.worldObj.rand.nextDouble() - 0.5F;
				Vec3 dir = Vec3.createVectorHelper(rx, ry, rz);
				dir = dir.normalize();
				rx = dir.xCoord * 4.0F;
				ry = dir.yCoord * 4.0F;
				rz = dir.zCoord * 4.0F;
				wight.setLocationAndAngles(event.x + rx, event.y + ry, event.z + rz, 0, 0);
				event.player.worldObj.spawnEntityInWorld(wight);
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event) {
		Chunk chunk = event.world.getChunkFromChunkCoords(event.x / 16, event.z / 16);
		if(chunk != null) {

			EntityPlayer player = event.getPlayer();
			if(player != null && !player.capabilities.isCreativeMode && this.isInLocationType(player, EnumLocationType.WIGHT_TOWER)) {
				if(!event.world.isRemote) {
					int warnings = player.getEntityData().getInteger("thebetweenlands.blWightTowerWarnings");
					if(warnings < 3) {
						if(warnings == 0) {
							player.addChatMessage(new ChatComponentTranslation("chat.wightTower.warning1"));
						}
						player.getEntityData().setInteger("thebetweenlands.blWightTowerWarnings", warnings + 1);
					} else {
						player.getEntityData().setInteger("thebetweenlands.blWightTowerWarnings", 0);
						player.addChatMessage(new ChatComponentTranslation("chat.wightTower.warning2"));
						this.spawnGuards(player);
					}
					EntityWight wight = new EntityWight(player.worldObj);
					wight.setRepairGuard(event.block, event.x, event.y, event.z, event.blockMetadata);
					wight.setVolatile(true);
					double rx = player.worldObj.rand.nextDouble() - 0.5F;
					double ry = player.worldObj.rand.nextDouble() - 0.5F;
					double rz = player.worldObj.rand.nextDouble() - 0.5F;
					Vec3 dir = Vec3.createVectorHelper(rx, ry, rz);
					dir = dir.normalize();
					rx = dir.xCoord * 4.0F;
					ry = dir.yCoord * 4.0F;
					rz = dir.zCoord * 4.0F;
					wight.setLocationAndAngles(event.x + rx, event.y + ry, event.z + rz, 0, 0);
					player.worldObj.spawnEntityInWorld(wight);

					event.world.setBlock(event.x, event.y, event.z, Blocks.air);
					event.setCanceled(true);
				} else {
					event.world.playAuxSFXAtEntity(null, 2001, event.x, event.y, event.z, Block.getIdFromBlock(event.block));
					event.world.setBlock(event.x, event.y, event.z, Blocks.air);
					event.setCanceled(true);
				}
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
					wight.setLocationGuard(player);
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
