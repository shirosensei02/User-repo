package cs204.project.Model.Player;

// package cs204.project.tournament;
// import cs204.project.Model.User.MyAppUser;
import cs204.project.Exception.*;

import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.sql.Array;
import java.sql.Date;
// import java.time.LocalDate;

import java.sql.ResultSet;
import java.sql.SQLException;

// import org.json.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

@Repository
public class PlayerSQLRepo implements PlayerRepository {

  // TODO implement SQL connection here
  @Autowired
  private JdbcTemplate jdbcTemplate;
  // for testing
  // List<Tournament> tournaments = new ArrayList<>();

  public PlayerSQLRepo(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public int deleteById(Long id) {
    // TODO change to delete from db
    String deleteSQL = "DELETE from player WHERE ID = ?";
    int affectedRows = jdbcTemplate.update(deleteSQL, id);
    return affectedRows > 0 ? affectedRows : 0;
    // if (affectedRows > 0) {
    //   return affectedRows;
    // } else {
    //   throw new PlayerNotFoundException(id);
    // }
    
  }

  @Override
  public List<Player> findAll() {
    // TODO change to query all from db

    return jdbcTemplate.query("SELECT * from player",
        (rs, rownum) -> mapRow(rs, rownum));
  }

  @Override
  public Optional<Player> findById(Long id) {
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              "SELECT * FROM player WHERE id = ?",
              (rs, rowNum) -> mapRow(rs, rowNum),
              id));

    } catch (EmptyResultDataAccessException e) {
      // Player not found - return an empty object
      return Optional.empty();
    }
  }

  @Override
  public Optional<Integer> findPlayerRank(Long id, String region) {
    String sql = "SELECT PlayerRank FROM player p inner join tournament t ON p.id = t.id WHERE p.id = ? AND t.region = ?";
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              sql,
              (rs, rowNum) -> rs.getInt("PlayerRank"),
              id,
              region));

    } catch (EmptyResultDataAccessException e) {
      // Player not found - return an empty object
      return Optional.empty();
    }
  }

  @Override
  public Optional<Player> findByUsername(String username) {
    String sql = "SELECT * FROM player WHERE PlayerName = ?";

    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
            sql,
              (rs, rowNum) -> mapRow(rs, rowNum),
              username));

    } catch (EmptyResultDataAccessException e) {
      // Player not found - return an empty object
      return Optional.empty();
    }
  }

  @Override
  public Long save(Player player) {
    String sql = "INSERT INTO player (PlayerName, PlayerPW, UserRole) " +
        "VALUES (?, ?, ?) RETURNING id"; // RETURNING id

    GeneratedKeyHolder holder = new GeneratedKeyHolder();
    jdbcTemplate.update((Connection conn) -> {
      PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      setDB(conn, statement, player);

      return statement;
    }, holder);

    Long primaryKey = holder.getKey().longValue();
    System.out.println(primaryKey);
    return primaryKey;
  }

  @Override
  public int update(Player player) {
    String sql = "UPDATE tournaments SET PlayerName = ?, PlayerPW = ? WHERE id = ?";

    return jdbcTemplate.update((Connection conn) -> {
      PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

      setDB(conn, statement, player);

      statement.setLong(3, player.getId());
      return statement;
    });
  }

  public PreparedStatement setDB(Connection conn, PreparedStatement statement, Player player) throws SQLException {
    statement.setString(1, player.getUsername());
    statement.setString(2, player.getPassword());
    statement.setString(3, player.getRole());
    return statement;
  }

  public Player mapRow(ResultSet rs, int rowNum) throws SQLException {

    // Map the ResultSet data to a Tournament object
    return new Player(
        rs.getLong("id"),
        rs.getString("PlayerName"),
        rs.getString("PlayerPW"));
  }
}
