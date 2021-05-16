# myeda

# Assumes RHI namespace
oc project rhi

# Load properties into a config map
# Alternatively, the config map YAML is already in source control in the deploy/ocp folder
oc create configmap stock-config --from-file=application.properties 

# STEP 1 - Download and process the pricing files
kamel run ProcessPriceHistoryFileRoute.java
kamel run ProcessPriceHistoryFileRoute.java --dev

# STEP 2 - Create fresh stock price update messages based on price history messages
kamel run StockPriceSimulationRoute.java
kamel run StockPriceSimulationRoute.java --dev      

# Final Message Consumption
kamel run LogStockPriceRoute.java --dev
