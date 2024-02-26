package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class HibernateExampleTest {
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    protected void setUp() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            entityManagerFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    @AfterEach
    protected void tearDown() {
        if ( entityManagerFactory != null ) {
            entityManagerFactory.close();
        }
    }

    @Test
    void save_user_to_database() {
        User user = new User("Nobody", LocalDate.now());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entityManager.persist(user);
        entityManager.getTransaction().commit();
    }

    @Test
    void retrieve_users_from_database() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        List<User> users = entityManager.createQuery("select u from User u", User.class).getResultList();
        users.forEach(System.out::println);

        entityManager.getTransaction().commit();
    }

    @Test
    void criteria_api_example() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("name"), "nobody"));

        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
        List<User> results = query.getResultList();
        results.forEach(System.out::println);

        entityManager.close();
    }
}
