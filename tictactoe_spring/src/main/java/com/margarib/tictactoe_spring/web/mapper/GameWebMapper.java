package com.margarib.tictactoe_spring.web.mapper;

import com.margarib.tictactoe_spring.domain.model.Game;
import com.margarib.tictactoe_spring.web.model.GameDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GameWebMapper {
    GameDTO toGameDTO(Game game);

    Game toGame(GameDTO gameDTO);

}
