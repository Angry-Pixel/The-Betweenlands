package thebetweenlands.client.gui;

import java.util.Random;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import thebetweenlands.event.debugging.DebugHandler;
import thebetweenlands.lib.ModInfo;

public class GuiDebugMenu extends GuiScreen {
	private static final ResourceLocation[] PANORAMA_PATHS = new ResourceLocation[6];
	static {
		for (int i = 0; i < PANORAMA_PATHS.length; i++) {
			PANORAMA_PATHS[i] = new ResourceLocation(ModInfo.ID, String.format("textures/gui/debug/background/panorama_%s.png", i));
		}
	}

	private DynamicTexture panoramaTexture;

	private ResourceLocation panoramaResourceLocation;

	private GuiUpgradedTextField worldSeedTextField;

	private int panoramaTimer;

	private int nextButtonId;

	private int nextButtonY;

	private GuiButton joinDebugWorldButton;

	private GuiButton deleteDebugWorldButton;

	private GuiButton returnButton;

	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		nextButtonId = 0;
		nextButtonY = height / 4 + 48;
		panoramaTexture = new DynamicTexture(256, 256);
		panoramaResourceLocation = mc.getTextureManager().getDynamicTextureLocation("debug-background", panoramaTexture);
		int x = width / 2 - 100, y = nextButtonY();
		buttonList.add(joinDebugWorldButton = new GuiButton(nextButtonId(), x, y, 138, 20, "Enter The Betweenlands"));
		worldSeedTextField = new GuiUpgradedTextField(fontRendererObj, x + 140, y + 1, 59, 18);
		worldSeedTextField.setPlaceholder("Seed");
		String saveFolder = DebugHandler.INSTANCE.worldFolderName;
		ISaveFormat saveLoader = mc.getSaveLoader();
		String seed = null;
		if (DebugHandler.INSTANCE.isInDebugWorld) {
			seed = String.valueOf(MinecraftServer.getServer().worldServers[0].getWorldInfo().getSeed());
		} else if (saveLoader.canLoadWorld(saveFolder)) {
			ISaveHandler saveHandler = saveLoader.getSaveLoader(saveFolder, false);
			seed = String.valueOf(saveHandler.loadWorldInfo().getSeed());
		}
		if (seed != null) {
			worldSeedTextField.setText(seed);
			worldSeedTextField.setCursorPositionZero();
			buttonList.add(deleteDebugWorldButton = new GuiSmallButton(nextButtonId(), x + 201, y, 20, 0));
		}
		buttonList.add(returnButton = new GuiButton(nextButtonId(), width / 2 - 100, height - 28, "Return"));
	}

	private void addButton(String label) {
		buttonList.add(new GuiButton(nextButtonId(), width / 2 - 100, nextButtonY(), label));
	}

	private int nextButtonId() {
		return nextButtonId++;
	}

	private int nextButtonY() {
		int nextButtonY = this.nextButtonY;
		this.nextButtonY += 24;
		return nextButtonY;
	}

	@Override
	public void updateScreen() {
		panoramaTimer++;
		worldSeedTextField.updateCursorCounter();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button == joinDebugWorldButton) {
			joinDebugWorld();
		} else if (button == deleteDebugWorldButton) {
			deleteDebugWorld();
		} else if (button == returnButton) {
			mc.displayGuiScreen(DebugHandler.INSTANCE.previousGuiScreen);
		}
	}

	private long getSeed() {
		String seedString = worldSeedTextField.getText();
		if (!MathHelper.stringNullOrLengthZero(seedString)) {
			try {
				return Long.parseLong(seedString);
			} catch (NumberFormatException numberformatexception) {
				return seedString.hashCode();
			}
		} else {
			return (new Random()).nextLong();
		}
	}

	private void joinDebugWorld() {
		String saveFolder = DebugHandler.INSTANCE.worldFolderName;
		ISaveFormat saveLoader = mc.getSaveLoader();
		ISaveHandler saveHandler = saveLoader.getSaveLoader(saveFolder, false);
		WorldInfo existingWorldInfo = saveHandler.loadWorldInfo();
		WorldSettings worldSettings = null;
		long seed = getSeed();
		boolean newWorld = false;
		if (existingWorldInfo == null) {
			newWorld = true;
		} else if (existingWorldInfo.getSeed() != seed || existingWorldInfo.getTerrainType() != WorldType.DEFAULT) {
			saveLoader.flushCache();
			saveLoader.deleteWorldDirectory(saveFolder);
			newWorld = true;
		}
		if (newWorld) {
			worldSettings = new WorldSettings(seed, WorldSettings.GameType.CREATIVE, true, false, WorldType.DEFAULT);
			worldSettings.enableCommands();
		}
		mc.launchIntegratedServer(saveFolder, DebugHandler.INSTANCE.worldName, worldSettings);
		DebugHandler.INSTANCE.isInDebugWorld = true;
	}

	private void deleteDebugWorld() {
		if (DebugHandler.INSTANCE.isInDebugWorld) {
			mc.loadWorld(null);
		}
		String saveFolder = DebugHandler.INSTANCE.worldFolderName;
		ISaveFormat saveLoader = mc.getSaveLoader();
		if (saveLoader.canLoadWorld(saveFolder)) {
			saveLoader.flushCache();
			saveLoader.deleteWorldDirectory(saveFolder);
		}
		buttonList.remove(deleteDebugWorldButton);
	}

	@Override
	protected void keyTyped(char character, int keyCode) {
		if (keyCode == 1) {
			mc.setIngameFocus();
			mc.displayGuiScreen(DebugHandler.INSTANCE.previousGuiScreen);
		} else {
			worldSeedTextField.textboxKeyTyped(character, keyCode);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		super.mouseClicked(mouseX, mouseY, button);
		worldSeedTextField.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialRenderTicks) {
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		renderSkybox(mouseX, mouseY, partialRenderTicks);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		Tessellator tessellator = Tessellator.instance;
		drawGradientRect(0, 0, width, height, 0x40FFFFFF, 0xFFFFFF);
		drawGradientRect(0, 0, width, height, 0, Integer.MIN_VALUE);
		drawCenteredString(fontRendererObj, ModInfo.NAME + " Debug", width / 2, 10, 0xFFFFFFFF);
		GL11.glColor4f(1, 1, 1, 1);
		tessellator.setColorOpaque_I(-1);
		worldSeedTextField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialRenderTicks);
	}

	private void drawPanorama(int mouseX, int mouseY, float partialRenderTicks) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		byte resolution = 8;
		for (int pass = 0; pass < resolution * resolution; ++pass) {
			GL11.glPushMatrix();
			float offsetX = ((float) (pass % resolution) / (float) resolution - 0.5F) / 64.0F;
			float offsetY = ((float) (pass / resolution) / (float) resolution - 0.5F) / 64.0F;
			GL11.glTranslatef(offsetX, offsetY, 0);
			GL11.glRotatef(MathHelper.sin((panoramaTimer + partialRenderTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-(panoramaTimer + partialRenderTicks) * 0.1F, 0.0F, 1.0F, 0.0F);
			for (int side = 0; side < 6; side++) {
				GL11.glPushMatrix();
				if (side == 1) {
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				} else if (side == 2) {
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				} else if (side == 3) {
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				} else if (side == 4) {
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				} else if (side == 5) {
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}
				mc.getTextureManager().bindTexture(PANORAMA_PATHS[side]);
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA_I(16777215, 255 / (pass + 1));
				tessellator.addVertexWithUV(-1.0D, -1.0D, 1.0D, 0.0F, 0.0F);
				tessellator.addVertexWithUV(1.0D, -1.0D, 1.0D, 1.0F, 0.0F);
				tessellator.addVertexWithUV(1.0D, 1.0D, 1.0D, 1.0F, 1.0F);
				tessellator.addVertexWithUV(-1.0D, 1.0D, 1.0D, 0.0F, 1.0F);
				tessellator.draw();
				GL11.glPopMatrix();
			}
			GL11.glPopMatrix();
			GL11.glColorMask(true, true, true, false);
		}
		tessellator.setTranslation(0.0D, 0.0D, 0.0D);
		GL11.glColorMask(true, true, true, true);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	private void rotateAndBlurSkybox(float partialRenderTicks) {
		mc.getTextureManager().bindTexture(panoramaResourceLocation);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glColorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		byte blurLevel = 3;

		for (int stage = 0; stage < blurLevel; stage++) {
			tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (stage + 1));
			int x = width;
			int y = height;
			float offset = (stage - blurLevel / 2) / 256.0F;
			tessellator.addVertexWithUV(x, y, zLevel, 0.0F + offset, 1.0D);
			tessellator.addVertexWithUV(x, 0.0D, zLevel, 1.0F + offset, 1.0D);
			tessellator.addVertexWithUV(0.0D, 0.0D, zLevel, 1.0F + offset, 0.0D);
			tessellator.addVertexWithUV(0.0D, y, zLevel, 0.0F + offset, 0.0D);
		}

		tessellator.draw();
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glColorMask(true, true, true, true);
	}

	private void renderSkybox(int mouseX, int mouseY, float partialRenderTicks) {
		mc.getFramebuffer().unbindFramebuffer();
		GL11.glViewport(0, 0, 256, 256);
		drawPanorama(mouseX, mouseY, partialRenderTicks);
		rotateAndBlurSkybox(partialRenderTicks);
		rotateAndBlurSkybox(partialRenderTicks);
		rotateAndBlurSkybox(partialRenderTicks);
		rotateAndBlurSkybox(partialRenderTicks);
		rotateAndBlurSkybox(partialRenderTicks);
		rotateAndBlurSkybox(partialRenderTicks);
		rotateAndBlurSkybox(partialRenderTicks);
		mc.getFramebuffer().bindFramebuffer(true);
		GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float scale = width > height ? 120.0F / width : 120.0F / height;
		float u = height * scale / 256.0F;
		float v = width * scale / 256.0F;
		tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
		int x = width;
		int y = height;
		tessellator.addVertexWithUV(0.0D, y, zLevel, 0.5F - u, 0.5F + v);
		tessellator.addVertexWithUV(x, y, zLevel, 0.5F - u, 0.5F - v);
		tessellator.addVertexWithUV(x, 0.0D, zLevel, 0.5F + u, 0.5F - v);
		tessellator.addVertexWithUV(0.0D, 0.0D, zLevel, 0.5F + u, 0.5F + v);
		tessellator.draw();
	}
}
