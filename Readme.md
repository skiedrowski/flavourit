# A flavouring proof-of-concept using Kotlin Annotation Processing (kapt)

# Idea
* Provide a `Fork` annotation which specifies which method should be forked to which method on which flavour.
* Generate new kotlin code implementing the delegating method as an extension function to the class.

# Usage

Implement the class acting as a fork target:

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
            println("Running Roskosmos 1")
        }
    
        internal fun toEsa1() {
            println("Running Esa 1")
        }
    
        internal fun toEsa2() {
            println("Running Esa 2")
        }
    
    }
    
Specify the current flavour in `build.gradle`:

    kapt {
    	arguments {
    		arg("flavourit.flavour", "Esa")
    	}
    }
    
It should be possible to specify the flavour somewhere else (command line, another gradle task, gradle.properties, ...).

# Run the build

    ./gradlew clean build
    
During compilation, kapt scans for `Fork` and `Forks` annotations and creates a new File `MultipleForksExt.kt` containing the delegates:

	package com.github.skiedrowski.flavourit
	
	fun MultipleForks.delegator1() {
		println("MultipleForksExt: delegating from: delegator1 - to: toEsa1 - because of flavour: Esa")
		toEsa1()
	}
	
	fun MultipleForks.delegator2() {
		println("MultipleForksExt: delegating from: delegator2 - to: toEsa2 - because of flavour: Esa")
		toEsa2()
	}

# Run a test program

If a client (`AForkMeClient`) calls `delegator1()` or `delegator2()`, it'll be delegated to the implementations specified using the `Fork` annotations.

	fun main(args: Array<String>) {
		val multipleForks = MultipleForks()
		println("calling delegator1")
		multipleForks.delegator1()
		println("calling delegator2")
		multipleForks.delegator2()
	} 
	
Output:

	calling delegator1
	MultipleForksExt: delegating from: delegator1 - to: toEsa1 - because of flavour: Esa
	Running Esa 1
	calling delegator2
	MultipleForksExt: delegating from: delegator2 - to: toEsa2 - because of flavour: Esa
	Running Esa 2
