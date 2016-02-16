package io.pivotal.stocks

import com.gemstone.gemfire.cache.Operation
import com.gemstone.gemfire.cache.Region
import com.gemstone.gemfire.cache.client.ClientCache
import com.gemstone.gemfire.cache.client.ClientCacheFactory
import com.gemstone.gemfire.cache.query.CqEvent
import com.gemstone.gemfire.cache.query.CqListener
import io.pivotal.stocks.domain.Stock
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.stage.Stage

class Chart extends Application {

  def chart
  NumberAxis timeAxis
  def seriesMap = [:]
  def t = 0

  @Override
  void start(Stage stage) throws Exception {

    def root = new Group()
    root.children.add(makeChart())
    stage.scene = new Scene(root)
    stage.show()

    setupListener()
  }

  def makeSeries(symbol, price = 0) {
    def series = new XYChart.Series()
    series.name = symbol
    chart.data.add(series)
    seriesMap[symbol] = series

    addPoint(symbol, price)
  }

  boolean existsSeries(symbol) {
    seriesMap.containsKey(symbol)
  }

  def addDataPoint(symbol, price) {
    if (!existsSeries(symbol)) {
      makeSeries(symbol)
    }

    t++

    if (t > timeAxis.upperBound) {
      shiftTimeAxisBounds()
    }

    addPoint(symbol, price)
  }

  def addPoint(symbol, price) {
    seriesMap[symbol].data.add(new XYChart.Data(t, price))
  }

  def shiftTimeAxisBounds() {
    timeAxis.lowerBound += 1
    timeAxis.upperBound += 1
  }

  def makeChart() {
    timeAxis = new NumberAxis(0, 25, 5)
    timeAxis.forceZeroInRange = false
    timeAxis.label = 'Time'

    def yAxis = new NumberAxis(0, 200, 20)
    yAxis.label = 'Stock Price'
    yAxis.tickLabelFormatter = new NumberAxis.DefaultFormatter(yAxis, '$', '')

    chart = new LineChart(timeAxis, yAxis)
    chart.id = 'StocksChart'
    chart.title = 'Stocks'
    chart.animated = false
    chart.createSymbols = false

    chart
  }

  def setupListener() {
    ClientCache cache = new ClientCacheFactory().set("cache-xml-file", "clientCache.xml").create()
    Region stocks = cache.getRegion("stocks")
    StocksRepo repo = new StocksRepo(stocks)

    repo.clear()
    repo.cqStocks('Eitan', new CqListener() {
      @Override
      void onEvent(CqEvent cqEvent) {
        Operation op = cqEvent.getQueryOperation()
        def symbol = cqEvent.key
        Stock stock = (Stock) cqEvent.newValue

        Platform.runLater {
          if (op == Operation.CREATE) {
            makeSeries(symbol, stock.price)
          } else if (op == Operation.UPDATE) {
            addDataPoint(symbol, stock.price)
          }
        }

      }

      @Override
      void onError(CqEvent cqEvent) {}

      @Override
      void close() {}
    })
  }

  static void main(String[] args) {
    launch(Chart, args)
  }
}
