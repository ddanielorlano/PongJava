package pong2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class opens a window when a game is being saved, it accepts a name then
 * closses. is super.dispose a good way of doing this?
 */
public class NameSave extends JFrame {

    private JLabel label;
    private JTextField textField;
    private JPanel panel;
    public String NAME;
    private JButton ok;

    public NameSave() {
        setTitle("Name Save");
        setDefaultCloseOperation(1);
        setResizable(false);
        setSize(400, 200);
        setup();
        add(panel);
    }

    private void setup() {
        ok = new JButton("OK");
        panel = new JPanel();
        label = new JLabel("Please enter name");
        textField = new JTextField(4);

        textField.addActionListener(new ListenToIt());
        ok.addActionListener(new ListenToIt());

        panel.add(label);
        panel.add(textField);
        panel.add(ok);
    }

    private class ListenToIt implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!textField.getText().isEmpty()) {
                NAME = textField.getText();
                label.setText("Name entered   '" + NAME + "'   ");
                textField.setEditable(false);
                NameSave.super.dispose();
            }
        }
    }

    public String getNameOf() {
        return NAME;
    }
}
