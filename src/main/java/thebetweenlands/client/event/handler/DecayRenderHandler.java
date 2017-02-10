package thebetweenlands.client.event.handler;

import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.CapabilityRegistry;

public class DecayRenderHandler {
	public static final ResourceLocation PLAYER_DECAY_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/player_decay.png");

	public static class LayerDecay implements LayerRenderer<AbstractClientPlayer> {
		private final RenderPlayer renderer;

		public LayerDecay(RenderPlayer renderer) {
			this.renderer = renderer;
		}

		@Override
		public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
			ModelPlayer model = this.renderer.getMainModel();
			model.bipedHeadwear.showModel = false;
			model.bipedRightLegwear.showModel = false;
			model.bipedLeftLegwear.showModel = false;
			model.bipedBodyWear.showModel = false;
			model.bipedRightArmwear.showModel = false;
			model.bipedLeftArmwear.showModel = false;

			//Render decay overlay
			int decay = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null).getDecayStats().getDecayLevel();
			float glow = (float) ((Math.cos(player.ticksExisted / 10.0D) + 1.0D) / 2.0D) * 0.15F;
			float transparency = 0.85F * decay / 20.0F - glow;
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			this.renderer.bindTexture(PLAYER_DECAY_TEXTURE);
			GlStateManager.color(1, 1, 1, transparency);
			model.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			GlStateManager.color(1, 1, 1, 1);

			model.bipedHeadwear.showModel = true;
			model.bipedRightLegwear.showModel = true;
			model.bipedLeftLegwear.showModel = true;
			model.bipedBodyWear.showModel = true;
			model.bipedRightArmwear.showModel = true;
			model.bipedLeftArmwear.showModel = true;
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}
	}

	private static final Deque<LayerDecay> LAYER_STACK = new ArrayDeque<>();

	@SubscribeEvent
	public static void onPreRenderPlayer(RenderPlayerEvent.Pre event) {
		onRenderPlayer(event);
	}

	@SubscribeEvent(receiveCanceled = true)
	public static void onPostRenderPlayer(RenderPlayerEvent.Post event) {
		onRenderPlayer(event);
	}

	private static void onRenderPlayer(RenderPlayerEvent event) {
		EntityPlayer player = event.getEntityPlayer();

		if(player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
			IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(capability.isDecayEnabled() && capability.getDecayStats().getDecayLevel() > 0) {
				if(event instanceof RenderPlayerEvent.Pre) {
					LayerDecay layer = new LayerDecay(event.getRenderer());
					LAYER_STACK.push(layer);
					event.getRenderer().addLayer(layer);
				} else if(event instanceof RenderPlayerEvent.Post) {
					event.getRenderer().removeLayer(LAYER_STACK.pop());
				}
			}
		}
	}

	private static ModelArmOverride modelArmOverrideLeft = null;
	private static ModelArmOverride modelArmOverrideRight = null;

	@SubscribeEvent
	public static void onRenderHand(RenderHandEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		Minecraft mc = Minecraft.getMinecraft();

		if(player != null && player.hasCapability(CapabilityRegistry.CAPABILITY_DECAY, null)) {
			IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);
			if(capability.isDecayEnabled() && capability.getDecayStats().getDecayLevel() > 0) {
				RenderPlayer playerRenderer = (RenderPlayer) Minecraft.getMinecraft().getRenderManager().getEntityRenderObject((AbstractClientPlayer) Minecraft.getMinecraft().thePlayer);

				ModelPlayer model = playerRenderer.getMainModel();

				//Should fix compatibility issues with mods that replace the player renderer or parts of it (e.g. More Player Models)
				if(playerRenderer.getClass() == RenderPlayer.class && model.getClass() == ModelPlayer.class) {
					boolean isSleeping = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)mc.getRenderViewEntity()).isPlayerSleeping();
					boolean shouldRenderHand = mc.gameSettings.thirdPersonView == 0 && !isSleeping && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator();

					if(shouldRenderHand) {
						//Right arm
						if ((modelArmOverrideRight == null || modelArmOverrideRight.model != model) && model != null) {
							modelArmOverrideRight = new ModelArmOverride(model);
						}
						modelArmOverrideRight.parent = model.bipedRightArm;
						modelArmOverrideRight.entity = player;
						ModelRenderer previousModelRight = model.bipedRightArm;
						model.bipedRightArm = modelArmOverrideRight;

						//Left arm
						if ((modelArmOverrideLeft == null || modelArmOverrideLeft.model != model) && model != null) {
							modelArmOverrideLeft = new ModelArmOverride(model);
						}
						modelArmOverrideLeft.parent = model.bipedLeftArm;
						modelArmOverrideLeft.entity = player;
						ModelRenderer previousModelLeft = model.bipedLeftArm;
						model.bipedLeftArm = modelArmOverrideLeft;

						//Render hands and item
						Minecraft.getMinecraft().entityRenderer.enableLightmap();
						mc.getItemRenderer().renderItemInFirstPerson(event.getPartialTicks());
						Minecraft.getMinecraft().entityRenderer.disableLightmap();

						model.bipedRightArm = previousModelRight;
						model.bipedLeftArm = previousModelLeft;

						//We already rendered the hand, don't render it twice
						event.setCanceled(true);
					}
				}
			}
		}
	}

	private static class ModelArmOverride extends ModelRenderer {
		private ModelRenderer parent;
		private EntityPlayer entity;
		public final ModelBase model;

		public ModelArmOverride(ModelBase modelBase) {
			super(modelBase);
			this.model = modelBase;
		}

		@Override
		public void render(float partialTicks) {
			//Render default hand
			super.render(partialTicks);
			this.parent.render(partialTicks);

			//Render decay overlay
			int decay = this.entity.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null).getDecayStats().getDecayLevel();
			GlStateManager.pushMatrix();
			Minecraft.getMinecraft().renderEngine.bindTexture(PLAYER_DECAY_TEXTURE);
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			float glow = (float) ((Math.cos(this.entity.ticksExisted / 10.0D) + 1.0D) / 2.0D) * 0.15F;
			float transparency = 0.85F * decay / 20.0F - glow;
			GlStateManager.color(1, 1, 1, transparency);
			this.parent.render(partialTicks);
			Minecraft.getMinecraft().renderEngine.bindTexture(Minecraft.getMinecraft().thePlayer.getLocationSkin());
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.popMatrix();
		}
	}
}
