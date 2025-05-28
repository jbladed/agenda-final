// Sources:
//  https://www.geeksforgeeks.org/introduction-to-java-swing/
//  https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
//  https://docs.oracle.com/javase/tutorial/uiswing/components/table.html

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import static javax.swing.JOptionPane.showMessageDialog;

public class GUI {
    //atributos de tipo ctrl, table y model
    private final DataBaseController ctrl;
    private final JTable table;
    private final DefaultTableModel model;
    //constructor
    public GUI(){
        this.ctrl = new DataBaseController();
        this.model = new DefaultTableModel();
        this.table = new JTable(model);
        //definir diseño de la interfaz gráfica
        JFrame frame = new JFrame("Agenda");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);
        //mostrar en la interfaz el metodo setupMenu
        setupMenu(frame);
        //mostrar en la interfaz el metodo setupGrid
        setupGrid(frame);
        frame.setVisible(true);
    }
    //metodo para definir un menu en la parte superior de la interfaz
    private void setupMenu(JFrame frame){
        //crear el menu
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        //definir nombre del desplegable
        JMenu fileMenu = menuBar.add(new JMenu("File"));
        //definir contenido del desplegable y su accion, se llama al metodo createContact
        fileMenu.add(new JMenuItem("New Contact")).addActionListener(e -> createContact());

       // fileMenu.add(new JMenuItem("Delete contact")).addActionListener(e -> deleteContact());

        fileMenu.add(new JMenuItem("Exit")).addActionListener(e -> System.exit(0));
        //definir nombre de otro desplegable
        JMenu searchMenu = menuBar.add(new JMenu("Search"));
        //definir contenido del desplegable
        searchMenu.add(new JMenuItem("Search all")).addActionListener(e -> searchAll());
        //definir otro desplegable dentro del desplegable "search"
        JMenuItem sByOption = searchMenu.add(new JMenu("Search by"));
        //contenido del desplegable "serach by"
        ((JMenuItem) sByOption.add(new JMenuItem("Search by ID"))).addActionListener(e -> searchModalPopup(frame, "id"));
        ((JMenuItem) sByOption.add(new JMenuItem("Search by name"))).addActionListener(e -> searchModalPopup(frame, "name"));
        ((JMenuItem) sByOption.add(new JMenuItem("Search by surnames"))).addActionListener(e -> searchModalPopup(frame, "surnames"));
        ((JMenuItem) sByOption.add(new JMenuItem("Search by phone"))).addActionListener(e -> searchModalPopup(frame, "phone"));//Afegeix la cerca per teléfon
        ((JMenuItem) sByOption.add(new JMenuItem("Search by email"))).addActionListener(e -> searchModalPopup(frame, "email"));//Afegeix la cerca per email
    }
    //metodo para crear la ventana para buscar por id/nombre/telefono/email, depende lo que se elija
    private void searchModalPopup(JFrame frame, String field){
        String value = (String)JOptionPane.showInputDialog(
                frame,
                "Select a " + field + ":", //texto que se muestra como titulo de ventana
                "Search by " + field, //texto que se muestra como titulo de campo
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                ""
        );

        searchContactByField(field, value); //field y value se sustituye por tipo de dato(field) y valor(value)
    }

    private void setupGrid(JFrame frame){
        // define el diseño de la tabla el nombre de las columnas
        this.model.addColumn("ID");
        this.model.addColumn("Name");
        this.model.addColumn("Surnames");
        this.model.addColumn("Phone");
        this.model.addColumn("Email");
        //crea un contenedor de desplazamiento por si hay muchos contactos
        JScrollPane scrollPane = new JScrollPane(this.table);
        //ajusta el tamaño de scrollPane
        scrollPane.setSize(frame.getSize());
        //añade el scrollPane al frame(ventana)
        frame.add(scrollPane);
        //Muestra un menu emergente al hacer clic derecho en un componente
        JPopupMenu contextMenu = new JPopupMenu();
        //Asocia el menu emergente anterior a la tabla
        this.table.setComponentPopupMenu(contextMenu);
        //hace que la tabla rellene todo el espacio visible dentro del scrollpane
        this.table.setFillsViewportHeight(true);
        //define el contenido del menu emergente y su accion, se llama al metodo deleteContact
        contextMenu.add(new JMenuItem("Delete")).addActionListener( e -> deleteContact());
        //implementa la edicion de edicion de las celdas, llamando al metodo updateContact
        table.addPropertyChangeListener("tableCellEditor", e -> {
            if(!table.isEditing()){
               updateContact();
            }
        });

        //Loading all data.
        searchAll();
    }

    private void createContact(){
        Frame frame = null;
        ContactForm form = new ContactForm(frame);
        //bucle while que mientras sea true...
        while (true) {
            //...formulario visible
            form.setVisible(true);
            //comprueba el estado de guardado, si "isSaved"=false se sale del bucle y no se ejecuta el formulario de creacion de contacto
            if (!form.isSaved()) {
                // usuario canceló, salimos
                break;
            }
            //formulario de contacto
            Contact nuevo = ctrl.nouContacte(
                    form.getNameInput(),
                    form.getSurnamesInput(),
                    form.getPhoneInput(),
                    form.getEmailInput()
            );

            if (nuevo != null) { //si nuevo(osea, el contacto que se ha creado no es nulo=hay contenido) se entra en el bucle

                addRow(nuevo); //se añade a la fila el nuevo contacto
                break; // contacto creado correctamente, salimos del bucle
            } else {
                //si el contacto existe, se muestra un mensaje por pantalla
                JOptionPane.showMessageDialog(null,
                        "El contacto ya existe con ese teléfono o email. Por favor, modifica los datos.",
                        "Error: contacto duplicado",
                        JOptionPane.WARNING_MESSAGE);
                // no hacemos break para que el formulario vuelva a abrirse


            }
        }
    }



    private void updateContact(){
        //devuelve el numero de la fila seleccionada
        int r = table.getSelectedRow();
        TableModel t = table.getModel();

        int id = (int) t.getValueAt(r, 0);
        //devuelve los valores mencionados, especificando numero de columna
        String name = (String) t.getValueAt(r, 1);
        String surnames = (String) t.getValueAt(r, 2);
        String telefon = (String) t.getValueAt(r, 3);
        String email = (String) t.getValueAt(r, 4);
        //si algun campo queda vacio salta un mensaje de error con el siguiente texto
        if(name.isEmpty() || surnames.isEmpty() || telefon.isEmpty() || email.isEmpty()) {
            showMessageDialog(null, "Ningún campo puede quedar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            searchAll();
            return;
        }
        //llama al metodo actualizarContacto de dataBaseController
        this.ctrl.actualitzarContacte(id, name, surnames, telefon, email);
    }

    private void deleteContact(){
        //guarda las filas seleccionadas
        int[] sr = this.table.getSelectedRows();
        //si no detecta ninguna fila seleccionada salta un mensaje de error
        if(sr.length == 0) showMessageDialog(null, "No row selected.");
        else{//bucle que recoge todos los valores del array sr que son los diferentes campos
            for(int i : sr){
                //coge el valor de la primera columna que es el ID, necesario para saber que contacto borrar
                int id = (int) this.table.getModel().getValueAt(i, 0);
                //llamada del metodo esborrarContacte de la clase dataBaseController
                this.ctrl.esborrarContacte(id);
            }
            //despues de borrar el contacto de la base de datos, se borra visualmente de la interfaz
            removeRows(sr);
        }
    }

    private void searchAll(){
        //establece el numero de filas en la interfaz, si detecta que el numero es mayor, agrega estas nuevas filas
        this.model.setRowCount(0);
        //ordena los contactos que obtiene del dataBaseController
        List<Contact> cs = this.ctrl.getContactes();
        //recorre la lista de contactos anteriormente creada y por cada uno de ellos ejecuta el metodo addRow(contact c) y añade el contacto como una fila en la tabla
        cs.forEach(this::addRow);
    }

    private void searchContactByField(String field, String value){
        this.model.setRowCount(0);
        //si el campo a buscar es id, convierte el valor a entero y busca un unico contacto por id y si se encuentra se muestra esa sola fila por pantalla
        if(field.equals("id")) addRow(this.ctrl.cercarContactePerID(Integer.parseInt(value)));
        else {//si el campo es otro(nombre, apellido,etc) se usa switch para decidir con los cases que metodo llamar y cada uno se esos metodos devuelve una lista con todos aquellos que ha encontrado
            List<Contact> cs = switch (field) {
                case "name" -> this.ctrl.cercarContactesPerNom(value);
                case "surnames" -> this.ctrl.cercarContactesPerCognoms(value);
                case "phone" -> this.ctrl.cercarContactesPerTelefon(value);
                case "email" -> this.ctrl.cercarContactesPerEmail(value);
                default -> null;
            };

            //si la lista no es nula, añade cada contacto como una fila a la tabla
            if (cs != null) cs.forEach(this::addRow);
        }
    }
    //el metodo que añade las filas
    private void addRow(Contact c){// toma contacto como parametro y extrae sus atributos, los mete en un array y agrega esta fila a la tabla

        this.model.addRow(new Object[]{c.getID(), c.getName(), c.getSurnames(), c.getPhone(), c.getEmail()});
    }
    //el metodo que elimina las filas
    private void removeRows(int[] rows){//toma como parametro a un array llamado rows
        Arrays.sort(rows); //lo ordena
        //bucle que va recorriendo cada valor del array rows y los va eliminando
        for (int i = 0; i < rows.length; i++) {
            this.model.removeRow(rows[i]);
            //bucle qeu cambia el valor de las filas. esto quiere decir que cuando se elimina una fila, la de debajo a esta sube una posicion y su valor se disminye en 1
            for (int j = 0; j < rows.length; j++) {
                rows[j]--;
            }
        }
    }
}
