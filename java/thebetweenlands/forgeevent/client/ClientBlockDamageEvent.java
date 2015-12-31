package thebetweenlands.forgeevent.client;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

public class ClientBlockDamageEvent extends Event {
	@Cancelable
	public static class Pre extends ClientBlockDamageEvent {}

	public static class Post extends ClientBlockDamageEvent {}
}
