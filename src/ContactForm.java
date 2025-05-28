import javax.swing.*;
import java.awt.*;

public class ContactForm extends JDialog {
    //atributos
    private JTextField tfName = new JTextField(20);
    private JTextField tfSurnames = new JTextField(20);
    private JTextField tfPhone = new JTextField(20);
    private JTextField tfEmail = new JTextField(20);
    private boolean saved =false;
    //metodo para crear la ventana con el formulario
    public ContactForm(Frame owner ){
        super( owner, "Nuevo Contacto", true);
        //definir diseño formulario
        setLayout(new GridLayout(5,2));
        //campos del formulario
        add(new JLabel("Name:"));
        add(tfName);
        add(new JLabel("Surname:"));
        add(tfSurnames);
        add(new JLabel("Phone:"));
        add(tfPhone);
        add(new JLabel("Email:"));
        add(tfEmail);
        //botones para guardar o cancelar
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        //añadir al layout
        add(save);
        add(cancel);
        //funcionalidad del boton save - que hace
        save.addActionListener(e -> {
            //si cualquier campo esta vacio, mostrar un mensaje
            if (tfName.getText().trim().isEmpty() ||
                    tfSurnames.getText().trim().isEmpty() ||
                    tfPhone.getText().trim().isEmpty() ||
                    tfEmail.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this, "Por favor, rellena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            }//si todos los campos estan llenos, cambiar el atributo "saved" a true
            else {
                saved = true;
                setVisible(false);
            }
        });
        //funcionalidad del boton cancel
        cancel.addActionListener(e -> {
            //si se presiona, "saved" sigue siendo false
            saved=false;
            setVisible(false);
        });
        //ajustar tamaño de la ventana al diseño del layout
        pack();
        //centra la ventana
        setLocationRelativeTo(owner);
    }
    //metodo para verificar si se ha guardado o no, retorna la variable saved que puede ser true o false
    public boolean isSaved(){
        return saved;
    }
    //getters para obtener el nombre, apellidos, telefono y email ingresados en el formulario
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
