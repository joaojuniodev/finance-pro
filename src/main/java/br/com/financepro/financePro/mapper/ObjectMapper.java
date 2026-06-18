package br.com.financepro.financePro.mapper;

public interface ObjectMapper<E, D, R> {

    E toEntity(R request);
    D toResponse(E entity);
}