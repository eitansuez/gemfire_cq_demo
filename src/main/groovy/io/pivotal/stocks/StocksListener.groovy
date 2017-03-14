package io.pivotal.stocks

import org.apache.geode.cache.query.CqEvent
import org.apache.geode.cache.query.CqListener

class StocksListener implements CqListener {

  @Override
  void onEvent(CqEvent cqEvent) {
    println "key: " + cqEvent.key
    println "new value: " + cqEvent.newValue
    println "operation: " + cqEvent.queryOperation
  }

  @Override
  void onError(CqEvent cqEvent) {
    println "an error occurred for.."
    println "key: " + cqEvent.key
    println "new value: " + cqEvent.newValue
    println "operation: " + cqEvent.queryOperation
  }

  @Override
  void close() {

  }
}
