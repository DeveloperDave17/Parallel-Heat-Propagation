package edu.oswego.cs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public class MetalAlloy {

    private int height;
    private int width;

    // Thermal Constants
    private double c1;
    private double c2;
    private double c3;

    private MetalAlloyRegion[][] metalAlloyRegions;

    public MetalAlloy(int height, int width, double c1, double c2, double c3) {
        this.height = height;
        this.width = width;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        metalAlloyRegions = new MetalAlloyRegion[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Initialize all regions of the alloy to have a temperature of 0 degrees Celsius.
                metalAlloyRegions[i][j] = new MetalAlloyRegion(0);
                metalAlloyRegions[i][j].calcRGB();
            }
        }
    }

    public void increaseTempOfRegion(double tempIncrease, int row, int col) {
        metalAlloyRegions[row][col].increaseTemperature(tempIncrease);
    }

    public void setTempOfRegion(double newTemp, int row, int col) {
        metalAlloyRegions[row][col].setTemperature(newTemp);
    }

    public MetalAlloyRegion getMetalAlloyRegion(int row, int col) {
        return metalAlloyRegions[row][col];
    }

    public double calculateNewTempForRegion(int row, int col) {
        double metal1TempSummation = getMetalSummation(row, col, 1);
        double metal2TempSummation = getMetalSummation(row, col, 2);
        double metal3TempSummation = getMetalSummation(row, col, 3);
        // finding the number of  neighbors
        int numNeighbors = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i != row && j != col && !(i < 0) && !(i >= height) && !(j < 0) && !(j >= width)) {
                    numNeighbors++;
                }
            }
        }
        double temperatureOfRegion = 0;
        temperatureOfRegion += c1 * metal1TempSummation / numNeighbors;
        temperatureOfRegion += c2 * metal2TempSummation / numNeighbors;
        temperatureOfRegion += c3 * metal3TempSummation / numNeighbors;
        return temperatureOfRegion;
    }

    /**
     * Takes a metal type of either 1, 2, or 3 since a metal alloy is made up of 3 metals.
     * @return
     */
    public double getMetalSummation(int row, int col, int metalType) {
        double metalSummation = 0;
        if (metalType == 1) {
            if (row > 0) {
                metalSummation += metalAlloyRegions[row - 1][col].getPercentOfMetal1() * metalAlloyRegions[row - 1][col].getTemperature();
            }
            if (row < height - 1) {
                metalSummation += metalAlloyRegions[row + 1][col].getPercentOfMetal1() * metalAlloyRegions[row + 1][col].getTemperature();
            }
            if (col > 0) {
                metalSummation += metalAlloyRegions[row][col - 1].getPercentOfMetal1() * metalAlloyRegions[row][col - 1].getTemperature();
            }
            if (col < width - 1) {
                metalSummation += metalAlloyRegions[row][col + 1].getPercentOfMetal1() * metalAlloyRegions[row][col + 1].getTemperature();
            }
        } else if (metalType == 2) {
            if (row > 0) {
                metalSummation += metalAlloyRegions[row - 1][col].getPercentOfMetal2() * metalAlloyRegions[row - 1][col].getTemperature();
            }
            if (row < height - 1) {
                metalSummation += metalAlloyRegions[row + 1][col].getPercentOfMetal2() * metalAlloyRegions[row + 1][col].getTemperature();
            }
            if (col > 0) {
                metalSummation += metalAlloyRegions[row][col - 1].getPercentOfMetal2() * metalAlloyRegions[row][col - 1].getTemperature();
            }
            if (col < width - 1) {
                metalSummation += metalAlloyRegions[row][col + 1].getPercentOfMetal2() * metalAlloyRegions[row][col + 1].getTemperature();
            }
        } else if (metalType == 3) {
            if (row > 0) {
                metalSummation += metalAlloyRegions[row - 1][col].getPercentOfMetal3() * metalAlloyRegions[row - 1][col].getTemperature();
            }
            if (row < height - 1) {
                metalSummation += metalAlloyRegions[row + 1][col].getPercentOfMetal3() * metalAlloyRegions[row + 1][col].getTemperature();
            }
            if (col > 0) {
                metalSummation += metalAlloyRegions[row][col - 1].getPercentOfMetal3() * metalAlloyRegions[row][col - 1].getTemperature();
            }
            if (col < width - 1) {
                metalSummation += metalAlloyRegions[row][col + 1].getPercentOfMetal3() * metalAlloyRegions[row][col + 1].getTemperature();
            }
        }
        return metalSummation;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void calculateQuadrant(MetalAlloy alloyToStoreResults, Quadrant quadrantToRun) {
        ExecutorService workStealingPool = new ForkJoinPool();
        // used to specify the dimensions of the quadrant.
        int quadrantHeight;
        int quadrantWidth;
        switch (quadrantToRun) {
            case ALL:
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
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case TOP_LEFT:
                quadrantHeight = height / 3;
                quadrantWidth = width / 3;
                for (int i = 0; i < quadrantHeight; i++) {
                    for (int j = 0; j < quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case TOP:
                quadrantHeight = height / 3;
                quadrantWidth = 2 * (width / 3);
                for (int i = 0; i < quadrantHeight; i++) {
                    for (int j = width / 3; j <  quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case TOP_RIGHT:
                quadrantHeight = height / 3;
                quadrantWidth = width;
                for (int i = 0; i < quadrantHeight; i++) {
                    for (int j = 2 * (width / 3); j < quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case LEFT:
                quadrantHeight = 2 * (height / 3);
                quadrantWidth = width / 3;
                for (int i = height / 3; i < quadrantHeight; i++) {
                    for (int j = 0; j < quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case MIDDLE:
                quadrantHeight = 2 * (height / 3);
                quadrantWidth = 2 * (width / 3);
                for (int i = height / 3; i < quadrantHeight; i++) {
                    for (int j = width / 3; j < quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case RIGHT:
                quadrantHeight = 2 * (height / 3);
                quadrantWidth = width;
                for (int i = height / 3; i < quadrantHeight; i++) {
                    for (int j = 2 * (width / 3); j < quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case BOTTOM_LEFT:
                quadrantHeight = height;
                quadrantWidth = width / 3;
                for (int i = 2 * (height / 3); i < quadrantHeight; i++) {
                    for (int j = 0; j < quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case BOTTOM:
                quadrantHeight = height;
                quadrantWidth = 2 * (width / 3);
                for (int i = 2 * (height / 3); i < quadrantHeight; i++) {
                    for (int j = width / 3; j < quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;

            case BOTTOM_RIGHT:
                quadrantHeight = height;
                quadrantWidth = width;
                for (int i = 2 * (height / 3); i < quadrantHeight; i++) {
                    for (int j = 2 * (width / 3); j < quadrantWidth; j++) {
                        if (i == 0 && i == j) {
                            continue;
                        }
                        if (i == height - 1 && j == width - 1) {
                            continue;
                        }
                        final int ROW = i;
                        final int COL = j;
                        workStealingPool.submit(() -> {
                            double result = calculateNewTempForRegion(ROW, COL);
                            alloyToStoreResults.setTempOfRegion(result, ROW, COL);
                            alloyToStoreResults.getMetalAlloyRegion(ROW, COL).calcRGB();
                        });
                    }
                }
                break;
        }
        workStealingPool.shutdown();
        try {
            workStealingPool.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
