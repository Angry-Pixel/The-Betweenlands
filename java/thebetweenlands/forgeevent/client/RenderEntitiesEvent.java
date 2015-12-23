package thebetweenlands.forgeevent.client;

import cpw.mods.fml.common.eventhandler.Event;

public abstract class RenderEntitiesEvent extends Event {
	public static class Pre extends RenderEntitiesEvent {}

	public static class Post extends RenderEntitiesEvent {}
}
