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
        setSize(800, 500);
        setMinimumSize(new Dimension(600, 400));

        // Top controls with wrapping layout
        JPanel topPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
        ButtonGroup typeGroup = new ButtonGroup();
        rgbRadio = new JRadioButton("RGB (6 chars)", true);
        rgbaRadio = new JRadioButton("RGBA (8 chars)");
        typeGroup.add(rgbRadio);
        typeGroup.add(rgbaRadio);

        JButton addButton = new JButton("Add");
        JButton removeButton = new JButton("Remove");
        JButton randomButton = new JButton("Random");
        calculateButton = new JButton("Calculate");

        // Set consistent button sizes
        Dimension btnSize = new Dimension(80, 25);
        addButton.setPreferredSize(btnSize);
        removeButton.setPreferredSize(btnSize);
        randomButton.setPreferredSize(btnSize);
        calculateButton.setPreferredSize(new Dimension(100, 25));

        topPanel.add(new JLabel("Type:"));
        topPanel.add(rgbRadio);
        topPanel.add(rgbaRadio);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(addButton);
        topPanel.add(removeButton);
        topPanel.add(randomButton);
        topPanel.add(calculateButton);
        add(topPanel, BorderLayout.NORTH);

        // Color entries area
        colorsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 10));
        JScrollPane scrollPane = new JScrollPane(colorsPanel);
        scrollPane.setPreferredSize(new Dimension(800, 350));
        add(scrollPane, BorderLayout.CENTER);

        // Result area
        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        resultLabel = new JLabel("Average Color: ");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        resultPreview = new JPanel();
        resultPreview.setPreferredSize(new Dimension(120, 50));
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
        calculateButton.addActionListener(e -> calculateAverage());

        addColorField(null);
    }

    private void addColorField(ActionEvent e) {
        ColorEntry entry = new ColorEntry();
        colorEntries.add(entry);

        JPanel entryPanel = new JPanel(new BorderLayout(5, 0));
        entryPanel.add(entry.colorField, BorderLayout.CENTER);
        entryPanel.add(entry.preview, BorderLayout.EAST);
        entryPanel.setMaximumSize(new Dimension(200, 30));

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

    private void addRandomColor(ActionEvent e) {
        boolean isRGB = rgbRadio.isSelected();
        String hex = generateRandomHex(isRGB ? 6 : 8);
        ColorEntry entry = new ColorEntry();
        entry.colorField.setText(hex);
        colorEntries.add(entry);

        JPanel entryPanel = new JPanel(new BorderLayout(5, 0));
        entryPanel.add(entry.colorField, BorderLayout.CENTER);
        entryPanel.add(entry.preview, BorderLayout.EAST);
        entryPanel.setMaximumSize(new Dimension(200, 30));

        colorsPanel.add(entryPanel);
        colorsPanel.revalidate();
        colorsPanel.repaint();
    }

    private String generateRandomHex(int length) {
        StringBuilder sb = new StringBuilder("#");
        String chars = "0123456789ABCDEF";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void calculateAverage() {
        List<int[]> rgbaColors = new ArrayList<>();

        for (ColorEntry entry : colorEntries) {
            try {
                String hex = entry.colorField.getText().trim();
                if (hex.isEmpty()) continue; // Skip empty fields

                hex = hex.startsWith("#") ? hex.substring(1) : hex;
                hex = hex.toUpperCase();

                boolean isRGB = rgbRadio.isSelected();
                if (isRGB) {
                    if (hex.length() != 6 || !hex.matches("[0-9A-F]+")) {
                        throw new IllegalArgumentException();
                    }
                    hex += "FF";
                } else {
                    if (hex.length() != 8 || !hex.matches("[0-9A-F]+")) {
                        throw new IllegalArgumentException();
                    }
                }

                rgbaColors.add(hexToRgba(hex));
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
        String hexResult = rgbaToHex(avgRgba);
        resultLabel.setText("Average Color: " + hexResult);
        resultPreview.setBackground(new Color(
                avgRgba[0], avgRgba[1], avgRgba[2], avgRgba[3]));
        resultPreview.repaint();
    }

    private int[] hexToRgba(String hex) {
        int[] rgba = new int[4];
        try {
            for (int i = 0; i < 4; i++) {
                String byteStr = hex.substring(i * 2, i * 2 + 2);
                rgba[i] = Integer.parseInt(byteStr, 16);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid hex string");
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

    private String rgbaToHex(int[] rgba) {
        for (int i = 0; i < 4; i++) {
            rgba[i] = Math.min(Math.max(rgba[i], 0), 255);
        }
        return String.format("#%02X%02X%02X%02X",
                rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    private class ColorEntry {
        JTextField colorField = new JTextField(12);
        JPanel preview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth()-1, getHeight()-1);
            }
        };

        ColorEntry() {
            preview.setPreferredSize(new Dimension(40, 25));
            colorField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }

                private void update() {
                    try {
                        String hex = colorField.getText().trim();
                        if (hex.startsWith("#")) hex = hex.substring(1);
                        if (hex.isEmpty()) {
                            preview.setBackground(Color.WHITE);
                            return;
                        }

                        boolean isRGB = rgbRadio.isSelected();
                        if (isRGB && hex.length() != 6) return;
                        if (!isRGB && hex.length() != 8) return;

                        int[] rgba = hexToRgba(hex + (isRGB ? "FF" : ""));
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