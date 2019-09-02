package main;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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

    public static TimeCounterHelper getTimeCounterHelper(int iterations) {
        return new TimeCounterHelper(iterations);
    }

    public static class TimeCounterHelper {
        private long initialTime, finishTime;
        private AtomicLong totalTime;
        private int iterations;
        private long maxTime, minTime;
        private AtomicBoolean isFinished;

        private TimeCounterHelper(int iterations) {
            maxTime = Long.MIN_VALUE;
            minTime = Long.MAX_VALUE;
            this.iterations = iterations;
            isFinished = new AtomicBoolean(false);
            totalTime = new AtomicLong();
        }

        public void update(long iterationTime) {
            totalTime.addAndGet(iterationTime);
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

        public void finish() {
            finishTime = System.nanoTime();
            isFinished.set(true);
        }

        public float getTimeElapsed() {
            if (isFinished.get()) {
                return CalcHelper.convertSystemNanosToSeconds(totalTime.get());
            }
            else return -1;
        }

        public float getMaxTime() {
            if (isFinished.get()) {
                return CalcHelper.convertSystemNanosToSeconds(maxTime);
            }
            else return -1;
        }

        public float getMinTime() {
            if (isFinished.get()) {
                return CalcHelper.convertSystemNanosToSeconds(minTime);
            }
            else return -1;
        }

        public float getSecondsPerIteration() {
            if (isFinished.get()) {
                return getTimeElapsed() / iterations;
            }
            else return -1;
        }

        public int getIterations() { return iterations; }

        @Override
        public String toString() {
            return String.format("Time elapsed for %d iterations: %f.\n" +
                            "Seconds per iteration: %f. Max: %f. Min: %f.\n",
                    iterations,
                    getTimeElapsed(),
                    getSecondsPerIteration(),
                    getMaxTime(), getMinTime());
        }
    }
}
