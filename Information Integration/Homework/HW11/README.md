My mashup application takes a cuisine and a place as input and outputs all restaurants in that location along with the weather at that location and the latest flickr photo by its side.

Data sources used:
yahoo search(YQL): to fetch restaurant data given location and cuisine.
flickr: to fetch latest flickr photos given restaurant.
geo(YQL): to fetch woeid given latitude and longitude.
weather(YQL): to fetch weather data given woeid.

YQL Query 1 - to fetch all restaurants that offer the cuisine in the location:
select * from local.search where query="sushi" and location="manhattan, nyc"

YQL Query 2 - to fetch weather data for given woeid:
select * from weather.forecast where woeid=12797168

YQL Query 3 - to fetch woeid given latitude and longitude:
select * from geo.placefinder where text="37.777089,-122.463516" and gflags="R"


Link to Yahoo Pipe
http://pipes.yahoo.com/pipes/pipe.info?_id=0ec866ece2745d9ed297f5dfa77d3edd