package dci.j24e01;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JFrame {
    private final List<ColorEntry> colorEntries = new ArrayList<>();
    private final JPanel colorsPanel;
    private final JRadioButton rgbRadio;
    private final JRadioButton rgbaRadio;
    private final JLabel resultLabel;
    private final JPanel resultPreview;
    private final JButton calculateButton;
    private final Random random = new Random();

    public Main() {
        setTitle("Color Average Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(720, 500);
        setMinimumSize(new Dimension(550, 400));

        // Top panel with GridBagLayout
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        ButtonGroup typeGroup = new ButtonGroup();
        rgbRadio = new JRadioButton("RGB", true);
        rgbaRadio = new JRadioButton("RGBA");
        typeGroup.add(rgbRadio);
        typeGroup.add(rgbaRadio);

        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton randomButton = new JButton("Random");
        JButton colorPickerButton = new JButton("Pick Color");
        calculateButton = new JButton("Calculate Average");

        // Row 1: Radio buttons
        gbc.gridy = 0;
        gbc.gridx = 0;
        topPanel.add(new JLabel("Output Format:"), gbc);

        gbc.gridx++;
        topPanel.add(rgbRadio, gbc);

        gbc.gridx++;
        topPanel.add(rgbaRadio, gbc);

        // Row 2: Action buttons
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(randomButton);
        buttonPanel.add(colorPickerButton);
        buttonPanel.add(calculateButton);
        topPanel.add(buttonPanel, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Color entries area
        colorsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        JScrollPane scrollPane = new JScrollPane(colorsPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Result area
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        resultLabel = new JLabel("Average Color: ");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        resultPreview = new JPanel();
        resultPreview.setPreferredSize(new Dimension(150, 60));
        resultPreview.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        resultPanel.add(resultLabel);
        resultPanel.add(resultPreview);
        add(resultPanel, BorderLayout.SOUTH);

        // Event handlers
        addButton.addActionListener(this::addColorField);
        removeButton.addActionListener(this::removeLastField);
        randomButton.addActionListener(this::addRandomColor);
        colorPickerButton.addActionListener(this::pickColor);
        calculateButton.addActionListener(e -> calculateAverage());

        addColorField(null);
    }

    private void addColorField(ActionEvent e) {
        ColorEntry entry = new ColorEntry();
        colorEntries.add(entry);

        JPanel entryPanel = new JPanel(new BorderLayout(5, 0));
        entryPanel.add(entry.colorField, BorderLayout.CENTER);
        entryPanel.add(entry.preview, BorderLayout.EAST);
        entryPanel.setMaximumSize(new Dimension(250, 35));

        colorsPanel.add(entryPanel);
        colorsPanel.revalidate();
        colorsPanel.repaint();
    }

    private void removeLastField(ActionEvent e) {
        if (!colorEntries.isEmpty()) {
            int lastIndex = colorEntries.size() - 1;
            colorsPanel.remove(lastIndex);
            colorEntries.remove(lastIndex);
            colorsPanel.revalidate();
            colorsPanel.repaint();
        }
    }

    private void pickColor(ActionEvent e) {
        Color selectedColor = JColorChooser.showDialog(this, "Choose Color", Color.WHITE);
        if (selectedColor != null) {
            String colorString;
            if (rgbaRadio.isSelected()) {
                colorString = String.format("#%02X%02X%02X%02X",
                        selectedColor.getRed(),
                        selectedColor.getGreen(),
                        selectedColor.getBlue(),
                        selectedColor.getAlpha());
            } else {
                colorString = String.format("#%02X%02X%02X",
                        selectedColor.getRed(),
                        selectedColor.getGreen(),
                        selectedColor.getBlue());
            }

            ColorEntry entry = new ColorEntry();
            entry.colorField.setText(colorString);
            colorEntries.add(entry);

            JPanel entryPanel = new JPanel(new BorderLayout(5, 0));
            entryPanel.add(entry.colorField, BorderLayout.CENTER);
            entryPanel.add(entry.preview, BorderLayout.EAST);
            entryPanel.setMaximumSize(new Dimension(250, 35));

            colorsPanel.add(entryPanel);
            colorsPanel.revalidate();
            colorsPanel.repaint();
        }
    }

    private void addRandomColor(ActionEvent e) {
        Color color = new Color(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
        );

        String colorString;
        if (rgbaRadio.isSelected()) {
            colorString = String.format("#%02X%02X%02X%02X",
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue(),
                    color.getAlpha());
        } else {
            colorString = String.format("#%02X%02X%02X",
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue());
        }

        ColorEntry entry = new ColorEntry();
        entry.colorField.setText(colorString);
        colorEntries.add(entry);

        JPanel entryPanel = new JPanel(new BorderLayout(5, 0));
        entryPanel.add(entry.colorField, BorderLayout.CENTER);
        entryPanel.add(entry.preview, BorderLayout.EAST);
        entryPanel.setMaximumSize(new Dimension(250, 35));

        colorsPanel.add(entryPanel);
        colorsPanel.revalidate();
        colorsPanel.repaint();
    }

    private void calculateAverage() {
        List<int[]> rgbaColors = new ArrayList<>();

        for (ColorEntry entry : colorEntries) {
            try {
                String input = entry.colorField.getText().trim();
                if (input.isEmpty()) continue;

                int[] rgba = parseColor(input);
                rgbaColors.add(rgba);
                entry.colorField.setBackground(Color.WHITE);
            } catch (IllegalArgumentException ex) {
                entry.colorField.setBackground(new Color(255, 200, 200));
                JOptionPane.showMessageDialog(this,
                        "Invalid color format: " + entry.colorField.getText(),
                        "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (rgbaColors.isEmpty()) {
            resultLabel.setText("Average Color: No valid colors!");
            resultPreview.setBackground(null);
            return;
        }

        int[] avgRgba = averageRgbaChannels(rgbaColors);
        String hexResult;
        Color previewColor;
        if (rgbRadio.isSelected()) {
            hexResult = String.format("#%02X%02X%02X", avgRgba[0], avgRgba[1], avgRgba[2]);
            previewColor = new Color(avgRgba[0], avgRgba[1], avgRgba[2]);
        } else {
            hexResult = String.format("#%02X%02X%02X%02X", avgRgba[0], avgRgba[1], avgRgba[2], avgRgba[3]);
            previewColor = new Color(avgRgba[0], avgRgba[1], avgRgba[2], avgRgba[3]);
        }
        resultLabel.setText("Average Color: " + hexResult);
        resultPreview.setBackground(previewColor);
        resultPreview.repaint();
    }

    private int[] parseColor(String input) {
        input = input.trim();
        if (input.startsWith("#")) {
            return parseHex(input);
        } else if (input.matches("^[0-9A-Fa-f]{6,8}$")) {
            return parseHex("#" + input);
        } else {
            return parseRgb(input);
        }
    }

    private int[] parseHex(String input) {
        String hex = input.startsWith("#") ? input.substring(1) : input;
        hex = hex.toUpperCase();

        if (hex.length() != 6 && hex.length() != 8) {
            throw new IllegalArgumentException("Invalid hex length");
        }
        if (!hex.matches("[0-9A-F]+")) {
            throw new IllegalArgumentException("Invalid hex characters");
        }

        if (hex.length() == 6) {
            hex += "FF";
        }

        return hexToRgba(hex);
    }

    private int[] parseRgb(String input) {
        input = input.replaceAll("[()]", "").trim();
        String[] parts = input.split(",");
        if (parts.length < 3 || parts.length > 4) {
            throw new IllegalArgumentException("Invalid component count");
        }

        int[] rgba = new int[4];
        for (int i = 0; i < 3; i++) {
            String part = parts[i].trim();
            int value = Integer.parseInt(part);
            if (value < 0 || value > 255) {
                throw new IllegalArgumentException("Value out of range: " + value);
            }
            rgba[i] = value;
        }

        if (parts.length == 4) {
            String alphaPart = parts[3].trim();
            try {
                float alpha = Float.parseFloat(alphaPart);
                if (alpha < 0 || alpha > 1) {
                    throw new IllegalArgumentException("Alpha out of range: " + alpha);
                }
                rgba[3] = Math.round(alpha * 255);
            } catch (NumberFormatException e) {
                int alpha = Integer.parseInt(alphaPart);
                if (alpha < 0 || alpha > 255) {
                    throw new IllegalArgumentException("Alpha out of range: " + alpha);
                }
                rgba[3] = alpha;
            }
        } else {
            rgba[3] = 255;
        }

        return rgba;
    }

    private int[] hexToRgba(String hex) {
        int[] rgba = new int[4];
        for (int i = 0; i < 4; i++) {
            String byteStr = hex.substring(i * 2, i * 2 + 2);
            rgba[i] = Integer.parseInt(byteStr, 16);
        }
        return rgba;
    }

    private int[] averageRgbaChannels(List<int[]> rgbaColors) {
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

    private class ColorEntry {
        JTextField colorField = new JTextField(15);
        JPanel preview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth()-1, getHeight()-1);
            }
        };

        ColorEntry() {
            preview.setPreferredSize(new Dimension(50, 30));
            colorField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }

                private void update() {
                    try {
                        String input = colorField.getText().trim();
                        if (input.isEmpty()) {
                            preview.setBackground(Color.WHITE);
                            return;
                        }

                        int[] rgba = parseColor(input);
                        preview.setBackground(new Color(rgba[0], rgba[1], rgba[2], rgba[3]));
                    } catch (Exception ex) {
                        preview.setBackground(Color.WHITE);
                    }
                    preview.repaint();
                }
            });
        }
    }

    public static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - insets.left - insets.right;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                for (Component comp : target.getComponents()) {
                    if (comp.isVisible()) {
                        Dimension d = preferred ? comp.getPreferredSize() : comp.getMinimumSize();
                        if (rowWidth + d.width + hgap > maxWidth) {
                            dim.width = Math.max(dim.width, rowWidth);
                            dim.height += rowHeight + vgap;
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        if (rowWidth != 0) rowWidth += hgap;
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                dim.width = Math.max(dim.width, rowWidth);
                dim.height += rowHeight;
                dim.width += insets.left + insets.right;
                dim.height += insets.top + insets.bottom;
                return dim;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}