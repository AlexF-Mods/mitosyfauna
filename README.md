# Mitos y Fauna (`mitosyfauna`)

Mod para Minecraft **26.3** (Fabric Loader + Fabric API, Java **25**) inspirado en la fauna nativa colombiana, los mitos y leyendas nocturnos y el comercio popular local.

## Contenido principal

### 1. Fauna nativa
- **Chigüiro (`chiguiro`)**: Pacífico en ríos, playas y manglares. Domesticable con caña de azúcar, sandía o manzana; puede sentarse o seguir a su dueño e irradia un aura calmante de Regeneración leve.
- **Oso de anteojos (`oso_anteojos`)**: Neutral en páramos, laderas y cumbres altas. Come bayas dulces, panales y bambú; protege agresivamente a sus crías si un jugador se acerca demasiado.
- **Cóndor de los Andes (`condor`)**: Ave planeadora que remonta vuelo en círculos sobre las cumbres rocosas y amortigua su descenso. Muda periódicamente **Plumas de cóndor** (útiles para reparar élitros con clic derecho y fabricar flechas).
- **Rana dardo venenosa (`rana_dardo`)**: Habita selvas en tres variantes de color (dorada, azul y roja). Envenena al contacto sin sigilo, y permite extraer **Toxina de rana dardo** (con frasco de vidrio) o impregnar **Dardos tóxicos** (con flecha) sin matarla.

### 2. Mitos y Leyendas (Jefes nocturnos)
Cada mito cuenta con barra de jefe opcional por proximidad (configurable en `config/mitosyfauna.json`) y suelta una reliquia única al ser derrotado:
- **El Mohán (`mohan`)**: Acecha ríos y pantanos de noche. Lanza chorros de agua y puede arrebatar temporalmente un ítem de tu mano (muestra un fardo robado visible y lo devuelve intacto al caer). Suelta el **Amuleto del Mohán**.
- **La Patasola (`patasola`)**: Acechadora de bosques densos (aparece también bajo copas de árboles oscuros) que salta sobre una sola pierna y emite sonidos direccionales detrás de ti para desorientarte antes de atacar. Suelta la **Garra de la Patasola**.
- **La Llorona (`llorona`)**: Espectro nocturno de riberas y bosques oscuros (aparece también bajo el dosel del bosque) con llantos direccionales lejanos y ondas de lamento que aplican Oscuridad y Lentitud. Suelta la **Lágrima de la Llorona**.
- **El Sombrerón (`sombreron`)**: Figura alta estilo Enderman que merodea caminos nocturnos, persigue a quienes viajan en montura o lo miran fijamente, se teletransporta a tu espalda y encabrita o desmonta caballos cercanos. Suelta el **Sombrero del Sombrerón**.

### 3. Comerciantes locales y economía
- **Peso colombiano (`peso_colombiano`)**: Moneda local intercambiable con Esmeraldas.
- **Tendero de barrio (`tendero`)**: Comerciante con delantal; intercambia Esmeraldas <-> Pesos colombianos, compra cosechas, vende víveres y la **Ruana tradicional** (exclusiva de comercio).
- **Vendedor ambulante (`vendedor_ambulante`)**: Dos variantes (frutas tropicales y café); vende **Mango biche**, **Lulo**, **Maracuyá**, **Café campesino**, **Plumas de cóndor** y el **Sombrero Vueltiao** (exclusivo de comercio; otorga Suerte y 25% de descuento dinámico con comerciantes locales mientras esté equipado).
- **Integración opcional con `gastronomia`**: Si el mod hermano `gastronomia` está cargado, ambos comerciantes añaden ofertas adicionales de tinto, empanadas, chocoramo, bon bon bum y café tostado sin crear dependencia obligatoria.

## Configuración
El archivo `config/mitosyfauna.json` se crea automáticamente al iniciar y permite activar/desactivar barras de jefe, sonidos direccionales, robo del Mohán, susto de monturas del Sombrerón, integración con `gastronomia`, puntos de vida de cada jefe, radios y tasa de cambio de pesos por esmeralda.

## Compilación y pruebas
```bash
./gradlew test
./gradlew build
```
