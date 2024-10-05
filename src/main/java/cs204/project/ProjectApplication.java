package cs204.project;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import io.github.cdimascio.dotenv.Dotenv;

import cs204.project.Model.Tournament.Tournament;
import cs204.project.Model.Tournament.TournamentRepository;

import java.util.List;
import java.util.Arrays;
import java.time.LocalDate;

@SpringBootApplication
public class ProjectApplication {

  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.configure().load();
    System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

    ApplicationContext ctx = SpringApplication.run(ProjectApplication.class, args);

    JdbcTemplate template = ctx.getBean(JdbcTemplate.class);
    TournamentRepository repo = ctx.getBean(TournamentRepository.class);

    // 1.77 quintillion years of weekly tournaments
    template.execute(
        "CREATE TABLE IF NOT EXISTS tournaments (" +
            "tournament_id          BIGSERIAL PRIMARY KEY," +
            "tournament_name        VARCHAR(255) NOT NULL," +
            "tournament_date        DATE NOT NULL," +
            "tournament_rank_range  INT[] NOT NULL," +
            "tournament_status      VARCHAR(50) NOT NULL," +
            "tournament_region      VARCHAR(100) NOT NULL," +
            "tournament_playerList  JSON NOT NULL" +
            "CONSTRAINT tournament_pk PRIMARY KEY (tournament_id)" + 
            ")");
    
    template.execute(
        "CREATE TABLE IF NOT EXISTS player(" +
            "player_id    BIGSERIAL   NOT NULL," +
            "player_name  VARCHAR(50) NOT NULL," +
            "player_pw    VARCHAR(50) NOT NULL," +
            "user_role    VARCHAR(50) NOT NULL," +
            "CONSTRAINT player_pk PRIMARY KEY (player_id)" +
            ")");

    template.execute(
        "CREATE TABLE IF NOT EXISTS administrator(" +
            "admin_id   BIGSERIAL   NOT NULL," +
            "admin_name VARCHAR(50) NOT NULL," +
            "admin_pw   VARCHAR(50) NOT NULL," +
            "user_role  VARCHAR(50) NOT NULL," +
            "CONSTRAINT player_pk PRIMARY KEY (admin_id)" +
            ")");

    template.execute(
        "CREATE TABLE player_rank(" +
            "PID			LONG			NOT NULL," +
            "region		VARCHAR(50)	NOT NULL," +
            "player_rank	VARCHAR(50)	NOT NULL," +
            "CONSTRAINT playerrank_pk	PRIMARY KEY (PID, region)," +
            "CONSTRAINT playerrank_fk	FOREIGN KEY (PID) REFERENCES PLAYER(player_id)" +
            ")");

    template.execute(
        "CREATE TABLE TOURNAMENTLIST( " +
            "PID			LONG			NOT NULL," +
            "Region		VARCHAR(50)	NOT NULL," +
            "TID			LONG			NOT NULL," +
            "TournStatus VARCHAR(50)	NOT NULL," +
            "CONSTRAINT tlist_pk		PRIMARY KEY(PID, Region, TID)," +
            "CONSTRAINT tlist_fk1	FOREIGN KEY (PID) REFERENCES PLAYER(player_id)," +
            "CONSTRAINT tlist_fk2	FOREIGN KEY (TID) REFERENCES TOURNAMENT(tournament_id)" +
            ")");

    // List<Tournament> listTournaments = Arrays.asList(
    // new Tournament("t1", LocalDate.of(2024, 9, 23), new int[]{1,2}, "open",
    // "asia"),
    // new Tournament("t2", LocalDate.of(2024, 9, 23), new int[]{3,6}, "open",
    // "west")
    // );

    List<Tournament> listTournaments = Arrays.asList(
        new Tournament("t1", LocalDate.of(2024, 9, 23), new int[] { 1, 2 }, "open", "asia"),
        new Tournament("t2", LocalDate.of(2024, 9, 23), new int[] { 3, 6 }, "open", "west"));

    listTournaments.forEach(tournament -> {
      repo.save(tournament);
    });
  }

}
