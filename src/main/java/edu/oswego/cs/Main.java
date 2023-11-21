package edu.oswego.cs;

public class Main {

    private static final double DEFAULT_S = 30.0;
    private static final double DEFAULT_T = 70.0;
    private static final double DEFAULT_C1 = 0.75;
    private static final double DEFAULT_C2 = 1.0;
    private static final double DEFAULT_C3 = 1.25;
    private static final int DEFAULT_HEIGHT = 1000;
    private static final int DEFAULT_WIDTH = 4000;

    public static void main(String[] args) {
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
    }
}