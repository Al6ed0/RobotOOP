package gui;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class BaseInternalFrame extends JInternalFrame {

    public BaseInternalFrame(String title, boolean resizable,
                             boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                confirmClose();
            }
        });
    }

    private void confirmClose() {
        int response = JOptionPane.showConfirmDialog(this,
                "Вы действительно хотите закрыть окно '" + getTitle() + "'?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            this.closeAction();
            dispose();
        }
    }

    protected void closeAction() {}

}
