package com.jrelay.ui.shared.utils.animate;

import javax.swing.Timer;

public class Animator {
    @FunctionalInterface
    public interface AnimationFrame {
        void onFrame(float fraction);
    }

    @FunctionalInterface
    public interface AnimationEvent {
        void run();
    }

    private final int duration;
    private AnimationEvent onBegin;
    private AnimationFrame onTimingEvent;
    private AnimationEvent onEnd;

    private Timer timer;

    public Animator(int durationMillis) {
        this.duration = durationMillis;
    }

    public Animator onBegin(AnimationEvent onBegin) {
        this.onBegin = onBegin;
        return this;
    }

    public Animator onTimingEvent(AnimationFrame onTimingEvent) {
        this.onTimingEvent = onTimingEvent;
        return this;
    }

    public Animator onEnd(AnimationEvent onEnd) {
        this.onEnd = onEnd;
        return this;
    }

    public void start() {
        long startTime;
        if (onBegin != null) {
            onBegin.run();
        }

        startTime = System.currentTimeMillis();

        timer = new Timer(10, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float fraction = Math.min(1.0f, elapsed / (float) duration);

            float eased = (float) (Math.sin(fraction * Math.PI - Math.PI / 2) + 1) / 2;

            if (onTimingEvent != null) {
                onTimingEvent.onFrame(eased);
            }

            if (fraction >= 1.0f) {
                timer.stop();
                if (onEnd != null) {
                    onEnd.run();
                }
            }
        });

        timer.start();
    }

    public boolean isRunning() {
        return timer != null && timer.isRunning();
    }
}
