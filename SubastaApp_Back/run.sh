#!/bin/bash
# Script de arranque para desarrollo local
# Usa el Maven bundleado de IntelliJ si mvn no está en el PATH

if command -v mvn &> /dev/null; then
  MVN="mvn"
elif [ -f "/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn" ]; then
  MVN="/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn"
else
  echo "ERROR: Maven no encontrado. Instalar con: brew install maven"
  exit 1
fi

echo "Iniciando SubastaApp Backend en http://localhost:8080/api/v1"
echo "Consola H2: http://localhost:8080/api/v1/h2-console"
"$MVN" spring-boot:run
