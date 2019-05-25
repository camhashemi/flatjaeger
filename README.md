# FlatJaeger

FlatJaeger is an automated apartment search bot.

Features:
1. ImmobilienScout Search
2. Filter by date available
3. Filter by public transit distance to a given location
4. Receive emails each day with flats matching the given parameters

For example, you can ask:

"show me all flats with at least 2 bed rooms, under 1500 euro / month, available after 1/8/19, within 30 minutes of Alexanderplatz by public transit"

## Status

This was a side project that helped me find an apartment in Berlin without so much manual work. It would run on AWS each day on a scheduled trigger, and send me an email with flats that matched our custom search parameters.

The idea was to productize this service for others, but ImmoScout denied my API access request and around that time started blocking bots from scraping their pages. In the end, having a product so dependent on others and with little defensibility wasn't a great investment of time, so it's discontinued.

There's not much up-to-date documentation, so the recommended path is to start with Lambda.kt and follow the code, to see how the script works.

it still remains an example of how to do parallel computation in Kotlin, and can be forked for other scraping use cases.
