function fetchTournamentList() {
  // Use Axios to send a GET request to the API Gateway
  axios
    .get("http://localhost:8080/tournaments")
    .then((response) => {
      // Handle success
      const data = response.data; // Get the JSON data
      const resultDiv = document.getElementById("result");
      resultDiv.innerHTML = "<h3>Tournaments:</h3>";

      if (data.length === 0) {
        resultDiv.innerHTML += "<p>No tournaments found.</p>";
      } else {
        // Create a list to display the tournaments
        const ul = document.createElement("ul");
        data.forEach((tournament) => {
          const li = document.createElement("li");
          li.textContent = `Tournament ID: ${tournament.id}, Name: ${tournament.name}`;
          ul.appendChild(li);
        });
        resultDiv.appendChild(ul);
      }
    })
    .catch((error) => {
      // Handle error
      console.error("Error fetching the tournament list:", error);
      document.getElementById(
        "result"
      ).innerHTML = `<p>Error fetching the tournament list: ${error.message}</p>`;
    });
}
