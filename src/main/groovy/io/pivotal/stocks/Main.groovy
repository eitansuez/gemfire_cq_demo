package io.pivotal.stocks

import com.gemstone.gemfire.cache.Region
import com.gemstone.gemfire.cache.client.ClientCache
import com.gemstone.gemfire.cache.client.ClientCacheFactory

class Main {
  static void main(String[] args) {

    ClientCache cache = new ClientCacheFactory()
        .set("cache-xml-file", "clientCache.xml")
        .create()

    Region stocks = cache.getRegion("stocks")
    StocksRepo repo = new StocksRepo(stocks)

    repo.clear()
    repo.cqStocks('Eitan')

    System.in.read()
  }
}
