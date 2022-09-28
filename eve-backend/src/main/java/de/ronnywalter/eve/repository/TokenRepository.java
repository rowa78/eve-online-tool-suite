package de.ronnywalter.eve.repository;

import de.ronnywalter.eve.model.Token;
import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, Integer> {
}
