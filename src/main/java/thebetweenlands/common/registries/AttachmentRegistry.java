package thebetweenlands.common.registries;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.SynchedAttachmentType;
import thebetweenlands.common.component.entity.BlessingData;
import thebetweenlands.common.component.entity.CircleGemData;
import thebetweenlands.common.component.entity.DecayData;
import thebetweenlands.common.component.entity.FallDamageReductionData;
import thebetweenlands.common.component.entity.FoodSicknessData;
import thebetweenlands.common.component.entity.InfestationIgnoreData;
import thebetweenlands.common.component.entity.LastKilledData;
import thebetweenlands.common.component.entity.MudWalkerData;
import thebetweenlands.common.component.entity.RotSmellData;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageSerializer;

public class AttachmentRegistry {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TheBetweenlands.ID);
	public static final DeferredRegister<SynchedAttachmentType<?>> SYNCHED_ATTACHMENT_TYPES = DeferredRegister.create(BLRegistries.Keys.SYNCHED_ATTACHMENT_TYPES, TheBetweenlands.ID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<BlessingData>> BLESSING = ATTACHMENT_TYPES.register("blessing", () -> AttachmentType.builder(() -> new BlessingData()).serialize(BlessingData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<CircleGemData>> CIRCLE_GEM = ATTACHMENT_TYPES.register("circle_gem", () -> AttachmentType.builder(() -> new CircleGemData()).serialize(CircleGemData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<DecayData>> DECAY = ATTACHMENT_TYPES.register("decay", () -> AttachmentType.builder(DecayData::new).serialize(DecayData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<FallDamageReductionData>> FALL_DAMAGE_REDUCTION = ATTACHMENT_TYPES.register("fall_damage_reduction", () -> AttachmentType.builder(() -> new FallDamageReductionData()).serialize(FallDamageReductionData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<FoodSicknessData>> FOOD_SICKNESS = ATTACHMENT_TYPES.register("food_sickness", () -> AttachmentType.builder(FoodSicknessData::new).serialize(FoodSicknessData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<InfestationIgnoreData>> INFESTATION_IGNORE = ATTACHMENT_TYPES.register("infestation_ignore", () -> AttachmentType.builder(() -> new InfestationIgnoreData()).serialize(InfestationIgnoreData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<LastKilledData>> LAST_KILLED = ATTACHMENT_TYPES.register("last_killed", () -> AttachmentType.builder(() -> new LastKilledData()).serialize(LastKilledData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<MudWalkerData>> MUD_WALKER = ATTACHMENT_TYPES.register("mud_walker", () -> AttachmentType.builder(() -> new MudWalkerData()).serialize(MudWalkerData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<RotSmellData>> ROT_SMELL = ATTACHMENT_TYPES.register("rot_smell", () -> AttachmentType.builder(RotSmellData::new).serialize(RotSmellData.CODEC).build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<BetweenlandsWorldStorage>> WORLD_STORAGE = ATTACHMENT_TYPES.register("world_storage", () -> AttachmentType.builder(BetweenlandsWorldStorage::new).serialize(new WorldStorageSerializer()).build());
	
	public static final DeferredHolder<SynchedAttachmentType<?>, SynchedAttachmentType<RotSmellData>> ROT_SMELL_SYNCHER = SYNCHED_ATTACHMENT_TYPES.register("rot_smell", () -> new SynchedAttachmentType<>(ROT_SMELL.getKey(), () -> RotSmellData.STREAM_CODEC));
	
}
