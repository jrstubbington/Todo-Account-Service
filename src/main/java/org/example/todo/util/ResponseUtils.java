package org.example.todo.util;

import org.example.todo.dto.DtoEntity;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public final class ResponseUtils {

	private static final ModelMapper modelMapper = new ModelMapper();

	static {
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
	}

	private ResponseUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static <R, T extends DtoEntity> ResponseContainer<T> pageToDtoResponseContainer(Page<R> page, Class<T> clazz) {
		List<R> elements = page.getContent();
		List<T> dtoElements = convertToDtoList(elements, clazz);
		ResponseContainer<T> responseContainer = new ResponseContainer<>(true, null, dtoElements);
		responseContainer.setTotalElements(page.getTotalElements());
		responseContainer.setTotalPages(page.getTotalPages());
		responseContainer.setLast(page.isLast());
		responseContainer.setPage(page.getPageable().getPageNumber());
		responseContainer.setPageSize(page.getPageable().getPageSize());

		return responseContainer;
	}

	public static <R, T extends DtoEntity> ResponseContainer<T> pageToDtoResponseContainer(List<R> elements, Class<T> clazz) {
		List<T> dtoElements = convertToDtoList(elements, clazz);
		ResponseContainer<T> responseContainer = new ResponseContainer<>(true, null, dtoElements);
		responseContainer.setTotalElements(dtoElements.size());
		responseContainer.setTotalPages(1L);
		responseContainer.setLast(true);
		responseContainer.setPage(0L);

		return responseContainer;
	}

	public static <R, T extends DtoEntity> List<T> convertToDtoList(List<R> objects, Class<T> clazz) {
		return objects.stream()
				.map(obj -> convertToDto(obj, clazz))
				.collect(Collectors.toList());
	}

	public static <R, T extends DtoEntity> T convertToDto(R object, Class<T> clazz) {
		return modelMapper.map(object, clazz);
	}
}
