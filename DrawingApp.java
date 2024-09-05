// Drawing Application 1.0 developed by Marius using Swing

// Libraries used
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.plaf.basic.BasicSliderUI;

public class DrawingApp extends JFrame {

    private int prevX, prevY; // previous mouse coordinates
    private boolean isDrawing = false;
    private Color currentColor = Color.BLACK;
    private int penSize = 5; // default pen size
    private DrawingPanel drawingPanel;

    class NoThumbSliderUI extends BasicSliderUI {

        public NoThumbSliderUI(JSlider slider) {
            super(slider);
        }
    
        @Override
        public void paintThumb(Graphics g) {
        }
    
        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);  // set track color
            int x = trackRect.x + trackRect.width / 2;
            g2d.drawLine(x, trackRect.y, x, trackRect.y + trackRect.height);

            int value = slider.getValue();  // get current value
            int y = yPositionForValue(value);  // calculate y position based on slider value
            g2d.setColor(Color.RED);  // set color for the cursor
            g2d.fillOval(x - 5, y - 5, 10, 10);  // draw a small circle to act as the cursor
        }
    
        @Override
        public void paintFocus(Graphics g) {
        }
    }

    // button styler
    public static void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.white);
        button.setBackground(new Color(123, 104, 238, 250));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(80, 30));
        button.setOpaque(true);
        button.setContentAreaFilled(true);

        // hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.GREEN);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(123, 104, 238, 250));
            }
        });
    }

    // interface
    public DrawingApp() {
        // window settings
        setTitle("Drawing Application 1.0"); // title
        setSize(800, 600); // resolution
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close the app
        setLocationRelativeTo(null); // set the location to be in the center of the screen

        // drawing panel
        drawingPanel = new DrawingPanel();
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        // use custom cursor
        ImageIcon penIcon = new ImageIcon("cursor.png");
        Image penImage = penIcon.getImage();
        Image resizedPenImage = penImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        Cursor penCursor = Toolkit.getDefaultToolkit().createCustomCursor(resizedPenImage, new Point(0, 0), "Pen Cursor");
        drawingPanel.setCursor(penCursor);

        add(drawingPanel, BorderLayout.CENTER);

        // background colors panel
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBackground(new Color(123, 104, 238, 150));

        JButton blueButton = new JButton("Blue");
        JButton greenButton = new JButton("Green");
        JButton redButton = new JButton("Red");

        styleButton(blueButton);
        styleButton(greenButton);
        styleButton(redButton);

        JLabel backgroundColorLabel = new JLabel("Background Colors", JLabel.CENTER);
        backgroundColorLabel.setForeground(Color.WHITE);
        backgroundColorLabel.setFont(new Font("Arial", Font.BOLD, 16));
        backgroundColorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  
        backgroundPanel.add(backgroundColorLabel);

        backgroundPanel.add(blueButton);
        backgroundPanel.add(greenButton);
        backgroundPanel.add(redButton);

        add(backgroundPanel, BorderLayout.NORTH);

        // Action listeners for background color buttons
        blueButton.addActionListener(e -> changeBackgroundColor(Color.BLUE));
        greenButton.addActionListener(e -> changeBackgroundColor(Color.GREEN));
        redButton.addActionListener(e -> changeBackgroundColor(Color.RED));

        // panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(123, 104, 238, 150));

        // buttons
        JButton clearButton = new JButton("Clear");
        JButton eraserButton = new JButton("Eraser");
        JButton colorButton = new JButton("Color");

        // styling the buttons
        styleButton(clearButton);
        styleButton(eraserButton);
        styleButton(colorButton);

        JLabel buttonsLabel = new JLabel("Drawing Buttons", JLabel.CENTER);
        buttonsLabel.setForeground(Color.WHITE);
        buttonsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        buttonsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  
        buttonPanel.add(buttonsLabel);

        // adding buttons to the panel
        buttonPanel.add(clearButton);
        buttonPanel.add(eraserButton);
        buttonPanel.add(colorButton);

        // panel for the slider
        JPanel sliderPanel = new JPanel();
        sliderPanel.setBackground(new Color(123, 104, 238, 150));  
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        JLabel cursorSizeLabel = new JLabel("Cursor Size", JLabel.CENTER);
        cursorSizeLabel.setForeground(Color.WHITE);
        cursorSizeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cursorSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cursorSizeLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  
        sliderPanel.add(cursorSizeLabel);

        sliderPanel.add(Box.createRigidArea(new Dimension(0, 30)));  

        JSlider penSizeSlider = new JSlider(JSlider.VERTICAL, 1, 30, penSize);
        penSizeSlider.setMajorTickSpacing(2);
        penSizeSlider.setMinorTickSpacing(2);
        penSizeSlider.setPaintTicks(true);    
        penSizeSlider.setPaintLabels(false); 
        penSizeSlider.setPreferredSize(new Dimension(50, 400));  
        penSizeSlider.setBackground(new Color(123, 104, 238, 0));  
        penSizeSlider.setOpaque(false);
        penSizeSlider.setUI(new NoThumbSliderUI(penSizeSlider));

        penSizeSlider.addChangeListener(e -> penSize = penSizeSlider.getValue());

        sliderPanel.add(penSizeSlider);
        sliderPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(sliderPanel, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        // action listener to the clear button
        clearButton.addActionListener(e -> {
            drawingPanel.setBackground(Color.WHITE);
            drawingPanel.clear();
            drawingPanel.setBackground(Color.WHITE); 
        });

        // action listener to the eraser button
        eraserButton.addActionListener(e -> {
            currentColor = drawingPanel.getBackground();
            
            ImageIcon eraserIcon = new ImageIcon("eraser.png");
            Image eraserImage = eraserIcon.getImage();
            Image resizedEraserImage = eraserImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            Cursor eraserCursor = Toolkit.getDefaultToolkit().createCustomCursor(resizedEraserImage, new Point(0, 0), "Eraser Cursor");
            drawingPanel.setCursor(eraserCursor);
        });

        // action listener to the color button
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
            if (selectedColor != null) {
                currentColor = selectedColor;
                drawingPanel.setCursor(penCursor);
            }
        });

        // window visible
        setVisible(true);
    }

    private void changeBackgroundColor(Color color) {
        drawingPanel.setBackground(color);
        drawingPanel.clear(); 
    }

    // drawing panel
    private class DrawingPanel extends JPanel {

        private Image canvasImage;
        private Graphics2D g2d;

        public DrawingPanel() {
            setBackground(Color.WHITE); // set background color to white

            // mouse listeners
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    prevX = e.getX();
                    prevY = e.getY();
                    isDrawing = true;
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isDrawing = false;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isDrawing) {
                        int currentX = e.getX();
                        int currentY = e.getY();
                        g2d.setColor(currentColor);
                        g2d.setStroke(new BasicStroke(penSize)); // set pen size
                        g2d.drawLine(prevX, prevY, currentX, currentY);
                        prevX = currentX;
                        prevY = currentY;
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (canvasImage == null) {
                canvasImage = createImage(getWidth(), getHeight());
                g2d = (Graphics2D) canvasImage.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                clear();
            }
            g.drawImage(canvasImage, 0, 0, null);
        }

        // clear the drawing area
        public void clear() {
            if (g2d != null) {
                g2d.setPaint(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setPaint(currentColor);
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Drawing App");
        SwingUtilities.invokeLater(DrawingApp::new);
    }
}
