package io.pivotal.stocks

import org.apache.geode.cache.Region
import org.apache.geode.cache.client.ClientCache
import org.apache.geode.cache.client.ClientCacheFactory

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
