package thebetweenlands.common.item.herblore.rune.properties;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.runechain.container.IRuneContainerFactory;
import thebetweenlands.api.runechain.rune.RuneStats;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.rune.TokenRunePattern;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class PatternTokenRuneItemProperties extends RuneItemProperties {
	private static final ResourceLocation TEXTURE_MARK = new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/runes/pattern_rune_mark.png");
	private static final ResourceLocation TEXTURE_CENTER = new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/runes/pattern_rune_center.png");

	private static final String NBT_PATTERN_CENTER_X = "thebetweenlands.pattern_rune.pattern_center_x";
	private static final String NBT_PATTERN_CENTER_Y = "thebetweenlands.pattern_rune.pattern_center_y";
	private static final String NBT_PATTERN_CENTER_Z = "thebetweenlands.pattern_rune.pattern_center_z";
	private static final String NBT_PATTERN_BLOCKS = "thebetweenlands.pattern_rune.pattern_blocks";

	private final ResourceLocation regName;

	public PatternTokenRuneItemProperties(ResourceLocation regName) {
		this.regName = regName;
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		return new DefaultRuneContainerFactory(this.regName, () -> {
			List<BlockPos> pattern = new ArrayList<>();

			if(stack.hasTagCompound()) {
				NBTTagList blocks = stack.getTagCompound().getTagList(NBT_PATTERN_BLOCKS, Constants.NBT.TAG_LONG);

				for(int i = 0; i < blocks.tagCount(); i++) {
					pattern.add(BlockPos.fromLong(((NBTTagLong)blocks.get(i)).getLong()));
				}
			}

			return new TokenRunePattern.Blueprint(
					RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build(),
					pattern);
		});
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack stack = player.getHeldItem(hand);

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

		boolean hasPattern = this.hasPattern(stack);
		BlockPos center = this.getCenter(stack);

		boolean changed = false;

		if(player.isSneaking() || !hasPattern) {
			if(!player.isSneaking()) {
				if(!worldIn.isRemote) {
					player.sendStatusMessage(new TextComponentTranslation("chat.pattern_rune_set_origin"), true);
				}
			} else {
				if(center.equals(pos)) {
					nbt.removeTag(NBT_PATTERN_CENTER_X);
					nbt.removeTag(NBT_PATTERN_CENTER_Y);
					nbt.removeTag(NBT_PATTERN_CENTER_Z);
					nbt.removeTag(NBT_PATTERN_BLOCKS);
					worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_REMOVE, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
				} else {
					nbt.setInteger(NBT_PATTERN_CENTER_X, pos.getX());
					nbt.setInteger(NBT_PATTERN_CENTER_Y, pos.getY());
					nbt.setInteger(NBT_PATTERN_CENTER_Z, pos.getZ());
					worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_ADD, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
				}

				changed = true;
			}
		} else {
			NBTTagList blocks = nbt.getTagList(NBT_PATTERN_BLOCKS, Constants.NBT.TAG_LONG);

			boolean contained = false;

			for(int i = 0; i < blocks.tagCount(); i++) {
				BlockPos block = BlockPos.fromLong(((NBTTagLong)blocks.get(i)).getLong()).add(center);

				if(block.equals(pos)) {
					blocks.removeTag(i);
					changed = true;
					contained = true;
				}
			}

			if(!contained) {
				blocks.appendTag(new NBTTagLong(pos.subtract(center).toLong()));
				changed = true;
				worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_ADD, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
			} else {
				worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_REMOVE, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
			}

			nbt.setTag(NBT_PATTERN_BLOCKS, blocks);
		}

		if(changed && player == TheBetweenlands.proxy.getClientPlayer()) {
			currentPatternCenter = null;
			currentPattern = null;
		}

		return EnumActionResult.SUCCESS;
	}

	public boolean hasPattern(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt != null && nbt.hasKey(NBT_PATTERN_CENTER_X, Constants.NBT.TAG_INT) && nbt.hasKey(NBT_PATTERN_CENTER_Y, Constants.NBT.TAG_INT) && nbt.hasKey(NBT_PATTERN_CENTER_Z, Constants.NBT.TAG_INT);
	}

	public BlockPos getCenter(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt == null ? BlockPos.ORIGIN : new BlockPos(nbt.getInteger(NBT_PATTERN_CENTER_X), nbt.getInteger(NBT_PATTERN_CENTER_Y), nbt.getInteger(NBT_PATTERN_CENTER_Z));
	}

	public List<BlockPos> getPattern(ItemStack stack) {
		List<BlockPos> pattern = new ArrayList<>();

		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null) {
			NBTTagList blocks = nbt.getTagList(NBT_PATTERN_BLOCKS, Constants.NBT.TAG_LONG);

			for(int i = 0; i < blocks.tagCount(); i++) {
				pattern.add(BlockPos.fromLong(((NBTTagLong)blocks.get(i)).getLong()));
			}
		}

		return pattern;
	}

	private static BlockPos currentPatternCenter = null;
	private static List<BlockPos> currentPattern = null;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		boolean holdingPattern = false;

		for(ItemStack stack : Minecraft.getMinecraft().player.getHeldEquipment()) {
			if(!stack.isEmpty() && stack.getItem() instanceof ItemRune) {
				RuneItemProperties properties = ((ItemRune) stack.getItem()).getProperties(stack);

				if(properties instanceof PatternTokenRuneItemProperties) {
					PatternTokenRuneItemProperties rune = (PatternTokenRuneItemProperties) properties;

					if(rune.hasPattern(stack)) {
						holdingPattern = true;

						if(currentPatternCenter == null || currentPattern == null) {
							currentPatternCenter = rune.getCenter(stack);
							currentPattern = rune.getPattern(stack);
						}

						GlStateManager.pushMatrix();

						GlStateManager.enableTexture2D();
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
						GlStateManager.glLineWidth(1F);
						GlStateManager.depthMask(false);

						GlStateManager.doPolygonOffset(-0.1F, -10.0F);
						GlStateManager.enablePolygonOffset();

						float alpha = (MathHelper.cos((Minecraft.getMinecraft().player.ticksExisted + event.getPartialTicks()) * 0.2f) + 1) * 0.5f * 0.3f + 0.5f;

						GlStateManager.color(1, 1, 1, alpha);

						RenderManager rm = Minecraft.getMinecraft().getRenderManager();

						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder buffer = tessellator.getBuffer();

						Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_CENTER);

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

						buffer.setTranslation(currentPatternCenter.getX() - rm.viewerPosX, currentPatternCenter.getY() - rm.viewerPosY, currentPatternCenter.getZ() - rm.viewerPosZ);

						buffer.pos(0, 0, 1).tex(0, 0).normal(0, 0, 1).endVertex();
						buffer.pos(1, 0, 1).tex(1, 0).normal(0, 0, 1).endVertex();
						buffer.pos(1, 1, 1).tex(1, 1).normal(0, 0, 1).endVertex();
						buffer.pos(0, 1, 1).tex(0, 1).normal(0, 0, 1).endVertex();

						buffer.pos(0, 0, 0).tex(0, 0).normal(0, 0, -1).endVertex();
						buffer.pos(0, 1, 0).tex(0, 1).normal(0, 0, -1).endVertex();
						buffer.pos(1, 1, 0).tex(1, 1).normal(0, 0, -1).endVertex();
						buffer.pos(1, 0, 0).tex(1, 0).normal(0, 0, -1).endVertex();

						buffer.pos(0, 0, 0).tex(0, 0).normal(-1, 0, 0).endVertex();
						buffer.pos(0, 0, 1).tex(1, 0).normal(-1, 0, 0).endVertex();
						buffer.pos(0, 1, 1).tex(1, 1).normal(-1, 0, 0).endVertex();
						buffer.pos(0, 1, 0).tex(0, 1).normal(-1, 0, 0).endVertex();

						buffer.pos(1, 0, 0).tex(0, 0).normal(1, 0, 0).endVertex();
						buffer.pos(1, 1, 0).tex(0, 1).normal(1, 0, 0).endVertex();
						buffer.pos(1, 1, 1).tex(1, 1).normal(1, 0, 0).endVertex();
						buffer.pos(1, 0, 1).tex(1, 0).normal(1, 0, 0).endVertex();

						buffer.pos(0, 1, 0).tex(0, 0).normal(0, 1, 0).endVertex();
						buffer.pos(0, 1, 1).tex(0, 1).normal(0, 1, 0).endVertex();
						buffer.pos(1, 1, 1).tex(1, 1).normal(0, 1, 0).endVertex();
						buffer.pos(1, 1, 0).tex(1, 0).normal(0, 1, 0).endVertex();

						buffer.pos(0, 0, 0).tex(0, 0).normal(0, -1, 0).endVertex();
						buffer.pos(1, 0, 0).tex(1, 0).normal(0, -1, 0).endVertex();
						buffer.pos(1, 0, 1).tex(1, 1).normal(0, -1, 0).endVertex();
						buffer.pos(0, 0, 1).tex(0, 1).normal(0, -1, 0).endVertex();

						tessellator.draw();

						Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_MARK);

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

						for(BlockPos pos : currentPattern) {

							buffer.setTranslation(pos.getX() + currentPatternCenter.getX() - rm.viewerPosX, pos.getY() + currentPatternCenter.getY() - rm.viewerPosY, pos.getZ() + currentPatternCenter.getZ() - rm.viewerPosZ);

							buffer.pos(0, 0, 1).tex(0, 0).normal(0, 0, 1).endVertex();
							buffer.pos(1, 0, 1).tex(1, 0).normal(0, 0, 1).endVertex();
							buffer.pos(1, 1, 1).tex(1, 1).normal(0, 0, 1).endVertex();
							buffer.pos(0, 1, 1).tex(0, 1).normal(0, 0, 1).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(0, 0, -1).endVertex();
							buffer.pos(0, 1, 0).tex(0, 1).normal(0, 0, -1).endVertex();
							buffer.pos(1, 1, 0).tex(1, 1).normal(0, 0, -1).endVertex();
							buffer.pos(1, 0, 0).tex(1, 0).normal(0, 0, -1).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 0, 1).tex(1, 0).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 1, 1).tex(1, 1).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 1, 0).tex(0, 1).normal(-1, 0, 0).endVertex();

							buffer.pos(1, 0, 0).tex(0, 0).normal(1, 0, 0).endVertex();
							buffer.pos(1, 1, 0).tex(0, 1).normal(1, 0, 0).endVertex();
							buffer.pos(1, 1, 1).tex(1, 1).normal(1, 0, 0).endVertex();
							buffer.pos(1, 0, 1).tex(1, 0).normal(1, 0, 0).endVertex();

							buffer.pos(0, 1, 0).tex(0, 0).normal(0, 1, 0).endVertex();
							buffer.pos(0, 1, 1).tex(0, 1).normal(0, 1, 0).endVertex();
							buffer.pos(1, 1, 1).tex(1, 1).normal(0, 1, 0).endVertex();
							buffer.pos(1, 1, 0).tex(1, 0).normal(0, 1, 0).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(0, -1, 0).endVertex();
							buffer.pos(1, 0, 0).tex(1, 0).normal(0, -1, 0).endVertex();
							buffer.pos(1, 0, 1).tex(1, 1).normal(0, -1, 0).endVertex();
							buffer.pos(0, 0, 1).tex(0, 1).normal(0, -1, 0).endVertex();
						}

						tessellator.draw();

						buffer.setTranslation(0, 0, 0);

						GlStateManager.disablePolygonOffset();

						GlStateManager.color(1, 1, 1, 1);
						GlStateManager.depthMask(true);
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
						GlStateManager.enableTexture2D();
						GlStateManager.enableDepth();
						GlStateManager.disableBlend();

						GlStateManager.popMatrix();
					}
				}
			}
		}

		if(!holdingPattern) {
			currentPattern = null;
			currentPatternCenter = null;
		}
	}
}
