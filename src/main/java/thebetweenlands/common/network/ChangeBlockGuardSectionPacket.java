package thebetweenlands.common.network;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationGuarded;
import thebetweenlands.common.world.storage.location.guard.BlockLocationGuard.GuardChunkSection;

public class ChangeBlockGuardSectionPacket implements CustomPacketPayload {
	private final String id;
	private final BlockPos pos;
	private final byte[] data;

	public static final Type<ChangeBlockGuardSectionPacket> TYPE = new Type<>(TheBetweenlands.prefix("change_block_guard_section"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ChangeBlockGuardSectionPacket> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, o -> o.id,
		BlockPos.STREAM_CODEC, o -> o.pos,
		ByteBufCodecs.byteArray(512), o -> o.data,
		ChangeBlockGuardSectionPacket::new);

	private ChangeBlockGuardSectionPacket(String id, BlockPos pos, byte[] data) {
		this.id = id;
		this.pos = pos;
		this.data = data;
	}

	public ChangeBlockGuardSectionPacket(LocationGuarded storage, BlockPos pos, @Nullable GuardChunkSection section) {
		this.id = storage.getID().getStringID();
		this.pos = pos;
		if (section != null && section.getBlockRefCount() > 0) {
			this.data = new byte[512];
			section.writeData(this.data);
		} else {
			this.data = null;
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(ChangeBlockGuardSectionPacket packet, IPayloadContext context) {
		context.enqueueWork(() -> {
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(context.player().level());
			ILocalStorage storage = worldStorage.getLocalStorageHandler().getLocalStorage(StorageID.fromString(packet.id));
			if (storage instanceof LocationGuarded location) {
				if (location.getGuard() != null) {
					GuardChunkSection section = location.getGuard().getSection(packet.pos);
					if (packet.data != null) {
						//Make sure chunk and section are loaded and not null
						location.getGuard().setGuarded(context.player().level(), packet.pos, true);
						section = location.getGuard().getSection(packet.pos);
						if (section != null) {
							section.loadData(packet.data);
						}
					} else if (section != null) {
						section.clear();
					}
				}
			}
		});
	}
}