package thebetweenlands.client.audio;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;
import paulscode.sound.libraries.ChannelLWJGLOpenAL;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;
import paulscode.sound.libraries.SourceLWJGLOpenAL;

public class SoundSystemOpenALAccess {
	/**
	 * Thrown when an error occurs that causes the OpenAL access to go uninitialized
	 * and enter the errored state
	 */
	public static final class OpenALAccessInitException extends Exception {
		private static final long serialVersionUID = -8924694992857474053L;

		private OpenALAccessInitException(String msg, Throwable ex) {
			super(msg, ex);
		}
	}

	private static final AtomicInteger ACCESS_THREAD_ID = new AtomicInteger(0);

	private static final Logger LOGGER = LogManager.getLogger();

	private static Field fieldSoundLibrary;
	private static Field fieldSourceMap;
	private static Field fieldChannelOpenAL;

	private volatile boolean initialized = false;

	private SoundManager soundManager;
	private LibraryLWJGLOpenAL library;

	private ExecutorService service;

	private boolean errored = false;

	static {
		try {
			fieldSoundLibrary = SoundSystem.class.getDeclaredField("soundLibrary");
			fieldSoundLibrary.setAccessible(true);
		} catch (Exception ex) {
			LOGGER.error("Failed retrieving 'soundLibrary' field from SoundSystem", ex);
		}

		try {
			fieldSourceMap = Library.class.getDeclaredField("sourceMap");
			fieldSourceMap.setAccessible(true);
		} catch (Exception ex) {
			LOGGER.error("Failed retrieving 'sourceMap' field from Library", ex);
		}

		try {
			fieldChannelOpenAL = SourceLWJGLOpenAL.class.getDeclaredField("channelOpenAL");
			fieldChannelOpenAL.setAccessible(true);
		} catch (Exception ex) {
			LOGGER.error("Failed retrieving 'channelOpenAL' field from SourceLWJGLOpenAL", ex);
		}
	}

	/**
	 * Initializes the OpenAL access for the specified sound manager
	 * @param soundManager
	 */
	public synchronized void init(SoundManager soundManager) {
		this.cleanup();

		this.errored = true;

		if(fieldSoundLibrary != null && fieldSourceMap != null && fieldChannelOpenAL != null) {
			try {
				if(soundManager.sndSystem instanceof SoundSystem) {
					Library library = (Library) fieldSoundLibrary.get((SoundSystem)soundManager.sndSystem);
					if(library instanceof LibraryLWJGLOpenAL) {
						this.library = (LibraryLWJGLOpenAL) library;
						this.soundManager = soundManager;
						this.service = Executors.newFixedThreadPool(1, (Runnable task) -> {
							Thread thread = new Thread(task);
							thread.setDaemon(true);
							thread.setName("Betweenlands Sound System Access #" + ACCESS_THREAD_ID.getAndIncrement());
							return thread;
						});
						this.errored = false;
						this.initialized = true;
					} else {
						LOGGER.error("Sound library is not OpenAL library");
					}
				} else {
					LOGGER.error("Field 'sndSystem' of SoundManager is not SoundSystem");
				}
			} catch (IllegalArgumentException | IllegalAccessException ex) {
				LOGGER.error("Failed accessing 'soundLibrary' field of SoundSystem", ex);
			}
		}
	}

	/**
	 * Cleans up any resources claimed by this
	 * object
	 */
	public synchronized void cleanup() {
		this.errored = false;
		this.initialized = false;

		this.library = null;
		this.soundManager = null;

		if(this.service != null) {
			this.service.shutdownNow();
			this.service = null;
		}
	}

	/**
	 * Submits a task to be executed for the sound system, handles synchronization
	 * @param task
	 * @return
	 */
	@Nullable
	public synchronized <T> Future<T> submitToSoundSystem(Callable<T> task) {
		return this.service != null ? this.service.submit(() -> {
			synchronized(this) {
				synchronized(SoundSystemConfig.THREAD_SYNC) {
					return task.call();
				}
			}
		}) : null;
	}

	/**
	 * Returns whether the OpenAL access is initialized and ready to be used
	 * @return
	 */
	public boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * Returns whether an error has occurred such that the OpenAL access
	 * can't be used
	 * @return
	 */
	public boolean isErrored() {
		return this.errored;
	}

	/**
	 * Returns the {@link SourceLWJGLOpenAL} for the specified sound, or null if none is found.
	 * Not synchronized -> must only be called through {@link #submitToSoundSystem(Callable)}
	 * @param sound
	 * @return
	 * @throws OpenALAccessInitException
	 */
	@Nullable
	public SourceLWJGLOpenAL getSourceAsync(ISound sound) throws OpenALAccessInitException {
		if(!this.initialized) {
			return null;
		}

		try {
			String sourceName = this.soundManager.invPlayingSounds.get(sound);

			if(sourceName != null) {
				@SuppressWarnings("unchecked")
				Source source = ((Map<String, Source>)fieldSourceMap.get(this.library)).get(sourceName);

				if(source instanceof SourceLWJGLOpenAL) {
					return (SourceLWJGLOpenAL) source;
				}
			}

			return null;
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			this.initialized = false;
			this.errored = true;
			throw new OpenALAccessInitException("Failed retrieving SourceLWJGLOpenAL from sound system", ex);
		}
	}

	/**
	 * Returns the {@link ChannelLWJGLOpenAL} for the specified sound, or null if none is found.
	 * Not synchronized -> must only be called through {@link #submitToSoundSystem(Callable)}
	 * @param sound
	 * @return
	 */
	@Nullable
	public ChannelLWJGLOpenAL getChannelAsync(ISound sound) {
		try {
			SourceLWJGLOpenAL source = this.getSourceAsync(sound);

			if(source != null && source.channel instanceof ChannelLWJGLOpenAL) {
				return (ChannelLWJGLOpenAL) source.channel;
			}
		} catch (OpenALAccessInitException ex) {
			LOGGER.info("Failed retrieving OpenAL channel", ex);
		}

		return null;
	}

	/**
	 * Sets the current offset (i.e. time since start) for the specified sound.
	 * Not synchronized -> must only be called through {@link #submitToSoundSystem(Callable)}.
	 * <b>Only works for non-streamed sounds!</b>
	 * @param sound
	 * @param seconds
	 * @return
	 */
	public boolean setOffsetSecondsAsync(ISound sound, float seconds) {
		ChannelLWJGLOpenAL channel = this.getChannelAsync(sound);

		if(channel != null && channel.ALSource != null && channel.attachedSource != null) {
			int sourceId = channel.ALSource.get(0);
			if(sourceId >= 0) {
				AL10.alSourcef(sourceId, AL11.AL_SEC_OFFSET, seconds);
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the current offset (i.e. time since start) for the specified sound, or -1 if the
	 * sound is not playing.
	 * Not synchronized -> must only be called through {@link #submitToSoundSystem(Callable)}.
	 * <b>Only works for non-streamed sounds!</b>
	 * @param sound
	 * @return
	 */
	public float getOffsetSecondsAsync(ISound sound) {
		ChannelLWJGLOpenAL channel = this.getChannelAsync(sound);

		if(channel != null && channel.ALSource != null) {
			int sourceId = channel.ALSource.get(0);
			if(sourceId >= 0) {
				return AL10.alGetSourcef(sourceId, AL11.AL_SEC_OFFSET);
			}
		}

		return -1.0f;
	}

	/**
	 * Sets the current offset (i.e. time since start) for the specified sound.
	 * <b>Only works for non-streamed sounds!</b>
	 * @param sound
	 * @param seconds
	 * @return
	 */
	@Nullable
	public Future<Boolean> setOffsetSeconds(ISound sound, float seconds) {
		return this.submitToSoundSystem(() -> this.setOffsetSecondsAsync(sound, seconds));
	}

	/**
	 * Returns the current offset (i.e. time since start) for the specified sound.
	 * <b>Only works for non-streamed sounds!</b>
	 * @param sound
	 * @return
	 */
	@Nullable
	public Future<Float> getOffsetSeconds(ISound sound) {
		return this.submitToSoundSystem(() -> this.getOffsetSecondsAsync(sound));
	}
}
