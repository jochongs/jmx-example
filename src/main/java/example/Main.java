package example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class RandomApplication {
    private int threadSleepTimeLimit = 3 * 1000;
    private int threadCountLimit = 10;

    // 랜덤값 분포 그래프를 그리기 위한 변수
    private List<Integer> randomValues = Collections.synchronizedList(new ArrayList<Integer>());

    private int incrementValueLimit = 3;

    private final List<Thread> threads = new ArrayList<>();
    private final AtomicInteger value = new AtomicInteger(0);

    // getter, setter
    // ------------------------------------------------------------
    // ------------------------------------------------------------

    public int getIncrementValueLimit() {
        return incrementValueLimit;
    }

    public void setIncrementValueLimit(int incrementValueLimit) {
        this.incrementValueLimit = incrementValueLimit;
    }

    public int getThreadCountLimit() {
        return threadCountLimit;
    }

    public void setThreadCountLimit(int threadCountLimit) {
        this.threadCountLimit = threadCountLimit;
    }

    public int getThreadSleepTimeLimit() {
        return threadSleepTimeLimit;
    }

    public void setThreadSleepTimeLimit(int threadSleepTimeLimit) {
        this.threadSleepTimeLimit = threadSleepTimeLimit;
    }

    public int getThreadCount() {
        return threads.size();
    }

    public int getValue() {
        return value.get();
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------


    public void printResult() {
        printResult(3000);
    }

    public void printResult(int milliseconds) {
        while (true) {
            try {
                System.out.println("Thread count: " + getThreadCount());
                System.out.println("Value: " + getValue());
                System.out.println("==================================");
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start() {
        // 1초마다 현재 스레드 개수와 value 값을 출력
        new Thread(this::printResult).start();

        // 1 ~ n개 사이의 스레드를 랜덤하게 생성하고, join을 통해 전체 종료를 대기 한다
        int repeatCount = 500;
        int j = 0;
        while (j <= repeatCount) {
            IntStream.range(1, 2).forEach(i -> {
                Thread thread = new Thread(() -> {
                    try {
                        // 0 ~ n초 사이의 랜덤한 시간만큼 스레드를 대기
                        Thread.sleep(1);
                        // -3 ~ 3 사이의 랜덤 정수(증분)
                        int ra = new Random(System.currentTimeMillis()).nextInt(incrementValueLimit * 2) - incrementValueLimit;
                        randomValues.add(ra);
                        value.updateAndGet(v -> v + ra);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                threads.add(thread);
            });

            threads.forEach(thread -> {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            threads.clear();
            j++;
        }

        Map<Integer, Integer> integerMap = randomValues.stream()
                .collect(Collectors.toMap(
                        value -> value,
                        value -> 1,
                        (oldValue, newValue) -> oldValue + 1
                ));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = -3; i <= 3; i++) {
            Number number = integerMap.get(i);
            dataset.addValue(number, "Frequency", i);
        }

        // 차트 생성
        JFreeChart barChart = ChartFactory.createBarChart(
                "Value Frequency",
                "Value",
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // 차트 패널 생성
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        // JFrame에 차트 패널 추가
        JFrame frame = new JFrame();
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        System.out.println(integerMap);
    }
}

public class Main {
    public static void main(String[] args) {
        new RandomApplication().start();
    }
}