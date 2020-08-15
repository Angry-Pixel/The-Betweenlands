package thebetweenlands.common.item.herblore.rune.properties;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
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
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.rune.IRuneContainerFactory;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.misc.BlockBurntScrivenerMark;
import thebetweenlands.common.herblore.rune.TokenRuneFormation;
import thebetweenlands.common.item.herblore.rune.DefaultRuneContainerFactory;
import thebetweenlands.common.item.herblore.rune.ItemRune;
import thebetweenlands.common.item.herblore.rune.ItemRune.RuneItemProperties;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.AspectRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.NBTHelper;

public class FormationTokenRuneItemProperties extends RuneItemProperties {
	private static final ResourceLocation TEXTURE_MARK = new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/runes/formation_rune_mark.png");
	private static final ResourceLocation TEXTURE_MARK_SIDE = new ResourceLocation(ModInfo.ID, "textures/items/strictly_herblore/runes/formation_rune_mark_side.png");

	private static final String NBT_FORMATION_BLOCKS = "thebetweenlands.formation_rune.formation_blocks";

	private final ResourceLocation regName;

	public FormationTokenRuneItemProperties(ResourceLocation regName) {
		this.regName = regName;
	}

	@Override
	public IRuneContainerFactory getFactory(ItemStack stack) {
		return new DefaultRuneContainerFactory(this.regName, () -> {
			List<BlockPos> formation = new ArrayList<>();

			if(stack.hasTagCompound()) {
				NBTTagList blocks = stack.getTagCompound().getTagList(NBT_FORMATION_BLOCKS, Constants.NBT.TAG_COMPOUND);

				for(int i = 0; i < blocks.tagCount(); i++) {
					formation.add(NBTUtil.getPosFromTag(blocks.getCompoundTagAt(i)));
				}
			}

			return new TokenRuneFormation.Blueprint(
					RuneStats.builder()
					.aspect(AspectRegistry.ORDANIIS, 1)
					.duration(5.0f)
					.build(),
					formation);
		});
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		ItemStack stack = player.getHeldItem(hand);

		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(stack);

		boolean changed = false;

		if(player.isSneaking()) {
			if(nbt.hasKey(NBT_FORMATION_BLOCKS, Constants.NBT.TAG_LIST)) {
				changed = true;
				nbt.removeTag(NBT_FORMATION_BLOCKS);

				worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_REMOVE, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);

				return EnumActionResult.SUCCESS;
			}
		} else {
			IBlockState state = worldIn.getBlockState(pos);

			if(state.getBlock() instanceof BlockBurntScrivenerMark) {
				if(nbt.hasKey(NBT_FORMATION_BLOCKS)) {
					//TODO Already linked message
				} else {
					NBTTagList blocks = new NBTTagList();

					//TODO Limit number of blocks

					Set<BlockPos> checked = new HashSet<>();
					Deque<BlockPos> queue = new LinkedList<>();

					queue.add(pos);

					while(!queue.isEmpty()) {
						BlockPos queuePos = queue.removeFirst();

						if(worldIn.isBlockLoaded(queuePos, false)) {
							state = worldIn.getBlockState(queuePos);

							if(state.getBlock() instanceof BlockBurntScrivenerMark) {
								if(!worldIn.isRemote) {
									worldIn.setBlockState(queuePos, state.withProperty(BlockBurntScrivenerMark.LINKED, true));
								}

								blocks.appendTag(NBTUtil.createPosTag(queuePos));

								for(EnumFacing offset : EnumFacing.HORIZONTALS) {
									for(int yo = -1; yo <= 1; yo++) {
										BlockPos offsetPos = queuePos.add(offset.getXOffset(), offset.getYOffset() + yo, offset.getZOffset());

										if(checked.add(offsetPos)) {
											queue.addLast(offsetPos);
										}
									}
								}
							}
						}
					}

					if(!blocks.isEmpty()) {
						changed = true;
						nbt.setTag(NBT_FORMATION_BLOCKS, blocks);

						worldIn.playSound(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, SoundRegistry.PATTERN_RUNE_ADD, SoundCategory.PLAYERS, 0.5f, 0.9f + 0.2f * worldIn.rand.nextFloat(), false);
					}
				}

				return EnumActionResult.SUCCESS;
			}
		}

		if(changed && player == TheBetweenlands.proxy.getClientPlayer()) {
			currentFormation = null;
		}

		return EnumActionResult.PASS;
	}

	public boolean hasFormation(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt != null && nbt.hasKey(NBT_FORMATION_BLOCKS, Constants.NBT.TAG_LIST);
	}

	public List<BlockPos> getFormation(ItemStack stack) {
		List<BlockPos> pattern = new ArrayList<>();

		NBTTagCompound nbt = stack.getTagCompound();

		if(nbt != null) {
			NBTTagList blocks = nbt.getTagList(NBT_FORMATION_BLOCKS, Constants.NBT.TAG_COMPOUND);

			for(int i = 0; i < blocks.tagCount(); i++) {
				pattern.add(NBTUtil.getPosFromTag(blocks.getCompoundTagAt(i)));
			}
		}

		return pattern;
	}

	private static List<BlockPos> currentFormation = null;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		boolean holdingFormation = false;

		for(ItemStack stack : Minecraft.getMinecraft().player.getHeldEquipment()) {
			if(!stack.isEmpty() && stack.getItem() instanceof ItemRune) {
				RuneItemProperties properties = ((ItemRune) stack.getItem()).getProperties(stack);

				if(properties instanceof FormationTokenRuneItemProperties) {
					FormationTokenRuneItemProperties rune = (FormationTokenRuneItemProperties) properties;

					if(rune.hasFormation(stack)) {
						holdingFormation = true;

						if(currentFormation == null) {
							currentFormation = rune.getFormation(stack);
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

						Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_MARK);

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

						for(BlockPos pos : currentFormation) {

							buffer.setTranslation(pos.getX() - rm.viewerPosX, pos.getY() - rm.viewerPosY, pos.getZ() - rm.viewerPosZ);

							buffer.pos(0, 0.3125f, 0).tex(0, 0).normal(0, 1, 0).endVertex();
							buffer.pos(0, 0.3125f, 1).tex(0, 1).normal(0, 1, 0).endVertex();
							buffer.pos(1, 0.3125f, 1).tex(1, 1).normal(0, 1, 0).endVertex();
							buffer.pos(1, 0.3125f, 0).tex(1, 0).normal(0, 1, 0).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(0, -1, 0).endVertex();
							buffer.pos(1, 0, 0).tex(1, 0).normal(0, -1, 0).endVertex();
							buffer.pos(1, 0, 1).tex(1, 1).normal(0, -1, 0).endVertex();
							buffer.pos(0, 0, 1).tex(0, 1).normal(0, -1, 0).endVertex();
						}

						tessellator.draw();

						Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE_MARK_SIDE);

						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

						for(BlockPos pos : currentFormation) {

							buffer.setTranslation(pos.getX() - rm.viewerPosX, pos.getY() - rm.viewerPosY, pos.getZ() - rm.viewerPosZ);

							buffer.pos(0, 0, 1).tex(0, 0).normal(0, 0, 1).endVertex();
							buffer.pos(1, 0, 1).tex(1, 0).normal(0, 0, 1).endVertex();
							buffer.pos(1, 0.3125f, 1).tex(1, 1).normal(0, 0, 1).endVertex();
							buffer.pos(0, 0.3125f, 1).tex(0, 1).normal(0, 0, 1).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(0, 0, -1).endVertex();
							buffer.pos(0, 0.3125f, 0).tex(0, 1).normal(0, 0, -1).endVertex();
							buffer.pos(1, 0.3125f, 0).tex(1, 1).normal(0, 0, -1).endVertex();
							buffer.pos(1, 0, 0).tex(1, 0).normal(0, 0, -1).endVertex();

							buffer.pos(0, 0, 0).tex(0, 0).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 0, 1).tex(1, 0).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 0.3125f, 1).tex(1, 1).normal(-1, 0, 0).endVertex();
							buffer.pos(0, 0.3125f, 0).tex(0, 1).normal(-1, 0, 0).endVertex();

							buffer.pos(1, 0, 0).tex(0, 0).normal(1, 0, 0).endVertex();
							buffer.pos(1, 0.3125f, 0).tex(0, 1).normal(1, 0, 0).endVertex();
							buffer.pos(1, 0.3125f, 1).tex(1, 1).normal(1, 0, 0).endVertex();
							buffer.pos(1, 0, 1).tex(1, 0).normal(1, 0, 0).endVertex();
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

		if(!holdingFormation) {
			currentFormation = null;
		}
	}
}
