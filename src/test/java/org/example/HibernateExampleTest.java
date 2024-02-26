package org.example;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class HibernateExampleTest {
    private EntityManagerFactory sessionFactory;

    @BeforeEach
    protected void setUp() throws Exception {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    @AfterEach
    protected void tearDown() throws Exception {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }

    @Test
    void save_user_to_database() {
        User user = new User("Nobody", LocalDate.now());
        EntityManager entityManager = sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    @Test
    void retrieve_users_from_database() {
        EntityManager entityManager = sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();

        List<User> users = entityManager.createQuery("select u from User u", User.class).getResultList();
        users.forEach(System.out::println);

        entityManager.getTransaction().commit();
    }
}
