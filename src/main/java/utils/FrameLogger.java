package utils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by Thales on 14/09/2017.
 */
public class FrameLogger {
    private JTextArea label;
    private JScrollPane sp;

    public FrameLogger() {
        this.label = new JTextArea();
        this.label.setSize(400, 200);
        this.label.setLineWrap(true);
        this.label.setFont(new Font( "SansSerif", Font.PLAIN, 12 ));

        this.sp = new JScrollPane(label, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    public void println(String log) {
        label.setVisible(false);
        String oldData = label.getText();
        label.setText(oldData + LocalDateTime.now() + " : " + log + System.lineSeparator());
        label.setVisible(true);
    }

    public JTextArea getLabel() {
        return label;
    }

    public void setLabel(JTextArea label) {
        this.label = label;
    }

    public JScrollPane getSp() {
        return sp;
    }

    public void setSp(JScrollPane sp) {
        this.sp = sp;
    }
}
