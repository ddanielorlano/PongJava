package pong2;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ScoreWindow extends JFrame {

    private ScorePanel panel;

    private String stringScore = " ";

    public ScoreWindow() {
        panel = new ScorePanel();
        setTitle("High Scores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(200, 200);
        setResizable(false);
        setVisible(true);
        add(panel);

    }

    public void draw(String str) {
        stringScore = str;
        panel.repaint();

    }

    private class ScorePanel extends JPanel {

        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.scale(2, 2);
            StringTokenizer st = new StringTokenizer(stringScore, " ");
            int index = 20;
            while (st.hasMoreTokens()) {
                g2.drawString(" * " + st.nextToken(), 25, index);
                index += 12;
            }
        }

    }
}
