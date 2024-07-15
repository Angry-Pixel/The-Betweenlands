package thebetweenlands.common.registries;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.CircleGemData;
import thebetweenlands.common.component.entity.DecayData;
import thebetweenlands.common.component.entity.MudWalkerData;
import thebetweenlands.common.component.entity.RotSmellData;

public class AttachmentRegistry {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, TheBetweenlands.ID);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<CircleGemData>> CIRCLE_GEM = ATTACHMENT_TYPES.register("circle_gem", () -> AttachmentType.builder(() -> new CircleGemData()).serialize(CircleGemData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<DecayData>> DECAY = ATTACHMENT_TYPES.register("decay", () -> AttachmentType.builder(DecayData::new).serialize(DecayData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<MudWalkerData>> MUD_WALKER = ATTACHMENT_TYPES.register("mud_walker", () -> AttachmentType.builder(() -> new MudWalkerData()).serialize(MudWalkerData.CODEC).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<RotSmellData>> ROT_SMELL = ATTACHMENT_TYPES.register("rot_smell", () -> AttachmentType.builder(RotSmellData::new).serialize(RotSmellData.CODEC).build());

}
