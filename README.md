# Anime List

In this project, I used Ktor to make a REST API for a similar web application to [MyAnimeList](https://myanimelist.net/). 

The application gets its anime data from the JSON database in [this](https://github.com/manami-project/anime-offline-database.git) GitHub repository, and stores user data in a<br>
MySQL database, that is hosted online. I decided to only keep the first 1119 animes (around 50000 lines in the<br>
JSON file), so it's easier to deal with.

To test the app, I recommend using Postman. I have made a [Workspace](https://www.postman.com/research-explorer-27143040/workspace/anime-list) to test each endpoint and function. You<br>
can access the documentation for each request by clicking the Documentation tab on the left side.

There are 6 endpoints in total:
- root: returns a welcome text


- home: returns the stored animes and accepts query parameters:
  - title: only returns the animes, that contain the given value in their title
  - tag: only returns the animes, that contain the given value among their tags
  - sortby:
    - title: returns the stored animes, sorted by their title
    - year: returns the stored animes, sorted by their release year
    - episodes: returns the stored animes, sorted by their number of episodes


- register: inserts the user data, given in the request body, into the database, if there were no errors


- login: logs the user, given in the request body, if there were no errors


- profile: only accessible after logging in, returns all animes, that were added to the user


- home/{id}:
  - GET: return the anime with id {id}
  - PUT: only accessible after logging in, adds the anime with id {id} to the user's profile, with the watch<br>
  status given in the request body


<br>
When writing the specification, I misunderstood the description of the Ktor homework, I thought I had to make<br>
a full-stack website, so I wrote the specification in that regard. However, after starting the project, I realized the<br>
description says to write a REST API, so I decided to follow the description instead and not make a full-stack<br>web app.


If the database is offline, or the Postman link does not work, please let me know.