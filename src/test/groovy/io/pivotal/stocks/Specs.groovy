package io.pivotal.stocks

import org.apache.geode.cache.Region
import org.apache.geode.cache.client.ClientCache
import org.apache.geode.cache.client.ClientCacheFactory
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

  def "scenario where stock prices move"() {
    when: 'i define some stocks'
    repo.addStock('MSFT', 100)
    repo.addStock('VMW', 80)
    repo.addStock('GOOG', 120)

    and: 'i subscribe some folks to some mix of these stocks'
    repo.subscribeToStock('MSFT', 'Eitan', 'John', 'Bill')
    repo.subscribeToStock('GOOG', 'Eitan')
    repo.subscribeToStock('VMW', 'John')

    and: 'i update the price of MSFT (concerns eitan)'
    repo.updatePrice('MSFT', 102)
    repo.updatePrice('MSFT', 104)
    repo.updatePrice('MSFT', 106)
    repo.updatePrice('MSFT', 108)
    repo.updatePrice('MSFT', 110)

    and: 'i update the price of VMW (does not concern eitan)'
    repo.updatePrice('VMW', 85)
    repo.updatePrice('VMW', 90)
    repo.updatePrice('VMW', 95)

    then:
    true
  }

}
