package com.ejercicio.proyectofinal.utils

object HealthyIngredients {

    val vegetables = listOf(
        "Espinaca", "Kale", "Brócoli", "Zanahoria", "Tomate",
        "Lechuga", "Pepino", "Pimiento", "Cebolla", "Ajo",
        "Col rizada", "Rúcula", "Berros", "Calabaza", "Berenjena"
    )

    val proteins = listOf(
        "Tofu", "Tempeh", "Lentejas", "Garbanzos", "Frijoles negros",
        "Quinoa", "Edamame", "Seitán", "Proteína de soya",
        "Frijoles pintos", "Alubias", "Guisantes"
    )

    val grains = listOf(
        "Arroz integral", "Quinoa", "Avena", "Pan sin gluten",
        "Pasta sin gluten", "Arroz blanco", "Cuscús",
        "Amaranto", "Trigo sarraceno", "Mijo"
    )

    val dairy = listOf(
        "Leche de almendra", "Leche de soya", "Leche de coco",
        "Yogurt vegano", "Queso vegano", "Mantequilla vegana",
        "Leche de avena", "Leche de arroz", "Crema vegana"
    )

    val fruits = listOf(
        "Aguacate", "Plátano", "Manzana", "Fresa", "Arándanos",
        "Mango", "Piña", "Papaya", "Sandía", "Melón",
        "Kiwi", "Naranja", "Limón", "Frambuesas"
    )

    val nutsAndSeeds = listOf(
        "Almendras", "Nueces", "Pistaches", "Semillas de chía",
        "Semillas de linaza", "Semillas de girasol", "Ajonjolí",
        "Semillas de calabaza", "Anacardos", "Cacahuates"
    )

    val oils = listOf(
        "Aceite de oliva", "Aceite de coco", "Aceite de aguacate",
        "Aceite de ajonjolí"
    )

    val condiments = listOf(
        "Sal marina", "Pimienta negra", "Cúrcuma", "Jengibre",
        "Orégano", "Albahaca", "Cilantro", "Perejil",
        "Comino", "Páprika", "Hierbas frescas", "Salsa de soya",
        "Vinagre balsámico", "Mostaza", "Tahini"
    )

    /**
     * Retorna todos los ingredientes disponibles en una sola lista
     */
    fun getAll(): List<String> {
        return vegetables + proteins + grains + dairy + fruits +
                nutsAndSeeds + oils + condiments
    }

    /**
     * Retorna los ingredientes agrupados por categoría
     */
    fun getGrouped(): Map<String, List<String>> {
        return mapOf(
            "Vegetales" to vegetables,
            "Proteínas" to proteins,
            "Granos y cereales" to grains,
            "Lácteos vegetales" to dairy,
            "Frutas" to fruits,
            "Nueces y semillas" to nutsAndSeeds,
            "Aceites" to oils,
            "Condimentos y especias" to condiments
        )
    }
}

/**
 * Tags dietéticos que se pueden asignar a los platillos
 */
object DietaryTags {
    val tags = listOf(
        "Vegano",
        "Sin gluten",
        "Sin lactosa",
        "Sin azúcar",
        "Alto en proteína",
        "Bajo en calorías",
        "Sin soya",
        "Keto-friendly",
        "Paleo",
        "Orgánico",
        "Sin frutos secos",
        "Crudo"
    )
}

/**
 * Categorías de platillos por tiempo de comida
 */
object DishCategories {
    val categories = listOf(
        "Desayuno",
        "Comida",
        "Cena",
        "Snack",
        "Bebida",
        "Postre"
    )
}