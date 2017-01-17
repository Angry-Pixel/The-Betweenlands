package thebetweenlands.common.network.interception;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ProgressivePromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;

public class EventLoopProxy implements EventLoop {
	private final EventLoop parent;
	private final Function<Runnable, Runnable> executeProxy;

	public EventLoopProxy(EventLoop parent, Function<Runnable, Runnable> executeProxy) {
		this.parent = parent;
		this.executeProxy = executeProxy;
	}

	@Override
	public boolean inEventLoop() {
		return this.parent.inEventLoop();
	}

	@Override
	public boolean inEventLoop(Thread thread) {
		return this.parent.inEventLoop(thread);
	}

	@Override
	public <V> Promise<V> newPromise() {
		return this.parent.newPromise();
	}

	@Override
	public <V> ProgressivePromise<V> newProgressivePromise() {
		return this.parent.newProgressivePromise();
	}

	@Override
	public <V> Future<V> newSucceededFuture(V result) {
		return this.parent.newSucceededFuture(result);
	}

	@Override
	public <V> Future<V> newFailedFuture(Throwable cause) {
		return this.parent.newFailedFuture(cause);
	}

	@Override
	public boolean isShuttingDown() {
		return this.parent.isShuttingDown();
	}

	@Override
	public Future<?> shutdownGracefully() {
		return this.parent.shutdownGracefully();
	}

	@Override
	public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
		return this.parent.shutdownGracefully(quietPeriod, timeout, unit);
	}

	@Override
	public Future<?> terminationFuture() {
		return this.parent.terminationFuture();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void shutdown() {
		this.parent.shutdown();
	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Runnable> shutdownNow() {
		return this.parent.shutdownNow();
	}

	@Override
	public Iterator<EventExecutor> iterator() {
		return this.parent.iterator();
	}

	@Override
	public Future<?> submit(Runnable task) {
		return this.parent.submit(task);
	}

	@Override
	public <T> Future<T> submit(Runnable task, T result) {
		return this.parent.submit(task, result);
	}

	@Override
	public <T> Future<T> submit(Callable<T> task) {
		return this.parent.submit(task);
	}

	@Override
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		return this.parent.schedule(command, delay, unit);
	}

	@Override
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		return this.parent.schedule(callable, delay, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return this.parent.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return this.parent.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

	@Override
	public boolean isShutdown() {
		return this.parent.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return this.parent.isTerminated();
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return this.parent.awaitTermination(timeout, unit);
	}

	@Override
	public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		return this.parent.invokeAll(tasks);
	}

	@Override
	public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
			TimeUnit unit) throws InterruptedException {
		return this.parent.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		return this.parent.invokeAny(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return this.parent.invokeAny(tasks, timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		command = this.executeProxy.apply(command);
		if(command != null) {
			this.parent.execute(command);
		}
	}

	@Override
	public EventLoop next() {
		return this.parent.next();
	}

	@Override
	public ChannelFuture register(Channel channel) {
		return this.parent.register(channel);
	}

	@Override
	public ChannelFuture register(Channel channel, ChannelPromise promise) {
		return this.parent.register(channel, promise);
	}

	@Override
	public EventLoopGroup parent() {
		return this.parent.parent();
	}

}
