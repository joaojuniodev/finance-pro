package br.com.financepro.financePro.mapper;

public interface ObjectMapper<E, D> {

    E toEntity(D dto);
    D toResponse(E entity);
}