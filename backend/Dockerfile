# Usa Node.js 20 (versión estable)
FROM node:20-alpine

# Carpeta de trabajo dentro del container
WORKDIR /app

# Copia los archivos de dependencias primero (para cache eficiente)
COPY package.json package-lock.json* ./

# Instala las dependencias
RUN npm install --production

# Copia el código del servidor
COPY server.js ./

# El servidor escucha en el puerto 3000
EXPOSE 3000

# Comando para iniciar el servidor
CMD ["node", "server.js"]
