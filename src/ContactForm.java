import javax.swing.*;
import java.awt.*;

public class ContactForm extends JDialog {

    private JTextField tfName = new JTextField(20);
    private JTextField tfSurnames = new JTextField(20);
    private JTextField tfPhone = new JTextField(20);
    private JTextField tfEmail = new JTextField(20);
    private boolean saved =false;

    public ContactForm(Frame owner){
        super( owner, "Nuevo Contacto", true);

        setLayout(new GridLayout(5,2));
        add(new JLabel("Name:"));
        add(tfName);
        add(new JLabel("Surname:"));
        add(tfSurnames);
        add(new JLabel("Phone:"));
        add(tfPhone);
        add(new JLabel("Email:"));
        add(tfEmail);

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        add(save);
        add(cancel);

        save.addActionListener(e -> {
            saved=true;
            setVisible(false);
        });

        cancel.addActionListener(e -> {
            saved=false;
            setVisible(false);
        });

        pack();
        setLocationRelativeTo(owner);
    }

    public boolean isSaved(){
        return saved;
    }

    public String getNameInput(){
        return tfName.getText();
    }
    public String getSurnamesInput() {
        return tfSurnames.getText();
    }

    public String getPhoneInput() {
        return tfPhone.getText();
    }

    public String getEmailInput() {
        return tfEmail.getText();
    }
}
