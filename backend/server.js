const express = require('express');
const cors = require('cors');
const { Client, Pool } = require('pg');

const app = express();
const PORT = 3000;

// ============================================================
// 🔧 CONFIGURACIÓN DE LA BASE DE DATOS
// ============================================================
// Lee la configuración desde variables de entorno (Docker) 
// o usa valores por defecto (PostgreSQL nativo de Windows).
//
// Si corres con Docker → usa las variables del docker-compose.yml
// Si corres con "node server.js" → usa tu PostgreSQL local
// ============================================================
const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  port: parseInt(process.env.DB_PORT || '5432'),
  user: process.env.DB_USER || 'postgres',
  password: process.env.DB_PASSWORD || 'Orki-12-31',
  database: process.env.DB_NAME || 'banco_digital_db',
};

let pool = new Pool(dbConfig);

app.use(cors());
app.use(express.json({ limit: '20mb' }));

// Función para inicializar PostgreSQL automáticamente
async function initDatabase() {
  try {
    // 1. Conectar al 'postgres' raíz para crear 'banco_digital_db' si no existe
    const rootClient = new Client({
      host: dbConfig.host,
      port: dbConfig.port,
      user: dbConfig.user,
      password: dbConfig.password,
      database: 'postgres',
    });

    await rootClient.connect();
    const checkDb = await rootClient.query(
      "SELECT 1 FROM pg_database WHERE datname = 'banco_digital_db';"
    );
    if (checkDb.rowCount === 0) {
      await rootClient.query('CREATE DATABASE banco_digital_db;');
      console.log("✨ Base de datos 'banco_digital_db' creada.");
    } else {
      console.log("ℹ️ Base de datos 'banco_digital_db' detectada.");
    }
    await rootClient.end();

    // 2. Conectar a 'banco_digital_db' y crear la tabla usuarios_registro
    pool = new Pool(dbConfig);
    await pool.query(`
      CREATE TABLE IF NOT EXISTS usuarios_registro (
        id VARCHAR(64) PRIMARY KEY,
        nombre_completo VARCHAR(255) NOT NULL,
        tipo_documento VARCHAR(20) NOT NULL,
        numero_documento VARCHAR(30) NOT NULL UNIQUE,
        fecha_nacimiento VARCHAR(20) NOT NULL,
        correo VARCHAR(255) NOT NULL UNIQUE,
        celular VARCHAR(30) NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        foto_dni_frontal_uri TEXT,
        foto_dni_reverso_uri TEXT,
        foto_selfie_uri TEXT,
        estado_verificacion VARCHAR(30) DEFAULT 'VERIFICADO',
        fecha_verificacion BIGINT,
        numero_cuenta VARCHAR(40) NOT NULL UNIQUE,
        fecha_creacion_cuenta BIGINT NOT NULL,
        saldo_inicial NUMERIC(15, 2) DEFAULT 0.00,
        acepto_terminos BOOLEAN DEFAULT TRUE,
        acepto_tratamiento_datos BOOLEAN DEFAULT TRUE,
        fecha_aceptacion_terminos BIGINT,
        creado_el TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    `);
    console.log('✅ Base de datos PostgreSQL conectada y tabla usuarios_registro lista.');
  } catch (error) {
    console.error('❌ Error conectando a PostgreSQL:', error.message);
    // Si falla, reintentar en 5 segundos (útil cuando Docker está arrancando)
    console.log('⏳ Reintentando conexión en 5 segundos...');
    setTimeout(initDatabase, 5000);
  }
}

// ============================================================
// 📥 POST /api/register - Recibe el registro desde tu celular
// ============================================================
app.post('/api/register', async (req, res) => {
  try {
    const user = req.body;
    console.log(`\n📥 [NUEVO REGISTRO RECIBIDO DESDE EL CELULAR]`);
    console.log(`   👤 Nombre: ${user.nombreCompleto}`);
    console.log(`   🪪 DNI: ${user.numeroDocumento}`);
    console.log(`   💳 Cuenta Generada: ${user.numeroCuenta}`);
    console.log(`   📧 Correo: ${user.correo}`);
    console.log(`   📱 Celular: ${user.celular}`);

    const query = `
      INSERT INTO usuarios_registro (
        id, nombre_completo, tipo_documento, numero_documento, fecha_nacimiento,
        correo, celular, password_hash, foto_dni_frontal_uri, foto_dni_reverso_uri,
        foto_selfie_uri, estado_verificacion, fecha_verificacion, numero_cuenta,
        fecha_creacion_cuenta, saldo_inicial, acepto_terminos, acepto_tratamiento_datos,
        fecha_aceptacion_terminos
      ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14, $15, $16, $17, $18, $19)
      ON CONFLICT (numero_documento) DO UPDATE SET
        nombre_completo = EXCLUDED.nombre_completo,
        correo = EXCLUDED.correo,
        celular = EXCLUDED.celular,
        password_hash = EXCLUDED.password_hash,
        estado_verificacion = EXCLUDED.estado_verificacion,
        fecha_verificacion = EXCLUDED.fecha_verificacion
      RETURNING *;
    `;

    const values = [
      user.id,
      user.nombreCompleto,
      user.tipoDocumento,
      user.numeroDocumento,
      user.fechaNacimiento,
      user.correo,
      user.celular,
      user.passwordHash,
      user.fotoDniFrontalUri,
      user.fotoDniReversoUri,
      user.fotoSelfieUri,
      user.estadoVerificacion || 'VERIFICADO',
      user.fechaVerificacion || Date.now(),
      user.numeroCuenta,
      user.fechaCreacionCuenta || Date.now(),
      user.saldoInicial || 0.0,
      user.aceptoTerminos,
      user.aceptoTratamientoDatos,
      user.fechaAceptacionTerminos || Date.now(),
    ];

    const result = await pool.query(query, values);
    const saved = result.rows[0];

    console.log(`💾 ✅ GUARDADO EXITOSAMENTE EN POSTGRESQL (ID: ${saved.id})`);

    res.status(201).json({
      success: true,
      message: 'Guardado exitosamente en PostgreSQL',
      user: saved,
    });
  } catch (error) {
    console.error('❌ Error guardando en PostgreSQL:', error.message);
    res.status(500).json({ success: false, error: error.message });
  }
});

// ============================================================
// 📋 GET /api/users - Muestra todos los usuarios registrados
// ============================================================
// Abre esto en tu navegador: http://localhost:3000/api/users
app.get('/api/users', async (req, res) => {
  try {
    const result = await pool.query('SELECT * FROM usuarios_registro ORDER BY creado_el DESC');
    console.log(`📋 Consulta de usuarios: ${result.rows.length} registros encontrados.`);
    res.json(result.rows);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// ============================================================
// 💚 GET /health - Verifica que el servidor está encendido
// ============================================================
// Abre esto en tu navegador: http://localhost:3000/health
app.get('/health', (req, res) => {
  res.json({ status: 'OK', message: 'Servidor Banco Digital activo y funcionando' });
});

// Iniciar servidor en 0.0.0.0 para aceptar conexiones del celular
app.listen(PORT, '0.0.0.0', async () => {
  console.log(`=======================================================`);
  console.log(`🏦 BANCO DIGITAL - Servidor API`);
  console.log(`=======================================================`);
  console.log(`📡 Servidor escuchando en: http://localhost:${PORT}`);
  console.log(`💚 Verificar estado:       http://localhost:${PORT}/health`);
  console.log(`📋 Ver usuarios:           http://localhost:${PORT}/api/users`);
  console.log(`=======================================================`);
  console.log(`🔧 Base de datos: ${dbConfig.host}:${dbConfig.port}/${dbConfig.database}`);
  console.log(`=======================================================`);
  await initDatabase();
});
