package io.pivotal.stocks.domain

class Stock {
  String symbol
  double price

  Stock() {}
  Stock(String symbol, double price) {
    this.symbol = symbol;
    this.price = price;
  }

  Set subscribers = []

  @Override
  String toString() {
    "$symbol @ $price"
  }
}
