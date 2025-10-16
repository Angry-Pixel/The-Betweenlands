package thebetweenlands.common.registries;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.*;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentDataSerializer;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageSerializer;

//TODO much like data components, consider making attachments immutable objects (records)
//blessing is already done
public class AttachmentRegistry {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TheBetweenlands.ID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<BlessingData>> BLESSING = ATTACHMENT_TYPES.register("blessing", () -> AttachmentType.builder(BlessingData::noBlessing).serialize(BlessingData.CODEC).sync(BlessingData.STREAM_CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<CircleGemData>> CIRCLE_GEM = ATTACHMENT_TYPES.register("circle_gem", () -> AttachmentType.builder(CircleGemData::new).serialize(CircleGemData.CODEC).sync(CircleGemData.STREAM_CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<DecayData>> DECAY = ATTACHMENT_TYPES.register("decay", () -> AttachmentType.builder(DecayData::new).serialize(DecayData.CODEC).sync(DecayData.STREAM_CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<EquipmentData>> EQUIPMENT = ATTACHMENT_TYPES.register("equipment", () -> AttachmentType.builder(EquipmentData::new).serialize(new EquipmentDataSerializer()).sync(new EquipmentDataSerializer()).copyOnDeath().build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<FallDamageReductionData>> FALL_DAMAGE_REDUCTION = ATTACHMENT_TYPES.register("fall_damage_reduction", () -> AttachmentType.builder(FallDamageReductionData::new).serialize(FallDamageReductionData.CODEC).sync(FallDamageReductionData.STREAM_CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<FoodSicknessData>> FOOD_SICKNESS = ATTACHMENT_TYPES.register("food_sickness", () -> AttachmentType.builder(FoodSicknessData::new).serialize(FoodSicknessData.CODEC).sync(FoodSicknessData.STREAM_CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<InfestationIgnoreData>> INFESTATION_IGNORE = ATTACHMENT_TYPES.register("infestation_ignore", () -> AttachmentType.builder(InfestationIgnoreData::new).serialize(InfestationIgnoreData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<LastKilledData>> LAST_KILLED = ATTACHMENT_TYPES.register("last_killed", () -> AttachmentType.builder(() -> LastKilledData.setLastKilled(null)).serialize(LastKilledData.CODEC).sync(LastKilledData.STREAM_CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<MudWalkerData>> MUD_WALKER = ATTACHMENT_TYPES.register("mud_walker", () -> AttachmentType.builder(MudWalkerData::new).serialize(MudWalkerData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<PuppetData>> PUPPET = ATTACHMENT_TYPES.register("puppet", () -> AttachmentType.builder(PuppetData::new).serialize(PuppetData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<PuppeteerData>> PUPPETEER = ATTACHMENT_TYPES.register("puppeteer", () -> AttachmentType.builder(PuppeteerData::new).serialize(PuppeteerData.CODEC).sync(PuppeteerData.STREAM_CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<RotSmellData>> ROT_SMELL = ATTACHMENT_TYPES.register("rot_smell", () -> AttachmentType.builder(RotSmellData::new).serialize(RotSmellData.CODEC).sync(RotSmellData.STREAM_CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<SwarmedData>> SWARMED = ATTACHMENT_TYPES.register("swarmed", () -> AttachmentType.builder(SwarmedData::new).serialize(SwarmedData.CODEC).sync(SwarmedData.STREAM_CODEC).build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<BetweenlandsWorldStorage>> WORLD_STORAGE = ATTACHMENT_TYPES.register("world_storage", () -> AttachmentType.builder(BetweenlandsWorldStorage::create).serialize(new WorldStorageSerializer()).sync(new WorldStorageSerializer()).copyHandler(BetweenlandsWorldStorage::copy).build());
}
