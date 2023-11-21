package edu.oswego.cs;

public class MetalAlloy {

    private int height;
    private int width;

    // Thermal Constants
    private double c1;
    private double c2;
    private double c3;

    private MetalAlloyRegion[][] metalAlloyRegions;

    public MetalAlloy(int height, double c1, double c2, double c3) {
        this.height = height;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
        // The rectangular alloy must be 4 times as wide as it is high
        this.width = height * 4;
        metalAlloyRegions = new MetalAlloyRegion[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // Initialize all regions of the alloy to have a temperature of 0 degrees Celsius.
                metalAlloyRegions[i][j] = new MetalAlloyRegion(0);
            }
        }
    }

    public void increaseTempOfRegion(int tempIncrease, int row, int col) {
        metalAlloyRegions[row][col].increaseTemperature(tempIncrease);
    }
}
