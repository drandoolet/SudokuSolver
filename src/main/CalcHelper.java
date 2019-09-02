package main;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CalcHelper {
    private CalcHelper() {}

    public static float getSecondsElapsedFrom(long sysTimeNanos) {
        return getSecondsElapsedFrom(sysTimeNanos, System.nanoTime());
    }

    public static float getSecondsElapsedFrom(long from, long to) {
        return convertSystemNanosToSeconds(to - from);
    }

    public static float convertSystemNanosToSeconds(long nanos) {
        return ((float) TimeUnit.NANOSECONDS.toSeconds(nanos*100)) / 100;
    }

    public static TimeCounterHelper getTimeCounterHelper() {
        return new TimeCounterHelper();
    }

    public static class TimeCounterHelper {
        private AtomicLong totalTime;
        private AtomicInteger iterations;
        private long maxTime, minTime;

        private TimeCounterHelper() {
            maxTime = Long.MIN_VALUE;
            minTime = Long.MAX_VALUE;
            iterations = new AtomicInteger();
            totalTime = new AtomicLong();
        }

        public void update(long iterationTime) {
            totalTime.addAndGet(iterationTime);
            iterations.incrementAndGet();
            updateMaxMinTime(iterationTime);
        }

        private void updateMaxMinTime(long time) {
            if (time > maxTime) {
                maxTime = time;
                return;
            }
            if (time < minTime) {
                minTime = time;
            }
        }

        public float getTimeElapsed() {
            return CalcHelper.convertSystemNanosToSeconds(totalTime.get());
        }

        public float getMaxTime() {
            return CalcHelper.convertSystemNanosToSeconds(maxTime);
        }

        public float getMinTime() {
            return CalcHelper.convertSystemNanosToSeconds(minTime);
        }

        public float getSecondsPerIteration() {
            return getTimeElapsed() / iterations.get();
        }

        public int getIterations() { return iterations.get(); }

        @Override
        public String toString() {
            return String.format("Time elapsed for %d iterations: %f.\n" +
                            "Seconds per iteration: %f. Max: %f. Min: %f.\n",
                    iterations.get(),
                    getTimeElapsed(),
                    getSecondsPerIteration(),
                    getMaxTime(), getMinTime());
        }
    }
}
