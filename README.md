# mit-data-proxy
[![Java CI](https://github.com/MyHomeworkSpace/mit-data-proxy/workflows/Java%20CI/badge.svg)](https://github.com/MyHomeworkSpace/mit-data-proxy/actions)

A Java server to retrieve data from the MIT [Data Warehouse](https://ist.mit.edu/warehouse). Since the Data Warehouse requires you to be on the MIT network, the idea is that this is hosted on the MIT network, and the normal MyHomeworkSpace api-server downloads data from it regularly.

Eventually, this will also support proxying Touchstone/Duo authentication requests, so that sign-in attempts look like they come from Cambridge instead of wherever api-server is hosted.

## Setup
This server requires the Oracle thin JDBC client, which isn't distributed with in this repository for license reasons. See [libs/README.md](./libs/README.md) for instructions.
