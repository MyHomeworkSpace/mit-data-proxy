# mit-data-proxy
A Java server to retrieve data from the MIT [Data Warehouse](https://ist.mit.edu/warehouse). Since the Data Warehouse requires you to be on the MIT network, the idea is that this would be hosted on the MIT network, and the normal MyHomeworkSpace api-server downloads data from it regularly.

This will also support proxying Touchstone/Duo authentication requests, so that it doesn't look like there's a sign-in attempt from "Hampden Sydney, Virginia".

## Setup
This server requires the Oracle thin JDBC client, which isn't distributed with in this repository for license reasons. See [libs/README.md](./libs/README.md) for instructions.
