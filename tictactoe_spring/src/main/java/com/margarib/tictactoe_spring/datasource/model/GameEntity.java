package com.margarib.tictactoe_spring.datasource.model;

import com.margarib.tictactoe_spring.gameStates.GameState;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "Games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GameEntity {

    @Id
    @Column(name = "game_id", unique = true, nullable = false)
    private UUID gameId;

    @Column(name = "game_board")
    @Lob
    byte[] board;

    @Column(name = "vs_computer", nullable = false)
    private boolean vsComputer;

    @Enumerated(EnumType.STRING)
    private GameState state;

    @ManyToOne
    @JoinColumn(name="playerX_id", nullable=false, foreignKey = @ForeignKey(name="fk_playerX_id"))
    private UserEntity playerX;

    @ManyToOne
    @JoinColumn(name="playerO_id", foreignKey = @ForeignKey(name="fk_playerO_id"))
    private UserEntity playerO;

}

