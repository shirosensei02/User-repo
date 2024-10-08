// package cs204.project.tournament;

// import java.util.ArrayList;
// import java.util.List;

// // import javax.persistence.Entity;
// // import javax.persistence.GeneratedValue;
// // import javax.persistence.GenerationType;
// // import javax.persistence.Id;

// import jakarta.annotation.Generated;

// // @Entity
// public class Tournament {
//     // @Id
//     // @GeneratedValue(strategy = GenerationType.AUTO)
//     private Long id;

//     private String name;
//     private String date;
//     private int[] rankRange;
//     private String status;
//     private String region;
//     private List<String> playerList;

//     public Tournament(Long id, String name, String date, int[] rankRange, String status, String region) {
//       this.id = id;
//       this.name = name;
//       this.date = date;
//       this.rankRange = rankRange;
//       this.status = status;
//       this.region = region;
//       playerList = new ArrayList<>();
//     }

//     public Tournament(Long id, String name, String date, int[] rankRange, String status, String region, List<String> playerList) {
//       this.id = id;
//       this.name = name;
//       this.date = date;
//       this.rankRange = rankRange;
//       this.status = status;
//       this.region = region;
//       this.playerList = playerList;
//     }

//     public String getName() {
//       return name;
//     }
//     public void setName(String name) {
//       this.name = name;
//     }
//     public String getDate() {
//       return date;
//     }
//     public void setDate(String date) {
//       this.date = date;
//     }
//     public int[] getRankRange() {
//       return rankRange;
//     }
//     public void setRankRange(int[] rankRange) {
//       this.rankRange = rankRange;
//     }
//     public String getStatus() {
//       return status;
//     }
//     public void setStatus(String status) {
//       this.status = status;
//     }
//     public String getRegion() {
//       return region;
//     }
//     public void setRegion(String region) {
//       this.region = region;
//     }
//     public List<String> getPlayerList() {
//       return playerList;
//     }
//     public void setPlayerList(List<String> playerList) {
//       this.playerList = playerList;
//     }

//     public Long getId() {
//       return id;
//     }

//     public void setId(Long id) {
//       this.id = id;
//     }

    
// }
