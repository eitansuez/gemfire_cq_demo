package io.pivotal.stocks

import com.gemstone.gemfire.cache.Region
import com.gemstone.gemfire.cache.query.SelectResults
import io.pivotal.stocks.domain.Stock

class StocksRepo {
  Region stocks

  StocksRepo(Region stocksRegion) {
    this.stocks = stocksRegion
  }

  def addStock(symbol, price) {
    stocks.put(symbol, new Stock(symbol: symbol, price: price))
  }

  def updateStock(symbol, closure) {
    def stock = stocks.get(symbol)
    def updatedStock = closure.call(stock)
    stocks.put(symbol, updatedStock)
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
    def qs = stocks.getRegionService().getQueryService()
    def query = qs.newQuery('select distinct * from /stocks where $1 in subscribers order by symbol')
    SelectResults results = query.execute(subscriber) as SelectResults
    return results.asList()
  }

}
