package org.example.todo.accounts.repository;


import org.apache.log4j.Logger;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;


//https://persistencelayer.wixsite.com/springboot-hibernate/post/the-best-way-to-batch-inserts-via-saveall-iterable-s-entities
//@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class BatchRepositoryImpl<T, ID extends Serializable>
		extends SimpleJpaRepository<T, ID> implements BatchRepository<T, ID> {

	private static final Logger logger = Logger.getLogger(BatchRepositoryImpl.class.getName());

	private final EntityManager entityManager;

	public BatchRepositoryImpl(JpaEntityInformation entityInformation,
	                           EntityManager entityManager) {
		super(entityInformation, entityManager);

		this.entityManager = entityManager;
	}

	@Override
	public <S extends T> void saveInBatch(Iterable<S> entities) {

		if (entities == null) {
			throw new IllegalArgumentException("The given Iterable of entities cannot be null!");
		}

		BatchExecutor batchExecutor = SpringContext.getBean(BatchExecutor.class);
		batchExecutor.saveInBatch(entities);
	}
}
