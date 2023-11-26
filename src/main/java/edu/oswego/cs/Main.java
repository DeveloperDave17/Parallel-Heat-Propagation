package edu.oswego.cs;

import java.util.Random;
import java.util.concurrent.*;

public class Main {

    private static final double DEFAULT_S = 1200;
    private static final double DEFAULT_T = 1200;
    private static final double DEFAULT_C1 = 0.75;
    private static final double DEFAULT_C2 = 1.0;
    private static final double DEFAULT_C3 = 1.25;
    private static final int DEFAULT_HEIGHT = 200;
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_THRESHOLD = 10000;

    private static volatile MetalAlloy alloyToBePainted;

    private static volatile boolean simulationIsActive;

    public static void main(String[] args) throws Exception {
        // How much the top left corner will be heated up at the beginning of each phase
        final double S;
        if (args.length > 0) {
            S = Double.parseDouble(args[0]);
        } else {
            S = DEFAULT_S;
        }
        // How much the bottom right corner will be heated up at the beginning of each phase
        final double T;
        if (args.length > 1) {
            T = Double.parseDouble(args[1]);
        } else {
            T = DEFAULT_T;
        }
        // Thermal constant for metal 1
        final double C1;
        if (args.length > 2) {
            C1 = Double.parseDouble(args[2]);
        } else {
            C1 = DEFAULT_C1;
        }
        // Thermal constant for metal 2
        final double C2;
        if (args.length > 3) {
            C2 = Double.parseDouble(args[3]);
        } else {
            C2 = DEFAULT_C2;
        }
        // Thermal constant for metal 3
        final double C3;
        if (args.length > 4) {
            C3 = Double.parseDouble(args[4]);
        } else {
            C3 = DEFAULT_C3;
        }
        final int HEIGHT;
        if (args.length > 5) {
            HEIGHT = Integer.parseInt(args[5]);
        } else {
            HEIGHT = DEFAULT_HEIGHT;
        }
        final int WIDTH;
        if (args.length > 6) {
            WIDTH = Integer.parseInt(args[6]);
        } else {
            WIDTH = DEFAULT_WIDTH;
        }
        final int THRESHOLD;
        if (args.length > 7) {
            THRESHOLD = Integer.parseInt(args[7]);
        } else {
            THRESHOLD = DEFAULT_THRESHOLD;
        }
        runSimulation(S, T, C1, C2, C3, HEIGHT, WIDTH, THRESHOLD);
    }

    public static void runSimulation(double s, double t, double c1, double c2, double c3, int height, int width, int threshold) throws SecurityException, InterruptedException {
        final MetalAlloy alloyA = new MetalAlloy(height, width, c1, c2, c3);
        // Display Alloy A first
        alloyToBePainted = alloyA;
        final MetalAlloy alloyB = new MetalAlloy(height, width, c1, c2, c3);
        final Phaser quadrantPhaser = new Phaser();
        final Phaser regionPhaser = new Phaser();
        MetalAlloyView metalAlloyView = new MetalAlloyView(height, width);
        metalAlloyView.displayRegions(alloyA);
        metalAlloyView.display();
        ExecutorService displayService = Executors.newFixedThreadPool(1);
        // Activate the display for the simulation
        simulationIsActive = true;
        displayService.submit(() -> {
            while (simulationIsActive) {
                metalAlloyView.displayRegions(alloyToBePainted);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        alloyA.setTempOfRegion(s, 0, 0);
        alloyA.setTempOfRegion(t, height - 1, width - 1);
        alloyA.getMetalAlloyRegion(0, 0).calcRGB();
        alloyA.getMetalAlloyRegion(height - 1, width - 1).calcRGB();
        alloyB.setTempOfRegion(s, 0, 0);
        alloyB.setTempOfRegion(t, height - 1, width - 1);
        alloyB.getMetalAlloyRegion(0, 0).calcRGB();
        alloyB.getMetalAlloyRegion(height - 1, width - 1).calcRGB();
        for (int currentIteration = 1; currentIteration <= threshold; currentIteration++) {
            ExecutorService workStealingPool = new ForkJoinPool();
            // Swap which alloy is the preOperationAlloy
            boolean useAForPreOp = currentIteration % 2 == 0;
            // Update the Metal Alloy
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (i == 0 && i == j) {
                        continue;
                    }
                    if (i == height - 1 && j == width - 1) {
                        continue;
                    }
                    final int ROW = i;
                    final int COL = j;
                    if (useAForPreOp) {
                        workStealingPool.submit(() -> {
                            double result = alloyA.calculateNewTempForRegion(ROW, COL);
                            alloyB.setTempOfRegion(result, ROW, COL);
                            alloyB.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    } else {
                        workStealingPool.submit(() -> {
                            double result = alloyB.calculateNewTempForRegion(ROW, COL);
                            alloyA.setTempOfRegion(result, ROW, COL);
                            alloyA.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
            }
            workStealingPool.shutdown();
            workStealingPool.awaitTermination(1, TimeUnit.SECONDS);
            // Display updates
            if (useAForPreOp) {
                alloyToBePainted = alloyB;
            } else {
                alloyToBePainted = alloyA;
            }
        }
        // Inform the gui that it no longer needs to refresh
        simulationIsActive = false;
        displayService.shutdown();
        displayService.awaitTermination(1, TimeUnit.SECONDS);
    }
}