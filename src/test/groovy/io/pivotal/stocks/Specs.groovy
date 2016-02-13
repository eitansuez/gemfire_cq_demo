package io.pivotal.stocks

import com.gemstone.gemfire.cache.Region
import com.gemstone.gemfire.cache.client.ClientCache
import com.gemstone.gemfire.cache.client.ClientCacheFactory
import spock.lang.Specification

class Specs extends Specification {

  ClientCache cache
  Region stocks
  StocksRepo repo

  def setup() {
    cache = new ClientCacheFactory().set("cache-xml-file", "clientCache.xml").create()
    stocks = cache.getRegion("stocks")
    stocks.clear()
    repo = new StocksRepo(stocks)
  }

  def "insert should put record in region"() {
    when:
    repo.addStock('MSFT', 100)

    then:
    stocks.get('MSFT').price == 100
  }

  def "getting all stocks i'm subscribe to"() {
    given:
    repo.addStock('MSFT', 100)
    repo.addStock('VMW', 80)
    repo.addStock('GOOG', 120)

    and:
    repo.subscribeToStock('MSFT', 'Eitan', 'John', 'Bill')
    repo.subscribeToStock('GOOG', 'Eitan')
    repo.subscribeToStock('VMW', 'John')

    when:
    def myStocks = repo.getStocksFor('Eitan')

    then:
    myStocks.size() == 2

    and:
    myStocks[0].symbol == 'GOOG'
    myStocks[1].symbol == 'MSFT'
  }

}
