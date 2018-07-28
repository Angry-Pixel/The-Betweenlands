package thebetweenlands.client.gui.inventory;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.container.ContainerRuneChainAltar;
import thebetweenlands.common.inventory.slot.SlotRune;
import thebetweenlands.common.network.serverbound.MessageSetRuneChainAltarPage;
import thebetweenlands.common.network.serverbound.MessageShiftRuneChainAltarSlot;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;
import thebetweenlands.util.ColoredItemRenderer;

@SideOnly(Side.CLIENT)
public class GuiRuneChainAltar extends GuiContainer {
	private static final Random rand = new Random();

	private static final ResourceLocation GUI_RUNE_CHAIN_ALTAR = new ResourceLocation("thebetweenlands:textures/gui/rune_chain_altar.png");
	private final TileEntityRuneChainAltar tile;
	private final ContainerRuneChainAltar container;
	private final EntityPlayer player;

	private static final int SWAP_ANIMATION_HALF_DURATION = 7;
	private int lastSwapAnimationTicks = 0;
	private int swapAnimationTicks = 0;
	private int newPage = 0;

	private static final int[][] CORD_PIECE_SLOT_UVs = new int[][] {
		{10, 251}, {36, 266}, {60, 261}, {84, 260}, {108, 260}, {132, 261}, {156, 266},
		{180, 274},
		{156, 312}, {132, 315}, {108, 318}, {84, 318}, {60, 315}, {36, 312}, {10, 309}
	};

	private static final int[][] CORD_PIECE_UVs = new int[][] {
		{248, 0}, {274, 15}, {298, 10}, {322, 9}, {346, 9}, {370, 10}, {394, 15},
		{418, 23},
		{394, 61}, {370, 64}, {346, 67}, {322, 67}, {298, 64}, {274, 61}, {248, 58}
	};

	private static final int[][] CORD_PIECE_SIZES = new int[][] {
		{10, 22}, {6, 6}, {6, 5}, {6, 4}, {6, 4}, {6, 5}, {6, 6},
		{9, 34},
		{6, 6}, {6, 5}, {6, 4}, {6, 4}, {6, 5}, {6, 6}, {10, 22}
	};

	public GuiRuneChainAltar(EntityPlayer player, TileEntityRuneChainAltar tile) {
		super(new ContainerRuneChainAltar(player.inventory, tile));
		this.container = (ContainerRuneChainAltar) this.inventorySlots;
		this.tile = tile;
		this.allowUserInput = false;
		this.xSize = 176;
		this.ySize = 224;
		this.player = player;
	}

	protected float preRenderSlab() {
		GlStateManager.pushMatrix();
		if(this.swapAnimationTicks > 0) {
			float tick = this.lastSwapAnimationTicks + (this.swapAnimationTicks - this.lastSwapAnimationTicks) * this.mc.getRenderPartialTicks();

			float easeInOut = 0.0F;

			if(tick <= SWAP_ANIMATION_HALF_DURATION) {
				easeInOut = this.easeInOutCubic(tick, 0, 1, SWAP_ANIMATION_HALF_DURATION);
				GlStateManager.translate(0, easeInOut * -130, 0);
			} else {
				easeInOut = 1 - this.easeInOutCubic(tick - SWAP_ANIMATION_HALF_DURATION, 0, 1, SWAP_ANIMATION_HALF_DURATION);
				GlStateManager.translate(0, -easeInOut * 130, 0);
			}

			float alpha = 1.0F - easeInOut;

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

			return alpha;
		}

		return 1.0F;
	}

	protected void postRenderSlab() {
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.popMatrix();
	}

	protected boolean isSlabSlot(Slot slot) {
		return slot.slotNumber < this.tile.getMaxChainLength() + 1;
	}

	protected float easeInOutCubic(float time, float start, float change, float duration) {
		time /= duration/2;
		if (time < 1) return change/2*time*time*time + start;
		time -= 2;
		return change/2*(time*time*time + 2) + start;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(this.swapAnimationTicks > 0 && this.hoveredSlot != null && this.isSlabSlot(this.hoveredSlot)) {
			this.hoveredSlot = null;
		}
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void drawSlot(Slot slot) {
		if(this.swapAnimationTicks > 0 && this.isSlabSlot(slot)) {
			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;
			GlStateManager.enableDepth();

			float alpha = this.preRenderSlab();

			int x = slot.xPos;
			int y = slot.yPos;
			ItemStack itemstack = slot.getStack();

			ColoredItemRenderer.renderItemAndEffectIntoGUI(this.itemRender, this.mc.player, itemstack, x, y, 1, 1, 1, alpha);
			ColoredItemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, x, y, null, 1, 1, 1, alpha);

			this.postRenderSlab();

			this.itemRender.zLevel = 0.0F;
			this.zLevel = 0.0F;
		} else {
			super.drawSlot(slot);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		this.mc.getTextureManager().bindTexture(GUI_RUNE_CHAIN_ALTAR);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();

		this.preRenderSlab();
		this.drawSlabBackground(this.guiLeft - 11, this.guiTop + 4);
		this.postRenderSlab();

		this.drawTexturedModalRect512(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		this.preRenderSlab();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		this.drawSlabForeground(this.guiLeft - 11, this.guiTop + 4);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		this.postRenderSlab();

		if(this.container.getCurrentPage().index > 0) {
			this.drawUpArrow(this.guiLeft + this.xSize + 2, this.guiTop + 18, x, y, this.preRenderSlab());
			this.postRenderSlab();
		}

		if(this.container.getCurrentPage().index < this.container.getPages() - 1) {
			this.drawDownArrow(this.guiLeft + this.xSize + 2, this.guiTop + 100, x, y, this.preRenderSlab());
			this.postRenderSlab();
		}

		this.preRenderSlab();

		for(int i = 0; i < Math.min(ContainerRuneChainAltar.SLOTS_PER_PAGE + 1, this.tile.getChainLength() - this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE); i++) {
			this.drawCordPiece(this.guiLeft - 11, this.guiTop + 4, i);
		}

		for(int i = this.tile.getChainLength() + 1; i < this.tile.getMaxChainLength(); i++) {
			SlotRune slot = (SlotRune) this.inventorySlots.getSlot(i + TileEntityRuneChainAltar.NON_INPUT_SLOTS);

			if(slot.getPage().isCurrent()) {
				this.drawSlotCoverStone(this.guiLeft + slot.xPos, this.guiTop + slot.yPos);
			}
		}
		int unfilledSlots = Math.max(0, ContainerRuneChainAltar.SLOTS_PER_PAGE - (this.tile.getMaxChainLength() - this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE));
		for(int i = ContainerRuneChainAltar.SLOTS_PER_PAGE - unfilledSlots; i < ContainerRuneChainAltar.SLOTS_PER_PAGE; i++) {
			this.drawSlotCoverStone(this.guiLeft + ContainerRuneChainAltar.SLOT_POSITIONS[i][0], this.guiTop + ContainerRuneChainAltar.SLOT_POSITIONS[i][1]);
		}

		this.postRenderSlab();

		for(int i = 0; i < ContainerRuneChainAltar.SLOTS_PER_PAGE; i++) {
			int slotIndex = this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE + TileEntityRuneChainAltar.NON_INPUT_SLOTS + i;

			SlotRune slot = (SlotRune) this.inventorySlots.getSlot(slotIndex);

			if(slot.getHasStack() && x >= this.guiLeft + slot.xPos - 4 && x <= this.guiLeft + slot.xPos + 16 + 3 && y >= this.guiTop + slot.yPos - 10 && y <= this.guiTop + slot.yPos + 16 + 10) {
				if(this.container.getShiftHoleSlot(slotIndex, false) >= 0) {
					if(i >= ContainerRuneChainAltar.SLOTS_PER_PAGE / 2) {
						this.drawShiftLeftArrow(this.guiLeft + slot.xPos + 2, this.guiTop + slot.yPos - 9, x, y, this.preRenderSlab());
						this.postRenderSlab();
					} else {
						this.drawShiftRightArrow(this.guiLeft + slot.xPos + 2, this.guiTop + slot.yPos - 9, x, y, this.preRenderSlab());
						this.postRenderSlab();
					}
				}

				if(this.container.getShiftHoleSlot(slotIndex, true) >= 0) {
					if(i >= ContainerRuneChainAltar.SLOTS_PER_PAGE / 2) {
						this.drawShiftRightArrow(this.guiLeft + slot.xPos + 2, this.guiTop + slot.yPos + 9 + 9, x, y, this.preRenderSlab());
						this.postRenderSlab();
					} else {
						this.drawShiftLeftArrow(this.guiLeft + slot.xPos + 2, this.guiTop + slot.yPos + 9 + 9, x, y, this.preRenderSlab());
						this.postRenderSlab();
					}
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(this.swapAnimationTicks == 0) {
			if(mouseButton == 0) {
				{
					int relX = mouseX - (this.guiLeft + this.xSize + 2);
					int relY = mouseY - (this.guiTop + 20);
					if(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24) {
						int currPage = this.container.getCurrentPage().index;
						if(currPage > 0) {
							this.newPage = currPage - 1;
							TheBetweenlands.networkWrapper.sendToServer(new MessageSetRuneChainAltarPage(this.newPage));
							this.swapAnimationTicks = 1;
						}
						return;
					}
				}
				{
					int relX = mouseX - (this.guiLeft + this.xSize + 2);
					int relY = mouseY - (this.guiTop + 100);
					if(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24) {
						int currPage = this.container.getCurrentPage().index;
						if(currPage < this.container.getPages() - 1) {
							this.newPage = currPage + 1;
							TheBetweenlands.networkWrapper.sendToServer(new MessageSetRuneChainAltarPage(this.newPage));
							this.swapAnimationTicks = 1;
						}
						return;
					}
				}

				for(int i = 0; i < ContainerRuneChainAltar.SLOTS_PER_PAGE; i++) {
					int slotIndex = this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE + TileEntityRuneChainAltar.NON_INPUT_SLOTS + i;

					SlotRune slot = (SlotRune) this.inventorySlots.getSlot(slotIndex);

					if(slot.getHasStack() && mouseX >= this.guiLeft + slot.xPos - 4 && mouseX <= this.guiLeft + slot.xPos + 16 + 3 && mouseY >= this.guiTop + slot.yPos - 10 && mouseY <= this.guiTop + slot.yPos + 16 + 10) {
						if(this.container.getShiftHoleSlot(slotIndex, false) >= 0) {
							int relX = mouseX - (this.guiLeft + slot.xPos + 2);
							int relY = mouseY - (this.guiTop + slot.yPos - 9);
							if(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7) {
								TheBetweenlands.networkWrapper.sendToServer(new MessageShiftRuneChainAltarSlot(slotIndex, false));
								this.mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.RUNE_SLOT_SHIFT, rand.nextFloat() * 0.066F + 0.933F, 1.0F));
								return;
							}
						}

						if(this.container.getShiftHoleSlot(slotIndex, true) >= 0) {
							int relX = mouseX - (this.guiLeft + slot.xPos + 2);
							int relY = mouseY - (this.guiTop + slot.yPos + 9 + 9);
							if(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7) {
								TheBetweenlands.networkWrapper.sendToServer(new MessageShiftRuneChainAltarSlot(slotIndex, true));
								this.mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.RUNE_SLOT_SHIFT, rand.nextFloat() * 0.066F + 0.933F, 1.0F));
								return;
							}
						}
					}
				}
			}

			super.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		this.lastSwapAnimationTicks = this.swapAnimationTicks;
		if(this.swapAnimationTicks > 0) {
			if(this.swapAnimationTicks == 1) {
				this.mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.RUNE_SLATE_MOVE, rand.nextFloat() * 0.066F + 0.933F, 1.0F));
				this.container.getCurrentPage().interactable = false;
			}
			if(this.swapAnimationTicks == SWAP_ANIMATION_HALF_DURATION) {
				this.container.getCurrentPage().interactable = true;
				this.container.setCurrentPage(this.newPage);
				this.container.getCurrentPage().interactable = false;
			}
			if(this.swapAnimationTicks >= SWAP_ANIMATION_HALF_DURATION * 2) {
				this.container.getCurrentPage().interactable = true;
				this.swapAnimationTicks = 0;
			} else {
				this.swapAnimationTicks++;
			}
		}
	}

	protected void drawSlotCoverStone(float x, float y) {
		this.drawTexturedModalRect512(x, y, 216, 17, 16, 16);
	}

	protected void drawUpArrow(float x, float y, int mouseX, int mouseY, float alpha) {
		int relX = mouseX - (int) x;
		int relY = mouseY - (int) y;
		if(this.swapAnimationTicks != 0 || !(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24)) {
			GlStateManager.color(0.7F, 0.7F, 0.7F, alpha);
		}
		this.drawTexturedModalRect512(x, y, 187, 16, 15, 24);
		GlStateManager.color(1, 1, 1);
	}

	protected void drawDownArrow(float x, float y, int mouseX, int mouseY, float alpha) {
		int relX = mouseX - (int) x;
		int relY = mouseY - (int) y;
		if(this.swapAnimationTicks != 0 || !(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24)) {
			GlStateManager.color(0.7F, 0.7F, 0.7F, alpha);
		}
		this.drawTexturedModalRect512(x, y, 187, 108, 15, 24);
		GlStateManager.color(1, 1, 1);
	}

	protected void drawShiftLeftArrow(float x, float y, int mouseX, int mouseY, float alpha) {
		int relX = mouseX - (int) x;
		int relY = mouseY - (int) y;
		if(this.swapAnimationTicks != 0 || !(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7)) {
			GlStateManager.color(0.7F, 0.7F, 0.7F, alpha);
		}
		this.drawTexturedModalRect512(x, y, 452, 0, 13, 7);
		GlStateManager.color(1, 1, 1);
	}

	protected void drawShiftRightArrow(float x, float y, int mouseX, int mouseY, float alpha) {
		int relX = mouseX - (int) x;
		int relY = mouseY - (int) y;
		if(this.swapAnimationTicks != 0 || !(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7)) {
			GlStateManager.color(0.7F, 0.7F, 0.7F, alpha);
		}
		this.drawTexturedModalRect512(x, y, 452, 29, 13, 7);
		GlStateManager.color(1, 1, 1);
	}

	protected void drawSlabForeground(float x, float y) {
		this.drawTexturedModalRect512(x, y, 0, 224, 198, 134);
	}

	protected void drawSlabBackground(float x, float y) {
		this.drawTexturedModalRect512(x, y, 0, 357, 198, 134);
	}

	protected void drawCordPiece(float slabX, float slabY, int piece) {
		this.drawCordPiecePositioned(CORD_PIECE_SLOT_UVs[piece][0] + slabX, CORD_PIECE_SLOT_UVs[piece][1] - 224 + slabY, piece);
	}

	protected void drawCordPiecePositioned(float x, float y, int piece) {
		this.drawTexturedModalRect512(x, y, CORD_PIECE_UVs[piece][0], CORD_PIECE_UVs[piece][1], CORD_PIECE_SIZES[piece][0], CORD_PIECE_SIZES[piece][1]);
	}

	/**
	 * Same as {@link #drawTexturedModalRect(int, int, int, int, int, int)} but for 512x512 textures
	 */
	protected void drawTexturedModalRect512(float x, float y, int minU, int minV, int width, int height) {
		float scale = 0.001953125F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + height) * scale)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + height) * scale)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + 0) * scale)).endVertex();
		bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + 0) * scale)).endVertex();
		tessellator.draw();
	}
}
