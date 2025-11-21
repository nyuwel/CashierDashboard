import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.LayoutManager; // <-- THIS IS THE MISSING IMPORT
import java.awt.FlowLayout;

class RoundedPanel extends JPanel {
    private int cornerRadius = 15; // Set your desired radius here

    public RoundedPanel(LayoutManager layout, int radius) {
        super(layout);
        this.cornerRadius = radius;
        setOpaque(false); // We need to set it transparent so we can draw the background
        
        // Add default padding/border to the content inside
        setBorder(new EmptyBorder(10, 10, 10, 10));
    }
    
    // Constructor without specific layout
    public RoundedPanel(int radius) {
        this(new FlowLayout(), radius);
    }

    // Constructor without specified radius (uses default)
    public RoundedPanel(LayoutManager layout) {
        this(layout, 15);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Anti-aliasing for smooth edges
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Get the current background color (set via setBackground())
        Color color = getBackground();
        graphics.setColor(color);
        
        // Draw the rounded rectangle using the panel's dimensions and the cornerRadius
        graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        // Draw a light border for definition (optional)
        graphics.setColor(new Color(220, 220, 220)); 
        graphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
    }
    
    // Override setBorder to apply it to the content, not the panel itself
    @Override
    public void setBorder(Border border) {
        super.setBorder(border);
    }
}