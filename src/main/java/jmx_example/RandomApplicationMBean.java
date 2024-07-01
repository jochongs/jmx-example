package jmx_example;

public interface RandomApplicationMBean {
    int getIncrementValueLimit();
    void setIncrementValueLimit(int incrementValueLimit);
    int getThreadCountLimit();
    void setThreadCountLimit(int threadCountLimit);
    int getThreadSleepTimeLimit();
    void setThreadSleepTimeLimit(int threadSleepTimeLimit);
    int getThreadCount();
    int getValue();
}
