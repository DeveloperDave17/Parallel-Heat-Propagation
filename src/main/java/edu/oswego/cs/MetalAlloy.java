package edu.oswego.cs;

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
}
