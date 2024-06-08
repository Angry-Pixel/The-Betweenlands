package net.minecraft.client.particle;

import com.google.common.base.Charsets;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleEngine implements PreparableReloadListener {
   private static final int MAX_PARTICLES_PER_LAYER = 16384;
   private static final List<ParticleRenderType> RENDER_ORDER = ImmutableList.of(ParticleRenderType.TERRAIN_SHEET, ParticleRenderType.PARTICLE_SHEET_OPAQUE, ParticleRenderType.PARTICLE_SHEET_LIT, ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT, ParticleRenderType.CUSTOM);
   protected ClientLevel level;
   private final Map<ParticleRenderType, Queue<Particle>> particles = Maps.newTreeMap(net.minecraftforge.client.ForgeHooksClient.makeParticleRenderTypeComparator(RENDER_ORDER));
   private final Queue<TrackingEmitter> trackingEmitters = Queues.newArrayDeque();
   private final TextureManager textureManager;
   private final Random random = new Random();
   private final Map<ResourceLocation, ParticleProvider<?>> providers = new java.util.HashMap<>();
   private final Queue<Particle> particlesToAdd = Queues.newArrayDeque();
   private final Map<ResourceLocation, ParticleEngine.MutableSpriteSet> spriteSets = Maps.newHashMap();
   private final TextureAtlas textureAtlas;
   private final Object2IntOpenHashMap<ParticleGroup> trackedParticleCounts = new Object2IntOpenHashMap<>();

   public ParticleEngine(ClientLevel p_107299_, TextureManager p_107300_) {
      this.textureAtlas = new TextureAtlas(TextureAtlas.LOCATION_PARTICLES);
      p_107300_.register(this.textureAtlas.location(), this.textureAtlas);
      this.level = p_107299_;
      this.textureManager = p_107300_;
      this.registerProviders();
   }

   private void registerProviders() {
      this.register(ParticleTypes.AMBIENT_ENTITY_EFFECT, SpellParticle.AmbientMobProvider::new);
      this.register(ParticleTypes.ANGRY_VILLAGER, HeartParticle.AngryVillagerProvider::new);
      this.register(ParticleTypes.BLOCK_MARKER, new BlockMarker.Provider());
      this.register(ParticleTypes.BLOCK, new TerrainParticle.Provider());
      this.register(ParticleTypes.BUBBLE, BubbleParticle.Provider::new);
      this.register(ParticleTypes.BUBBLE_COLUMN_UP, BubbleColumnUpParticle.Provider::new);
      this.register(ParticleTypes.BUBBLE_POP, BubblePopParticle.Provider::new);
      this.register(ParticleTypes.CAMPFIRE_COSY_SMOKE, CampfireSmokeParticle.CosyProvider::new);
      this.register(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, CampfireSmokeParticle.SignalProvider::new);
      this.register(ParticleTypes.CLOUD, PlayerCloudParticle.Provider::new);
      this.register(ParticleTypes.COMPOSTER, SuspendedTownParticle.ComposterFillProvider::new);
      this.register(ParticleTypes.CRIT, CritParticle.Provider::new);
      this.register(ParticleTypes.CURRENT_DOWN, WaterCurrentDownParticle.Provider::new);
      this.register(ParticleTypes.DAMAGE_INDICATOR, CritParticle.DamageIndicatorProvider::new);
      this.register(ParticleTypes.DRAGON_BREATH, DragonBreathParticle.Provider::new);
      this.register(ParticleTypes.DOLPHIN, SuspendedTownParticle.DolphinSpeedProvider::new);
      this.register(ParticleTypes.DRIPPING_LAVA, DripParticle.LavaHangProvider::new);
      this.register(ParticleTypes.FALLING_LAVA, DripParticle.LavaFallProvider::new);
      this.register(ParticleTypes.LANDING_LAVA, DripParticle.LavaLandProvider::new);
      this.register(ParticleTypes.DRIPPING_WATER, DripParticle.WaterHangProvider::new);
      this.register(ParticleTypes.FALLING_WATER, DripParticle.WaterFallProvider::new);
      this.register(ParticleTypes.DUST, DustParticle.Provider::new);
      this.register(ParticleTypes.DUST_COLOR_TRANSITION, DustColorTransitionParticle.Provider::new);
      this.register(ParticleTypes.EFFECT, SpellParticle.Provider::new);
      this.register(ParticleTypes.ELDER_GUARDIAN, new MobAppearanceParticle.Provider());
      this.register(ParticleTypes.ENCHANTED_HIT, CritParticle.MagicProvider::new);
      this.register(ParticleTypes.ENCHANT, EnchantmentTableParticle.Provider::new);
      this.register(ParticleTypes.END_ROD, EndRodParticle.Provider::new);
      this.register(ParticleTypes.ENTITY_EFFECT, SpellParticle.MobProvider::new);
      this.register(ParticleTypes.EXPLOSION_EMITTER, new HugeExplosionSeedParticle.Provider());
      this.register(ParticleTypes.EXPLOSION, HugeExplosionParticle.Provider::new);
      this.register(ParticleTypes.FALLING_DUST, FallingDustParticle.Provider::new);
      this.register(ParticleTypes.FIREWORK, FireworkParticles.SparkProvider::new);
      this.register(ParticleTypes.FISHING, WakeParticle.Provider::new);
      this.register(ParticleTypes.FLAME, FlameParticle.Provider::new);
      this.register(ParticleTypes.SOUL, SoulParticle.Provider::new);
      this.register(ParticleTypes.SOUL_FIRE_FLAME, FlameParticle.Provider::new);
      this.register(ParticleTypes.FLASH, FireworkParticles.FlashProvider::new);
      this.register(ParticleTypes.HAPPY_VILLAGER, SuspendedTownParticle.HappyVillagerProvider::new);
      this.register(ParticleTypes.HEART, HeartParticle.Provider::new);
      this.register(ParticleTypes.INSTANT_EFFECT, SpellParticle.InstantProvider::new);
      this.register(ParticleTypes.ITEM, new BreakingItemParticle.Provider());
      this.register(ParticleTypes.ITEM_SLIME, new BreakingItemParticle.SlimeProvider());
      this.register(ParticleTypes.ITEM_SNOWBALL, new BreakingItemParticle.SnowballProvider());
      this.register(ParticleTypes.LARGE_SMOKE, LargeSmokeParticle.Provider::new);
      this.register(ParticleTypes.LAVA, LavaParticle.Provider::new);
      this.register(ParticleTypes.MYCELIUM, SuspendedTownParticle.Provider::new);
      this.register(ParticleTypes.NAUTILUS, EnchantmentTableParticle.NautilusProvider::new);
      this.register(ParticleTypes.NOTE, NoteParticle.Provider::new);
      this.register(ParticleTypes.POOF, ExplodeParticle.Provider::new);
      this.register(ParticleTypes.PORTAL, PortalParticle.Provider::new);
      this.register(ParticleTypes.RAIN, WaterDropParticle.Provider::new);
      this.register(ParticleTypes.SMOKE, SmokeParticle.Provider::new);
      this.register(ParticleTypes.SNEEZE, PlayerCloudParticle.SneezeProvider::new);
      this.register(ParticleTypes.SNOWFLAKE, SnowflakeParticle.Provider::new);
      this.register(ParticleTypes.SPIT, SpitParticle.Provider::new);
      this.register(ParticleTypes.SWEEP_ATTACK, AttackSweepParticle.Provider::new);
      this.register(ParticleTypes.TOTEM_OF_UNDYING, TotemParticle.Provider::new);
      this.register(ParticleTypes.SQUID_INK, SquidInkParticle.Provider::new);
      this.register(ParticleTypes.UNDERWATER, SuspendedParticle.UnderwaterProvider::new);
      this.register(ParticleTypes.SPLASH, SplashParticle.Provider::new);
      this.register(ParticleTypes.WITCH, SpellParticle.WitchProvider::new);
      this.register(ParticleTypes.DRIPPING_HONEY, DripParticle.HoneyHangProvider::new);
      this.register(ParticleTypes.FALLING_HONEY, DripParticle.HoneyFallProvider::new);
      this.register(ParticleTypes.LANDING_HONEY, DripParticle.HoneyLandProvider::new);
      this.register(ParticleTypes.FALLING_NECTAR, DripParticle.NectarFallProvider::new);
      this.register(ParticleTypes.FALLING_SPORE_BLOSSOM, DripParticle.SporeBlossomFallProvider::new);
      this.register(ParticleTypes.SPORE_BLOSSOM_AIR, SuspendedParticle.SporeBlossomAirProvider::new);
      this.register(ParticleTypes.ASH, AshParticle.Provider::new);
      this.register(ParticleTypes.CRIMSON_SPORE, SuspendedParticle.CrimsonSporeProvider::new);
      this.register(ParticleTypes.WARPED_SPORE, SuspendedParticle.WarpedSporeProvider::new);
      this.register(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, DripParticle.ObsidianTearHangProvider::new);
      this.register(ParticleTypes.FALLING_OBSIDIAN_TEAR, DripParticle.ObsidianTearFallProvider::new);
      this.register(ParticleTypes.LANDING_OBSIDIAN_TEAR, DripParticle.ObsidianTearLandProvider::new);
      this.register(ParticleTypes.REVERSE_PORTAL, ReversePortalParticle.ReversePortalProvider::new);
      this.register(ParticleTypes.WHITE_ASH, WhiteAshParticle.Provider::new);
      this.register(ParticleTypes.SMALL_FLAME, FlameParticle.SmallFlameProvider::new);
      this.register(ParticleTypes.DRIPPING_DRIPSTONE_WATER, DripParticle.DripstoneWaterHangProvider::new);
      this.register(ParticleTypes.FALLING_DRIPSTONE_WATER, DripParticle.DripstoneWaterFallProvider::new);
      this.register(ParticleTypes.DRIPPING_DRIPSTONE_LAVA, DripParticle.DripstoneLavaHangProvider::new);
      this.register(ParticleTypes.FALLING_DRIPSTONE_LAVA, DripParticle.DripstoneLavaFallProvider::new);
      this.register(ParticleTypes.VIBRATION, VibrationSignalParticle.Provider::new);
      this.register(ParticleTypes.GLOW_SQUID_INK, SquidInkParticle.GlowInkProvider::new);
      this.register(ParticleTypes.GLOW, GlowParticle.GlowSquidProvider::new);
      this.register(ParticleTypes.WAX_ON, GlowParticle.WaxOnProvider::new);
      this.register(ParticleTypes.WAX_OFF, GlowParticle.WaxOffProvider::new);
      this.register(ParticleTypes.ELECTRIC_SPARK, GlowParticle.ElectricSparkProvider::new);
      this.register(ParticleTypes.SCRAPE, GlowParticle.ScrapeProvider::new);
   }

   public <T extends ParticleOptions> void register(ParticleType<T> p_107382_, ParticleProvider<T> p_107383_) {
      this.providers.put(Registry.PARTICLE_TYPE.getKey(p_107382_), p_107383_);
   }

   public <T extends ParticleOptions> void register(ParticleType<T> p_107379_, ParticleEngine.SpriteParticleRegistration<T> p_107380_) {
      ParticleEngine.MutableSpriteSet particleengine$mutablespriteset = new ParticleEngine.MutableSpriteSet();
      this.spriteSets.put(Registry.PARTICLE_TYPE.getKey(p_107379_), particleengine$mutablespriteset);
      this.providers.put(Registry.PARTICLE_TYPE.getKey(p_107379_), p_107380_.create(particleengine$mutablespriteset));
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_107305_, ResourceManager p_107306_, ProfilerFiller p_107307_, ProfilerFiller p_107308_, Executor p_107309_, Executor p_107310_) {
      Map<ResourceLocation, List<ResourceLocation>> map = Maps.newConcurrentMap();
      CompletableFuture<?>[] completablefuture = Registry.PARTICLE_TYPE.keySet().stream().map((p_107315_) -> {
         return CompletableFuture.runAsync(() -> {
            this.loadParticleDescription(p_107306_, p_107315_, map);
         }, p_107309_);
      }).toArray((p_107303_) -> {
         return new CompletableFuture[p_107303_];
      });
      return CompletableFuture.allOf(completablefuture).thenApplyAsync((p_107324_) -> {
         p_107307_.startTick();
         p_107307_.push("stitching");
         TextureAtlas.Preparations textureatlas$preparations = this.textureAtlas.prepareToStitch(p_107306_, map.values().stream().flatMap(Collection::stream), p_107307_, 0);
         p_107307_.pop();
         p_107307_.endTick();
         return textureatlas$preparations;
      }, p_107309_).thenCompose(p_107305_::wait).thenAcceptAsync((p_107328_) -> {
         this.particles.clear();
         p_107308_.startTick();
         p_107308_.push("upload");
         this.textureAtlas.reload(p_107328_);
         p_107308_.popPush("bindSpriteSets");
         TextureAtlasSprite textureatlassprite = this.textureAtlas.getSprite(MissingTextureAtlasSprite.getLocation());
         map.forEach((p_172268_, p_172269_) -> {
            ImmutableList<TextureAtlasSprite> immutablelist = p_172269_.isEmpty() ? ImmutableList.of(textureatlassprite) : p_172269_.stream().map(this.textureAtlas::getSprite).collect(ImmutableList.toImmutableList());
            this.spriteSets.get(p_172268_).rebind(immutablelist);
         });
         p_107308_.pop();
         p_107308_.endTick();
      }, p_107310_);
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }

   private void loadParticleDescription(ResourceManager p_107317_, ResourceLocation p_107318_, Map<ResourceLocation, List<ResourceLocation>> p_107319_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_107318_.getNamespace(), "particles/" + p_107318_.getPath() + ".json");

      try {
         Resource resource = p_107317_.getResource(resourcelocation);

         try {
            Reader reader = new InputStreamReader(resource.getInputStream(), Charsets.UTF_8);

            try {
               ParticleDescription particledescription = ParticleDescription.fromJson(GsonHelper.parse(reader));
               List<ResourceLocation> list = particledescription.getTextures();
               boolean flag = this.spriteSets.containsKey(p_107318_);
               if (list == null) {
                  if (flag) {
                     throw new IllegalStateException("Missing texture list for particle " + p_107318_);
                  }
               } else {
                  if (!flag) {
                     throw new IllegalStateException("Redundant texture list for particle " + p_107318_);
                  }

                  p_107319_.put(p_107318_, list.stream().map((p_107387_) -> {
                     return new ResourceLocation(p_107387_.getNamespace(), "particle/" + p_107387_.getPath());
                  }).collect(Collectors.toList()));
               }
            } catch (Throwable throwable2) {
               try {
                  reader.close();
               } catch (Throwable throwable1) {
                  throwable2.addSuppressed(throwable1);
               }

               throw throwable2;
            }

            reader.close();
         } catch (Throwable throwable3) {
            if (resource != null) {
               try {
                  resource.close();
               } catch (Throwable throwable) {
                  throwable3.addSuppressed(throwable);
               }
            }

            throw throwable3;
         }

         if (resource != null) {
            resource.close();
         }

      } catch (IOException ioexception) {
         throw new IllegalStateException("Failed to load description for particle " + p_107318_, ioexception);
      }
   }

   public void createTrackingEmitter(Entity p_107330_, ParticleOptions p_107331_) {
      this.trackingEmitters.add(new TrackingEmitter(this.level, p_107330_, p_107331_));
   }

   public void createTrackingEmitter(Entity p_107333_, ParticleOptions p_107334_, int p_107335_) {
      this.trackingEmitters.add(new TrackingEmitter(this.level, p_107333_, p_107334_, p_107335_));
   }

   @Nullable
   public Particle createParticle(ParticleOptions p_107371_, double p_107372_, double p_107373_, double p_107374_, double p_107375_, double p_107376_, double p_107377_) {
      Particle particle = this.makeParticle(p_107371_, p_107372_, p_107373_, p_107374_, p_107375_, p_107376_, p_107377_);
      if (particle != null) {
         this.add(particle);
         return particle;
      } else {
         return null;
      }
   }

   @Nullable
   private <T extends ParticleOptions> Particle makeParticle(T p_107396_, double p_107397_, double p_107398_, double p_107399_, double p_107400_, double p_107401_, double p_107402_) {
      ParticleProvider<T> particleprovider = (ParticleProvider<T>)this.providers.get(Registry.PARTICLE_TYPE.getKey(p_107396_.getType()));
      return particleprovider == null ? null : particleprovider.createParticle(p_107396_, this.level, p_107397_, p_107398_, p_107399_, p_107400_, p_107401_, p_107402_);
   }

   public void add(Particle p_107345_) {
      Optional<ParticleGroup> optional = p_107345_.getParticleGroup();
      if (optional.isPresent()) {
         if (this.hasSpaceInParticleLimit(optional.get())) {
            this.particlesToAdd.add(p_107345_);
            this.updateCount(optional.get(), 1);
         }
      } else {
         this.particlesToAdd.add(p_107345_);
      }

   }

   public void tick() {
      this.particles.forEach((p_107349_, p_107350_) -> {
         this.level.getProfiler().push(p_107349_.toString());
         this.tickParticleList(p_107350_);
         this.level.getProfiler().pop();
      });
      if (!this.trackingEmitters.isEmpty()) {
         List<TrackingEmitter> list = Lists.newArrayList();

         for(TrackingEmitter trackingemitter : this.trackingEmitters) {
            trackingemitter.tick();
            if (!trackingemitter.isAlive()) {
               list.add(trackingemitter);
            }
         }

         this.trackingEmitters.removeAll(list);
      }

      Particle particle;
      if (!this.particlesToAdd.isEmpty()) {
         while((particle = this.particlesToAdd.poll()) != null) {
            this.particles.computeIfAbsent(particle.getRenderType(), (p_107347_) -> {
               return EvictingQueue.create(16384);
            }).add(particle);
         }
      }

   }

   private void tickParticleList(Collection<Particle> p_107385_) {
      if (!p_107385_.isEmpty()) {
         Iterator<Particle> iterator = p_107385_.iterator();

         while(iterator.hasNext()) {
            Particle particle = iterator.next();
            this.tickParticle(particle);
            if (!particle.isAlive()) {
               particle.getParticleGroup().ifPresent((p_172289_) -> {
                  this.updateCount(p_172289_, -1);
               });
               iterator.remove();
            }
         }
      }

   }

   private void updateCount(ParticleGroup p_172282_, int p_172283_) {
      this.trackedParticleCounts.addTo(p_172282_, p_172283_);
   }

   private void tickParticle(Particle p_107394_) {
      try {
         p_107394_.tick();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking Particle");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being ticked");
         crashreportcategory.setDetail("Particle", p_107394_::toString);
         crashreportcategory.setDetail("Particle Type", p_107394_.getRenderType()::toString);
         throw new ReportedException(crashreport);
      }
   }

   /**@deprecated Forge: use {@link #render(PoseStack, MultiBufferSource.BufferSource, LightTexture, Camera, float, net.minecraft.client.renderer.culling.Frustum)} with Frustum as additional parameter*/
   @Deprecated
   public void render(PoseStack p_107337_, MultiBufferSource.BufferSource p_107338_, LightTexture p_107339_, Camera p_107340_, float p_107341_) {
       render(p_107337_, p_107338_, p_107339_, p_107340_, p_107341_, null);
   }

   public void render(PoseStack p_107337_, MultiBufferSource.BufferSource p_107338_, LightTexture p_107339_, Camera p_107340_, float p_107341_, @Nullable net.minecraft.client.renderer.culling.Frustum clippingHelper) {
      p_107339_.turnOnLightLayer();
      RenderSystem.enableDepthTest();
      RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE2);
      RenderSystem.enableTexture();
      RenderSystem.activeTexture(org.lwjgl.opengl.GL13.GL_TEXTURE0);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.mulPoseMatrix(p_107337_.last().pose());
      RenderSystem.applyModelViewMatrix();

      for(ParticleRenderType particlerendertype : this.particles.keySet()) { // Forge: allow custom IParticleRenderType's
         if (particlerendertype == ParticleRenderType.NO_RENDER) continue;
         Iterable<Particle> iterable = this.particles.get(particlerendertype);
         if (iterable != null) {
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            particlerendertype.begin(bufferbuilder, this.textureManager);

            for(Particle particle : iterable) {
               if (clippingHelper != null && particle.shouldCull() && !clippingHelper.isVisible(particle.getBoundingBox())) continue;
               try {
                  particle.render(bufferbuilder, p_107340_, p_107341_);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering Particle");
                  CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
                  crashreportcategory.setDetail("Particle", particle::toString);
                  crashreportcategory.setDetail("Particle Type", particlerendertype::toString);
                  throw new ReportedException(crashreport);
               }
            }

            particlerendertype.end(tesselator);
         }
      }

      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.depthMask(true);
      RenderSystem.disableBlend();
      p_107339_.turnOffLightLayer();
   }

   public void setLevel(@Nullable ClientLevel p_107343_) {
      this.level = p_107343_;
      this.particles.clear();
      this.trackingEmitters.clear();
      this.trackedParticleCounts.clear();
   }

   public void destroy(BlockPos p_107356_, BlockState p_107357_) {
      if (!p_107357_.isAir() && !net.minecraftforge.client.RenderProperties.get(p_107357_).addDestroyEffects(p_107357_, this.level, p_107356_, this)) {
         VoxelShape voxelshape = p_107357_.getShape(this.level, p_107356_);
         double d0 = 0.25D;
         voxelshape.forAllBoxes((p_172273_, p_172274_, p_172275_, p_172276_, p_172277_, p_172278_) -> {
            double d1 = Math.min(1.0D, p_172276_ - p_172273_);
            double d2 = Math.min(1.0D, p_172277_ - p_172274_);
            double d3 = Math.min(1.0D, p_172278_ - p_172275_);
            int i = Math.max(2, Mth.ceil(d1 / 0.25D));
            int j = Math.max(2, Mth.ceil(d2 / 0.25D));
            int k = Math.max(2, Mth.ceil(d3 / 0.25D));

            for(int l = 0; l < i; ++l) {
               for(int i1 = 0; i1 < j; ++i1) {
                  for(int j1 = 0; j1 < k; ++j1) {
                     double d4 = ((double)l + 0.5D) / (double)i;
                     double d5 = ((double)i1 + 0.5D) / (double)j;
                     double d6 = ((double)j1 + 0.5D) / (double)k;
                     double d7 = d4 * d1 + p_172273_;
                     double d8 = d5 * d2 + p_172274_;
                     double d9 = d6 * d3 + p_172275_;
                     this.add(new TerrainParticle(this.level, (double)p_107356_.getX() + d7, (double)p_107356_.getY() + d8, (double)p_107356_.getZ() + d9, d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, p_107357_, p_107356_).updateSprite(p_107357_, p_107356_));
                  }
               }
            }

         });
      }
   }

   public void crack(BlockPos p_107368_, Direction p_107369_) {
      BlockState blockstate = this.level.getBlockState(p_107368_);
      if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
         int i = p_107368_.getX();
         int j = p_107368_.getY();
         int k = p_107368_.getZ();
         float f = 0.1F;
         AABB aabb = blockstate.getShape(this.level, p_107368_).bounds();
         double d0 = (double)i + this.random.nextDouble() * (aabb.maxX - aabb.minX - (double)0.2F) + (double)0.1F + aabb.minX;
         double d1 = (double)j + this.random.nextDouble() * (aabb.maxY - aabb.minY - (double)0.2F) + (double)0.1F + aabb.minY;
         double d2 = (double)k + this.random.nextDouble() * (aabb.maxZ - aabb.minZ - (double)0.2F) + (double)0.1F + aabb.minZ;
         if (p_107369_ == Direction.DOWN) {
            d1 = (double)j + aabb.minY - (double)0.1F;
         }

         if (p_107369_ == Direction.UP) {
            d1 = (double)j + aabb.maxY + (double)0.1F;
         }

         if (p_107369_ == Direction.NORTH) {
            d2 = (double)k + aabb.minZ - (double)0.1F;
         }

         if (p_107369_ == Direction.SOUTH) {
            d2 = (double)k + aabb.maxZ + (double)0.1F;
         }

         if (p_107369_ == Direction.WEST) {
            d0 = (double)i + aabb.minX - (double)0.1F;
         }

         if (p_107369_ == Direction.EAST) {
            d0 = (double)i + aabb.maxX + (double)0.1F;
         }

         this.add((new TerrainParticle(this.level, d0, d1, d2, 0.0D, 0.0D, 0.0D, blockstate, p_107368_).updateSprite(blockstate, p_107368_)).setPower(0.2F).scale(0.6F));
      }
   }

   public String countParticles() {
      return String.valueOf(this.particles.values().stream().mapToInt(Collection::size).sum());
   }

   public void addBlockHitEffects(BlockPos pos, net.minecraft.world.phys.BlockHitResult target) {
      BlockState state = level.getBlockState(pos);
      if (!net.minecraftforge.client.RenderProperties.get(state).addHitEffects(state, level, target, this))
         crack(pos, target.getDirection());
   }

   private boolean hasSpaceInParticleLimit(ParticleGroup p_172280_) {
      return this.trackedParticleCounts.getInt(p_172280_) < p_172280_.getLimit();
   }

   @OnlyIn(Dist.CLIENT)
   static class MutableSpriteSet implements SpriteSet {
      private List<TextureAtlasSprite> sprites;

      public TextureAtlasSprite get(int p_107413_, int p_107414_) {
         return this.sprites.get(p_107413_ * (this.sprites.size() - 1) / p_107414_);
      }

      public TextureAtlasSprite get(Random p_107418_) {
         return this.sprites.get(p_107418_.nextInt(this.sprites.size()));
      }

      public void rebind(List<TextureAtlasSprite> p_107416_) {
         this.sprites = ImmutableList.copyOf(p_107416_);
      }
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface SpriteParticleRegistration<T extends ParticleOptions> {
      ParticleProvider<T> create(SpriteSet p_107420_);
   }
}
