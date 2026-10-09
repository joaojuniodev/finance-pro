package br.com.financepro.financePro.common.contract;

import java.util.List;
import java.util.UUID;

public interface ICrud<E, Res, Req, Filter> {

    List<Res> getAll();

    List<Res> filter(Filter filter);

    Res getById(UUID id);

    Res create(Req request);

    Res update(Req request);

    void delete(UUID id);
}