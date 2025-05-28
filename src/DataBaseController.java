import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.swing.*;
import java.util.List;
import java.util.logging.Level;

// Sources:
//      https://docs.jboss.org/hibernate/orm/6.6/quickstart/html_single/
//      https://www.baeldung.com/hibernate-criteria-queries
//      https://hibernate.org/orm/quickly/

public class DataBaseController implements AutoCloseable/*interfaz para que no se quede abierta la app*/  {
    //atributos
    private final SessionFactory factory;
    private final Session session;
    private final CriteriaBuilder criteriaBuilder;

    public DataBaseController(){
        //codigo para implementar hibernate
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        this.factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        this.session = this.factory.openSession();
        this.criteriaBuilder = this.factory.getCriteriaBuilder();
    }

    public void close() throws Exception {
        //metodo para cerrar el codigo y liberar recursos
        this.session.close();
        this.factory.close();
    }

    public Contact nouContacte(String name, String surnames, String phone, String email) {
        //listas para recorrer en la base de datos si existe o no un telefono o email igual
        List<Contact> existentelefono = this.cercarContactesPerTelefon(phone);
        List<Contact> existeemail = this.cercarContactesPerEmail(email);
        //si no existe, realiza las operaciones de crear contacto, guardarlo en la base de datos y mostrarlo por pantalla
        if (existentelefono.isEmpty()&&existeemail.isEmpty()) {
            Contact c = new Contact(name, surnames, phone, email);
            Transaction transaction = this.session.beginTransaction();
            this.session.persist(c);
            transaction.commit();
            return c;
        }else{ //si no, devuelve null

            return null;
        }


    }

    public Contact actualitzarContacte(int ID, String name, String surnames, String phone, String email) {
       //obtiene el parametro id de la clase contacto
        Contact c = this.session.get(Contact.class, ID);
        //si no es nulo(si hay informacion)
       if (c != null) {
           //pone el nuevo nombre, apellidos, etc
           c.setName(name);
           c.setSurnames(surnames);
           c.setPhone(phone);
           c.setEmail(email);
       }
       //hace la transaccion con la base de datos y lo muestra por pantalla
       Transaction transaction = this.session.beginTransaction();
       this.session.persist(c);
       transaction.commit();
       return c;
    }

    public void esborrarContacte(int ID){
        Contact c = session.get(Contact.class, ID);

        if(c != null){
            Transaction transaction= session.beginTransaction();
            //elimina
            session.remove(c);
            transaction.commit();
        }
    }

    public Contact cercarContactePerID(int ID){
        //obtiene el id de contacto
        return this.session.get(Contact.class, ID);
    }
    //obtiene todos los contantos que coincidan con las diferentes busquedas
    public List<Contact> cercarContactesPerNom(String name){

       return cercarContactesPerCamp("name", name);
    }

    public List<Contact> cercarContactesPerCognoms(String surnames){
       return cercarContactesPerCamp("surnames", surnames);
    }

    public List<Contact> cercarContactesPerTelefon(String phone){
        return cercarContactesPerCamp("phone", phone);
    }

    public List<Contact> cercarContactesPerEmail(String email){
        return cercarContactesPerCamp("email", email);
    }
//diferentes metodos que hacen consultas usando la API de Criteria de Hibernate
    public List<Contact> getContactes() {
        CriteriaQuery<Contact> cr = this.criteriaBuilder.createQuery(Contact.class);
        Root<Contact> root = cr.from(Contact.class);

        CriteriaQuery<Contact> query = cr.select(root);
        return this.session.createQuery(query).getResultList();
    }

    private List<Contact> cercarContactesPerCamp(String camp, String valor){
        CriteriaQuery<Contact> cr = this.criteriaBuilder.createQuery(Contact.class);
        Root<Contact> root = cr.from(Contact.class);

        CriteriaQuery<Contact> query = cr.select(root).where(this.criteriaBuilder.like(root.get(camp), "%" + valor + "%"));
        return this.session.createQuery(query).getResultList();
    }
}
