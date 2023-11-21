package edu.oswego.cs;

public class MetalAlloy {

    private int height;
    private int width;

    private MetalAlloyRegion[][] metalAlloyRegions;

    public MetalAlloy(int height) {
        this.height = height;
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
}
