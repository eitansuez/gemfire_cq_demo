
A simple demonstration of the use of continuous queries in gemfire to notify a client of updates to stock prices for stocks that a user is interested in.  The stocks list is made up and so are the prices (i.e. not using a web service or anything).


1. `server` contains a simple start script and configuration to start a locator and two servers locally:

        cd server
        ./start.sh

2. start the Chart app

        gradle chartClient

3. start the FeedSimulator

        gradle feedSimulator

See the chart render prices for stocks as updates are made to prices in the region on the server.


