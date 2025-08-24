package co.com.pragma.r2dbc.repository;

import co.com.pragma.r2dbc.entities.TipoPrestamoData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TipoPrestamoRepository extends ReactiveCrudRepository<TipoPrestamoData, Long>, ReactiveQueryByExampleExecutor<TipoPrestamoData> {

}
