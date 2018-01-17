#!/bin/sh

path="target"
mvn clean install -U
rm -rf $path
cp -rf ./out $path
cp -rf ./tidyspider-core/target/lib $path/libs
cp -f ./tidyspider-core/target/tidyspider-core-0.1.0.jar $path/tidyspider-core.jar
cp -f ./tidyspider-samples/target/tidyspider-samples-0.1.0.jar $path/tidyspider-samples.jar
rm -f tidyspider.zip
zip -r tidyspider.zip $path
