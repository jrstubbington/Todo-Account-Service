package org.example.todo.accounts.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@Component
@Slf4j
public class BatchExecutor<T> {

	@Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
	private int batchSize;

	private final EntityManagerFactory entityManagerFactory;

	public BatchExecutor(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	public <S extends T> void saveInBatch(Iterable<S> entities) {

		if (entities == null) {
			throw new IllegalArgumentException("The given Iterable of entities not be null!");
		}

		EntityManager entityManager = entityManagerFactory.createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();

		try {
			entityTransaction.begin();

			int i = 0;
			for (S entity : entities) {
				if (i % batchSize == 0 && i > 0) {
					log.info("Flushing the EntityManager containing {} entities ...", batchSize);

					entityTransaction.commit();
					entityTransaction.begin();

					entityManager.clear();
				}

				entityManager.persist(entity);
				i++;
			}

			log.info("Flushing the remaining entities ...");

			entityTransaction.commit();
		}
		catch (RuntimeException e) {
			if (entityTransaction.isActive()) {
				entityTransaction.rollback();
			}

			throw e;
		}
		finally {
			entityManager.close();
		}
	}
}
