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
    private final DataBaseController ctrl;
    private final JTable table;
    private final DefaultTableModel model;

    public GUI(){
        this.ctrl = new DataBaseController();
        this.model = new DefaultTableModel();
        this.table = new JTable(model);

        JFrame frame = new JFrame("Agenda");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        setupMenu(frame);
        setupGrid(frame);
        frame.setVisible(true);
    }

    private void setupMenu(JFrame frame){
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = menuBar.add(new JMenu("File"));
        fileMenu.add(new JMenuItem("New Contact")).addActionListener(e -> createContact());

        fileMenu.add(new JMenuItem("Delete contact")).addActionListener(e -> deleteContact());

        fileMenu.add(new JMenuItem("Exit")).addActionListener(e -> System.exit(0));

        JMenu searchMenu = menuBar.add(new JMenu("Search"));
        searchMenu.add(new JMenuItem("Search all")).addActionListener(e -> searchAll());

        JMenuItem sByOption = searchMenu.add(new JMenu("Search by"));
        ((JMenuItem) sByOption.add(new JMenuItem("Search by ID"))).addActionListener(e -> searchModalPopup(frame, "id"));
        ((JMenuItem) sByOption.add(new JMenuItem("Search by name"))).addActionListener(e -> searchModalPopup(frame, "name"));
        ((JMenuItem) sByOption.add(new JMenuItem("Search by surnames"))).addActionListener(e -> searchModalPopup(frame, "surnames"));
        ((JMenuItem) sByOption.add(new JMenuItem("Search by phone"))).addActionListener(e -> searchModalPopup(frame, "phone"));//Afegeix la cerca per teléfon
        ((JMenuItem) sByOption.add(new JMenuItem("Search by email"))).addActionListener(e -> searchModalPopup(frame, "email"));//Afegeix la cerca per email
    }

    private void searchModalPopup(JFrame frame, String field){
        String value = (String)JOptionPane.showInputDialog(
                frame,
                "Select a " + field + ":",
                "Search by " + field,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                ""
        );

        searchContactByField(field, value);
    }

    private void setupGrid(JFrame frame){
        this.model.addColumn("ID");
        this.model.addColumn("Name");
        this.model.addColumn("Surnames");
        this.model.addColumn("Phone");
        this.model.addColumn("Email");

        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.setSize(frame.getSize());
        frame.add(scrollPane);

        JPopupMenu contextMenu = new JPopupMenu();
        this.table.setComponentPopupMenu(contextMenu);
        this.table.setFillsViewportHeight(true);

        contextMenu.add(new JMenuItem("Delete")).addActionListener( e -> deleteContact());

        table.addPropertyChangeListener("tableCellEditor", e -> {
            if(!table.isEditing()){
               updateContact();
            }
        });

        //Loading all data.
        searchAll();
    }

    private void createContact(){
        Frame Frame = null;
        ContactForm form = new ContactForm(Frame);
        form.setVisible(true);

        if (form.isSaved()) {
            Contact nuevo = ctrl.nouContacte(
                    form.getNameInput(),
                    form.getSurnamesInput(),
                    form.getPhoneInput(),
                    form.getEmailInput()
            );
            addRow(nuevo);




        }
    }



    private void updateContact(){
        int r = table.getSelectedRow();
        TableModel t = table.getModel();

        int id = (int) t.getValueAt(r, 0);
        String name = (String) t.getValueAt(r, 1);
        String surnames = (String) t.getValueAt(r, 2);
        String telefon = (String) t.getValueAt(r, 3);
        String email = (String) t.getValueAt(r, 4);
        if(name.isEmpty() || surnames.isEmpty() || telefon.isEmpty() || email.isEmpty()) {
            showMessageDialog(null, "Ningún campo puede quedar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            searchAll();
            return;
        }

        this.ctrl.actualitzarContacte(id, name, surnames, telefon, email);
    }

    private void deleteContact(){
        int[] sr = this.table.getSelectedRows();
        if(sr.length == 0) showMessageDialog(null, "No row selected.");
        else{
            for(int i : sr){
                int id = (int) this.table.getModel().getValueAt(i, 0);
                this.ctrl.esborrarContacte(id);
            }
            removeRows(sr);
        }
    }

    private void searchAll(){
        this.model.setRowCount(0);

        List<Contact> cs = this.ctrl.getContactes();
        cs.forEach(this::addRow);
    }

    private void searchContactByField(String field, String value){
        this.model.setRowCount(0);

        if(field.equals("id")) addRow(this.ctrl.cercarContactePerID(Integer.parseInt(value)));
        else {
            List<Contact> cs = switch (field) {
                case "name" -> this.ctrl.cercarContactesPerNom(value);
                case "surnames" -> this.ctrl.cercarContactesPerCognoms(value);
                case "phone" -> this.ctrl.cercarContactesPerTelefon(value);
                case "email" -> this.ctrl.cercarContactesPerEmail(value);
                default -> null;
            };


            if (cs != null) cs.forEach(this::addRow);
        }
    }

    private void addRow(Contact c){
        this.model.addRow(new Object[]{c.getID(), c.getName(), c.getSurnames(), c.getPhone(), c.getEmail()});
    }

    private void removeRows(int[] rows){
        Arrays.sort(rows);

        for (int i = 0; i < rows.length; i++) {
            this.model.removeRow(rows[i]);
            for (int j = 0; j < rows.length; j++) {
                rows[j]--;
            }
        }
    }
}
