package io.pivotal.stocks

import com.gemstone.gemfire.cache.Region
import com.gemstone.gemfire.cache.query.*
import io.pivotal.stocks.domain.Stock

class StocksRepo {
  Region stocks

  StocksRepo(Region stocksRegion) {
    this.stocks = stocksRegion
  }

  def clear() {
    stocks.clear()
  }

  def addStock(symbol, price) {
    stocks.put(symbol, new Stock(symbol: symbol, price: price))
  }

  def getPrice(symbol) {
    stocks.get(symbol).price
  }

  def updateStock(symbol, closure) {
    def stock = stocks.get(symbol)
    def updatedStock = closure.call(stock)
    stocks.put(symbol, updatedStock)
  }

  def updatePrice(symbol, price) {
    updateStock(symbol, { stock ->
      stock.price = price
      stock
    })
  }

  def subscribeToStock(symbol, String... users) {
    updateStock(symbol, { stock ->
      users.each { user ->
        stock.subscribers << user
      }
      stock
    })
  }

  def getStocksFor(subscriber) {
    def query = queryService().newQuery('select distinct * from /stocks where $1 in subscribers order by symbol')
    SelectResults results = query.execute(subscriber) as SelectResults
    return results.asList()
  }

  private def queryService() {
    stocks.getRegionService().getQueryService()
  }

  private def cqAttributes(CqListener listener) {
    CqAttributesFactory cqf = new CqAttributesFactory()
    CqListener stocksListener = listener
    cqf.addCqListener(stocksListener)
    cqf.create()
  }

  def cqStocks(subscriber, CqListener listener = new StocksListener()) {
    def cqAttributes = cqAttributes(listener)

    String queryStr = "select * from /stocks where '$subscriber' in subscribers"

    CqQuery myStocksTracker = queryService().newCq("myStocks", queryStr, cqAttributes)

    SelectResults sResults = myStocksTracker.executeWithInitialResults()
    def stocks = sResults.collect { Struct result ->
      result.get('value')
    }
    println "initial results: " + stocks
    stocks
  }

}
