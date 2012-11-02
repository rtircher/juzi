# juzi2

Backend of my quote wall app

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.
You will need Erlang version R15B01 or above installed.  On a Mac I recommend homebrew to install it (brew install erlang)

[1]: https://github.com/technomancy/leiningen

## Running

To start riak and application web server, run:

    ./go

This script will also make sure all the prerequisites are met or download what is required.

## TODO

* Fix go script for already created cluster
* Create configs for dev, (staging), prod
* Force https (x-forwarded protto and all that.  Look at lib-noir on how they did that)
* Create nicer 500 and 404 pages

## License

Copyright © 2012 Renaud Tircher
