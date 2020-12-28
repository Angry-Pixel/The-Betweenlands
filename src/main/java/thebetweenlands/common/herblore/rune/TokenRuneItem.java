package thebetweenlands.common.herblore.rune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.rune.INodeComposition;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneItemStackAccess;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.api.rune.impl.ISetter;
import thebetweenlands.api.rune.impl.InventoryRuneItemStackAccess;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.api.rune.impl.RuneConfiguration;
import thebetweenlands.api.rune.impl.RuneEffectModifier;
import thebetweenlands.api.rune.impl.RuneStats;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.util.LightingUtil;

public final class TokenRuneItem extends AbstractRune<TokenRuneItem> {

	public static final class Blueprint extends AbstractRune.Blueprint<TokenRuneItem> {
		private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

		private final ItemStack item;
		private final Predicate<ItemStack> itemFilter;

		public Blueprint(RuneStats stats, ItemStack item, Predicate<ItemStack> itemFilter) {
			super(stats);
			this.item = item;
			this.itemFilter = itemFilter;
		}

		public static final RuneConfiguration CONFIGURATION_1;
		private static final ISetter<IRuneItemStackAccess> OUT_ITEM_1;

		public static final RuneConfiguration CONFIGURATION_2;
		private static final ISetter<Collection<IRuneItemStackAccess>> OUT_ITEMS_2;

		static {
			RuneConfiguration.Builder builder = RuneConfiguration.create();

			OUT_ITEM_1 = builder.out(RuneTokenDescriptors.ITEM).type(IRuneItemStackAccess.class).setter();
			CONFIGURATION_1 = builder.build();

			OUT_ITEMS_2 = builder.out(RuneTokenDescriptors.ITEM).type(IRuneItemStackAccess.class).collection().setter();
			CONFIGURATION_2 = builder.build();
		}

		@Override
		public List<RuneConfiguration> getConfigurations(IConfigurationLinkAccess linkAccess, boolean provisional) {
			return ImmutableList.of(CONFIGURATION_1, CONFIGURATION_2);
		}

		@Override
		public TokenRuneItem create(int index, INodeComposition<RuneExecutionContext> composition, INodeConfiguration configuration) {
			return new TokenRuneItem(this, index, composition, (RuneConfiguration) configuration);
		}

		@Override
		protected RuneEffectModifier.Subject activate(TokenRuneItem state, RuneExecutionContext context, INodeIO io) {

			IInventory inventory = context.getUser().getInventory();

			if(inventory != null) {
				List<IRuneItemStackAccess> accesses = new ArrayList<>();

				io.schedule(scheduler -> {
					int i = scheduler.getUpdateCount();

					if(i < inventory.getSizeInventory()) {
						ItemStack stack = inventory.getStackInSlot(i);

						if(!stack.isEmpty() && this.itemFilter.test(stack)) {
							Entity entity = context.getUser().getEntity();

							IRuneItemStackAccess access = new InventoryRuneItemStackAccess(inventory, i,
									s -> !s.isEmpty() && this.itemFilter.test(s) && (entity == null || entity.isEntityAlive()),
									s -> s.isEmpty() && (entity == null || entity.isEntityAlive()));

							if(state.getConfiguration() == CONFIGURATION_1) {
								OUT_ITEM_1.set(io, access);
								scheduler.terminate();
							} else {
								accesses.add(access);
							}

						}

						scheduler.sleep(0.05f);
					} else {
						if(state.getConfiguration() == CONFIGURATION_2) {
							OUT_ITEMS_2.set(io, accesses);
						}

						scheduler.terminate();
					}
				});
			} else {
				io.fail();
			}

			return null;
		}

		@Override
		protected RuneEffectModifier createRuneEffectModifier(TokenRuneItem state, AbstractRune<?> target, AbstractRune<?> ioNode, int ioIndex) {
			return new RuneEffectModifier() {
				class State extends RenderState {
					private int rotation;

					@Override
					protected void tick() {
						this.rotation++;
					}
				}

				@SideOnly(Side.CLIENT)
				@Override
				public void render(Subject subject, int index, RenderProperties properties, RenderState state, float partialTicks) {
					float scale = Math.min(properties.sizeX, Math.min(properties.sizeY, properties.sizeZ));

					GlStateManager.pushMatrix();
					GlStateManager.scale(scale, scale, scale);

					if(state != null && !properties.fixed) {
						State rotationState = state.get(State.class, State::new);

						GlStateManager.rotate((rotationState.rotation + partialTicks) * 10, 0, 1, 0);
					}

					TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
					RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

					IBakedModel model = renderItem.getItemModelMesher().getItemModel(TokenRuneItem.Blueprint.this.item);

					textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					ITextureObject texture = textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					texture.setBlurMipmap(false, false);

					GlStateManager.enableBlend();
					GlStateManager.color(properties.red, properties.green, properties.blue, properties.alpha);
					GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

					ItemStack stack = TokenRuneItem.Blueprint.this.item;

					//Modified RenderItem.renderItem
					if(!stack.isEmpty()) {
						GlStateManager.pushMatrix();
						GlStateManager.translate(-0.5F, -0.5F, -0.5F);

						if(model.isBuiltInRenderer()) {
							GlStateManager.enableRescaleNormal();
							stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
						} else {
							int color =
									((int)(MathHelper.clamp(properties.alpha, 0, 1) * 255) << 24) |
									((int)(MathHelper.clamp(properties.red, 0, 1) * 255) << 16) |
									((int)(MathHelper.clamp(properties.green, 0, 1) * 255) << 8) |
									((int)(MathHelper.clamp(properties.blue, 0, 1) * 255));

							if(properties.emissive) {
								LightingUtil.INSTANCE.setLighting(255);
							}
							
							renderItem.renderModel(model, color, stack);
							
							if(properties.emissive) {
								LightingUtil.INSTANCE.revert();
							}
							
							if(stack.hasEffect()) {
								color = ((int)(MathHelper.clamp(properties.alpha, 0, 1) * 255) << 24) | 8405196;

								GlStateManager.depthMask(false);
								GlStateManager.depthFunc(GL11.GL_EQUAL);
								GlStateManager.disableLighting();
								GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
								textureManager.bindTexture(RES_ITEM_GLINT);
								GlStateManager.matrixMode(GL11.GL_TEXTURE);
								GlStateManager.pushMatrix();
								GlStateManager.scale(8.0F, 8.0F, 8.0F);
								float offset1 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
								GlStateManager.translate(offset1, 0.0F, 0.0F);
								GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
								renderItem.renderModel(model, color, ItemStack.EMPTY);
								GlStateManager.popMatrix();
								GlStateManager.pushMatrix();
								GlStateManager.scale(8.0F, 8.0F, 8.0F);
								float offset2 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
								GlStateManager.translate(-offset2, 0.0F, 0.0F);
								GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
								renderItem.renderModel(model, color, ItemStack.EMPTY);
								GlStateManager.popMatrix();
								GlStateManager.matrixMode(GL11.GL_MODELVIEW);
								GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
								GlStateManager.enableLighting();
								GlStateManager.depthFunc(GL11.GL_LEQUAL);
								GlStateManager.depthMask(true);
								textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
							}
						}

						GlStateManager.popMatrix();
					}

					textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					texture.restoreLastBlurMipmap();

					GlStateManager.popMatrix();
				}

				@Override
				public int getRendererCount(Subject subject) {
					return 1;
				}
				
				@Override
				public int getColorModifier(Subject subject, int index) {
					return TokenRuneItem.Blueprint.this.getStats().getAspect().type.getColor();
				}
				
				@Override
				public int getColorModifierCount(Subject subject) {
					return 1;
				}
			};
		}

		@Override
		protected boolean isDelegatedRuneEffectModifier(TokenRuneItem state, AbstractRune<?> target, AbstractRune<?> inputRune, int outputIndex) {
			return true;
		}
	}

	private TokenRuneItem(Blueprint blueprint, int index, INodeComposition<RuneExecutionContext> composition, RuneConfiguration configuration) {
		super(blueprint, index, composition, configuration);
	}
}
