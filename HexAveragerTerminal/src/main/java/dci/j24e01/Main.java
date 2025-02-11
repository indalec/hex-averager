package dci.j24e01;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Color type selection
        System.out.println("Choose color type:");
        System.out.println("1. RGB (6 characters, e.g. #FF0000 or 00FF00)");
        System.out.println("2. RGBA (8 characters, e.g. #FF0000FF or 00FF0080)");
        System.out.print("Enter your choice (1 or 2): ");

        int colorType = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (colorType != 1 && colorType != 2) {
            System.err.println("Invalid choice. Please run the program again and select 1 or 2.");
            System.exit(1);
        }

        // Input prompt with format example
        String formatPrompt = colorType == 1 ?
                "Enter RGB hex colors (6 characters, separated by spaces):\nExample: FF0000 00FF00 0000FF" :
                "Enter RGBA hex colors (8 characters, separated by spaces):\nExample: FF0000FF 00FF0080 0000FFFF";

        System.out.println(formatPrompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.err.println("Error: No colors were entered.");
            System.exit(1);
        }

        String[] hexTokens = input.split("\\s+");
        List<int[]> rgbaColors = new ArrayList<>();

        for (String token : hexTokens) {
            try {
                String hex = token.startsWith("#") ? token.substring(1) : token;
                hex = hex.toUpperCase();

                // Validate based on selected color type
                if (colorType == 1) { // RGB
                    if (hex.length() != 6 || !hex.matches("[0-9A-F]+")) {
                        throw new IllegalArgumentException();
                    }
                    hex += "FF"; // Add full opacity alpha channel
                } else { // RGBA
                    if (hex.length() != 8 || !hex.matches("[0-9A-F]+")) {
                        throw new IllegalArgumentException();
                    }
                }

                rgbaColors.add(hexToRgba(hex));
            } catch (IllegalArgumentException e) {
                String expectedFormat = colorType == 1 ?
                        "6-character RGB (e.g. FF0000 or #00FF00)" :
                        "8-character RGBA (e.g. FF0000FF or #00FF0080)";
                System.err.println("Invalid color: " + token + " (expected " + expectedFormat + ")");
                System.exit(1);
            }
        }

        int[] avgRgba = averageRgbaChannels(rgbaColors);
        System.out.println("Average color: " + rgbaToHex(avgRgba));
    }

    private static int[] hexToRgba(String hex) {
        int[] rgba = new int[4];
        for (int i = 0; i < 4; i++) {
            String byteStr = hex.substring(i * 2, i * 2 + 2);
            rgba[i] = Integer.parseInt(byteStr, 16);
        }
        return rgba;
    }

    private static int[] averageRgbaChannels(List<int[]> rgbaColors) {
        int[] avg = new int[4];
        int count = rgbaColors.size();

        for (int[] color : rgbaColors) {
            for (int i = 0; i < 4; i++) {
                avg[i] += color[i];
            }
        }

        for (int i = 0; i < 4; i++) {
            avg[i] = Math.round((float) avg[i] / count);
        }

        return avg;
    }

    private static String rgbaToHex(int[] rgba) {
        return String.format("#%02X%02X%02X%02X", rgba[0], rgba[1], rgba[2], rgba[3]);
    }
}