package thebetweenlands.api.rune;

public final class RuneMenuDrawingContext {
	public static enum Connection {
		/**
		 * A connection from the rune GUI is rendered
		 * to the dropdown menu
		 */
		VIA_DROPDOWN,

		/**
		 * A connection from the primary rune GUI is directly
		 * rendered to the secondary rune GUI
		 */
		DIRECTLY,

		/**
		 * A connection is rendered while connecting
		 * a rune token
		 */
		CONNECTING,

		/**
		 * Any other situation
		 */
		OTHER
	}

	public static enum Token {
		/**
		 * A token is rendered in the dropdown menu
		 */
		DROPDOWN,

		/**
		 * A token is rendered in the dropdown menu
		 * while connecting a rune token
		 */
		DROPDOWN_CONNECTING,

		/**
		 * A token is rendered in the dropdown menu
		 * with a connection connected to it
		 */
		DROPDOWN_CONNECTION,

		/**
		 * A token is rendered in the dropdown menu
		 * with a connection connected to it while
		 * connecting a rune token
		 */
		DROPDOWN_CONNECTION_AND_CONNECTING,

		/**
		 * Any other situation
		 */
		OTHER
	}

	public static enum Tooltip {
		/**
		 * A tooltip of a token in the dropdown
		 * menu is rendered
		 */
		DROPDOWN,

		/**
		 * A tooltip of a token in the dropdown
		 * menu is rendered while connecting
		 * a rune token
		 */
		DROPDOWN_CONNECTING,

		/**
		 * A tooltip of a token in the dropdown
		 * menu is rendered with a connection
		 * connected to it
		 */
		DROPDOWN_CONNECTION,

		/**
		 * A tooltip of a token in the dropdown
		 * menu is rendered with a connection
		 * connected to it while connecting
		 * a rune token
		 */
		DROPDOWN_CONNECTION_AND_CONNECTING,

		/**
		 * A tooltip is rendered at one of the
		 * ends of a connection
		 */
		CONNECTION_END,

		/**
		 * Any other situation
		 */
		OTHER
	}
}
