package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class GunkData {

	public static final int GUNK_MAX = 100;
	public static final int ENTER_WAIT_TIME = 20;
	public static final int EXIT_WAIT_TIME = 35;

	public static final Codec<GunkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("gunk_counter").forGetter(o -> o.gunkCounter),
		Codec.INT.fieldOf("enter_pause_timer").forGetter(o -> o.enterPauseTimer),
		Codec.INT.fieldOf("exit_pause_timer").forGetter(o -> o.exitPauseTimer)
	).apply(instance, GunkData::new));

	public static final StreamCodec<FriendlyByteBuf, GunkData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, o -> o.gunkCounter,
		ByteBufCodecs.INT, o -> o.enterPauseTimer,
		ByteBufCodecs.INT, o -> o.exitPauseTimer,
		GunkData::new
	);

	// 0-100, counts how much gunk is on the player
	private int gunkCounter;
	// 0-20, tracks the amount of time the player can be in swamp water (not necessarily in the swim pose) before they will start to gain gunk from swimming (only in the swim pose)
	// The player will still gain gunk from swimming through plants (in the swim pose) even if this has not reached 0 yet
	// Once the player exits, increases by 1 each tick until it reaches its maximum of 20
	private int enterPauseTimer;
	// 0-35, tracks the amount of time before any out-of-water behaviours (e.g. gunk slowly decreasing) begin
	// If the player enters water, immediately set to its maximum of 35
	private int exitPauseTimer;
	
	public GunkData() {
		this(0, ENTER_WAIT_TIME, EXIT_WAIT_TIME);
	}
	
	public GunkData(int gunkCounter, int enterPauseTimer, int exitPauseTimer) {
		this.gunkCounter = gunkCounter;
		this.enterPauseTimer = enterPauseTimer;
		this.exitPauseTimer = exitPauseTimer;
	}
}
