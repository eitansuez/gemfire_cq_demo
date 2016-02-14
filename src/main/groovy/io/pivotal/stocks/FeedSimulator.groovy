package io.pivotal.stocks

import com.gemstone.gemfire.cache.Region
import com.gemstone.gemfire.cache.client.ClientCache
import com.gemstone.gemfire.cache.client.ClientCacheFactory

class FeedSimulator {

  ClientCache cache
  Region stocks
  StocksRepo repo

  FeedSimulator() {
    setup()
  }

  def setup() {
    cache = new ClientCacheFactory().set("cache-xml-file", "clientCache.xml").create()
    stocks = cache.getRegion("stocks")
    stocks.clear()
    repo = new StocksRepo(stocks)

    addStocksAndSubscribers()
  }

  static def priceLowerBound = 0
  static def priceUpperBound = 200
  static def priceFluctuationFactor = 0.15

  def random = new Random()

  def tickers = ['MSFT', 'VMW', 'GOOG']

  def someStartingValue() {
    random.nextDouble() * priceUpperBound
  }
  def someTicker() {
    def index = random.nextInt(tickers.size())
    tickers[index]
  }

  def addStocksAndSubscribers() {
    tickers.each { ticker ->
      repo.addStock(ticker, someStartingValue())
      repo.subscribeToStock(ticker, 'Eitan', 'John', 'Bill')
    }
  }

  def start() {
    new Timer().schedule({
      def ticker = someTicker()
      repo.updatePrice(ticker, newPrice(repo.getPrice(ticker)))
    } as TimerTask, 1000, 500)
  }

  def newPrice(currentPrice) {
    def price = nextPrice(currentPrice)
    while (price < priceLowerBound || price > priceUpperBound) {
      price = nextPrice(currentPrice)
    }

    price
  }

  def nextPrice(currentPrice) {
    def band = currentPrice * priceFluctuationFactor
    def fluctuation = random.nextDouble() * band
    currentPrice - (band/2) + fluctuation
  }


  static void main(String[] args) {
    new FeedSimulator().start()
  }

}

