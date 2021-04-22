# myeda


# Download and process the pricing files
kamel run ProcessPriceHistoryFileRoute.java  --property-file ../application.properties --dependency mvn:org.apache.camel/camel-jackson --dependency mvn:org.apache.camel/camel-bindy

# Create fresh stock price update messages based on price history messages
kamel run StockPriceSimulationRoute.java  --property-file ../application.properties --dependency mvn:org.apache.camel/camel-jackson      

# Final Message Consumption
kamel run LogStockPriceRoute.java --property-file ../application.properties --dependency mvn:org.apache.camel/camel-atlasmap --resource mapping.json
