package io.pivotal.stocks.domain

class Stock {
  String symbol
  double price

  Set subscribers = []

  @Override
  String toString() {
    "$symbol @ $price"
  }
}
