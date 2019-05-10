package com.github.skiedrowski.flavourit

@Forks(
    [
        Fork(from = "delegator1", to = "toEsa1", ifActive = "Esa"),
        Fork(from = "delegator1", to = "toRoskosmos1", ifActive = "Roskosmos"),
        Fork(from = "delegator2", to = "toEsa2", ifActive = "Esa")
    ]
)
class MultipleForks() {

    internal fun toRoskosmos1() {

    }

    internal fun toEsa1() {

    }

    internal fun toEsa2() {

    }

}