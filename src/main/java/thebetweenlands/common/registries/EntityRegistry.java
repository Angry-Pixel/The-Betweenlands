package thebetweenlands.common.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.*;
import thebetweenlands.common.entity.boss.Barrishee;
import thebetweenlands.common.entity.boss.DreadfulPeatMummy;
import thebetweenlands.common.entity.boss.PrimordialMalevolence;
import thebetweenlands.common.entity.boss.malevolence.*;
import thebetweenlands.common.entity.creature.*;
import thebetweenlands.common.entity.creature.frog.Frog;
import thebetweenlands.common.entity.fishing.BLFishHook;
import thebetweenlands.common.entity.fishing.BubblerCrab;
import thebetweenlands.common.entity.fishing.FishBait;
import thebetweenlands.common.entity.fishing.SiltCrab;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.monster.*;
import thebetweenlands.common.entity.monster.chiromaw.*;
import thebetweenlands.common.entity.projectile.*;
import thebetweenlands.common.entity.projectile.arrow.AnglerToothArrow;
import thebetweenlands.common.entity.projectile.arrow.BasiliskArrow;
import thebetweenlands.common.entity.projectile.arrow.ChiromawBarb;
import thebetweenlands.common.entity.projectile.arrow.ChiromawShockBarb;
import thebetweenlands.common.entity.projectile.arrow.OctineArrow;
import thebetweenlands.common.entity.projectile.arrow.PoisonAnglerToothArrow;
import thebetweenlands.common.entity.projectile.arrow.ShockArrow;
import thebetweenlands.common.entity.projectile.arrow.SludgeWormArrow;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, TheBetweenlands.ID);
	public static final DeferredRegister.Items SPAWN_EGGS = DeferredRegister.createItems(TheBetweenlands.ID);
	public static final Map<Holder<EntityType<?>>, Supplier<AttributeSupplier.Builder>> ATTRIBUTES = new HashMap<>();

	public static final DeferredHolder<EntityType<?>, EntityType<Anadia>> ANADIA = registerWithEgg("anadia", EntityType.Builder.of(Anadia::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F), 0x5D633A, 0xB53D2F, Anadia::registerAttributes);
	//angler
	public static final DeferredHolder<EntityType<?>, EntityType<AshSprite>> ASH_SPRITE = registerWithEgg("ash_sprite", EntityType.Builder.of(AshSprite::new, MobCategory.MONSTER).sized(0.4F, 0.8F).fireImmune().eyeHeight(0.51875F).passengerAttachments(0.7375F).ridingOffset(0.04F).clientTrackingRange(8), 0x1B1F28, 0x721C16, AshSprite::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Barrishee>> BARRISHEE = registerWithEgg("barrishee", EntityType.Builder.of(Barrishee::new, MobCategory.MONSTER).sized(2.25F, 1.8F).clientTrackingRange(10), 0x604C30, 0xFFD92C, Barrishee::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<BipedCryptCrawler>> BIPED_CRYPT_CRAWLER = registerWithAttributes("biped_crypt_crawler", EntityType.Builder.of(BipedCryptCrawler::new, MobCategory.MONSTER).sized(0.75F, 1.5F), BipedCryptCrawler::registerAttributes);
	//blood snail
	//boulder sprite
	public static final DeferredHolder<EntityType<?>, EntityType<BubblerCrab>> BUBBLER_CRAB = registerWithEgg("bubbler_crab", EntityType.Builder.of(BubblerCrab::new, MobCategory.WATER_CREATURE).sized(0.7F, 0.6F), 0xD8D5CB, 0xC7692C, BubblerCrab::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<CaveFish>> CAVE_FISH = registerWithEgg("cave_fish", EntityType.Builder.of(CaveFish::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.3F, 0.2F), 0xFF8000, 0xE6E6E6, CaveFish::registerAttributes);
	//cave jellyfish
	public static final DeferredHolder<EntityType<?>, EntityType<Chiromaw>> CHIROMAW = registerWithEgg("chiromaw", EntityType.Builder.of(Chiromaw::new, MobCategory.MONSTER).sized(0.7F, 0.9F), 0x142728, 0x7A4E42, Chiromaw::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<ChiromawGreeblingRider>> CHIROMAW_GREEBLING_RIDER = registerWithEgg("chiromaw_greebling_rider", EntityType.Builder.of(ChiromawGreeblingRider::new, MobCategory.MONSTER).sized(0.7F, 0.9F), 0x142728, 0x7CB2AA, ChiromawGreeblingRider::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<ChiromawHatchling>> CHIROMAW_HATCHLING = registerWithAttributes("chiromaw_hatchling", EntityType.Builder.of(ChiromawHatchling::new, MobCategory.MONSTER).sized(0.75F, 1.0F), BasicProximitySpawner::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<ChiromawMatriarch>> CHIROMAW_MATRIARCH = registerWithEgg("chiromaw_matriarch", EntityType.Builder.of(ChiromawMatriarch::new, MobCategory.MONSTER).sized(1.75F, 2.0F), 0x142728, 0x4A2120, ChiromawMatriarch::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<ChiefCryptCrawler>> CHIEF_CRYPT_CRAWLER = registerWithAttributes("chief_crypt_crawler", EntityType.Builder.of(ChiefCryptCrawler::new, MobCategory.MONSTER).sized(0.98F, 1.9F), ChiefCryptCrawler::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<CryptCrawler>> CRYPT_CRAWLER = registerWithAttributes("crypt_crawler", EntityType.Builder.of(CryptCrawler::new, MobCategory.MONSTER).sized(0.95F, 1.0F), CryptCrawler::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<DarkDruid>> DARK_DRUID = registerWithEgg("dark_druid", EntityType.Builder.of(DarkDruid::new, MobCategory.MONSTER).sized(0.9F, 1.9F), 0x000000, 0xFF0000, DarkDruid::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Dragonfly>> DRAGONFLY = registerWithEgg("dragonfly", EntityType.Builder.of(Dragonfly::new, MobCategory.AMBIENT).sized(0.9F, 0.5F).eyeHeight(0.25F), 0x356721, 0xC0FFEE, Dragonfly::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<DreadfulPeatMummy>> DREADFUL_PEAT_MUMMY = registerWithEgg("dreadful_peat_mummy", EntityType.Builder.of(DreadfulPeatMummy::new, MobCategory.MONSTER).sized(1.1F, 2.0F).clientTrackingRange(10).fireImmune(), 0x000000, 0x591E08, DreadfulPeatMummy::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Emberling>> EMBERLING = registerWithEgg("emberling", EntityType.Builder.of(Emberling::new, MobCategory.MONSTER).sized(0.9F, 0.85F).clientTrackingRange(10).fireImmune(), 0xA43B2E, 0x391211, Emberling::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<EmberlingShaman>> EMBERLING_SHAMAN = registerWithEgg("emberling_shaman", EntityType.Builder.of(EmberlingShaman::new, MobCategory.MONSTER).sized(0.9F, 1.0F).clientTrackingRange(10).fireImmune(), 0xA43B2E, 0x391211, EmberlingShaman::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Firefly>> FIREFLY = registerWithEgg("firefly", EntityType.Builder.of(Firefly::new, MobCategory.AMBIENT).sized(0.6F, 0.6F), 0xDCFF51, 0x402A21, Firefly::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<FreshwaterUrchin>> FRESHWATER_URCHIN = registerWithEgg("freshwater_urchin", EntityType.Builder.of(FreshwaterUrchin::new, MobCategory.WATER_AMBIENT).sized(0.6875F, 0.4375F), 0x612E2E, 0x13363F, FreshwaterUrchin::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Frog>> FROG = registerWithEgg("frog", EntityType.Builder.of(Frog::new, MobCategory.WATER_CREATURE).sized(0.7F, 0.5F), 0x1E4921, 0x479219, Frog::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Gecko>> GECKO = registerWithEgg("gecko", EntityType.Builder.of(Gecko::new, MobCategory.CREATURE).sized(0.75F, 0.35F),  0xFF8000, 0x22E0B1, Gecko::registerAttributes);
	//giant toad
	public static final DeferredHolder<EntityType<?>, EntityType<Greebling>> GREEBLING = registerWithEgg("greebling", EntityType.Builder.of(Greebling::new, MobCategory.AMBIENT).sized(1.0F, 0.75F).clientTrackingRange(16), 0x7CB2AA, 0xC3726C, Greebling::createMobAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<GreeblingCoracle>> GREEBLING_CORACLE = registerWithEgg("greebling_coracle", EntityType.Builder.of(GreeblingCoracle::new, MobCategory.AMBIENT).sized(1.0F, 1.0F).clientTrackingRange(16), 0x7CB2AA, 0xC3726C, GreeblingCoracle::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<GreeblingVolarpadFloater>> GREEBLING_VOLARPAD_FLOATER = register("greebling_volarpad_floater", EntityType.Builder.<GreeblingVolarpadFloater>of(GreeblingVolarpadFloater::new, MobCategory.MISC).sized(1.0F, 1.0F).clientTrackingRange(16).noSave().fireImmune().noSummon().canSpawnFarFromPlayer());
	//infestation
	public static final DeferredHolder<EntityType<?>, EntityType<Jellyfish>> JELLYFISH = registerWithEgg("jellyfish", EntityType.Builder.of(Jellyfish::new, MobCategory.WATER_AMBIENT).sized(0.8F, 0.8F), 0xFFEBE5, 0xFF506B, Jellyfish::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<LargeSludgeWorm>> LARGE_SLUDGE_WORM = registerWithEgg("large_sludge_worm", EntityType.Builder.of(LargeSludgeWorm::new, MobCategory.MONSTER).sized(0.8F, 0.8F).fireImmune(), 0x726459, 0xE2DED9, LargeSludgeWorm::registerAttributes);
	//leech
	public static final DeferredHolder<EntityType<?>, EntityType<Lurker>> LURKER = registerWithEgg("lurker", EntityType.Builder.of(Lurker::new, MobCategory.CREATURE).sized(1.6F, 0.9F).eyeHeight(0.45F), 0x283320, 0x827856, Lurker::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<MireSnail>> MIRE_SNAIL = registerWithEgg("mire_snail", EntityType.Builder.of(MireSnail::new, MobCategory.CREATURE).sized(0.75F, 0.6F), 0x8E9456, 0xF2FA96, MireSnail::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<MummyArm>> MUMMY_ARM = registerWithAttributes("mummy_arm", EntityType.Builder.<MummyArm>of(MummyArm::new, MobCategory.MISC).sized(0.7F, 0.7F).clientTrackingRange(10).noSummon().fireImmune(), MummyArm::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Olm>> OLM = registerWithEgg("olm", EntityType.Builder.of(Olm::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(0.95F, 0.25F).eyeHeight(0.125F), 0xE8D8B8, 0xE79B7B, Olm::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<PeatMummy>> PEAT_MUMMY = registerWithEgg("peat_mummy", EntityType.Builder.of(PeatMummy::new, MobCategory.MONSTER).sized(1.0F, 1.2F).passengerAttachments(0.35F).clientTrackingRange(10).canSpawnFarFromPlayer(), 0x524D3A, 0x69463F, PeatMummy::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<PrimordialMalevolence>> PRIMORDIAL_MALEVOLENCE = registerWithEgg("primordial_malevolence", EntityType.Builder.of(PrimordialMalevolence::new, MobCategory.MONSTER).sized(1.9F, 1.9F).canSpawnFarFromPlayer(), 0x000000, 0x00FFFA, PrimordialMalevolence::registerAttributes);
	//pyrad
	//rock snot
	public static final DeferredHolder<EntityType<?>, EntityType<RootSprite>> ROOT_SPRITE = registerWithEgg("root_sprite", EntityType.Builder.of(RootSprite::new, MobCategory.CREATURE).sized(0.3F, 0.55F), 0x5D533D, 0x8F952B, RootSprite::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<GasCloud>> SHALLOWBREATH = registerWithEgg("shallowbreath", EntityType.Builder.of(GasCloud::new, MobCategory.MONSTER).sized(1.75F,1.75F), 0x61988D, 0x4C7F76, GasCloud::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Shambler>> SHAMBLER = registerWithEgg("shambler", EntityType.Builder.of(Shambler::new, MobCategory.MONSTER).sized(0.95F, 1.25F), 0x14331C, 0xCD5472, Shambler::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<SiltCrab>> SILT_CRAB = registerWithEgg("silt_crab", EntityType.Builder.of(SiltCrab::new, MobCategory.WATER_CREATURE).sized(0.8F, 0.6F), 0x468282, 0xBC4114, SiltCrab::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Sludge>> SLUDGE = registerWithEgg("sludge", EntityType.Builder.of(Sludge::new, MobCategory.MONSTER).sized(1.1F, 1.2F).fireImmune(), 0x726459, 0x726459, Sludge::registerAttributes);
	//sludge jet
	//sludge menace
	public static final DeferredHolder<EntityType<?>, EntityType<SludgeWorm>> SLUDGE_WORM = registerWithEgg("sludge_worm", EntityType.Builder.of(SludgeWorm::new, MobCategory.MONSTER).sized(0.4375F, 0.3125F).fireImmune(), 0x6D3D39, 0x301411, SludgeWorm::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<SmolSludge>> SMOL_SLUDGE = registerWithEgg("smol_sludge", EntityType.Builder.of(SmolSludge::new, MobCategory.MONSTER).sized(0.7F, 0.7F).fireImmune(), 0x726459, 0x726459, SmolSludge::registerAttributes);
	//spirit tree faces
	public static final DeferredHolder<EntityType<?>, EntityType<Splodeshroom>> SPLODESHROOM = registerWithEgg("splodeshroom", EntityType.Builder.of(Splodeshroom::new, MobCategory.MONSTER).sized(0.5F, 1.0F), 0xC8527D, 0x792B48, Mob::createMobAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Sporeling>> SPORELING = registerWithEgg("sporeling", EntityType.Builder.of(Sporeling::new, MobCategory.CREATURE).sized(0.3F, 0.6F), 0x696144, 0xFFFB00, Sporeling::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Stalker>> STALKER = registerWithEgg("stalker", EntityType.Builder.of(Stalker::new, MobCategory.MONSTER).sized(0.85F, 0.85F).clientTrackingRange(10), 0xE4DCC9, 0xD58888, Stalker::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<SwampHag>> SWAMP_HAG = registerWithEgg("swamp_hag", EntityType.Builder.of(SwampHag::new, MobCategory.MONSTER).sized(0.6F, 1.8F), 0x6E5B36, 0x226124, SwampHag::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<TameChiromaw>> TAME_CHIROMAW = registerWithAttributes("tame_chiromaw", EntityType.Builder.of(TameChiromaw::new, MobCategory.CREATURE).sized(0.7F, 0.9F).noSummon(), TameChiromaw::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<TarBeast>> TAR_BEAST = registerWithEgg("tar_beast", EntityType.Builder.of(TarBeast::new, MobCategory.MONSTER).sized(1.5F, 2.0F), 0x000000, 0x202020, TarBeast::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Tarminion>> TARMINION = registerWithEgg("tarminion", EntityType.Builder.of(Tarminion::new, MobCategory.CREATURE).sized(0.3F, 0.5F), 0x000000, 0x2E2E2E, Tarminion::registerAttributes);
	//termite
	public static final DeferredHolder<EntityType<?>, EntityType<TinySludgeWorm>> TINY_SLUDGE_WORM = registerWithEgg("tiny_sludge_worm", EntityType.Builder.of(TinySludgeWorm::new, MobCategory.MONSTER).sized(0.3125F, 0.3125F).fireImmune(), 0xDAC2A7, 0x5C4639, TinySludgeWorm::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<TinySludgeWormHelper>> TINY_SLUDGE_WORM_HELPER = registerWithAttributes("tiny_sludge_worm_helper", EntityType.Builder.of(TinySludgeWormHelper::new, MobCategory.CREATURE).sized(0.3125F, 0.3125F).fireImmune(), TinySludgeWormHelper::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<Wight>> WIGHT = registerWithEgg("wight", EntityType.Builder.of(Wight::new, MobCategory.MONSTER).sized(0.7F, 2.2F), 0xECF8E0, 0x243B0B, Wight::registerAttributes);

	//misc
	public static final DeferredHolder<EntityType<?>, EntityType<AnglerToothArrow>> ANGLER_TOOTH_ARROW = register("angler_tooth_arrow", EntityType.Builder.<AnglerToothArrow>of(AnglerToothArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<AngryPebble>> ANGRY_PEBBLE = register("angry_pebble", EntityType.Builder.<AngryPebble>of(AngryPebble::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
	public static final DeferredHolder<EntityType<?>, EntityType<BasiliskArrow>> BASILISK_ARROW = register("basilisk_arrow", EntityType.Builder.<BasiliskArrow>of(BasiliskArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<BetweenstonePebble>> BETWEENSTONE_PEBBLE = register("betweenstone_pebble", EntityType.Builder.<BetweenstonePebble>of(BetweenstonePebble::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20));
	//bubbler crab bubble
	public static final DeferredHolder<EntityType<?>, EntityType<ChiromawBarb>> CHIROMAW_BARB = register("chiromaw_barb", EntityType.Builder.<ChiromawBarb>of(ChiromawBarb::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<ChiromawShockBarb>> CHIROMAW_SHOCK_BARB = register("chiromaw_shock_barb", EntityType.Builder.<ChiromawShockBarb>of(ChiromawShockBarb::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<ChiromawDroppings>> CHIROMAW_DROPPINGS = register("chiromaw_droppings", EntityType.Builder.<ChiromawDroppings>of(ChiromawDroppings::new, MobCategory.MISC).sized(0.5F, 0.5F).noSave().noSummon().fireImmune());
	//decay pit target
	//draeton
	//draeton chiromaw
	//draeton dragonfly
	//draeton firefly
	public static final DeferredHolder<EntityType<?>, EntityType<ElectricShock>> ELECTRIC_SHOCK = register("electric_shock", EntityType.Builder.<ElectricShock>of(ElectricShock::new, MobCategory.MISC).sized(0.5F, 0.5F).fireImmune().noSave().noSummon());
	public static final DeferredHolder<EntityType<?>, EntityType<ThrownElixir>> ELIXIR = register("elixir", EntityType.Builder.<ThrownElixir>of(ThrownElixir::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10));
	//fake XP orb
	//falling block
	public static final DeferredHolder<EntityType<?>, EntityType<FishBait>> FISH_BAIT = register("fish_bait", EntityType.Builder.<FishBait>of(FishBait::new, MobCategory.MISC).sized(0.25F, 0.25F).eyeHeight(0.2125F).clientTrackingRange(6).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<BLFishHook>> FISH_HOOK = register("fish_hook", EntityType.Builder.<BLFishHook>of(BLFishHook::new, MobCategory.MISC).sized(0.25F, 0.25F));
	public static final DeferredHolder<EntityType<?>, EntityType<FishVortex>> FISH_VORTEX = register("fish_vortex", EntityType.Builder.of(FishVortex::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().noSummon());
	//fishing spear
	public static final DeferredHolder<EntityType<?>, EntityType<FlameJet>> FLAME_JET = register("flame_jet", EntityType.Builder.<FlameJet>of(FlameJet::new, MobCategory.MISC).sized(1.0F, 2.6F).fireImmune().noSummon().noSave());
	public static final DeferredHolder<EntityType<?>, EntityType<GalleryFrame>> GALLERY_FRAME = register("gallery_frame", EntityType.Builder.<GalleryFrame>of(GalleryFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).noSummon().clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
	//glowing goop
	//grappling hoke node
	//greebling corpse
	//ground spawner
	public static final DeferredHolder<EntityType<?>, EntityType<BLItemFrame>> ITEM_FRAME = register("item_frame", EntityType.Builder.<BLItemFrame>of(BLItemFrame::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE));
	//lightning bolt
	public static final DeferredHolder<EntityType<?>, EntityType<LurkerSkinRaft>> LURKER_SKIN_RAFT = register("lurker_skin_raft", EntityType.Builder.<LurkerSkinRaft>of(LurkerSkinRaft::new, MobCategory.MISC).sized(1.25F, 0.25F).clientTrackingRange(10));
	//mist bridge
	//moving wall
	public static final DeferredHolder<EntityType<?>, EntityType<OctineArrow>> OCTINE_ARROW = register("octine_arrow", EntityType.Builder.<OctineArrow>of(OctineArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<PoisonAnglerToothArrow>> POISON_ANGLER_TOOTH_ARROW = register("poison_angler_tooth_arrow", EntityType.Builder.<PoisonAnglerToothArrow>of(PoisonAnglerToothArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<PredatorArrowGuide>> PREDATOR_ARROW_GUIDE = register("predator_arrow_guide", EntityType.Builder.of(PredatorArrowGuide::new, MobCategory.MISC).sized(0.1F, 0.1F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<PrimordialMalevolenceBlockade>> PRIMORDIAL_MALEVOLENCE_BLOCKADE = registerWithAttributes("primordial_malevolence_blockade", EntityType.Builder.<PrimordialMalevolenceBlockade>of(PrimordialMalevolenceBlockade::new, MobCategory.MISC).sized(1.0F, 0.2F), PrimordialMalevolenceBlockade::createAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<PrimordialMalevolenceProjectile>> PRIMORDIAL_MALEVOLENCE_PROJECTILE = register("primordial_malevolence_projectile", EntityType.Builder.<PrimordialMalevolenceProjectile>of(PrimordialMalevolenceProjectile::new, MobCategory.MISC).sized(0.65F, 0.65F));
	public static final DeferredHolder<EntityType<?>, EntityType<PrimordialMalevolenceSpawner>> PRIMORDIAL_MALEVOLENCE_SPAWNER = register("primordial_malevolence_spawner", EntityType.Builder.<PrimordialMalevolenceSpawner>of(PrimordialMalevolenceSpawner::new, MobCategory.MISC).sized(0.4F, 0.4F));
	public static final DeferredHolder<EntityType<?>, EntityType<PrimordialMalevolenceTeleporter>> PRIMORDIAL_MALEVOLENCE_TELEPORTER = register("primordial_malevolence_teleporter", EntityType.Builder.of(PrimordialMalevolenceTeleporter::new, MobCategory.MISC).sized(1.0F, 1.0F));
	public static final DeferredHolder<EntityType<?>, EntityType<PrimordialMalevolenceTurret>> PRIMORDIAL_MALEVOLENCE_TURRET = registerWithAttributes("primordial_malevolence_turret", EntityType.Builder.<PrimordialMalevolenceTurret>of(PrimordialMalevolenceTurret::new, MobCategory.MISC).sized(0.4F, 0.4F), Mob::createMobAttributes);
	//pyrad flame
	//resurrection
	//root grabber
	//rope node
	public static final DeferredHolder<EntityType<?>, EntityType<SapSpit>> SAP_SPIT = register("sap_spit", EntityType.Builder.<SapSpit>of(SapSpit::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<Seat>> SEAT = register("seat", EntityType.Builder.<Seat>of(Seat::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().noSummon());
	public static final DeferredHolder<EntityType<?>, EntityType<ShockArrow>> SHOCK_ARROW = register("shock_arrow", EntityType.Builder.<ShockArrow>of(ShockArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
	//shockwave block
	//shockwave sword item
	public static final DeferredHolder<EntityType<?>, EntityType<SludgeBall>> SLUDGE_BALL = register("sludge_ball", EntityType.Builder.<SludgeBall>of(SludgeBall::new, MobCategory.MISC).sized(0.75F, 0.75F).clientTrackingRange(4).updateInterval(20));
	//sludge wall jet
	public static final DeferredHolder<EntityType<?>, EntityType<SludgeWormArrow>> SLUDGE_WORM_ARROW = register("sludge_worm_arrow", EntityType.Builder.<SludgeWormArrow>of(SludgeWormArrow::new, MobCategory.MISC).sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20));
	//snail poison jet
	//spike wave
	//sword energy
	public static final DeferredHolder<EntityType<?>, EntityType<ThrownTarminion>> THROWN_TARMINION = register("thrown_tarminion", EntityType.Builder.<ThrownTarminion>of(ThrownTarminion::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20));
	public static final DeferredHolder<EntityType<?>, EntityType<SludgeWormEggSac>> SLUDGE_WORM_EGG_SAC = registerWithAttributes("sludge_worm_egg_sac", EntityType.Builder.of(SludgeWormEggSac::new, MobCategory.MISC).sized(1.0F, 0.5F), BasicProximitySpawner::registerAttributes);
	public static final DeferredHolder<EntityType<?>, EntityType<UrchinSpike>> URCHIN_SPIKE = register("urchin_spike", EntityType.Builder.<UrchinSpike>of(UrchinSpike::new, MobCategory.MISC).sized(0.0F, 0.0F).fireImmune().noSummon());
	//volarkite
	public static final DeferredHolder<EntityType<?>, EntityType<VolatileSoul>> VOLATILE_SOUL = register("volatile_soul", EntityType.Builder.<VolatileSoul>of(VolatileSoul::new, MobCategory.MISC).sized(0.3F, 0.3F).fireImmune().noSummon());
	//weedwood rowboat

	public static <E extends Mob> DeferredHolder<EntityType<?>, EntityType<E>> registerWithEgg(String name, EntityType.Builder<E> builder, int primaryColor, int secondaryColor, Supplier<AttributeSupplier.Builder> attributes) {
		DeferredHolder<EntityType<?>, EntityType<E>> ret = registerWithAttributes(name, builder, attributes);
		SPAWN_EGGS.register(name + "_spawn_egg", () -> new DeferredSpawnEggItem(ret, primaryColor, secondaryColor, new Item.Properties()));
		return ret;
	}

	public static <E extends LivingEntity> DeferredHolder<EntityType<?>, EntityType<E>> registerWithAttributes(String name, EntityType.Builder<E> builder, Supplier<AttributeSupplier.Builder> attributes) {
		DeferredHolder<EntityType<?>, EntityType<E>> ret = register(name, builder);
		ATTRIBUTES.put(ret, attributes);
		return ret;
	}

	public static <E extends Entity> DeferredHolder<EntityType<?>, EntityType<E>> register(String name, EntityType.Builder<E> builder) {
		return ENTITY_TYPES.register(name, () -> builder.build(prefix(name)));
	}

	private static String prefix(String name) {
		return TheBetweenlands.prefix(name).toString();
	}
}
