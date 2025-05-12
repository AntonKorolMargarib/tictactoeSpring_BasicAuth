package com.margarib.tictactoe_spring.datasource.repository;

import com.margarib.tictactoe_spring.datasource.model.GameEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GameRepository extends CrudRepository<GameEntity, UUID> {
    @Query("select game from GameEntity game where game.vsComputer = false and game.playerO is null and game.playerX.id <> :ownerId")
    List<GameEntity> findAvailableMultiGames(@Param("ownerId") UUID ownerId);
}
